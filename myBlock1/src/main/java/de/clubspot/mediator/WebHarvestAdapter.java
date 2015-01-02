package de.clubspot.mediator;

import org.apache.log4j.PropertyConfigurator;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public class WebHarvestAdapter implements SourceAdapter{

	private URL sourceURL;
	
	private String result;
	
	private Date startDate;
	
	private Date endDate; 
	
    /**
     *  Constructor-Method
     */
    public WebHarvestAdapter() {
    	
    	Properties props = new Properties();
    	props.setProperty("log4j.rootLogger", "INFO, stdout");
    	props.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
    	props.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
    	props.setProperty("log4j.appender.stdout.layout.ConversionPattern", "%-5p (%20F:%-3L) - %m\n");
        PropertyConfigurator.configure(props);
        
        this.setStartDate(new Date());
        this.setEndDate(new Date());
    }
	
	public String getResult() {
		return this.result;
	}

	private void setResult(String result) {
		this.result = result;
	}
	
	public void setSource(URL sourceURL) {
		this.sourceURL = sourceURL;
	}

	public void setSourceString(String source) 
		throws MalformedURLException {
		this.sourceURL = new URL(source);
	}
	
	public String getSourceString() {
		try {
		return this.sourceURL.toString();
		} catch (NullPointerException e) {
			return "please insert";
		}
	}
	

	@SuppressWarnings("unused")
	private URL getSource() {
		return this.sourceURL;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void parseStartDate(String startDate) {
		try {
			SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
			df.setTimeZone( TimeZone.getDefault() ); 
			this.setStartDate(df.parse(startDate));
		} catch (ParseException e) {
			//e.printStackTrace();
		}
	}
	
	public void parseEndDate(String endDate) {
		try {
			SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
			df.setTimeZone( TimeZone.getDefault() ); 
			this.setEndDate(df.parse(endDate));
		} catch (ParseException e) {
			//e.printStackTrace();
		}
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}