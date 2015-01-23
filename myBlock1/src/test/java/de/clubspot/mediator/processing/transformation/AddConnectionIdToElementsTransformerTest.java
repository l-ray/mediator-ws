package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

public class AddConnectionIdToElementsTransformerTest {

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
    public void testAddingIdToElement()
            throws Exception {

        String SOURCE_XML =
                "<resultset><result><picture>http://picture.de/picture.jpg</picture></result></resultset>";

        String EXPECTED_RESULT_XML =
                "<resultset><result>" +
                "<picture>http://picture.de/picture.jpg</picture>" +
                        "<connection>testId</connection>" +
                "</result></resultset>";


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testNullCase()
            throws Exception {

        String SOURCE_XML =
                "<resultset><whatever><picture>dummy</picture></whatever></resultset>";

        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);

        final Diff diff = new Diff(SOURCE_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testPreventOverriding() throws Exception {

        String SOURCE_XML =
                "<resultset><result><connection>someConnection</connection><picture>dummy</picture></result></resultset>";

        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML);

        final Diff diff = new Diff(SOURCE_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


    private ByteArrayOutputStream transformThroughPipeline(String SOURCE_XML) throws Exception {
        final Pipeline<SAXPipelineComponent> pipeline =
                new NonCachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(SOURCE_XML));
        SAXPipelineComponent underTest = new AddConnectionIdToElementsTransformer();
        underTest.setConfiguration(new HashMap<String, Object>() {
            {
                put(AddConnectionIdToElementsTransformer.PARAM_ELEMENT_LOCAL_NAME, "result");
                put(AddConnectionIdToElementsTransformer.PARAM_ID_ELEMENT_LOCAL_NAME, "connection");
                put(AddConnectionIdToElementsTransformer.PARAM_ID, "testId");
            }
        });
        pipeline.addComponent(underTest);
        pipeline.addComponent(new XMLSerializer());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos);
        pipeline.execute();
        return baos;
    }
}