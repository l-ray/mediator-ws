package com.mycompany.myBlock1.generation;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.AbstractGenerator;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;


	public class RequestExampleGenerator extends AbstractGenerator 
	{

	    // Will be initialized in the setup() method and used in generate()
	    Request request = null;
	    Enumeration paramNames = null;
	    String uri = null;

	    // We will use attributes this time.
	    AttributesImpl myAttr = new AttributesImpl();
	    AttributesImpl emptyAttr = new AttributesImpl();
	    
	   
	    public void setup(SourceResolver resolver, Map objectModel, 
	             String src, Parameters par)  
	         throws ProcessingException, SAXException, IOException 
	    {
	       super.setup(resolver, objectModel, src, par);
	       request = ObjectModelHelper.getRequest(objectModel);
	       paramNames = request.getParameterNames();
	       uri = request.getRequestURI();
	    } 

	    /**
	     * Implement the generate() method from AbstractGenerator.
	     */

	    public void generate() throws SAXException
	    {

	      contentHandler.startDocument();
	      
	      
	      
	      contentHandler.startElement("", "doc", "doc", emptyAttr);

	      // <uri> and all following elements will be nested inside the doc element
	      contentHandler.startElement("", "uri", "uri", emptyAttr);

	      contentHandler.characters(uri.toCharArray(),0,uri.length());

	      contentHandler.endElement("", "uri", "uri");
	      
	      contentHandler.startElement("", "params", "params", emptyAttr);
	         
	      while (paramNames.hasMoreElements())
	      {
	          // Get the name of this request parameter.
	          String param = (String)paramNames.nextElement();
	          String paramValue = request.getParameter(param);
	      
	          // Since we've chosen to reuse one AttributesImpl instance, 
	          // we need to call its clear() method before each use.  We 
	          // use the request.getParameter() method to look up the value 
	          // associated with the current request parameter.
	          myAttr.clear();
	          myAttr.addAttribute("","value","value","",paramValue);

	          // Each <param> will be nested inside the containing <params> element.
	          contentHandler.startElement("", "param", "param", myAttr);
	          contentHandler.characters(param.toCharArray(),0,param.length());
	          contentHandler.endElement("","param", "param");
	      }
	            
	      contentHandler.endElement("","params", "params");

	      contentHandler.startElement("", "date", "date", emptyAttr);

	      String dateString = (new Date()).toString();
	      contentHandler.characters(dateString.toCharArray(),0,dateString.length());

	      contentHandler.endElement("", "date", "date");
	      contentHandler.endElement("","doc", "doc");
	      contentHandler.endDocument();
	   }

	   public void recycle() {
	      super.recycle();
	      this.request = null;
	      this.paramNames = null;
	      //this.parNames = null;
	      this.uri = null;
	   }
	}

