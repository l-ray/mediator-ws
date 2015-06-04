package de.clubspot.mediator.processing;

import de.clubspot.mediator.templates.SourceTemplate;
import de.clubspot.mediator.templates.WebHarvestTemplate;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import java.sql.Connection;

import static junit.framework.Assert.assertTrue;

public class SourceTemplateTest {

    SourceTemplate _underTest;

    @Before
    public void beforeMethod() {

        _underTest = new WebHarvestTemplate(Mockito.mock(Connection.class));
    }

    @Test
    public void doesSourceInfoAsXml() throws Exception {

        String dummyNameString = "pattern-name";
        String dummyIconString = "http://t.de/favicon.ico";
        String dummyId = "01";

        String EXPECTED_RESULT_XML =
                "<source>" +
                "<id>"+dummyId+"</id>" +
                "<name>"+dummyNameString+"</name>" +
                "<icon>"+dummyIconString+"</icon>" +
                "</source>";

        ((WebHarvestTemplate)_underTest).setIcon(dummyIconString);

        ((WebHarvestTemplate)_underTest).setName(dummyNameString);

        ((WebHarvestTemplate)_underTest).setId(dummyId);

        String result =_underTest.toXML();

        final Diff diff = new Diff(EXPECTED_RESULT_XML, result);
        assertTrue("Transformation did not work like expected:" + diff + ":"+result+" versus "+EXPECTED_RESULT_XML,
                diff.identical());
    }
}