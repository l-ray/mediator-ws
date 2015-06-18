package de.clubspot.mediator.processing.transformation;

import java.sql.Connection;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class TermWeightTransformer extends AbstractSAXTransformer implements SAXConsumer{

	private static final Logger LOG =
            LoggerFactory.getLogger(TermWeightTransformer.class.getName());
	public static final String DB_CONNECTION = "dbConnection";

	// contains the concatenated text
	String locationContent = "";
	
	boolean insideLocationTag = false;
	
	boolean insideTitleTag = false;
	
	boolean insideArticleTag = false;

    private Connection dbConnection;


     @Override
     public void setup(Map<String, Object> parameter) {
         LOG.trace("IN TERM_WEIGHT Setup");
         this.dbConnection = (Connection) parameter.get(DB_CONNECTION);
         LOG.trace("Out TERM_WEIGHT Setup");
     }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

		LOG.trace("IN startElement");
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
        LOG.trace("OUT startElement");
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
        LOG.trace("IN endElement");
		if (localName.equals("location")) {
			this.setInsideLocationTag(false);
		}

		if (localName.equals("title")) {
			this.setInsideTitleTag(false);
		}

		if (localName.equals("results")) {
			this.setInsideArticleTag(false);
            if (!locationContent.isEmpty()) {
                super.startElement(namespaceURI, "weights", "weights", new AttributesImpl());
                try {
                    addTokenizedContent(namespaceURI, locationContent);
                } catch (NullPointerException e) {
                    LOG.trace("NPE when adding tokenized Content");
                }
                super.endElement(namespaceURI, "weights", "weights");

                locationContent = "";
            }
		}
		
		super.endElement(namespaceURI, localName, qName);
        LOG.trace("OUT startElement");
	}

    @Override
	public void characters(char[] buffer, int start, int length)
			throws SAXException {
        LOG.trace("IN characters");
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
			LOG.trace("changing |" + element + "|(" + element.length() + ") to |" + cleanString(element.replaceAll("\\W+", " ")) + "| ");
			if (element.length() > 2) tokenSet.add(cleanString(element.replaceAll("\\W+", " ")));
		}

		Map<String,String> weightedTerms = getWeightForToken(tokenSet);
		
		for (String key:weightedTerms.keySet()) {
			
			super.startElement(namespace, "weight-term","weight-term",new AttributesImpl());

            super.startElement(namespace, "term","term",new AttributesImpl());
            super.characters(key.toCharArray(), 0, key.length());
            super.endElement(namespace, "term", "term");

            super.startElement(namespace, "weight", "weight", new AttributesImpl());
			super.characters(weightedTerms.get(key).toCharArray(), 0, weightedTerms.get(key).length());
            super.endElement(namespace, "weight", "weight");

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
			
			LOG.trace(sQuery);
			
			Statement stmt;
			ResultSet rs;
			
			try {
				stmt = this.dbConnection.createStatement();
				rs = stmt.executeQuery(sQuery);
                while (rs.next()) {
                    LOG.trace("Got |"+rs.getString("weight")+"| for |"+rs.getString("term")+"|");
						termWithWeight.put(rs.getString("term"),rs.getString("weight"));
                }

            } catch (SQLException e) {
				LOG.trace("error getting statement:"+e.getMessage());
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
