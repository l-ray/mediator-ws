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

public class ExtractElementsTransformer extends AbstractSAXTransformer implements SAXConsumer, CachingPipelineComponent {

    private static final Logger LOG =
            LoggerFactory.getLogger(ExtractElementsTransformer.class.getName());

    public static final String PARAM_ELEMENT_TO_BE_EXTRACTED = "elementToBeExtracted";
    public static final String PARAM_NEW_EXTRACTED_ELEMENT_NAME = "newExtractedElementName";
    public static final String PARAM_TARGET_PARENT = "targetParent";
    public static final String PARAM_ELEMENT_PARENT = "elementParent";
    public static final String PARAM_ELEMENT_PARENT_ID = "elementParentId";
    public static final String PARAM_ELEMENT_PARENT_ID_PREFIX = "elementParentIdPrefix";
    public static final String PARAM_EXTRACTED_ELEMENT_ID = "extractedElementId";
    public static final String PARAM_EXTRACTED_ELEMENT_ID_PREFIX = "extractedElementIdPrefix";
    public static final String PARAM_EXTRACTED_ELEMENT_CALLBACK_ELEMENT = "extractedElementCallbackElement";

    public static final String PARAM_CACHE_ID = "cache-id";


    private String elementToBeExtracted = "picture";

    private String newExtractedElementName = "pictures";

    private String targetParent = "results";
    private String elementParent = "article";
    private String elementParentId = "id";
    private String elementParentIdPrefix = "";
    private String extractedElementId = "id";
    private String extractedElementCallbackElement = "result";

    private String extractedElementIdPrefix = "";

    private Map<String, Map<String,String>> elementsToBeExcluded = null;

    private boolean insideElementToBeExtracted = false;

    private int elementIdCounter = 0;

    private int parentIdCounter = 0;

    private String currentParentId = null;

    private boolean inParentIdTag = false;

    private boolean inParentElement = false;

    private boolean parentElementNeedsId = false;

    private String cacheId;

     @Override
     public void setup(Map<String, Object> parameter) {
         LOG.trace("IN RegionalFormatsTransformer Setup");
         // TreeMap, to make the result comparable by unit test
         elementsToBeExcluded = new TreeMap<String,Map<String,String>>();
         LOG.trace("Out RegionalFormatsTransformer Setup");
     }

    @Override
    public void setConfiguration(Map<String, ? extends Object> parameter) {

        if (parameter.get(PARAM_ELEMENT_TO_BE_EXTRACTED) != null) {
            elementToBeExtracted = (String) parameter.get(PARAM_ELEMENT_TO_BE_EXTRACTED);
        }

        if (parameter.get(PARAM_NEW_EXTRACTED_ELEMENT_NAME) != null) {
            newExtractedElementName = (String) parameter.get(PARAM_NEW_EXTRACTED_ELEMENT_NAME);
        }

        if (parameter.get(PARAM_TARGET_PARENT) != null) {
            targetParent = (String) parameter.get(PARAM_TARGET_PARENT);
        }

        if (parameter.get(PARAM_ELEMENT_PARENT) != null) {
            elementParent = (String) parameter.get(PARAM_ELEMENT_PARENT);
        }

        if (parameter.get(PARAM_ELEMENT_PARENT_ID) != null) {
            elementParentId = (String) parameter.get(PARAM_ELEMENT_PARENT_ID);
        }

        if (parameter.get(PARAM_ELEMENT_PARENT_ID_PREFIX) != null) {
            elementParentIdPrefix = (String) parameter.get(PARAM_ELEMENT_PARENT_ID_PREFIX);
        }

        if (parameter.get(PARAM_EXTRACTED_ELEMENT_ID) != null) {
            extractedElementId = (String) parameter.get(PARAM_EXTRACTED_ELEMENT_ID);
        }

        if (parameter.get(PARAM_EXTRACTED_ELEMENT_ID_PREFIX) != null) {
            extractedElementIdPrefix = (String) parameter.get(PARAM_EXTRACTED_ELEMENT_ID_PREFIX);
        }

        if (parameter.get(PARAM_EXTRACTED_ELEMENT_CALLBACK_ELEMENT) != null) {
            extractedElementCallbackElement = (String) parameter.get(PARAM_EXTRACTED_ELEMENT_CALLBACK_ELEMENT);
        }

        if (parameter.get(PARAM_CACHE_ID) != null) {
            cacheId = (String) parameter.get(PARAM_CACHE_ID);
        }


    }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        LOG.trace("IN startElement {0}", localName);

