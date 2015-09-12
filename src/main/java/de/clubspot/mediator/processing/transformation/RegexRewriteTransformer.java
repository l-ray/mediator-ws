package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.ExpiresCacheKey;
import org.apache.cocoon.pipeline.caching.ParameterCacheKey;
import org.apache.cocoon.pipeline.component.CachingPipelineComponent;
import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.*;

public class RegexRewriteTransformer extends AbstractSAXTransformer implements SAXConsumer, CachingPipelineComponent {

    private static final Logger LOG =
            LoggerFactory.getLogger(RegexRewriteTransformer.class.getName());

    public static final String PARAM_REGEX = "replaceAll";
    public static final String PARAM_REPLACEMENT = "withReplacement";
    public static final String PARAM_ELEMENT_LIST = "onElements";
    public static final String PARAM_CACHE_ID = "cache-id";

    Collection<String> elementsHoldingReplaceableContent = Arrays.asList("easy");

	boolean insideTagToReplaceRegex = false;

	String regex = "\n";
    String replacement = "";

    private String cacheId;

    @Override
    public void setConfiguration(Map<String, ? extends Object> parameter) {

        if (parameter.get(PARAM_ELEMENT_LIST) != null) {
            elementsHoldingReplaceableContent = Arrays.asList(
                    ((String) parameter.get(PARAM_ELEMENT_LIST)).split(",")
            );
        }

        if (parameter.get(PARAM_REGEX) != null) {
            regex = (String) parameter.get(PARAM_REGEX);
        }

        if (parameter.get(PARAM_REPLACEMENT) != null) {
            replacement = (String) parameter.get(PARAM_REPLACEMENT);
        }

        if (parameter.get(PARAM_CACHE_ID) != null) {
            cacheId = (String) parameter.get(PARAM_CACHE_ID);
        }
    }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

		// start a root element paragraph
		super.startElement(namespaceURI, localName, qName, attributes);
		if (elementsHoldingReplaceableContent.contains(localName)) {
			this.setInsideTagToReplaceRegex(true);
		}
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (elementsHoldingReplaceableContent.contains(localName)) {
			this.setInsideTagToReplaceRegex(false);
		}
		super.endElement(namespaceURI, localName, qName);
	}

    @Override
	public void characters(char[] buffer, int start, int length)
			throws SAXException {
		// concatenate the content
		String contentBuffer = new String(buffer, start, length);

		if (isInsideTagToReplaceRegex()) {
			contentBuffer = contentBuffer.replaceAll(regex,replacement);
		}

		super.characters(contentBuffer.toCharArray(), 0, contentBuffer.length());
	}


	public boolean isInsideTagToReplaceRegex() {
		return insideTagToReplaceRegex;
	}

	public void setInsideTagToReplaceRegex(boolean insideTagToReplaceRegex) {
		this.insideTagToReplaceRegex = insideTagToReplaceRegex;
	}

    @Override
    public CacheKey constructCacheKey() {

        return new ExpiresCacheKey(
                new ParameterCacheKey(
                        new HashMap<String, String>() {
                            { put(PARAM_CACHE_ID,cacheId); }
                        }
                ),
                "3600"
        );
    }
}
