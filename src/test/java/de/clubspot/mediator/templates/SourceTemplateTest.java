package de.clubspot.mediator.templates;

import org.custommonkey.xmlunit.Diff;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
        String dummyUrlString = "http://t.de";
        String dummyIconString = "http://t.de/favicon.ico";
        String dummyId = "01";

        String EXPECTED_RESULT_XML =
                "<source>" +
                "<id>"+dummyId+"</id>" +
                "<name>"+dummyNameString+"</name>" +
                "<url>"+dummyUrlString+"</url>" +
                "<icon>"+dummyIconString+"</icon>" +
                "</source>";

        String EXPECTED_RESULT_PLURAL_XML =
                "<sources>" +
                "<id>"+dummyId+"</id>" +
                "<name>"+dummyNameString+"</name>" +
                "<url>"+dummyUrlString+"</url>" +
                "<icon>"+dummyIconString+"</icon>" +
                "</sources>";


        ((WebHarvestTemplate)_underTest).setIcon(dummyIconString);

        ((WebHarvestTemplate)_underTest).setName(dummyNameString);

        ((WebHarvestTemplate)_underTest).setUrl(dummyUrlString);

        ((WebHarvestTemplate)_underTest).setId(dummyId);

        String result =_underTest.toXML();

        final Diff singleDiff = new Diff(EXPECTED_RESULT_XML, result);
        assertTrue("Transformation did not work like expected:" + singleDiff + ":" + result + " versus " + EXPECTED_RESULT_XML,
                singleDiff.identical());

        result =_underTest.toXML("sources");

        final Diff pluralDiff = new Diff(EXPECTED_RESULT_PLURAL_XML, result);
        assertTrue("Transformation did not work like expected:" + pluralDiff + ":" + result + " versus " + EXPECTED_RESULT_PLURAL_XML,
                pluralDiff.identical());


    }
}