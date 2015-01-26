package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.SAXPipelineComponent;
import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

public class AddConnectionIdToElementsTransformerTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);


    private SAXPipelineComponent underTest;

    @Before
    public void beforeMethod() {

        underTest = new AddConnectionIdToElementsTransformer();
        underTest.setConfiguration(new HashMap<String, Object>() {
            {
                put(AddConnectionIdToElementsTransformer.PARAM_ELEMENT_LOCAL_NAME, "result");
                put(AddConnectionIdToElementsTransformer.PARAM_ID_ELEMENT_LOCAL_NAME, "connection");
                put(AddConnectionIdToElementsTransformer.PARAM_ID, "testId");
            }
        });

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


        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);

        final Diff diff = new Diff(EXPECTED_RESULT_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testNullCase()
            throws Exception {

        String SOURCE_XML =
                "<resultset><whatever><picture>dummy</picture></whatever></resultset>";

        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);

        final Diff diff = new Diff(SOURCE_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    @Test
    public void testPreventOverriding() throws Exception {

        String SOURCE_XML =
                "<resultset><result><connection>someConnection</connection><picture>dummy</picture></result></resultset>";

        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);

        final Diff diff = new Diff(SOURCE_XML, new String(baos.toByteArray()));
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }


}