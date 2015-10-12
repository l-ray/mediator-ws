package de.clubspot.database;

import de.clubspot.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionListener implements ServletContextListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(DatabaseConnectionListener.class.getName());

    public static final String DB_CONNECTION_ATTRIBUTE = "DBConnection";

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();

        try {
            URI uri = new URI(System.getenv("DATABASE_URL"));

            //initialize DB Connection
            String dbURL = "jdbc:postgresql://" + uri.getHost() + ':' + uri.getPort() + uri.getPath() + "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            String user = uri.getUserInfo().split(":")[0];
            String pwd = uri.getUserInfo().split(":")[1];

            LOG.trace("dburl:" + dbURL);
            LOG.trace("user:" + user);
            LOG.trace("pw:" + pwd);

            DBConnectionManager connectionManager = new DBConnectionManager(dbURL, user, pwd);
            ctx.setAttribute(DB_CONNECTION_ATTRIBUTE, connectionManager.getConnection());
            LOG.info("DB Connection initialized successfully.");
        } catch (ClassNotFoundException | SQLException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Connection con = (Connection) servletContextEvent.getServletContext().getAttribute(DB_CONNECTION_ATTRIBUTE);
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
