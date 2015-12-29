package xslt;

import de.clubspot.mediator.processing.transformation.AbstractTransformerTest;
import de.clubspot.mediator.processing.transformation.AddConnectionIdToElementsTransformer;
import de.clubspot.mediator.processing.transformation.RegionalFormatsRewriteTransformerTest;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XSLTTransformer;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

@Ignore
public class PatternCodeXSLTransformationTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);


    private SAXPipelineComponent underTest;

    @Before
    public void beforeMethod() throws MalformedURLException {

        underTest = new XSLTTransformer();
        underTest.setConfiguration(new HashMap<String, Object>() {
            {
                put("source", new URL("file:///home/lray/workspace/java/mediator-ws/src/main/resources/xslt/transform.xsl"));
            }
        });

    }


    @Test
    public void testTransformingXML()
            throws Exception {

        String SOURCE_XML = readFile("src/test/resources/xslt/ra_schema_template.xml");
        String EXPECTED_RESULT_XML = readFile("src/test/resources/xslt/ra_schema_template_as_webharvest.xml");

        XMLUnit.setIgnoreWhitespace(true);

        final ByteArrayOutputStream baos = transformThroughPipeline(SOURCE_XML, underTest);

        System.out.println(new String(baos.toByteArray()));

        final Diff diff = new Diff(
                EXPECTED_RESULT_XML,
                new String(baos.toByteArray())
        );
        assertTrue("Transformation did not work like expected:" + diff + ":"+new String(baos.toByteArray()),
                diff.identical());
    }

    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    }

}
