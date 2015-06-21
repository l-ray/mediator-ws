package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.caching.Cache;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.SimpleCache;
import org.apache.cocoon.pipeline.caching.SimpleCacheKey;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.custommonkey.xmlunit.Diff;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

public class ExtractElementsTransformerTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);

    private SAXPipelineComponent underTest;

    @Before
    public void beforeMethod() {

        underTest = new ExtractElementsTransformer();
    }

    @Test
    public void canExtractNoElementFromArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article></article></results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void canForceNewIdOnArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><id>0</id></article></results>";

        underTest.setConfiguration(new HashMap<String, Object>(){{
                    put(ExtractElementsTransformer.PARAM_FORCE_NEW_PARENT_ID, "true");
        }});

        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void canExtractSingleElementFromArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article><picture>http://picture.de/picture.jpg</picture></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><picture>0</picture><id>0</id></article>"
                        +"<pictures><id>0</id><result>0</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void canExtractElementFromArticleHoldingGivenId()
            throws Exception {

        String SOURCE_XML =
                "<results><article><id>myOwnId</id><picture>http://picture.de/picture.jpg</picture></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><id>myOwnId</id><picture>0</picture></article>"
                        +"<pictures><id>0</id><result>myOwnId</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


    @Test
    public void canExtractMultipleElementsFromArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article>"+
                        "<picture>http://picture.de/picture.jpg</picture>"+
                        "<picture>http://picture.de/picture2.jpg</picture>"+
                        "</article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><picture>0</picture><picture>1</picture><id>0</id></article>"
                        +"<pictures><id>0</id><result>0</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"<pictures><id>1</id><result>0</result><url>http://picture.de/picture2.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void canExtractMultipleElementsFromMultipleArticles()
            throws Exception {

        String SOURCE_XML =
                "<results><article>"+
                        "<picture>http://picture.de/1.jpg</picture>"+
                        "<picture>http://picture.de/2.jpg</picture>"+
                        "</article>"+
                        "<article>"+
                        "<picture>http://picture.de/3.jpg</picture>"+
                        "<picture>http://picture.de/4.jpg</picture>"+
                        "</article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><picture>0</picture><picture>1</picture><id>0</id></article>"
                        +"<article><picture>2</picture><picture>3</picture><id>1</id></article>"
                        +"<pictures><id>0</id><result>0</result><url>http://picture.de/1.jpg</url></pictures>"
                        +"<pictures><id>1</id><result>0</result><url>http://picture.de/2.jpg</url></pictures>"
                        +"<pictures><id>2</id><result>1</result><url>http://picture.de/3.jpg</url></pictures>"
                        +"<pictures><id>3</id><result>1</result><url>http://picture.de/4.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void canExtractMultipleElementsFromMultipleArticlesWithOtherData()
            throws Exception {

        String SOURCE_XML =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><results><source><link>http://www.l-ray.de/fileadmin/</link><name>dummyData l-ray</name><icon>http://www.l-ray.de/fileadmin/template/css/images/ico_cached.gif</icon></source>" +
                        "<article>" +
                        "<title>test titel 1</title><subtitle>die subtitle die</subtitle>" +
                        "<price>5 Euro</price><picture>http://www.l-ray.de/fileadmin/\n" +
                        "anonym.jpg</picture>" +
                        "<date>Freitag, 30. Januar</date><location>dummyLocation</location>" +
                        "<sourcelink>http://www.l-ray.de/fileadmin/</sourcelink>" +
                        "</article>" +
                        "<article>" +
                        "<title>weiterer test titel (2)</title><subtitle>dummy subtitle</subtitle>" +
                        "<price>2.50 Euro</price>" +
                        "<picture>\nhttp://www.l-ray.de/fileadmin/\ntemplate/css/images/logo.gif\n</picture>" +
                        "<date>Freitag, 30. Januar</date><location>dummyLocation</location>" +
                        "<sourcelink>http://www.l-ray.de/fileadmin/</sourcelink>" +
                        "</article>" +
                        "<article>" +
                        "<title>alte live mein Titel</title><subtitle/>" +
                        "<price/><picture>http://www.l-ray.de/fileadmin/</picture>" +
                        "<date>Freitag, 30. Januar</date>" +
                        "<location>dummyLocation</location>" +
                        "<sourcelink>http://www.l-ray.de/fileadmin/</sourcelink>" +
                        "</article>" +
                        "</results>\n";

        String EXPECTED_RESULT_XML =
                "<results><source><link>http://www.l-ray.de/fileadmin/</link><name>dummyData l-ray</name><icon>http://www.l-ray.de/fileadmin/template/css/images/ico_cached.gif</icon></source>" +
                        "<article>" +
                        "<title>test titel 1</title><subtitle>die subtitle die</subtitle>" +
                        "<price>5 Euro</price>" +
                        "<picture>0</picture>" +
                        "<date>Freitag, 30. Januar</date><location>dummyLocation</location>" +
                        "<sourcelink>http://www.l-ray.de/fileadmin/</sourcelink>" +
                        "<id>0</id>" +
                        "</article>" +
                        "<article>" +
                        "<title>weiterer test titel (2)</title><subtitle>dummy subtitle</subtitle>" +
                        "<price>2.50 Euro</price>" +
                        "<picture>1</picture>" +
                        "<date>Freitag, 30. Januar</date><location>dummyLocation</location>" +
                        "<sourcelink>http://www.l-ray.de/fileadmin/</sourcelink>" +
                        "<id>1</id>" +
                        "</article>" +
                        "<article>" +
                        "<title>alte live mein Titel</title><subtitle/>" +
                        "<price/>" +
                        "<picture>2</picture>" +
                        "<date>Freitag, 30. Januar</date>" +
                        "<location>dummyLocation</location>" +
                        "<sourcelink>http://www.l-ray.de/fileadmin/</sourcelink>" +
                        "<id>2</id>" +
                        "</article>" +
                        "<pictures><id>0</id><result>0</result><url>http://www.l-ray.de/fileadmin/\nanonym.jpg</url></pictures>"+
                        "<pictures><id>1</id><result>1</result><url>http://www.l-ray.de/fileadmin/\ntemplate/css/images/logo.gif</url></pictures>"+
                        "<pictures><id>2</id><result>2</result><url>http://www.l-ray.de/fileadmin/</url></pictures>"+
                        "</results>\n";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


    @Test
    public void testConfigurationParameter()
            throws Exception {

        String SOURCE_XML =
                "<root><parent><element>http://picture.de/picture.jpg</element></parent></root>";

        String EXPECTED_RESULT_XML =
                "<root><parent><element>eprefix-0</element><uid>pprefix-0</uid></parent>"
                +"<extract><id>eprefix-0</id><callback>pprefix-0</callback><url>http://picture.de/picture.jpg</url></extract>"
                +"</root>";


        underTest.setConfiguration(new HashMap<String, Object>() {
            {
                put(ExtractElementsTransformer.PARAM_ELEMENT_PARENT, "parent");
                put(ExtractElementsTransformer.PARAM_ELEMENT_PARENT_ID, "uid");
                put(ExtractElementsTransformer.PARAM_ELEMENT_PARENT_ID_PREFIX, "pprefix-");
                put(ExtractElementsTransformer.PARAM_ELEMENT_TO_BE_EXTRACTED, "element");
                put(ExtractElementsTransformer.PARAM_EXTRACTED_ELEMENT_CALLBACK_ELEMENT, "callback");
                put(ExtractElementsTransformer.PARAM_EXTRACTED_ELEMENT_ID, "id");
                put(ExtractElementsTransformer.PARAM_EXTRACTED_ELEMENT_ID_PREFIX, "eprefix-");
                put(ExtractElementsTransformer.PARAM_NEW_EXTRACTED_ELEMENT_NAME, "extract");
                put(ExtractElementsTransformer.PARAM_TARGET_PARENT, "root");
            }
        });

        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());

    }

    @Test
    public void doesCacheSameOutput() throws Exception {

        String SOURCE_XML =
                "<resultset><result>cacheMe</result></resultset>";

        String SHOULD_NEVER_APPEAR = "<resultset><result /></resultset>";

        String EXPECTED_RESULT_XML =
                "<resultset><result>cacheMe</result></resultset>";

        final CacheKey simpleCachekey = new SimpleCacheKey();
        final Cache simpleCache = new SimpleCache();

        underTest.setConfiguration(new HashMap<String, Object>() {
            { put(ExtractElementsTransformer.PARAM_CACHE_ID, "id1"); }
        });

        transformCachedThroughPipeline(SOURCE_XML, underTest, simpleCachekey, simpleCache);

        final ByteArrayOutputStream baos = transformCachedThroughPipeline(SHOULD_NEVER_APPEAR, underTest, simpleCachekey, simpleCache);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void doesNotCacheDifferentOutput() throws Exception {

        String SOURCE_XML =
                "<resultset><result>cacheOn</result></resultset>";

        String SHOULD_APPEAR = "<resultset><result>cacheOff</result></resultset>";

        final CacheKey simpleCachekey = new SimpleCacheKey();
        final Cache simpleCache = new SimpleCache();

        underTest.setConfiguration(new HashMap<String, Object>() {
            { put(ExtractElementsTransformer.PARAM_CACHE_ID, "id1");}
        });

        transformCachedThroughPipeline(SOURCE_XML, underTest, simpleCachekey, simpleCache);

        underTest.setConfiguration(new HashMap<String, Object>() {
            { put(ExtractElementsTransformer.PARAM_CACHE_ID, "id2");}
        });

        final ByteArrayOutputStream baos = transformCachedThroughPipeline(SHOULD_APPEAR, underTest, simpleCachekey, simpleCache);

        final Diff diff = new Diff(SHOULD_APPEAR, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


    @After
    public void tearDown() throws Exception {

    }


}