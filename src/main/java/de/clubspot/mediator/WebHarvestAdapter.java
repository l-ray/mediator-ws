package de.clubspot.mediator;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.InputSource;

import de.clubspot.mediator.criteria.UrlDateWrapper;
import de.clubspot.mediator.templates.SourceTemplate;
import de.clubspot.mediator.templates.WebHarvestTemplateCollection;

public class WebHarvestAdapter implements SourceAdapter{

	private static String _PROXY_HOST ;
	
	private static int _PROXY_PORT;
	
	private URL sourceURL;
	
	private String result;
	
	private SourceTemplate selectedTemplate;
	
	private Date startDate;
	
	private Date endDate; 
	
	private WebHarvestTemplateCollection templateCollection;
	
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
        
       // _PROXY_HOST = DataBaseConnectionSingleton.getProperties().getProperty("proxy_host","");
       // _PROXY_PORT = Integer.parseInt(DataBaseConnectionSingleton.getProperties().getProperty("proxy_port","0"));
        
        templateCollection = new WebHarvestTemplateCollection(null);
        selectedTemplate = null;
        
        this.setStartDate(new Date());
        this.setEndDate(new Date());
    }
	
	/*public InputSource getInputStream() {
		
		UrlDateWrapper urlWrapper = new UrlDateWrapper(this.getStartDate());
		System.out.println(selectedTemplate.getDateFormat());
		SimpleDateFormat df = new SimpleDateFormat( selectedTemplate.getDateFormat() );
				
		StringBuffer pattern = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
			.append("<config charset=\"UTF-8\">\n")
			.append("<var-def name=\"baseUrl\"><![CDATA["+selectedTemplate.getUrl()+"]]></var-def>\n")
			.append("<var-def name=\"startUrl\"><![CDATA["+urlWrapper.getUrl(selectedTemplate.getStartUrl())+"]]></var-def>\n")
			.append("<var-def name=\"startDate\">"+df.format(getStartDate())+"</var-def>\n")
			.append("<var-def name=\"endDate\">"+df.format(getEndDate())+"</var-def>\n")
			.append("<var-def name=\"result\">\n")
			.append(selectedTemplate.getPattern())
			.append("</var-def>")
			.append("</config>");

		
		System.out.println(pattern.toString());
		
		return new  InputSource(new StringReader(pattern.toString()));
	}*/
    
//    public boolean execute() {
//
//			//ScraperConfiguration config = new ScraperConfiguration("c:/temp/scrapertest/configs/test2.xml");
//			//ScraperConfiguration config = new ScraperConfiguration("location-examples/banqxquery.xml");
//			ScraperConfiguration config = new ScraperConfiguration(getInputStream());
//			//ScraperConfiguration config = new ScraperConfiguration(this.getSource());
//			//ScraperConfiguration config = new ScraperConfiguration( new URL("http://localhost/scripts/test/sample8.xml") );
//			Scraper scraper = new Scraper(config, "results");
//			scraper.setDebug(true);
//			if (_PROXY_HOST != "")
//				scraper.getHttpClientManager().setHttpProxy(_PROXY_HOST, _PROXY_PORT);
//			
//			long startTime = System.currentTimeMillis();
//			scraper.execute();
//			
//			this.setResult("<results><source>"+
//					       "<link>"+selectedTemplate.getUrl()+"</link>"+
//					       "<name>"+selectedTemplate.getName()+"</name>"+
//					       "<icon>"+selectedTemplate.getIcon()+"</icon></source>"
//					       +(scraper.getContext().getVar("result")).toString()+
//					       "</results>");
//			
//			System.out.println("time elapsed: "
//					+ (System.currentTimeMillis() - startTime));
//		
//		return true;
//    }

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
	
//	public Iterator getTemplateListIterator() {
//		return templateCollection.iterator();
//	}
//	
//	public SelectItem[] getTemplateListSelectItem() {
//		Iterator<WebHarvestTemplate> tmpIterator = getTemplateListIterator();
//		
//		List<SelectItem> tmpSelectedItemList = new ArrayList<SelectItem>();
//		
//		while (tmpIterator.hasNext()) {
//			WebHarvestTemplate tmpItem = tmpIterator.next();
//			SelectItem tmpSelectItem = new SelectItem(tmpItem.getId(), tmpItem.getName());
//			tmpSelectedItemList.add(tmpSelectItem);
//		}
//		return tmpSelectedItemList.toArray((SelectItem[]) new SelectItem[tmpSelectedItemList.size()]);
//	}
	
	@SuppressWarnings("unused")
	private URL getSource() {
		return this.sourceURL;
	}

//	public String doAction() {
//		System.out.println("in DoAction");
//		if (this.execute())
//			return "success";
//		else
//			return "failure";
//	}

	public SourceTemplate getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(SourceTemplate selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}
	
	public String getSelectedTemplateId() {
		if (this.selectedTemplate != null)
			return this.selectedTemplate.getId();
		else
			return null;
	}

	public void setSelectedTemplateId(String sId) {
		for (Iterator<SourceTemplate> iter = templateCollection.iterator();iter.hasNext();) {
			SourceTemplate tmpTemplate = iter.next();
			if (tmpTemplate.getId().equals(sId)) {
				System.out.println("found matching sID");
				this.setSelectedTemplate(tmpTemplate);
				return;
			}
		}
	}
	
//	public String getResultThroughGetParameter() {
//		this.setSelectedTemplateId((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("patternId"));
//		this.parseStartDate((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("startDate"));
//		this.parseEndDate((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("endDate"));
//		this.doAction();
//		return this.getResult();
//	}

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