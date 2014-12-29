package com.mycompany.myBlock1.transformation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.databases.bridge.spring.avalon.SpringToAvalonDataSourceBridge;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TermWeightTransformer extends AbstractSAXTransformer {
	// contains the concatenated text
	String locationContent = "";
	
	boolean insideLocationTag = false;
	
	boolean insideTitleTag = false;
	
	boolean insideArticleTag = false;

	private DataSourceComponent datasource;
	
	private ServiceManager manager;
	
   /**
     * @see org.apache.cocoon.transformation.AbstractSAXTransformer#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager aManager) throws ServiceException {
        super.service(aManager);
    
        this.manager = aManager;
        SpringToAvalonDataSourceBridge selector = 
        	(SpringToAvalonDataSourceBridge) manager
        	.lookup(DataSourceComponent.ROLE + "Selector");

        this.datasource = (DataSourceComponent) selector.select("myBlockDatabase");    
    }

    /**
     * @see org.apache.cocoon.transformation.AbstractSAXTransformer#dispose()
     */
    public void dispose() {
        super.dispose();
    }


	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

		// start a root element paragraph
		super.startElement(namespaceURI, localName, qName, attributes);
		if (localName.equals("location")) {
			this.setInsideLocationTag(true);
		}
		if (localName.equals("title")) {
			this.setInsideTitleTag(true);
		}
		if (localName.equals("results")) {
			this.setInsideArticleTag(true);
		}

	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		if (localName.equals("location")) {
			this.setInsideLocationTag(false);
		}

		if (localName.equals("title")) {
			this.setInsideTitleTag(false);
		}

		if (localName.equals("results")) {
			this.setInsideArticleTag(false);
            /*
            super.startElement(namespaceURI, "weight", "weight", null);
            try {
                addTokenizedContent(namespaceURI, locationContent);
            } catch (NullPointerException e) {
                System.out.println("NPE when adding tokenized Content");
            }
            super.endElement(namespaceURI, "weight", "weight");
            */
			locationContent = "";
		}
		
		super.endElement(namespaceURI, localName, qName);

	}

	public void characters(char[] buffer, int start, int length)
			throws SAXException {

		// concatenate the content

		StringBuffer contentBuffer = new StringBuffer();
		contentBuffer.append(buffer, start, length);

		if (isInsideLocationTag() || isInsideTitleTag()) {
			locationContent += " ";
			locationContent += contentBuffer.toString();
		}

		super.characters(buffer, start, length);
	}

	/**
	 * @param namespace
	 * @param content
	 * @throws SAXException
	 */
	public void addTokenizedContent(String namespace,String content) throws SAXException {
		
		StringTokenizer tokenizeBySpace = new StringTokenizer(content.replaceAll("\n", " ")," ");
		Set<String> tokenSet = new HashSet<String>();
		
		while (tokenizeBySpace.hasMoreElements()) {
			String element = tokenizeBySpace.nextToken();
			// System.out.println("changing |"+element+"|("+element.length()+") to |"+cleanString(element.replaceAll("\\W+", " "))+"| ");
			if (element.length() > 2) tokenSet.add(cleanString(element.replaceAll("\\W+", " ")));
		}

		Map<String,String> weightedTerms = getWeightForToken(tokenSet);
		
		for (String key:weightedTerms.keySet()) {
			
			AttributesImpl myAttribute = new AttributesImpl();
			myAttribute.addAttribute(namespace, "term", "term", "String", key);
			myAttribute.addAttribute(namespace, "weight", "weight", "String", weightedTerms.get(key));
			
			super.startElement(namespace, "weight-term","weight-term",myAttribute);
			// super.characters(element.toCharArray(), 0, element.length());
			super.endElement(namespace, "weight-term", "weight-term");
		}
	}
	
	/**
	 * 
	 * 
	 * @param terms - List of terms the weight is needed for
	 * @return map where the found term is the key, the weight is the value
	 * @author lray
	 */
	public Map<String,String> getWeightForToken(Set<String> terms) {
		
		Iterator<String> termIterator = terms.iterator();
		Map<String,String> termWithWeight = new HashMap<String,String>();
		
		if (termIterator.hasNext()) {
			StringBuffer whereInList = new StringBuffer("'"+termIterator.next()+"'");
			
			while (termIterator.hasNext()) {
				String nextTerm = termIterator.next();
				whereInList.append(",'"+nextTerm+"'");
			}
			
			String sQuery = "SELECT * FROM term_weight WHERE term IN ("+whereInList+");";
			
			System.out.println(sQuery);
			
			Statement stmt;
			ResultSet rs;
			
			try {
				stmt = this.datasource.getConnection().createStatement();
				rs = stmt.executeQuery(sQuery);
				while (rs.next()) { 
						System.out.println("Got |"+rs.getString("weight")+"| for |"+rs.getString("term")+"|");
						termWithWeight.put(rs.getString("term"),rs.getString("weight"));
				}
				
			} catch (SQLException e) {
				System.out.println("error getting statement:"+e.getMessage());
				e.printStackTrace();
			}
		}	
		return termWithWeight;
	}
	
	/**
	 * @param term
	 * @return term without whitespaces and stripped
	 */
	public String cleanString(String term) {
		term = term.trim();
		return term.toLowerCase();
	}
	
	public boolean isInsideLocationTag() {
		return insideLocationTag;
	}

	public void setInsideLocationTag(boolean insideLocationTag) {
		this.insideLocationTag = insideLocationTag;
	}

	public boolean isInsideArticleTag() {
		return insideArticleTag;
	}

	public void setInsideArticleTag(boolean insideArticleTag) {
		this.insideArticleTag = insideArticleTag;
	}

	public boolean isInsideTitleTag() {
		return insideTitleTag;
	}

	public void setInsideTitleTag(boolean insideTitleTag) {
		this.insideTitleTag = insideTitleTag;
	}

}
