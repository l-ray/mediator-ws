package org.apache.cocoon.optional.pipeline.components.sax.json;

import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class JsonSerializerTest {

    @Test
    public void testFinishForDroppingRootElement() throws Exception {

        String SOURCE_XML = "<toBeDropped><item>1</item><item>2</item></toBeDropped>";
        String EXPECTED_RESULT = "{\"item\":[1,2]}";

        final Pipeline<SAXPipelineComponent> pipeline =
                new NonCachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(SOURCE_XML));
        pipeline.addComponent(new JsonSerializer());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos);
        pipeline.execute();

        assertEquals("valid json with dropped root element",EXPECTED_RESULT, new String(baos.toByteArray()));

    }
}