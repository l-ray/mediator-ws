package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.*;

public class ExtractElementsTransformer extends AbstractSAXTransformer implements SAXConsumer{

    private static final Logger LOG =
            LoggerFactory.getLogger(ExtractElementsTransformer.class.getName());

    public String elementToBeExtracted = "picture";

    public String newExtractedElementName = "pictures";

    public String targetParent = "results";
    public String elementParent = "article";
    public String elementParentId = "id";
    public String extractedElementId = "id";

    private Map<String, Map<String,String>> elementsToBeExcluded = null;

    private boolean insideElementToBeExtracted = false;

    private int elementIdCounter = 0;

    private int parentIdCounter = 0;

    private String currentParentId = null;

    private boolean inParentIdTag = false;

    private boolean inParentElement = false;

    private boolean parentElementNeedsId = false;



     @Override
     public void setup(Map<String, Object> parameter) {
         LOG.trace("IN RegionalFormatsTransformer Setup");
         // TreeMap, to make the result comparable by unit test
         elementsToBeExcluded = new TreeMap<String,Map<String,String>>();
         LOG.trace("Out RegionalFormatsTransformer Setup");
     }

    @Override
    public void setConfiguration(Map<String, ? extends Object> parameter) {

        if (parameter.get("elementToBeExtracted") != null) {
            elementToBeExtracted = (String) parameter.get("elementToBeExtracted");
        }

        if (parameter.get("newExtractedElementName") != null) {
            newExtractedElementName = (String) parameter.get("newExtractedElementName");
        }

        if (parameter.get("targetParent") != null) {
            targetParent = (String) parameter.get("targetParent");
        }

        if (parameter.get("elementParent") != null) {
            elementParent = (String) parameter.get("elementParent");
        }

        if (parameter.get("elementParentId") != null) {
            elementParentId = (String) parameter.get("elementParentId");
        }

        if (parameter.get("extractedElementId") != null) {
            extractedElementId = (String) parameter.get("extractedElementId");
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
                    currentParentId = String.valueOf(parentIdCounter++);
                    super.characters(currentParentId.toCharArray(),0,currentParentId.length());
                    super.endElement(namespaceURI, elementParentId, elementParentId);
                }

                for (Map.Entry<String,Map<String,String>> elementToBeExcluded: elementsToBeExcluded.entrySet()) {
                    if (!elementToBeExcluded.getValue().containsKey("result")) {
                        elementToBeExcluded.getValue().put("result", currentParentId);
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
                String idCounterAsString = String.valueOf(elementIdCounter++);
                Map<String, String> elementData = new TreeMap<String, String>();
                elementsToBeExcluded.put(idCounterAsString, elementData);
                elementData.put("url", bufferString);
                super.characters(idCounterAsString.toCharArray(), 0, idCounterAsString.length());
            }
        }
        LOG.trace("OUT characters");
	}

}
