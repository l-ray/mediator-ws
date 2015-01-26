package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.SAXPipelineComponent;
import org.custommonkey.xmlunit.Diff;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

import static junit.framework.Assert.assertTrue;

public class RegionalFormatsRewriteTransformerTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);
    public static final String TEST_XML = "<easy>\nxml\n</easy>";

    public SAXPipelineComponent underTest;

    @Before
    public void beforeMethod() {
        underTest = new RegionalFormatsRewriteTransformer();
    }

    @Test
    public void testTrimmingWhitespacesOnTextNodes()
            throws Exception {


        final ByteArrayOutputStream baos = transformThroughPipeline(TEST_XML, underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff(TEST_XML.replace("\n",""), actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @After
    public void tearDown() throws Exception {

    }

}