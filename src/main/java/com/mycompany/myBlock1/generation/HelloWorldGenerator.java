package com.mycompany.myBlock1.generation;

import java.io.IOException;
import java.io.StringReader;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.generation.AbstractGenerator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class HelloWorldGenerator extends AbstractGenerator {

	AttributesImpl emptyAttr = new AttributesImpl();

    /**
     * Override the generate() method from AbstractGenerator.
     * It simply generates SAX events using SAX methods.  
     * I haven't done the comparison myself, but this 
     * has to be faster than parsing them from a string.
     */

    public void generate() throws IOException, SAXException,
    ProcessingException 
    {
       
      // the org.xml.sax.ContentHandler is inherited 
      // through org.apache.cocoon.xml.AbstractXMLProducer 
    	 
    	      String message = "<doc>My first Cocoon 2 generator!</doc>";
    	      
    	      XMLReader xmlreader = XMLReaderFactory.createXMLReader();
    	      xmlreader.setContentHandler(super.xmlConsumer);
    	      InputSource source = new InputSource(new StringReader(message));
    	      xmlreader.parse(source);

    }
}
