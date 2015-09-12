package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.ExpiresCacheKey;
import org.apache.cocoon.pipeline.caching.ParameterCacheKey;
import org.apache.cocoon.pipeline.caching.SimpleCacheKey;
import org.apache.cocoon.pipeline.component.CachingPipelineComponent;
import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.HashMap;
import java.util.Map;

public class AddConnectionIdToElementsTransformer extends AbstractSAXTransformer implements SAXConsumer, CachingPipelineComponent {

    private static final Logger LOG =
            LoggerFactory.getLogger(AddConnectionIdToElementsTransformer.class.getName());

    public static final String PARAM_ELEMENT_LOCAL_NAME = "elementLocalName";
    public static final String PARAM_ID_ELEMENT_LOCAL_NAME = "idElementLocalName";
    public static final String PARAM_ID = "id";

    private String elementLocalName = null;

    private String idElementLocalName = null;

    private String id = null;

    private boolean inElementLocalName = false;

    private boolean idAlreadyExists= false;

     @Override
     public void setConfiguration(Map<String, ? extends Object> parameter) {

         elementLocalName = (String) parameter.get(PARAM_ELEMENT_LOCAL_NAME);

         idElementLocalName = (String) parameter.get(PARAM_ID_ELEMENT_LOCAL_NAME);

         id = (String) parameter.get(PARAM_ID);

     }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        super.startElement(namespaceURI, localName, qName, attributes);

        if (localName.equals(elementLocalName)) {
            inElementLocalName = true;
        }

        if (inElementLocalName && localName.equals(idElementLocalName)) {
            idAlreadyExists = true;
        }
	}

    @Override
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException{

        if (localName.equals(elementLocalName)) {
            inElementLocalName = false;

            if (!idAlreadyExists) {
                super.startElement(namespaceURI, idElementLocalName, idElementLocalName, new AttributesImpl());
                super.characters(id.toCharArray(),0,id.length());
                super.endElement(namespaceURI, idElementLocalName, idElementLocalName);
            }
            idAlreadyExists = false;
        }

        super.endElement(namespaceURI, localName, qName);

    }

    @Override
    public CacheKey constructCacheKey() {
        return new ExpiresCacheKey(
                new ParameterCacheKey(
                    new HashMap<String, String>() {
                        {
                            put(PARAM_ID,id);
                        }
                    }
                ),
                "3600"
        );
    }
}
