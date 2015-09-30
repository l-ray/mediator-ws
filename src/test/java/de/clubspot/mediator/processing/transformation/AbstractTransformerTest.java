package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.CachingPipeline;
import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.pipeline.caching.Cache;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by lray on 26.01.15.
 */
public abstract class AbstractTransformerTest {

    protected Map<String,Object> pipelineSetup = null;

    @BeforeClass
    public static void setUp() {
        XMLUnit.setIgnoreWhitespace(false);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        final SAXParserFactory saxPFactory = SAXParserFactory.newInstance();
        saxPFactory.setValidating(false);
        XMLUnit.setSAXParserFactory(saxPFactory);

        final DocumentBuilderFactory docBuildFactory =
                DocumentBuilderFactory.newInstance();
        docBuildFactory.setNamespaceAware(true);
        docBuildFactory.setValidating(false);

        XMLUnit.setControlDocumentBuilderFactory(docBuildFactory);
        XMLUnit.setTestDocumentBuilderFactory(docBuildFactory);
    }

    protected ByteArrayOutputStream transformThroughPipeline(String SOURCE_XML, SAXPipelineComponent underTest) throws Exception {
        final Pipeline<SAXPipelineComponent> pipeline =
                new NonCachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(SOURCE_XML));
        pipeline.addComponent(underTest);
        pipeline.addComponent(new XMLSerializer());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos,pipelineSetup);
        pipeline.execute();
        return baos;
    }

    protected ByteArrayOutputStream transformCachedThroughPipeline(final String SOURCE_XML, SAXPipelineComponent underTest, final CacheKey simpleCachekey, Cache simpleCache) throws Exception {
        CachingPipeline<SAXPipelineComponent> pipeline =
                new CachingPipeline<SAXPipelineComponent>();
        pipeline.setCache(simpleCache);
        pipeline.addComponent(new XMLGenerator(SOURCE_XML){
            public CacheKey constructCacheKey() {
                return simpleCachekey;
            }
        });
        pipeline.addComponent(underTest);

        pipeline.addComponent(new XMLSerializer(){
            public CacheKey constructCacheKey() {
                return simpleCachekey;
            }
        });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos,pipelineSetup);
        pipeline.execute();
        return baos;
    }
}
