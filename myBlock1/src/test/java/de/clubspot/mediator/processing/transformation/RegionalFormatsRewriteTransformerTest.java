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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;

import static junit.framework.Assert.assertTrue;

public class RegionalFormatsRewriteTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);
    public static final String TEST_XML = "<easy>\nxml\n</easy>";

    @BeforeClass
    public static void setUp() {
        XMLUnit.setIgnoreWhitespace(false);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        final SAXParserFactory saxPFactory = SAXParserFactory.newInstance();
        saxPFactory.setValidating(false);
        /*try {
            saxPFactory.setFeature(
                    "http://apache.org/xml/features/"
                            + "nonvalidating/load-external-dtd",
                    false);
        } catch (Exception e) {
            LOG.error("While setting up SAX parser factory", e);
        }*/
        XMLUnit.setSAXParserFactory(saxPFactory);

        final DocumentBuilderFactory docBuildFactory =
                DocumentBuilderFactory.newInstance();
        docBuildFactory.setNamespaceAware(true);
        docBuildFactory.setValidating(false);
        /*try {
            docBuildFactory.setFeature(
                    "http://apache.org/xml/features/"
                            + "nonvalidating/load-external-dtd",
                    false);
        } catch (ParserConfigurationException e) {
            LOG.error("While setting up Document builder factory", e);
        }*/
        XMLUnit.setControlDocumentBuilderFactory(docBuildFactory);
        XMLUnit.setTestDocumentBuilderFactory(docBuildFactory);
    }

    @Test
    public void testTrimmingWhitespacesOnTextNodes()
            throws Exception {

        final Pipeline<SAXPipelineComponent> pipeline =
                new NonCachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(TEST_XML));

        final RegionalFormatsRewriteTransformer rewriteTransformer =
                new RegionalFormatsRewriteTransformer();

        pipeline.addComponent(rewriteTransformer);

        pipeline.addComponent(new XMLSerializer());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos);
        pipeline.execute();

        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff(TEST_XML.replace("\n",""), actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @After
    public void tearDown() throws Exception {

    }

}