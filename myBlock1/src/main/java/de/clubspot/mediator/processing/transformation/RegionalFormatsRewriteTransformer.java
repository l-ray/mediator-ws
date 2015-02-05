package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class RegionalFormatsRewriteTransformer extends AbstractSAXTransformer implements SAXConsumer{

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformer.class.getName());
    public static final String DATE_OUTPUT_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_INPUT_FORMAT = "EEEE, dd.MMMM yyyy";
    public static final String PARAM_DATE_PATTERN = "date-pattern";

    SimpleDateFormat inputFormat = null;
    SimpleDateFormat outputFormat = null;


    private boolean inStartDate = false;

    public String startDateElement = "start";

     @Override
     public void setup(Map<String, Object> parameter) {
         LOG.trace("IN RegionalFormatsTransformer Setup");
         inputFormat = new SimpleDateFormat(DEFAULT_INPUT_FORMAT, Locale.GERMAN);
         outputFormat = new SimpleDateFormat(DATE_OUTPUT_FORMAT,Locale.GERMAN);
         LOG.trace("Out RegionalFormatsTransformer Setup");
     }

    @Override
    public void setConfiguration(Map<String, ? extends Object> configuration) {
        if (configuration.containsKey(PARAM_DATE_PATTERN)) {
            outputFormat = new SimpleDateFormat(
                    (String) configuration.get(PARAM_DATE_PATTERN)
            );
        }
    }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        LOG.trace("IN startElement");

        if (localName.equals(startDateElement)) {
            inStartDate = true;
        }

        super.startElement(namespaceURI, localName, qName, attributes);
        LOG.trace("OUT startElement");
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        LOG.trace("IN endElement");

        if (localName.equals(startDateElement)) {
            inStartDate = false;
        }
		super.endElement(namespaceURI, localName, qName);
        LOG.trace("OUT startElement");
	}

    @Override
	public void characters(char[] buffer, int start, int length)
            throws SAXException {
        LOG.trace("IN characters");
        // remove whitespaces
        String newTerm = new String(buffer,start, length).trim();

        if (inStartDate) {
            newTerm = normalizedDate(newTerm);
        }

        super.characters(newTerm.toCharArray(), 0, newTerm.length());
        LOG.trace("OUT characters");
	}

    private String normalizedDate(String unnormalizedDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(DEFAULT_INPUT_FORMAT, Locale.GERMAN);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_OUTPUT_FORMAT,Locale.GERMAN);
        Date parsedDate = null;
        try {
            parsedDate = inputFormat.parse(unnormalizedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parsedDate != null ? outputFormat.format(parsedDate):unnormalizedDate;
    }

}
