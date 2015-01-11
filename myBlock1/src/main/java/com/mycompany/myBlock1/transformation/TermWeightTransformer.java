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

import org.apache.cocoon.sax.SAXConsumer;
import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TermWeightTransformer extends AbstractSAXTransformer implements SAXConsumer{

	// contains the concatenated text
	String locationContent = "";
	
	boolean insideLocationTag = false;
	
	boolean insideTitleTag = false;
	
	boolean insideArticleTag = false;

    @Autowired
    private BasicDataSource datasource;


     @Override
     public void setup(Map<String, Object> parameter) {
         System.out.println("IN TERM_WEIGHT Setup");
         // this.datasource = (BasicDataSource) this.context
         //        .getBean("ds:"+parameter.get(USE_CONNECTION));
         System.out.println("Out TERM_WEIGHT Setup");
     }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

		System.out.println("IN startElement");
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
        System.out.println("OUT startElement");
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
        System.out.println("IN endElement");
		if (localName.equals("location")) {
			this.setInsideLocationTag(false);
		}

		if (localName.equals("title")) {
			this.setInsideTitleTag(false);
		}

		if (localName.equals("results")) {
			this.setInsideArticleTag(false);
            try {
                super.startElement(namespaceURI, "weight", "weight", null);
                addTokenizedContent(namespaceURI, locationContent);
            } catch (NullPointerException e) {
                System.out.println("NPE when adding tokenized Content");
            } finally {
                super.endElement(namespaceURI, "weight", "weight");
            }

			locationContent = "";
		}
		
		super.endElement(namespaceURI, localName, qName);
        System.out.println("OUT startElement");
	}

    @Override
	public void characters(char[] buffer, int start, int length)
			throws SAXException {
        System.out.println("IN characters");
		// concatenate the content

		StringBuilder contentBuffer = new StringBuilder();
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
			System.out.println("changing |"+element+"|("+element.length()+") to |"+cleanString(element.replaceAll("\\W+", " "))+"| ");
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
