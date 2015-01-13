package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Map;

public class RegionalFormatsTransformer extends AbstractSAXTransformer implements SAXConsumer{

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsTransformer.class.getName());

     @Override
     public void setup(Map<String, Object> parameter) {
         LOG.trace("IN RegionalFormatsTransformer Setup");

         LOG.trace("Out RegionalFormatsTransformer Setup");
     }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        LOG.trace("IN startElement");

        LOG.trace("OUT startElement");
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
        LOG.trace("IN endElement");

		super.endElement(namespaceURI, localName, qName);
        LOG.trace("OUT startElement");
	}

    @Override
	public void characters(char[] buffer, int start, int length)
			throws SAXException {
        LOG.trace("IN characters");
		// remove
        super.characters(String.valueOf(buffer).trim().toCharArray(), start, length);
        LOG.trace("OUT characters");
	}

}
