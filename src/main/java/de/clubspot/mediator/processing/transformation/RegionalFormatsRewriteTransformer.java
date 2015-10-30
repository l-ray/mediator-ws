package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.ExpiresCacheKey;
import org.apache.cocoon.pipeline.caching.ParameterCacheKey;
import org.apache.cocoon.pipeline.component.CachingPipelineComponent;
import org.apache.cocoon.sax.AbstractSAXTransformer;
import org.apache.cocoon.sax.SAXConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RegionalFormatsRewriteTransformer extends AbstractSAXTransformer implements SAXConsumer, CachingPipelineComponent{

    private static final Logger LOG =
            LoggerFactory.getLogger(RegionalFormatsRewriteTransformer.class.getName());

    public static final String DATE_OUTPUT_FORMAT = "yyyy-MM-dd";
    public static final String[] DEFAULT_INPUT_FORMAT = {"EEEE, dd.MMMM yyyy","EEEE, dd. MMMM","dd.MM.yy","EEEE, dd MMMM","EEEE, dd MMMM \n\nh:mm a"};
    public static final String PARAM_DATE_PATTERN = "date-pattern";
    public static final Locale[] SUPPORTED_LOCALES = {Locale.GERMAN, Locale.ENGLISH};
    public static final String PARAM_CACHE_ID = "cache-id";

    //SimpleDateFormat inputFormat = null;
    SimpleDateFormat outputFormat = null;


    private boolean inStartDate = false;

    public String startDateElement = "start";

    private String cacheId;

     @Override
     public void setup(Map<String, Object> parameter) {
         outputFormat = new SimpleDateFormat(DATE_OUTPUT_FORMAT,Locale.GERMAN);
     }

    @Override
    public void setConfiguration(Map<String, ? extends Object> configuration) {
        if (configuration.containsKey(PARAM_DATE_PATTERN)) {
            outputFormat = new SimpleDateFormat(
                    (String) configuration.get(PARAM_DATE_PATTERN)
            );
        }

        if (configuration.get(PARAM_CACHE_ID) != null) {
            cacheId = (String) configuration.get(PARAM_CACHE_ID);
        }
    }

	@Override
    public void startElement(String namespaceURI, String localName,
			String qName, Attributes attributes) throws SAXException {

        if (localName.equals(startDateElement)) {
            inStartDate = true;
        }

        super.startElement(namespaceURI, localName, qName, attributes);
	}

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {

        if (localName.equals(startDateElement)) {
            inStartDate = false;
        }
		super.endElement(namespaceURI, localName, qName);
	}

    @Override
	public void characters(char[] buffer, int start, int length)
            throws SAXException {
        // remove whitespaces
        String newTerm = new String(buffer,start, length).trim();

        if (inStartDate) {
            newTerm = normalizedDate(newTerm);
        }

        super.characters(newTerm.toCharArray(), 0, newTerm.length());
	}

    private String normalizedDate(String unnormalizedDate) {
        SimpleDateFormat inputFormat;
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_OUTPUT_FORMAT,Locale.GERMAN);
        Date parsedDate = null;

        for (Locale locale : SUPPORTED_LOCALES){
            for (String inputPattern : DEFAULT_INPUT_FORMAT) {
                try {
                    inputFormat = new SimpleDateFormat(inputPattern, locale);
                    parsedDate = inputFormat.parse(unnormalizedDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(parsedDate);
                    if (cal.get(Calendar.YEAR) == 1970) {
                        cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                        parsedDate = cal.getTime();
                    }
                } catch (ParseException e) {
                    // bad idea, never expect something with an try-catch-block
                    // e.printStackTrace();
                }
                if (parsedDate != null) {
                    break;
                }
            }
        }

        return parsedDate != null ? outputFormat.format(parsedDate):unnormalizedDate;
    }

    @Override
    public CacheKey constructCacheKey() {

        return new ExpiresCacheKey(
                new ParameterCacheKey(
                        new HashMap<String, String>() {
                            { put(PARAM_CACHE_ID,cacheId); }
                        }
                ),
                "3600"
        );
    }

}
