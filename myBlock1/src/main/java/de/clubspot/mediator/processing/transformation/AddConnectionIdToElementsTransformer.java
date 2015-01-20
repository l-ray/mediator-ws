package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Map;

public class AddConnectionIdToElementsTransformer extends AbstractSAXTransformer implements SAXConsumer{

    private static final Logger LOG =
            LoggerFactory.getLogger(AddConnectionIdToElementsTransformer.class.getName());

    public static final String PARAM_ELEMENT_LOCAL_NAME = "elementLocalName";
    public static final String PARAM_ID_ELEMENT_LOCAL_NAME = "idElementLocalName";
    public static final String PARAM_ID = "id";

    private String elementLocalName = null;

    private String idElementLocalName = null;

    private String id = null;

     @Override
     public void setConfiguration(Map<String, ? extends Object> parameter) {
         LOG.trace("IN AddSourceIdToElementsTransformer Setup");

         elementLocalName = (String) parameter.get(PARAM_ELEMENT_LOCAL_NAME);

         idElementLocalName = (String) parameter.get(PARAM_ID_ELEMENT_LOCAL_NAME);

         id = (String) parameter.get(PARAM_ID);

         LOG.trace("Out AddSourceIdToElementsTransformer Setup");
     }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        LOG.trace("IN startElement {0}", localName);
        super.startElement(namespaceURI, localName, qName, attributes);

        if (localName.equals(elementLocalName)) {
            super.startElement(namespaceURI, idElementLocalName, idElementLocalName, new AttributesImpl());
            super.characters(id.toCharArray(),0,id.length());
            super.endElement(namespaceURI, idElementLocalName, idElementLocalName);
        }
        LOG.trace("OUT startElement");
	}

}