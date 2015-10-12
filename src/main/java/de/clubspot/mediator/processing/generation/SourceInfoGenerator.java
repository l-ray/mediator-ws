package de.clubspot.mediator.processing.generation;

import de.clubspot.mediator.templates.WebHarvestTemplate;
import org.apache.cocoon.pipeline.ProcessingException;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.ExpiresCacheKey;
import org.apache.cocoon.pipeline.caching.ParameterCacheKey;
import org.apache.cocoon.sax.util.XMLUtils;
import org.apache.cocoon.stringtemplate.StringTemplateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class SourceInfoGenerator extends StringTemplateGenerator {

    private static final Logger LOG =
            LoggerFactory.getLogger(SourceInfoGenerator.class.getName());
    public static final String PARAM_PATTERN_ID = "patternId";
    public static final int EXPIRING_TIME = 3600;
    public static final String DB_CONNECTION = "dbConnection";

    private static final int[] SOURCE_IDS_TO_USE = new int[]{0,1,2,4,6,7};

    private Connection dbConnection;

    private String patternId = null;

    @Override
    public void setup(final Map<String, Object> parameter) {
        LOG.trace("IN PATTERNCODE-SETUP with parameter "+parameter);
        this.dbConnection = (Connection) parameter.get(DB_CONNECTION);
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

        LOG.trace("In SetConfiguration with: "+parameter+" and "+this.patternId);

        if (parameter.get(PARAM_PATTERN_ID) != null) {
            this.patternId = (String) parameter.get(PARAM_PATTERN_ID);
        }

        LOG.trace("OUT SourceInfo-SETConfiguration with patternId ");
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
        LOG.trace("IN PATTERNCODE-EXECUTE with patternId"+patternId);
        Connection myConnection;

        try {
            myConnection = this.dbConnection;

            if (this.patternId != null) {
                System.out.println("Collecting source Id "+this.patternId);
                WebHarvestTemplate template = new WebHarvestTemplate(this.patternId, myConnection);

                XMLUtils.createXMLReader(this.getSAXConsumer()).parse(convertToInputSource(template.toXML()));
            } else {
                System.out.println("Generating sources overview");
                StringBuffer result = new StringBuffer("<sources>");

                for (int i=0; i<SOURCE_IDS_TO_USE.length; i++) {
                    WebHarvestTemplate template = new WebHarvestTemplate(
                            Integer.toString(SOURCE_IDS_TO_USE[i]),
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
