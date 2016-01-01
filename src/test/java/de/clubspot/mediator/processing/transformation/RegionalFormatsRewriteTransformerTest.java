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
import sun.util.calendar.Gregorian;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

public class RegionalFormatsRewriteTransformerTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformerTest.class);
    public static final String TEST_XML = "<easy>\nxml\n</easy>";

    public SAXPipelineComponent underTest;

    public String currentYear;

    @Before
    public void beforeMethod() {
        underTest = new RegionalFormatsRewriteTransformer();
        currentYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
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

        final Diff diff = new Diff("<start>"+currentYear+"-05-16</start>", actualDocument);

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

        final Diff diff = new Diff("<start>"+currentYear+"-05-05</start>", actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @Test
    public void doesCacheSameOutput() throws Exception {
        doesCacheSameOutputInternal(underTest, RegionalFormatsRewriteTransformer.PARAM_CACHE_ID);
    }

    @Test
    public void doesNotCacheDifferentOutput() throws Exception {
        doesNotCacheDifferentOutputInternal(underTest, RegionalFormatsRewriteTransformer.PARAM_CACHE_ID);
    }

    @After
    public void tearDown() throws Exception {

    }

}