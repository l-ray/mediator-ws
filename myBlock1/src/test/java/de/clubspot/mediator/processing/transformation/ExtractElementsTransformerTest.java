package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
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
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testExtractingSingleElementFromArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article><pictures>http://picture.de/picture.jpg</pictures></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><pictures>0</pictures><id>0</id></article>"
                        +"<pictures><id>0</id><result>0</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testExtractingElementFromArticleHoldingGivenId()
            throws Exception {

        String SOURCE_XML =
                "<results><article><id>myOwnId</id><pictures>http://picture.de/picture.jpg</pictures></article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><id>myOwnId</id><pictures>0</pictures></article>"
                        +"<pictures><id>0</id><result>myOwnId</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


    @Test
    public void testExtractingMultipleElementsFromArticle()
            throws Exception {

        String SOURCE_XML =
                "<results><article>"+
                        "<pictures>http://picture.de/picture.jpg</pictures>"+
                        "<pictures>http://picture.de/picture2.jpg</pictures>"+
                        "</article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><pictures>0</pictures><pictures>1</pictures><id>0</id></article>"
                        +"<pictures><id>0</id><result>0</result><url>http://picture.de/picture.jpg</url></pictures>"
                        +"<pictures><id>1</id><result>0</result><url>http://picture.de/picture2.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testExtractingMultipleElementsFromMultipleArticles()
            throws Exception {

        String SOURCE_XML =
                "<results><article>"+
                        "<pictures>http://picture.de/1.jpg</pictures>"+
                        "<pictures>http://picture.de/2.jpg</pictures>"+
                        "</article>"+
                        "<article>"+
                        "<pictures>http://picture.de/3.jpg</pictures>"+
                        "<pictures>http://picture.de/4.jpg</pictures>"+
                        "</article></results>";

        String EXPECTED_RESULT_XML =
                "<results><article><pictures>0</pictures><pictures>1</pictures><id>0</id></article>"
                        +"<article><pictures>2</pictures><pictures>3</pictures><id>1</id></article>"
                        +"<pictures><id>0</id><result>0</result><url>http://picture.de/1.jpg</url></pictures>"
                        +"<pictures><id>1</id><result>0</result><url>http://picture.de/2.jpg</url></pictures>"
                        +"<pictures><id>2</id><result>1</result><url>http://picture.de/3.jpg</url></pictures>"
                        +"<pictures><id>3</id><result>1</result><url>http://picture.de/4.jpg</url></pictures>"
                        +"</results>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);
        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
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