package de.clubspot.mediator.results;

import de.clubspot.database.DatabaseConnectionListener;
import de.clubspot.mediator.processing.caching.PipelineCacheListener;
import de.clubspot.mediator.processing.generation.PatternCodeGenerator;
import de.clubspot.mediator.processing.generation.SourceInfoGenerator;
import de.clubspot.mediator.processing.transformation.AddConnectionIdToElementsTransformer;
import de.clubspot.mediator.processing.transformation.ExtractElementsTransformer;
import de.clubspot.mediator.processing.transformation.RegexRewriteTransformer;
import de.clubspot.mediator.processing.transformation.RegionalFormatsRewriteTransformer;
import org.apache.cocoon.optional.pipeline.components.sax.json.JsonSerializer;
import org.apache.cocoon.pipeline.CachingPipeline;
import org.apache.cocoon.pipeline.caching.Cache;
import org.apache.cocoon.sax.AbstractSAXProducer;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

@WebServlet(value = "/api/results/*", name = "ResultsServlet")
public class ResultsServlet extends HttpServlet {

    private static final Logger LOG =
            LoggerFactory.getLogger(ResultsServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] parameter = request.getPathInfo().split("/");
        final String patternId = parameter[1];
        final String startDate = parameter[2];

        final Connection dbConnection = (Connection) getServletContext().getAttribute(DatabaseConnectionListener.DB_CONNECTION_ATTRIBUTE);
        final Cache cache = (Cache) getServletContext().getAttribute(PipelineCacheListener.PIPELINE_CACHE_ATTRIBUTE);

        LOG.debug(patternId + " - " + startDate);

        try {

            try {

                JsonSerializer serializer = doSerializer(patternId, startDate);

                CachingPipeline<SAXPipelineComponent> pipeline = new CachingPipeline<>();

                pipeline.setCache(cache);

                pipeline.addComponent(doGenerator(patternId, startDate));
                pipeline.addComponent(doRegionalFormatsAdjustment(patternId, startDate));
                pipeline.addComponent(doRemoveNewLineFromElementContent(patternId, startDate));
                pipeline.addComponent(doConvertConnectionIdToElement(patternId, startDate));
                pipeline.addComponent(doExtractPicturesFromResultToBaseLevel(patternId, startDate));
                pipeline.addComponent(serializer);
                pipeline.setup(response.getOutputStream(), new HashMap<String, Object>() {
                    {
                        put(SourceInfoGenerator.DB_CONNECTION, dbConnection);
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

    private SAXPipelineComponent doExtractPicturesFromResultToBaseLevel(final String patternId, final String startDate) {
        SAXPipelineComponent extractElement = new ExtractElementsTransformer();
        extractElement.setConfiguration(new HashMap<String, Object>() {{
            put(ExtractElementsTransformer.PARAM_ELEMENT_TO_BE_EXTRACTED, "pictures");
            put(ExtractElementsTransformer.PARAM_NEW_EXTRACTED_ELEMENT_NAME, "pictures");
            put(ExtractElementsTransformer.PARAM_TARGET_PARENT, "resultset");
            put(ExtractElementsTransformer.PARAM_ELEMENT_PARENT, "results");
            put(ExtractElementsTransformer.PARAM_ELEMENT_PARENT_ID, "id");
            put(ExtractElementsTransformer.PARAM_EXTRACTED_ELEMENT_ID, "id");
            put(ExtractElementsTransformer.PARAM_EXTRACTED_ELEMENT_ID_PREFIX, patternId + "-" + startDate + "-");
            put(ExtractElementsTransformer.PARAM_ELEMENT_PARENT_ID_PREFIX, patternId + "-" + startDate + "-");
            put(ExtractElementsTransformer.PARAM_FORCE_NEW_PARENT_ID, Boolean.TRUE);
        }});
        return extractElement;
    }

    private SAXPipelineComponent doConvertConnectionIdToElement(final String patternId, final String startDate) {
        SAXPipelineComponent connectionId = new AddConnectionIdToElementsTransformer();
        connectionId.setConfiguration(new HashMap<String, Object>() {{
            put(AddConnectionIdToElementsTransformer.PARAM_ELEMENT_LOCAL_NAME, "results");
            put(AddConnectionIdToElementsTransformer.PARAM_ID_ELEMENT_LOCAL_NAME, "connection");
            put(AddConnectionIdToElementsTransformer.PARAM_ID, patternId + "-" + startDate);
        }});
        return connectionId;
    }

    private SAXPipelineComponent doRemoveNewLineFromElementContent(final String patternId, final String startDate) {
        SAXPipelineComponent regexRewrite = new RegexRewriteTransformer();
        regexRewrite.setConfiguration(new HashMap<String, Object>() {{
            put(RegexRewriteTransformer.PARAM_ELEMENT_LIST, "pictures");
            put(RegexRewriteTransformer.PARAM_CACHE_ID, patternId + "-" + startDate);
        }});
        return regexRewrite;
    }

    private SAXPipelineComponent doRegionalFormatsAdjustment(final String patternId, final String startDate) {
        SAXPipelineComponent regionalFormatsRewrite = new RegionalFormatsRewriteTransformer();
        regionalFormatsRewrite.setConfiguration(new HashMap<String, Object>() {{
            put(RegexRewriteTransformer.PARAM_CACHE_ID, patternId + "-" + startDate);
        }});
        return regionalFormatsRewrite;
    }

    private JsonSerializer doSerializer(final String patternId, final String startDate) {
        JsonSerializer serializer = new JsonSerializer();
        serializer.setConfiguration(new HashMap<String, Object>() {{
            put(JsonSerializer.DROP_ROOT_ELEMENT, "true");
            put(JsonSerializer.PATTERN_ID_ELEMENT, patternId);
            put(JsonSerializer.START_DATE_ELEMENT, startDate);
        }});
        return serializer;
    }

    private AbstractSAXProducer doGenerator(final String patternId, final String startDate) {
        AbstractSAXProducer generator = new PatternCodeGenerator();
        generator.setConfiguration(new HashMap<String, Object>() {{
            put(PatternCodeGenerator.PARAM_PATTERN_ID, patternId);
            put(PatternCodeGenerator.PARAM_START_DATE, startDate);
            put(PatternCodeGenerator.PARAM_END_DATE, "2015-01-30");
        }});
        return generator;
    }
}