        if (localName.equals(elementParent)) {
            inParentElement = true;
        }

        if ( inParentElement && localName.equals(elementParentId)) {
            inParentIdTag = true;
        }

        if (!insideElementToBeExtracted) {
            if (localName.equals(elementToBeExtracted)) {
                insideElementToBeExtracted = true;
                parentElementNeedsId = true;
            }
            super.startElement(namespaceURI, localName, qName, attributes);
        }
        LOG.trace("OUT startElement");
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        LOG.trace("IN endElement");

        if (localName.equals(elementParent)) {
            inParentElement = false;
            inParentIdTag = false;
            if (parentElementNeedsId) {
                if (currentParentId == null || currentParentId.isEmpty()) {
                    super.startElement(namespaceURI, elementParentId, elementParentId, new AttributesImpl());
                    currentParentId = elementParentIdPrefix + String.valueOf(parentIdCounter++);
                    super.characters(currentParentId.toCharArray(),0,currentParentId.length());
                    super.endElement(namespaceURI, elementParentId, elementParentId);
                }

                for (Map.Entry<String,Map<String,String>> elementToBeExtracted: elementsToBeExcluded.entrySet()) {
                    if (!elementToBeExtracted.getValue().containsKey(extractedElementCallbackElement)) {
                        elementToBeExtracted.getValue().put(extractedElementCallbackElement, currentParentId);
                    }
                }
            }

            parentElementNeedsId = false;
            currentParentId = null;

        }

        if (localName.equals(elementToBeExtracted)) {
            insideElementToBeExtracted = false;
        }


        if (localName.equals(targetParent)) {
            for (Map.Entry<String,Map<String,String>> entry : elementsToBeExcluded.entrySet()) {

                super.startElement(namespaceURI, newExtractedElementName, newExtractedElementName, new AttributesImpl());

                super.startElement(namespaceURI, extractedElementId, extractedElementId, new AttributesImpl());
                super.characters(entry.getKey().toCharArray(), 0, entry.getKey().length());
                super.endElement(namespaceURI, extractedElementId, extractedElementId);

                for (Map.Entry<String, String> keyValue: entry.getValue().entrySet()) {
                    LOG.trace("Adds |{0}| with |{1}|", new String[]{keyValue.getKey(), keyValue.getValue()});

                    super.startElement(namespaceURI, keyValue.getKey(), keyValue.getKey(), new AttributesImpl());
                    super.characters(keyValue.getValue().toCharArray(), 0, keyValue.getValue().length());
                    super.endElement(namespaceURI, keyValue.getKey(), keyValue.getKey());
                }

                super.endElement(namespaceURI, newExtractedElementName, newExtractedElementName);
            }
        }
        super.endElement(namespaceURI, localName, qName);

        LOG.trace("OUT startElement");
	}

    @Override
	public void characters(char[] buffer, int start, int length)
            throws SAXException {
        LOG.trace("IN characters");
        if (!insideElementToBeExtracted) {
            if (inParentIdTag) {
                currentParentId = new String(buffer, start, length);
            }
            super.characters(buffer, start, length);
        }
        if (insideElementToBeExtracted) {
            String bufferString = new String(buffer, start, length).trim();

            if (!bufferString.isEmpty()) {
                String idCounterAsString = extractedElementIdPrefix + String.valueOf(elementIdCounter++);
                Map<String, String> elementData = new TreeMap<String, String>();
                elementsToBeExcluded.put(idCounterAsString, elementData);
                elementData.put("url", bufferString);
                super.characters(idCounterAsString.toCharArray(), 0, idCounterAsString.length());
            }
        }
        LOG.trace("OUT characters");
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
