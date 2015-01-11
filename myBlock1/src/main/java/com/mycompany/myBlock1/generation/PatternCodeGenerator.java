package com.mycompany.myBlock1.generation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.clubspot.mediator.criteria.UrlDateWrapper;
import de.clubspot.mediator.templates.WebHarvestTemplate;
import org.apache.cocoon.pipeline.ProcessingException;
import org.apache.cocoon.pipeline.SetupException;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.CompoundCacheKey;
import org.apache.cocoon.pipeline.caching.ParameterCacheKey;
import org.apache.cocoon.pipeline.caching.TimestampCacheKey;
import org.apache.cocoon.sax.util.XMLUtils;
import org.apache.cocoon.stringtemplate.StringTemplateGenerator;
import org.apache.commons.dbcp.BasicDataSource;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class PatternCodeGenerator extends StringTemplateGenerator {

    @Autowired
    private BasicDataSource datasource;

    private String patternId = null;

    private Date startDate = null;

    private Date endDate = null;

    @Override
    public void setup(final Map<String, Object> parameter) {
        System.out.println("IN PATTERNCODE-SETUP");
        super.setup(parameter);
        System.out.println("OUT PATTERNCODE-SETUP");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.sax.AbstractSAXProducer#setConfiguration(java.util.Map)
     */
    @Override
    public void setConfiguration(final Map<String, ? extends Object> parameter) {
        super.setConfiguration(parameter);
        // this.url = (URL) parameters.get("source");
        System.out.println("In SetConfiguration with "+parameter);
        patternId = (String) parameter.get("patternId");
        System.out.println("got first param");
        startDate = parseStartDate((String)parameter.get("startDate"));
        System.out.println("got second param");
        if (parameter.get("endDate") != null && parameter.get("endDate") != "")
            endDate = parseEndDate((String)parameter.get("endDate"));

        System.out.println("RETRIEVING datasource:"+this.datasource);
        //this.datasource = (BasicDataSource) this.context
        //        .getBean("ds:"+parameter.get(USE_CONNECTION));
        System.out.println("OUT PATTERNCODE-SETConfiguration");
    }

    public void setSource(URL source) {
        System.out.println("in SETSOURCE");
        super.setSource(source);
        System.out.println("out SETSOURCE");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.pipeline.component.CachingPipelineComponent#constructCacheKey()
     */
    @Override
    public CacheKey constructCacheKey() {
        System.out.println("In ConstructCacheKey");
        CompoundCacheKey cacheKey = new CompoundCacheKey();

        cacheKey.addCacheKey(new ParameterCacheKey("contextParameters", this.parameters));
        System.out.println("out ConstructCacheKey");
        return cacheKey;
    }


    @Override
    public void execute() {
        System.out.println("IN PATTERNCODE-EXECUTE");
        Connection myConnection;

        try {
            myConnection = this.datasource.getConnection();

            String harvestPattern = loadAndCompleteHarvestTemplate(myConnection);

            String myXMLAnswer = retrieveAndProcessSource(harvestPattern);

            XMLUtils.createXMLReader(this.getSAXConsumer()).parse(convertToInputSource(myXMLAnswer));
        } catch (Exception e) {
            throw new ProcessingException("Exception occured ", e);
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

        System.out.println("time elapsed: "
                + (System.currentTimeMillis() - startTime));

        return "<results>"
                + (scraperContext.getVar("result")).toString()
                + "</results>";
    }

    private String loadAndCompleteHarvestTemplate(Connection myConnection) throws ProcessingException {
        WebHarvestTemplate template = new WebHarvestTemplate(patternId, myConnection);

        UrlDateWrapper urlWrapper = new UrlDateWrapper(startDate);
        System.out.println("DateFormat" + template.getDateFormat());
        SimpleDateFormat df = new SimpleDateFormat(template.getDateFormat(), new java.util.Locale("de", "DE"));


        return new StringBuffer("<config xmlns=\"http://web-harvest.sourceforge.net/schema/1.0/config\" charset=\"UTF-8\">")
                .append("\n<var-def name=\"baseUrl\"><![CDATA[" + urlWrapper.getUrl(template.getUrl()) + "]]></var-def>")
                .append("\n<var-def name=\"startUrl\"><![CDATA[" + urlWrapper.getUrl(template.getStartUrl()) + "]]></var-def>")
                .append("\n<var-def name=\"startDate\">" + df.format(this.startDate) + "</var-def>")
                .append((this.endDate != null) ? "\n<var-def name=\"endDate\">" + df.format(this.endDate) + "</var-def>" : "")
                .append(template.getCompiledPattern())
                .append("\n</config>").toString();
    }

    public Date parseStartDate(String startDate) {
        try {
            System.out.println("parsing date "+startDate);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getDefault());
            return df.parse(startDate);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Date parseEndDate(String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getDefault());
            return df.parse(endDate);
        } catch (ParseException e) {
            //e.printStackTrace();
            return null;
        }
    }

}
