package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.caching.Cache;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.SimpleCache;
import org.apache.cocoon.pipeline.caching.SimpleCacheKey;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.custommonkey.xmlunit.Diff;
import org.junit.After;

import static junit.framework.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class RegexRewriteTransformerTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegexRewriteTransformerTest.class);

    public static final String TEST_XML = "<easy>xml\nis\neasy</easy>";

    public SAXPipelineComponent underTest;

    @Before
    public void beforeMethod() {
        underTest = new RegexRewriteTransformer();
    }

    @Test
    public void doesReplaceWhitespacesInGivenTextNodes()
            throws Exception {

        underTest.setConfiguration(new HashMap<String, Object>() {
            {
                put(RegexRewriteTransformer.PARAM_REGEX, "\n");
                put(RegexRewriteTransformer.PARAM_REPLACEMENT, "");
                put(RegexRewriteTransformer.PARAM_ELEMENT_LIST, "easy");
            }
        });

        final ByteArrayOutputStream baos = transformThroughPipeline(
                "<easy>xml\nis\neasy</easy>",
                underTest
        );
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff("<easy>xmliseasy</easy>", actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @Test
    public void doesNoteReplaceWhitespacesInArbitraryTextNodes()
            throws Exception {
        doesNotCacheDifferentOutputInternal(underTest, RegexRewriteTransformer.PARAM_CACHE_ID);
    }

    @Test
    public void doesCacheSameOutput() throws Exception {
        doesCacheSameOutputInternal(underTest, RegexRewriteTransformer.PARAM_CACHE_ID);
    }

    @After
    public void tearDown() throws Exception {

    }
}