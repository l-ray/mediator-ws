package com.mycompany.myBlock1.generation;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.databases.bridge.spring.avalon.SpringToAvalonDataSourceBridge;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.excalibur.source.SourceValidity;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import de.clubspot.mediator.criteria.UrlDateWrapper;
import de.clubspot.mediator.templates.WebHarvestTemplate;

public class PatternCodeGenerator extends ServiceableGenerator implements
CacheableProcessingComponent, Disposable {

	private DataSourceComponent datasource;
	
	private String patternId = null;
	
	private Date startDate = null;
	
	private Date endDate = null;
	
//	public void compose(ComponentManager manager) throws ComponentException {
//	}
	
	public void dispose() {
		super.dispose();
	}

	public void recycle() {
		myAttr.clear();
		super.recycle();
	}

	public void setup(SourceResolver resolver, Map objectModel, String src,
			Parameters par) throws ProcessingException, IOException, SAXException {
		
		try {
			patternId = par.getParameter("patternId");
			startDate = parseStartDate(par.getParameter("startDate"));
			if (par.getParameter("endDate") != null && par.getParameter("endDate") != "")
			endDate   = parseEndDate(par.getParameter("endDate"));
		} catch ( ParameterException e) {
			//e.printStackTrace();
		}
		
	}

	 public void compose(ComponentManager manager) throws ComponentException {

         ComponentSelector dbselector =

(ComponentSelector) manager.lookup(DataSourceComponent.ROLE + "Selector");

datasource = (DataSourceComponent) dbselector.select("oracle_db"); //name as defined in cocoon.xconf

try {

                         Connection conn = datasource.getConnection();

         } catch (SQLException e) {

                         e.printStackTrace();

         }

}
	
	public void service(ServiceManager manager) throws ServiceException {
		super.service(manager);
		
		SpringToAvalonDataSourceBridge selector = (SpringToAvalonDataSourceBridge) manager
				.lookup(DataSourceComponent.ROLE + "Selector");

			this.datasource = (DataSourceComponent) selector
					.select("myBlockDatabase");
	}

	public void generate() throws SAXException, ProcessingException {
		
		Connection myConnection = null;
		try {

			myConnection = this.datasource.getConnection();
			
			WebHarvestTemplate template = new WebHarvestTemplate(patternId, myConnection);
	      
			UrlDateWrapper urlWrapper = new UrlDateWrapper(startDate);
			System.out.println("DateFormat"+template.getDateFormat());
			SimpleDateFormat df = new SimpleDateFormat( template.getDateFormat(), new java.util.Locale("de","DE") );

	      
	      StringBuffer message = new StringBuffer("<config charset=\"UTF-8\">")
	      .append("\n<var-def name=\"baseUrl\"><![CDATA["+urlWrapper.getUrl(template.getUrl())+"]]></var-def>")
	      .append("\n<var-def name=\"startUrl\"><![CDATA["+urlWrapper.getUrl(template.getStartUrl())+"]]></var-def>")
	      .append("\n<var-def name=\"startDate\">"+df.format(this.startDate)+"</var-def>")
	      .append((this.endDate!=null)?"\n<var-def name=\"endDate\">"+df.format(this.endDate)+"</var-def>":"")
	      .append(template.getCompiledPattern())
	      .append("\n</config>");
	      
	      System.out.println(message.toString());
  	      
  	      XMLReader xmlreader = XMLReaderFactory.createXMLReader();
	      xmlreader.setContentHandler(super.xmlConsumer);
	      InputSource source = new InputSource(new StringReader(message.toString()));
	      xmlreader.parse(source);
	      
	      contentHandler = xmlreader.getContentHandler();

		} catch (SQLException e) {
			throw new ProcessingException(e);
		}
		catch (IOException e) {
			throw new ProcessingException(e);
		} 
		finally
		{
			try {
				myConnection.close();				
			} catch (Exception e) {}
		}
	}

	public Serializable getKey() {
		// Default non-caching behaviour. We could implement this later.
		return null;
	}

	public SourceValidity getValidity() {
		// Default non-caching behaviour. We could implement this later.
		return null;
	}



	private AttributesImpl myAttr = new AttributesImpl();

	private String EMPLOYEE_QUERY = "SELECT name, uid as id, pattern "
			+ "FROM tx_lrmediator_pattern ";

//	private void endDept() throws SAXException {
//		contentHandler.endElement("", "dept", "dept");
//	}
//
//	private void newDept(ResultSet res, String dept, boolean isFirstRow)
//			throws SAXException {
//		if (!isFirstRow) {
//			endDept();
//		}
//		myAttr.clear();
//		myAttr.addAttribute("", "name", "name", "", dept);
//		contentHandler.startElement("", "dept", "dept", myAttr);
//	}
//
//	private void addEmployee(ResultSet res, String id, String name)
//			throws SAXException {
//		myAttr.clear();
//		myAttr.addAttribute("", "id", "id", "", id);
//		contentHandler.startElement("", "employee", "employee", myAttr);
//		contentHandler.characters(name.toCharArray(), 0, name.length());
//		contentHandler.endElement("", "employee", "employee");
//	}
//
//	private String attrFromDB(ResultSet res, String column) throws SQLException {
//		String value = res.getString(column);
//		return (res.wasNull()) ? "" : value;
//	}

	public Date parseStartDate(String startDate) {
		try {
			SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
			df.setTimeZone( TimeZone.getDefault() ); 
			return df.parse(startDate);
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
	public Date parseEndDate(String endDate) {
		try {
			SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
			df.setTimeZone( TimeZone.getDefault() ); 
			return df.parse(endDate);
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}
	}
	
}
