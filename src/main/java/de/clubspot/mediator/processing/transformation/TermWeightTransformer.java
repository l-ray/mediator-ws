package de.clubspot.mediator.processing.transformation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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

	public static final String PARAM_PARENT_ELEMENT = "parentElement";

	public static final String PARAM_INDEX_ELEMENTS = "elementsToIndex";

	Set<String> indexingElements = new HashSet<>();
	Map<String,Boolean> insideIndexingElement = null;
	String parentElement = "results";

	final String weightTermElement = "weight-term";
	final String termElement = "term";
	final String weightElement = "weight";
	final String weightsElement = "weights";

	// contains the concatenated text
	String locationContent = "";
	
	boolean insideParentTag = false;

    private Connection dbConnection;

	@Override
     public void setup(Map<String, Object> parameter) {
         LOG.trace("IN TERM_WEIGHT Setup");
 		 if (insideIndexingElement == null) {
			insideIndexingElement =new HashMap<>();
		 }
         this.dbConnection = (Connection) parameter.get(DB_CONNECTION);
         LOG.trace("Out TERM_WEIGHT Setup");
     }

	@Override
	public void setConfiguration(Map<String, ? extends Object> configuration) {

		if (insideIndexingElement == null) {
			insideIndexingElement =new HashMap<>();
		} else {
			insideIndexingElement.clear();
		}

		if (configuration.containsKey(PARAM_PARENT_ELEMENT)) {
			this.parentElement = (String) configuration.get(PARAM_PARENT_ELEMENT);
		}

		if (configuration.containsKey(PARAM_INDEX_ELEMENTS)) {
			this.indexingElements = new HashSet<>();
			this.indexingElements.addAll(
					getElementsAsList((String)configuration.get(PARAM_INDEX_ELEMENTS))
			);
		}

		for (String element:indexingElements) {
			insideIndexingElement.put(element,false);
		}

	}

	private List<String> getElementsAsList(String configuration) {
		return Arrays.asList(configuration.split(","));
	}

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

		LOG.trace("IN startElement");
		// start a root element paragraph
		super.startElement(namespaceURI, localName, qName, attributes);

		if (indexingElements.contains(localName)) {
			this.insideIndexingElement.put(localName, true);
		}

		if (localName.equals(parentElement)) {
			this.setInsideParentTag(true);
		}
        LOG.trace("OUT startElement");
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
        LOG.trace("IN endElement");
		if (indexingElements.contains(localName)) {
			this.insideIndexingElement.put(localName,false);
		}

		if (localName.equals(parentElement)) {
			this.setInsideParentTag(false);
            if (!locationContent.isEmpty()) {

				super.startElement(namespaceURI, weightsElement, weightsElement, new AttributesImpl());
                try {
                    addTokenizedContent(namespaceURI, locationContent);
                } catch (NullPointerException e) {
                    LOG.trace("NPE when adding tokenized Content");
                }
                super.endElement(namespaceURI, weightsElement, weightsElement);

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

		if (this.insideIndexingElement.containsValue(Boolean.TRUE)) {
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

			super.startElement(namespace, weightTermElement, weightTermElement,new AttributesImpl());

			super.startElement(namespace, termElement, termElement,new AttributesImpl());
            super.characters(key.toCharArray(), 0, key.length());
            super.endElement(namespace, termElement, termElement);

			super.startElement(namespace, weightElement, weightElement, new AttributesImpl());
			super.characters(weightedTerms.get(key).toCharArray(), 0, weightedTerms.get(key).length());
            super.endElement(namespace, weightElement, weightElement);

            super.endElement(namespace, weightTermElement, weightTermElement);
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
		Map<String,String> termWithWeight = new HashMap<>();
		
		if (termIterator.hasNext()) {
			StringBuilder whereInList = new StringBuilder("'"+termIterator.next()+"'");
			
			while (termIterator.hasNext()) {
				String nextTerm = termIterator.next();
				whereInList.append(",'").append(nextTerm).append("'");
			}
			
			String sQuery = "SELECT * FROM term_weight WHERE term IN ("+whereInList+");";
			
			LOG.trace(sQuery);
			
			Statement stmt;
			ResultSet rs;
			
			try {
				stmt = this.dbConnection.createStatement();
				rs = stmt.executeQuery(sQuery);
                while (rs.next()) {
                    String key = rs.getString("term");
					String value = rs.getString("weight");
					LOG.trace("Got |"+value+"| for |"+key+"|");

					termWithWeight.put(key,value);
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

	public void setInsideParentTag(boolean insideParentTag) {
		this.insideParentTag = insideParentTag;
	}

}