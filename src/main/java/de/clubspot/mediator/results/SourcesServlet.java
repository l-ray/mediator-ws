package de.clubspot.mediator.results;

import de.clubspot.database.DatabaseConnectionListener;
import de.clubspot.mediator.processing.generation.SourceInfoGenerator;
import org.apache.cocoon.optional.pipeline.components.sax.json.JsonSerializer;
import org.apache.cocoon.pipeline.CachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.pipeline.caching.SimpleCache;
import org.apache.cocoon.sax.SAXPipelineComponent;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(value = "/sources/*", name = "SourcesServlet")
public class SourcesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] parameter = new String[0];
        if (request.getPathInfo() != null) {
            parameter = request.getPathInfo().split("/");
        }
        if (parameter.length > 1) {
            final String sourceId = parameter[1];
            try {
                SourceInfoGenerator generator = new SourceInfoGenerator();
                generator.setConfiguration(new HashMap<String, Object>() {{
                    put(SourceInfoGenerator.PARAM_PATTERN_ID, sourceId);
                }});

                JsonSerializer serializer = new JsonSerializer();
                serializer.setConfiguration(new HashMap<String, Object>() {{
                    put(JsonSerializer.DROP_ROOT_ELEMENT, "false");
                    put(JsonSerializer.PATTERN_ID_ELEMENT, sourceId);
                }});

                CachingPipeline<SAXPipelineComponent> pipeline = new CachingPipeline<>();
                pipeline.setCache(new SimpleCache());

                pipeline.addComponent(generator);
                pipeline.addComponent(serializer);

                pipeline.setup(response.getOutputStream(), new HashMap<String, Object>() {
                    {
                        put(SourceInfoGenerator.DB_CONNECTION, getServletContext().getAttribute("DBConnection"));
                    }
                });

                pipeline.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JsonSerializer serializer = new JsonSerializer();
                serializer.setConfiguration(new HashMap<String, Object>() {{
                    put(JsonSerializer.DROP_ROOT_ELEMENT, "true");
                }});

                CachingPipeline<SAXPipelineComponent> pipeline = new CachingPipeline<>();
                pipeline.setCache(new SimpleCache());

                pipeline.addComponent(new SourceInfoGenerator());
                pipeline.addComponent(serializer);

                pipeline.setup(response.getOutputStream(), new HashMap<String, Object>() {
                    {
                        put(SourceInfoGenerator.DB_CONNECTION, getServletContext().getAttribute(DatabaseConnectionListener.DB_CONNECTION_ATTRIBUTE));
                    }
                });

                pipeline.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}