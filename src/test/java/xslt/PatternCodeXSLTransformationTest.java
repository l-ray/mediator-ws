package xslt;

import de.clubspot.mediator.processing.transformation.AbstractTransformerTest;
import de.clubspot.mediator.processing.transformation.AddConnectionIdToElementsTransformer;
import de.clubspot.mediator.processing.transformation.RegionalFormatsRewriteTransformerTest;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XSLTTransformer;
import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

public class PatternCodeXSLTransformationTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);


    private SAXPipelineComponent underTest;

    @Before
    public void beforeMethod() {

        underTest = new XSLTTransformer();
        underTest.setConfiguration(new HashMap<String, Object>() {
            {
                put("source", "result");
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

}
