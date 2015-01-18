package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class ExtractElementsTransformer extends AbstractSAXTransformer implements SAXConsumer{

    private static final Logger LOG =
            LoggerFactory.getLogger(ExtractElementsTransformer.class.getName());
    public static final String ELEMENT_TO_BE_EXTRACTED = "pictures";

    public static final String TARGET_PARENT = "results";
    public static final String ELEMENT_PARENT = "article";
    public static final String ELEMENT_PARENT_ID = "id";
    public static final String EXTRACTED_ELEMENT_ID = "id";

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
         elementsToBeExcluded = new HashMap<String,Map<String,String>>();
         LOG.trace("Out RegionalFormatsTransformer Setup");
     }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        LOG.trace("IN startElement {0}", localName);

        if (localName.equals(ELEMENT_PARENT)) {
            inParentElement = true;
        }

        if ( inParentElement && localName.equals(ELEMENT_PARENT_ID)) {
            inParentIdTag = true;
        }

        if (!insideElementToBeExtracted) {
            if (localName.equals(ELEMENT_TO_BE_EXTRACTED)) {
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

        if (localName.equals(ELEMENT_PARENT)) {
            inParentElement = false;
            inParentIdTag = false;
            if (parentElementNeedsId) {
                if (currentParentId == null || currentParentId.isEmpty()) {
                    super.startElement(namespaceURI, ELEMENT_PARENT_ID, ELEMENT_PARENT_ID, new AttributesImpl());
                    currentParentId = String.valueOf(parentIdCounter++);
                    super.characters(currentParentId.toCharArray(),0,currentParentId.length());
                    super.endElement(namespaceURI, ELEMENT_PARENT_ID, ELEMENT_PARENT_ID);
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

        if (localName.equals(ELEMENT_TO_BE_EXTRACTED)) {
            insideElementToBeExtracted = false;
        }


        if (localName.equals(TARGET_PARENT)) {
            for (Map.Entry<String,Map<String,String>> entry : elementsToBeExcluded.entrySet()) {
                super.startElement(namespaceURI, ELEMENT_TO_BE_EXTRACTED, ELEMENT_TO_BE_EXTRACTED, new AttributesImpl());

                super.startElement(namespaceURI, EXTRACTED_ELEMENT_ID, EXTRACTED_ELEMENT_ID, new AttributesImpl());
                super.characters(entry.getKey().toCharArray(), 0, entry.getKey().length());
                super.endElement(namespaceURI, EXTRACTED_ELEMENT_ID, EXTRACTED_ELEMENT_ID);

                for (Map.Entry<String, String> keyValue: entry.getValue().entrySet()) {
                    LOG.trace("Adds |{0}| with |{1}|", new String[]{keyValue.getKey(), keyValue.getValue()});

                    super.startElement(namespaceURI, keyValue.getKey(), keyValue.getKey(), new AttributesImpl());
                    super.characters(keyValue.getValue().toCharArray(), 0, keyValue.getValue().length());
                    super.endElement(namespaceURI, keyValue.getKey(), keyValue.getKey());
                }

                super.endElement(namespaceURI, ELEMENT_TO_BE_EXTRACTED, ELEMENT_TO_BE_EXTRACTED);
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
            String idCounterAsString = String.valueOf(elementIdCounter++);
            Map<String,String> elementData = new HashMap<String,String>();
            elementsToBeExcluded.put(idCounterAsString, elementData);
            elementData.put("url", new String(buffer, start, length));
            super.characters(idCounterAsString.toCharArray(),0, idCounterAsString.length());
        }
        LOG.trace("OUT characters");
	}

}
