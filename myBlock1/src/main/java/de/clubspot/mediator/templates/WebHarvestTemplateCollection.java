package de.clubspot.mediator.templates;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WebHarvestTemplateCollection {

	List<SourceTemplate> templates;
	Connection connection;
	
	public WebHarvestTemplateCollection(Connection connection) {
		 templates = new ArrayList<SourceTemplate>();
		 this.connection = connection;
	}
	
	public void collectTemplateList() {
		
		Statement stmt;
		try {
			stmt = connection.createStatement();
	 
		    ResultSet rs = stmt.executeQuery( "SELECT uid, name, url, starturl, pattern, icon, dateformat, rgt, lft FROM tx_lrmediator_pattern" ); 
		 
	    	while ( rs.next() ) { 
		        System.out.printf( "%s, %s %n", rs.getString(1), rs.getString(2) ); 

		        WebHarvestTemplate tmpTemplate = new WebHarvestTemplate(this.connection);
		        tmpTemplate.setId(rs.getString("uid"));
		        tmpTemplate.setName(rs.getString("name"));
		        tmpTemplate.setUrl(rs.getString("url"));
		        tmpTemplate.setStartUrl(rs.getString("starturl"));
		        tmpTemplate.setPattern(rs.getString("pattern"));
		        tmpTemplate.setIcon(rs.getString("icon"));
		        tmpTemplate.setDateFormat(rs.getString("dateformat"));
		        tmpTemplate.setRgt(rs.getInt("rgt"));
		        tmpTemplate.setLft(rs.getInt("lft"));
		        
		        templates.add(tmpTemplate);
	    	}
	    	
		    rs.close(); 
		    stmt.close(); 
		    
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		/*InputSource inputSource = new InputSource(new StringReader(word));
	
		ScraperConfiguration config = new ScraperConfiguration(inputSource);*/
	}

	public Iterator<SourceTemplate> iterator() {
		if (templates == null || templates.size() < 1) {
			this.collectTemplateList();
		}
		return templates.iterator();
	}
	
	
}
