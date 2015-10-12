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

    @Test
    public void doesCorrectDateRewrite()
            throws Exception {

        final ByteArrayOutputStream baos = transformThroughPipeline("<start>Dienstag, 23.Dezember 2015</start>", underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff("<start>2015-12-23</start>", actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @Test
    public void doesCorrectDateWithoutYearRewrite()
            throws Exception {

        final ByteArrayOutputStream baos = transformThroughPipeline("<start>Freitag, 16. Mai</start>", underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff("<start>2015-05-16</start>", actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @Test
    public void doesCorrectDateWithoutWorkingday()
            throws Exception {

        final ByteArrayOutputStream baos = transformThroughPipeline("<start>16.5.15</start>", underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff("<start>2015-05-16</start>", actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @Test
    public void doesRecognizeEnglishWorkingDays()
            throws Exception {

        final ByteArrayOutputStream baos = transformThroughPipeline("<start>Tuesday, 5 May</start>", underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff("<start>2015-05-05</start>", actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
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

    @After
    public void tearDown() throws Exception {

    }

}