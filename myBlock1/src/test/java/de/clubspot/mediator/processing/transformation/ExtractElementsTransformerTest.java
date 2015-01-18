package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;

import static junit.framework.Assert.assertTrue;

public class ExtractElementsTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);

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

    @Test
    public void testExtractingNoElementFromArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article></article></results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testExtractingSingleElementFromArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article><picture>http://picture.de/picture.jpg</picture></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><picture>0</picture><id>0</id></article>"
                        +"<pictures><id>0</id><result>0</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testExtractingElementFromArticleHoldingGivenId()
            throws Exception {

        String SOURCE_XML =
                "<results><article><id>myOwnId</id><picture>http://picture.de/picture.jpg</picture></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><id>myOwnId</id><picture>0</picture></article>"
                        +"<pictures><id>0</id><result>myOwnId</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


    @Test
    public void testExtractingMultipleElementsFromArticle()
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


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testExtractingMultipleElementsFromMultipleArticles()
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


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testStrippingWhitespaces()
            throws Exception {

        String SOURCE_XML =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><results><source><link>http://www.l-ray.de/fileadmin/</link><name>dummyData l-ray</name><icon>http://www.l-ray.de/fileadmin/template/css/images/ico_cached.gif</icon></source>" +
                        "<article>" +
                        "<title>test titel 1</title><subtitle>die subtitle die</subtitle>" +
                        "<price>5 Euro</price><picture>\nhttp://www.l-ray.de/fileadmin/\n" +
                        "anonym.jpg\n</picture>" +
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
                        "<pictures><id>0</id><result>0</result><url>http://www.l-ray.de/fileadmin/\nanonym.jpg</url></pictures>"+
                        "<pictures><id>1</id><result>1</result><url>http://www.l-ray.de/fileadmin/\ntemplate/css/images/logo.gif</url></pictures>"+
                        "</results>\n";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


    @After
    public void tearDown() throws Exception {

    }

    private ByteArrayOutputStream transformThroughPipeline(String SOURCE_XML) throws Exception {
        final Pipeline<SAXPipelineComponent> pipeline =
                new NonCachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(SOURCE_XML));
        pipeline.addComponent(new ExtractElementsTransformer());
        pipeline.addComponent(new XMLSerializer());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos);
        pipeline.execute();
        return baos;
    }

}