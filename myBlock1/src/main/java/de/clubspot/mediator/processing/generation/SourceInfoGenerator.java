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

public class SourceInfoGenerator extends StringTemplateGenerator {

    private static final Logger LOG =
            LoggerFactory.getLogger(SourceInfoGenerator.class.getName());
    public static final String PARAM_PATTERN_ID = "patternId";
    public static final int EXPIRING_TIME = 3600;

    @Autowired
    private BasicDataSource datasource;

    private String patternId = null;

    @Override
    public void setup(final Map<String, Object> parameter) {
        LOG.trace("IN PATTERNCODE-SETUP");
        super.setup(parameter);
        LOG.trace("OUT PATTERNCODE-SETUP");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.sax.AbstractSAXProducer#setConfiguration(Map)
     */
    @Override
    public void setConfiguration(final Map<String, ? extends Object> parameter) {
        super.setConfiguration(parameter);

        LOG.trace("In SetConfiguration with: "+parameter);

        if (parameter.get(PARAM_PATTERN_ID) != null) {
            patternId = (String) parameter.get(PARAM_PATTERN_ID);
        }

        LOG.trace("OUT SourceInfo-SETConfiguration");
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

            if (patternId != null) {
                WebHarvestTemplate template = new WebHarvestTemplate(patternId, myConnection);

                XMLUtils.createXMLReader(this.getSAXConsumer()).parse(convertToInputSource(template.toXML()));
            } else {

                StringBuffer result = new StringBuffer("<sources>");

                for (int i=0; i<=2; i++) {
                    WebHarvestTemplate template = new WebHarvestTemplate(
                            Integer.toString(i),
                            myConnection
                    );

                    result.append(template.toXML("sources"));
                }

                result.append("</sources>");

                XMLUtils.createXMLReader(this.getSAXConsumer()).parse(convertToInputSource(result.toString()));

            }

        } catch (Exception e) {
            throw new ProcessingException("Exception occurred ", e);
        }
    }

    private InputSource convertToInputSource(String myXMLAnswer) {
        return new InputSource(new StringReader(myXMLAnswer));
    }

}
