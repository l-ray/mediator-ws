package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Map;

public class RegionalFormatsRewriteTransformer extends AbstractSAXTransformer implements SAXConsumer{

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformer.class.getName());

     @Override
     public void setup(Map<String, Object> parameter) {
         LOG.trace("IN RegionalFormatsTransformer Setup");

         LOG.trace("Out RegionalFormatsTransformer Setup");
     }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        LOG.trace("IN startElement");
        super.startElement(namespaceURI, localName, qName, attributes);
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
        String newTerm = new String(buffer,start, length).trim();
		// remove whitespaces
        super.characters(newTerm.toCharArray(), 0, newTerm.length());
        LOG.trace("OUT characters");
	}

}
