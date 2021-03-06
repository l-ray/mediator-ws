package de.clubspot.mediator.processing.generation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.clubspot.mediator.criteria.UrlDateWrapper;
import de.clubspot.mediator.templates.WebHarvestTemplate;
import org.apache.cocoon.pipeline.ProcessingException;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.ExpiresCacheKey;
import org.apache.cocoon.pipeline.caching.ParameterCacheKey;
import org.apache.cocoon.sax.util.XMLUtils;
import org.apache.cocoon.stringtemplate.StringTemplateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webharvest.Harvest;
import org.webharvest.HarvestLoadCallback;
import org.webharvest.Harvester;
import org.webharvest.definition.BufferConfigSource;
import org.webharvest.definition.ConfigSource;
import org.webharvest.definition.IElementDef;
import org.webharvest.ioc.HttpModule;
import org.webharvest.ioc.ScraperModule;
import org.webharvest.runtime.DynamicScopeContext;
import org.webharvest.runtime.web.HttpClientManager;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// TODO: refactor to just implement abstract SAX producer and eliminate StringTemplateGenerator
public class PatternCodeGenerator extends StringTemplateGenerator {

    private static final Logger LOG =
            LoggerFactory.getLogger(PatternCodeGenerator.class.getName());
    public static final String PARAM_END_DATE = "endDate";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String PARAM_START_DATE = "startDate";
    public static final String PARAM_PATTERN_ID = "patternId";
    public static final String PARAM_MAX_RESULTS = "maxResults";

    public static final int EXPIRING_TIME = 3600;
    public static final String DB_CONNECTION = "dbConnection";

    private Connection dbConnection;

    private String patternId = null;

    private Date startDate = null;

    private Date endDate = null;

    private Integer maxResult = -1;

    @Override
    public void setup(final Map<String, Object> parameter) {
        this.dbConnection = (Connection) parameter.get(DB_CONNECTION);
        super.setup(parameter);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.sax.AbstractSAXProducer#setConfiguration(java.util.Map)
     */
    @Override
    public void setConfiguration(final Map<String, ? extends Object> parameter) {
        super.setConfiguration(parameter);

        patternId = (String) parameter.get(PARAM_PATTERN_ID);

        startDate = parseDate((String) parameter.get(PARAM_START_DATE));

        if (parameter.get(PARAM_END_DATE) != null)
            endDate = parseDate((String) parameter.get(PARAM_END_DATE));

        if (parameter.get(PARAM_MAX_RESULTS) != null)
            maxResult = (Integer) parameter.get(PARAM_MAX_RESULTS);


    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.pipeline.component.CachingPipelineComponent#constructCacheKey()
     */
    @Override
    public CacheKey constructCacheKey() {
        LOG.trace("In ConstructCacheKey");
        CacheKey cacheKey = new ExpiresCacheKey(
                new ParameterCacheKey(
                        new HashMap<String, String>() {{
                            put(PARAM_PATTERN_ID, patternId);
                            put(PARAM_START_DATE, startDate.toString());
                            put(PARAM_MAX_RESULTS, maxResult.toString());

                        }}
                ),
                Integer.toString(EXPIRING_TIME)
        );

        LOG.trace("out ConstructCacheKey");
        return cacheKey;
    }


    @Override
    public void execute() {
        Connection myConnection;

        try {
            myConnection = this.dbConnection;

            String harvestPattern = loadAndCompleteHarvestTemplate(myConnection);

            String myXMLAnswer = retrieveAndProcessSource(harvestPattern);

            XMLUtils.createXMLReader(this.getSAXConsumer()).parse(convertToInputSource(myXMLAnswer));

        } catch (Exception e) {
            throw new ProcessingException("Exception occurred ", e);
        }
    }

    private InputSource convertToInputSource(String myXMLAnswer) {
        return new InputSource(new StringReader(myXMLAnswer));
    }

    private String retrieveAndProcessSource(String harvestPattern) throws IOException {
        final Injector injector = Guice.createInjector(
                new ScraperModule("."),
                new HttpModule(HttpClientManager.ProxySettings.NO_PROXY_SET)
        );

        final Harvest harvest = injector.getInstance(Harvest.class);

        ConfigSource configSource = new BufferConfigSource(harvestPattern);

        Harvester harvester = harvest.getHarvester(configSource,
                new HarvestLoadCallback() {
                    @Override
                    public void onSuccess(List<IElementDef> elements) {
                        // TODO: Auto-generated method stub :-/
                    }
                });

        long startTime = System.currentTimeMillis();
        DynamicScopeContext scraperContext = harvester.execute(new Harvester.ContextInitCallback() {
            @Override
            public void onSuccess(DynamicScopeContext context) {
                // TODO: add initial variables to the scrapers content, if any
            }
        });

        LOG.trace("time elapsed: "
                + (System.currentTimeMillis() - startTime));

        String scraperResult = (scraperContext.getVar("result")).toString();

        LOG.trace(scraperResult);

        return String.format("<resultset>%s</resultset>",scraperResult);
    }

    private String loadAndCompleteHarvestTemplate(Connection myConnection) throws ProcessingException {

        WebHarvestTemplate template = new WebHarvestTemplate(patternId, myConnection);

        UrlDateWrapper urlWrapper = new UrlDateWrapper(startDate);

        LOG.trace("DateFormat" + template.getDateFormat());

        SimpleDateFormat df = getDateFormat(template);

        LOG.trace("Searching for date: " + df.format(this.startDate));

        String harvestPattern = "<config xmlns=\"http://web-harvest.sourceforge.net/schema/1.0/config\" charset=\"UTF-8\">"
                + "\n<var-def name=\"baseUrl\"><![CDATA[" + urlWrapper.getUrl(template.getUrl()) + "]]></var-def>"
                + "\n<var-def name=\"startUrl\"><![CDATA[" + urlWrapper.getUrl(template.getStartUrl()) + "]]></var-def>"
                + "\n<var-def name=\"startDate\"><![CDATA[" + df.format(this.startDate) + "]]></var-def>"
                + "\n<var-def name=\"maxResult\"><![CDATA[" + this.maxResult + "]]></var-def>"
                + ((this.endDate != null) ? "\n<var-def name=\"endDate\"><![CDATA[" + df.format(this.endDate) + "]]></var-def>" : "")
                + template.getCompiledPattern() + "\n</config>";

        LOG.trace(harvestPattern);

        return harvestPattern;
    }

    private SimpleDateFormat getDateFormat(WebHarvestTemplate template) {
        return new SimpleDateFormat(
                template.getDateFormat(),
                new Locale(
                    template.getCountryCode().split("_")[1],
                    template.getCountryCode().split("_")[0]
                )
        );
    }

    public Date parseDate(String startOrEndDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setTimeZone(TimeZone.getDefault());
            return df.parse(startOrEndDate);
        } catch (ParseException e) {
            //e.printStackTrace();
            return null;
        }
    }

}
