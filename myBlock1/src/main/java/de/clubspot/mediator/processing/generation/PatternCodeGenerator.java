package de.clubspot.mediator.processing.generation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.clubspot.mediator.criteria.UrlDateWrapper;
import de.clubspot.mediator.templates.WebHarvestTemplate;
import org.apache.cocoon.pipeline.ProcessingException;
import org.apache.cocoon.pipeline.SetupException;
import org.apache.cocoon.pipeline.caching.*;
import org.apache.cocoon.sax.util.XMLUtils;
import org.apache.cocoon.stringtemplate.StringTemplateGenerator;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

public class PatternCodeGenerator extends StringTemplateGenerator {

    private static final Logger LOG =
            LoggerFactory.getLogger(PatternCodeGenerator.class.getName());
    public static final String PARAM_END_DATE = "endDate";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String PARAM_START_DATE = "startDate";
    public static final String PARAM_PATTERN_ID = "patternId";
    public static final int EXPIRING_TIME = 3600;

    @Autowired
    private BasicDataSource datasource;

    private String patternId = null;

    private Date startDate = null;

    private Date endDate = null;

    @Override
    public void setup(final Map<String, Object> parameter) {
        LOG.trace("IN PATTERNCODE-SETUP");
        super.setup(parameter);
        LOG.trace("OUT PATTERNCODE-SETUP");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.sax.AbstractSAXProducer#setConfiguration(java.util.Map)
     */
    @Override
    public void setConfiguration(final Map<String, ? extends Object> parameter) {
        super.setConfiguration(parameter);

        LOG.trace("In SetConfiguration with: "+parameter);

        patternId = (String) parameter.get(PARAM_PATTERN_ID);

        startDate = parseDate((String) parameter.get(PARAM_START_DATE));

        if (parameter.get(PARAM_END_DATE) != null)
            endDate = parseDate((String) parameter.get(PARAM_END_DATE));

        LOG.trace("OUT PATTERNCODE-SETConfiguration");
    }

    public void setSource(URL source) {
        LOG.trace("in SETSOURCE");
        super.setSource(source);
        LOG.trace("out SETSOURCE");
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
                        }}
                ),
                Integer.toString(EXPIRING_TIME)
        );

        LOG.trace("out ConstructCacheKey");
        return cacheKey;
    }


    @Override
    public void execute() {
        LOG.trace("IN PATTERNCODE-EXECUTE");
        Connection myConnection;

        try {
            myConnection = this.datasource.getConnection();

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

        return "<resultset>"
                + (scraperContext.getVar("result")).toString()
                + "</resultset>";
    }

    private String loadAndCompleteHarvestTemplate(Connection myConnection) throws ProcessingException {
        WebHarvestTemplate template = new WebHarvestTemplate(patternId, myConnection);

        UrlDateWrapper urlWrapper = new UrlDateWrapper(startDate);
        LOG.trace("DateFormat" + template.getDateFormat());
        SimpleDateFormat df = new SimpleDateFormat(template.getDateFormat(), new java.util.Locale("de", "DE"));


        return new StringBuffer("<config xmlns=\"http://web-harvest.sourceforge.net/schema/1.0/config\" charset=\"UTF-8\">")
                .append("\n<var-def name=\"baseUrl\"><![CDATA[" + urlWrapper.getUrl(template.getUrl()) + "]]></var-def>")
                .append("\n<var-def name=\"startUrl\"><![CDATA[" + urlWrapper.getUrl(template.getStartUrl()) + "]]></var-def>")
                .append("\n<var-def name=\"startDate\">" + df.format(this.startDate) + "</var-def>")
                .append((this.endDate != null) ? "\n<var-def name=\"endDate\">" + df.format(this.endDate) + "</var-def>" : "")
                .append(template.getCompiledPattern())
                .append("\n</config>").toString();
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
