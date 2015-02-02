package org.apache.cocoon.optional.pipeline.components.sax.json;

import org.apache.cocoon.pipeline.CachingPipeline;
import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.pipeline.caching.*;
import org.apache.cocoon.pipeline.component.CachingPipelineComponent;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class JsonSerializerTest {

    @Test
    public void doesDropRootElement() throws Exception {

        String SOURCE_XML = "<toBeDropped><item>1</item><item>2</item></toBeDropped>";
        String EXPECTED_RESULT = "{\"item\":[1,2]}";

        final Pipeline<SAXPipelineComponent> pipeline =
                new NonCachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(SOURCE_XML));
        pipeline.addComponent(new JsonSerializer());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos);
        pipeline.execute();

        assertEquals("valid json with dropped root element",EXPECTED_RESULT, new String(baos.toByteArray()));

    }

    @Test
    public void doesCacheIdenticalOutput() throws Exception {
        String SOURCE_XML = "<toBeDropped><item>1</item><item>2</item></toBeDropped>";
        String FAKED_XML = "<should><never>exist</never></should>";
        String EXPECTED_RESULT = "{\"item\":[1,2]}";

        SAXPipelineComponent underTest = new JsonSerializer();
        underTest.setConfiguration(new HashMap<String, String>(){
            {
                put("patternId", "5");
                put("startDate", "2015-01-02");
            }
        });

        final CacheKey simpleCachekey = new SimpleCacheKey();
        final Cache simpleCache = new SimpleCache();

        doPipelineTransformation(SOURCE_XML, underTest, simpleCachekey, simpleCache);

        ByteArrayOutputStream baos = doPipelineTransformation(FAKED_XML, underTest, simpleCachekey, simpleCache);

        assertEquals("second result is a cached result",EXPECTED_RESULT, new String(baos.toByteArray()));
    }

    @Test
    public void doesNotCacheDifferentOutput() throws Exception {
        String SOURCE_XML = "<toBeDropped><item>1</item><item>2</item></toBeDropped>";
        String FAKED_XML = "<should><be>there</be></should>";
        String EXPECTED_RESULT = "{\"be\":\"there\"}";

        SAXPipelineComponent underTest = new JsonSerializer();
        underTest.setConfiguration(new HashMap<String, String>(){
            {
                put("patternId", "5");
                put("startDate", "2015-01-02");
            }
        });

        final CacheKey simpleCachekey = new SimpleCacheKey();
        final Cache simpleCache = new SimpleCache();

        doPipelineTransformation(SOURCE_XML, underTest, simpleCachekey, simpleCache);

        underTest.setConfiguration(new HashMap<String, String>(){
            {
                put("patternId", "4");
                put("startDate", "2015-01-01");
            }
        });

        ByteArrayOutputStream baos = doPipelineTransformation(FAKED_XML, underTest, simpleCachekey, simpleCache);

        assertEquals("second result is a cached result",EXPECTED_RESULT, new String(baos.toByteArray()));
    }


    private ByteArrayOutputStream doPipelineTransformation(final String SOURCE_XML, SAXPipelineComponent underTest, final CacheKey simpleCachekey, Cache simpleCache) throws Exception {
        CachingPipeline<SAXPipelineComponent> pipeline =
                new CachingPipeline<SAXPipelineComponent>();
        pipeline.setCache(simpleCache);
        pipeline.addComponent(new XMLGenerator(SOURCE_XML){
            public CacheKey constructCacheKey() {
                return simpleCachekey;
            }
        });
        pipeline.addComponent(underTest);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos);
        pipeline.execute();
        return baos;
    }
}