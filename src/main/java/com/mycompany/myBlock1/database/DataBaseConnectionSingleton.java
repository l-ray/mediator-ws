package com.mycompany.myBlock1.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Deprecated
public final class DataBaseConnectionSingleton {

	private static Connection instance = null;
	
	private static Properties properties = null;

	private final static String PROPERTIES_FILE = "/Users/lray/Documents/workspace/webharvest/src/connections.properties";
//	private final static String PROPERTIES_FILE = "d:/work/webharvest/src/connections.properties";
	
	
	private DataBaseConnectionSingleton() {
	}

	public synchronized static Connection getInstance() {
		if (instance == null) {

			try 
		    { 
		      Class.forName( "com.mysql.jdbc.Driver" ); 
		    } 
		    catch ( ClassNotFoundException e ) 
		    { 
		      System.err.println( "Keine Treiber-Klasse!" ); 
		    } 
		 
		    String sHost 	= getProperties().getProperty("db_host");
		    String sDatabase= getProperties().getProperty("db_database");
		    String sLogin	= getProperties().getProperty("db_login");
		    String sPasswd	= getProperties().getProperty("db_passwd","");
		    String sPort 	= getProperties().getProperty("db_port","3306");
		    
		    try 
		    { 
		      instance = DriverManager.getConnection( "jdbc:mysql://"+sHost+":"+sPort+"/"+sDatabase+"", sLogin, sPasswd ); 
		      
		    } 
		    catch ( SQLException e ) 
		    { 
		        System.err.println("jdbc:mysql://"+sHost+":"+sPort+"/"+sDatabase+" with Login:"+sLogin+" and PW:"+sPasswd);
		    	e.printStackTrace(); 
		    } 
		    
			
		}
		return instance;
	}

	public static Properties getProperties() {
		
		// Read properties file.
	    if (properties == null) {
			properties = new Properties();
		    try {
		        properties.load(new FileInputStream(PROPERTIES_FILE));
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
	    }
	    return properties;
	}
	
	protected void finalize() {
		      if ( instance != null ) 
		        try {
		        	instance.close(); 
	        	} catch ( SQLException e ) { e.printStackTrace(); } 
	}
}
