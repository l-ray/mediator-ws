package de.clubspot.database; /**
 * Created by lray on 16.06.15.
 */

import de.clubspot.util.DBConnectionManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

@WebListener()
public class DatabaseConnectionListener implements ServletContextListener {

    public static final String DB_CONNECTION_ATTRIBUTE = "DBConnection";

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();

        try {
            URI uri = new URI(System.getenv("DATABASE_URL"));

            //initialize DB Connection
            String dbURL = "jdbc:postgresql://" + uri.getHost() + ':' + uri.getPort() + uri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            String user = uri.getUserInfo().split(":")[0];
            String pwd = uri.getUserInfo().split(":")[1];

            System.out.println("dburl:"+dbURL);
            System.out.println("user:"+user);
            System.out.println("pw:"+pwd);

            DBConnectionManager connectionManager = new DBConnectionManager(dbURL, user, pwd);
            ctx.setAttribute(DB_CONNECTION_ATTRIBUTE, connectionManager.getConnection());
            System.out.println("DB Connection initialized successfully.");
        } catch (ClassNotFoundException | SQLException | URISyntaxException e) {
            e.printStackTrace();
        }

        //initialize log4j
        /*
        String log4jConfig = ctx.getInitParameter("log4j-config");
        if(log4jConfig == null){
            System.err.println("No log4j-config init param, initializing log4j with BasicConfigurator");
            BasicConfigurator.configure();
        } else {
            String webAppPath = ctx.getRealPath("/");
            String log4jProp = webAppPath + log4jConfig;
            File log4jConfigFile = new File(log4jProp);
            if (log4jConfigFile.exists()) {
                System.out.println("Initializing log4j with: " + log4jProp);
                DOMConfigurator.configure(log4jProp);
            } else {
                System.err.println(log4jProp + " file not found, initializing log4j with BasicConfigurator");
                BasicConfigurator.configure();
            }
        }*/
        System.out.println("log4j configured properly");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Connection con = (Connection) servletContextEvent.getServletContext().getAttribute("DBConnection");
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
