package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.pipeline.NonCachingPipeline;
import org.apache.cocoon.pipeline.Pipeline;
import org.apache.cocoon.sax.SAXPipelineComponent;
import org.apache.cocoon.sax.component.XMLGenerator;
import org.apache.cocoon.sax.component.XMLSerializer;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by lray on 26.01.15.
 */
public abstract class AbstractTransformerTest {
    @BeforeClass
    public static void setUp() {
        XMLUnit.setIgnoreWhitespace(false);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        final SAXParserFactory saxPFactory = SAXParserFactory.newInstance();
        saxPFactory.setValidating(false);
        XMLUnit.setSAXParserFactory(saxPFactory);

        final DocumentBuilderFactory docBuildFactory =
                DocumentBuilderFactory.newInstance();
        docBuildFactory.setNamespaceAware(true);
        docBuildFactory.setValidating(false);

        XMLUnit.setControlDocumentBuilderFactory(docBuildFactory);
        XMLUnit.setTestDocumentBuilderFactory(docBuildFactory);
    }

    protected ByteArrayOutputStream transformThroughPipeline(String SOURCE_XML, SAXPipelineComponent underTest) throws Exception {
        final Pipeline<SAXPipelineComponent> pipeline =
                new NonCachingPipeline<SAXPipelineComponent>();
        pipeline.addComponent(new XMLGenerator(SOURCE_XML));
        pipeline.addComponent(underTest);
        pipeline.addComponent(new XMLSerializer());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipeline.setup(baos);
        pipeline.execute();
        return baos;
    }
}
