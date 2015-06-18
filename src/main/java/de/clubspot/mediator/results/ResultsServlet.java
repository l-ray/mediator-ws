package de.clubspot.mediator.results;

import de.clubspot.mediator.processing.generation.PatternCodeGenerator;
import de.clubspot.mediator.processing.generation.SourceInfoGenerator;
import de.clubspot.mediator.processing.transformation.AddConnectionIdToElementsTransformer;
import de.clubspot.mediator.processing.transformation.ExtractElementsTransformer;
import de.clubspot.mediator.processing.transformation.RegexRewriteTransformer;
import de.clubspot.mediator.processing.transformation.RegionalFormatsRewriteTransformer;
import org.apache.cocoon.optional.pipeline.components.sax.json.JsonSerializer;
import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.sax.AbstractSAXProducer;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

@WebServlet(value="/results/*",name="ResultsServlet")
public class ResultsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] parameter = request.getPathInfo().split("/");
        final String patternId = parameter[1];
        final String startDate = parameter[2];

        System.out.println(patternId+" - "+startDate);

        try {

            try {
                AbstractSAXProducer generator = new PatternCodeGenerator();
                generator.setConfiguration(new HashMap<String, Object>() {{
                    put(PatternCodeGenerator.PARAM_PATTERN_ID, patternId);
                    put(PatternCodeGenerator.PARAM_START_DATE, startDate);
                    put(PatternCodeGenerator.PARAM_END_DATE, "2015-01-30");
                }});

                JsonSerializer serializer = new JsonSerializer();
                serializer.setConfiguration(new HashMap<String, Object>() {{
                    put(JsonSerializer.DROP_ROOT_ELEMENT, "false");
                    put(JsonSerializer.PATTERN_ID_ELEMENT, patternId);
                    put(JsonSerializer.START_DATE_ELEMENT, startDate);
                }});

                Pipeline<SAXPipelineComponent> pipeline = new NonCachingPipeline<>();

                pipeline.addComponent(generator);
                pipeline.addComponent(new RegionalFormatsRewriteTransformer());

                SAXPipelineComponent regexRewrite = new RegexRewriteTransformer();
                regexRewrite.setConfiguration(new HashMap<String,Object>(){{
                    put("onElements", "pictures");
                }});

                pipeline.addComponent(regexRewrite);

                SAXPipelineComponent connectionId = new AddConnectionIdToElementsTransformer();
                regexRewrite.setConfiguration(new HashMap<String,Object>(){{
                    put("elementLocalName", "results");
                    put("idElementLocalName", "connection");
                    put("id", patternId+"-"+startDate);

                }});

                pipeline.addComponent(connectionId);

                SAXPipelineComponent extractElement = new ExtractElementsTransformer();
                regexRewrite.setConfiguration(new HashMap<String,Object>(){{
                    put("elementToBeExtracted", "pictures");
                    put("newExtractedElementName", "pictures");
                    put("targetParent", "resultset");
                    put("elementParent", "results");
                    put("elementParentId", "id");
                    put("extractedElementId", "id");
                    put("extractedElementIdPrefix", patternId+"-"+startDate+"-");
                    put("elementParentIdPrefix", patternId+"-"+startDate+"-");

                }});
                pipeline.addComponent(extractElement);

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


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
