package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.CachingPipeline;
import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.pipeline.caching.Cache;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.SimpleCache;
import org.apache.cocoon.pipeline.caching.SimpleCacheKey;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

public abstract class AbstractTransformerTest {

    protected Map<String,Object> pipelineSetup = null;

    @BeforeClass
    public static void setUp() throws Exception {
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

    protected void doesCacheSameOutputInternal(final SAXPipelineComponent underTest, final String cacheIdParameter) throws Exception {
        String SOURCE_XML =
                "<resultset><result>cacheMe</result></resultset>";

        String SHOULD_NEVER_APPEAR = "<resultset><result /></resultset>";

        String EXPECTED_RESULT_XML =
                "<resultset><result>cacheMe</result></resultset>";

        final CacheKey simpleCachekey = new SimpleCacheKey();
        final Cache simpleCache = new SimpleCache();

        underTest.setConfiguration(new HashMap<String, Object>() {
            { put(cacheIdParameter, "id1"); }
        });

        transformCachedThroughPipeline(SOURCE_XML, underTest, simpleCachekey, simpleCache);

        final ByteArrayOutputStream baos = transformCachedThroughPipeline(SHOULD_NEVER_APPEAR, underTest, simpleCachekey, simpleCache);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    protected void doesNotCacheDifferentOutputInternal(final SAXPipelineComponent underTest, final String cacheIdParameter) throws Exception {

        String SOURCE_XML =
                "<resultset><result>cacheOn</result></resultset>";

        String SHOULD_APPEAR = "<resultset><result>cacheOff</result></resultset>";

        final CacheKey simpleCachekey = new SimpleCacheKey();
        final Cache simpleCache = new SimpleCache();

        underTest.setConfiguration(new HashMap<String, Object>() {
            { put(cacheIdParameter, "id1");}
        });

        transformCachedThroughPipeline(SOURCE_XML, underTest, simpleCachekey, simpleCache);

        underTest.setConfiguration(new HashMap<String, Object>() {
            { put(cacheIdParameter, "id2");}
        });

        final ByteArrayOutputStream baos = transformCachedThroughPipeline(SHOULD_APPEAR, underTest, simpleCachekey, simpleCache);

        final Diff diff = new Diff(SHOULD_APPEAR, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }
}
