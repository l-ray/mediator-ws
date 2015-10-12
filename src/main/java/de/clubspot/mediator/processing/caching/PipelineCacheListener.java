package de.clubspot.mediator.processing.caching;

import de.clubspot.database.DatabaseConnectionListener;
import org.apache.cocoon.pipeline.caching.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;

public class PipelineCacheListener implements ServletContextListener {

    private static final Logger LOG =
            LoggerFactory.getLogger(PipelineCacheListener.class.getName());

    public static final String PIPELINE_CACHE_ATTRIBUTE = "pipelineCache";

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        Connection dbConnection = (Connection) ctx.getAttribute(
                DatabaseConnectionListener.DB_CONNECTION_ATTRIBUTE
        );

        Cache cache= new SimplePostgreCache(dbConnection);
        LOG.debug("Setting Pipeline cache to "+cache.toString());
        ctx.setAttribute(PIPELINE_CACHE_ATTRIBUTE, cache);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        Cache cache = (Cache) sce.getServletContext().getAttribute(PIPELINE_CACHE_ATTRIBUTE);
        LOG.debug("Clearing Pipeline cache "+cache.toString());
        cache.clear();
    }

}
