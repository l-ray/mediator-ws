package de.clubspot.mediator.processing.transformation;

import org.apache.cocoon.sax.SAXPipelineComponent;
import org.custommonkey.xmlunit.Diff;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import static junit.framework.Assert.assertTrue;

public class TermWeightTransformerTest extends AbstractTransformerTest {

    private static final Logger LOG =
            LoggerFactory.getLogger(TermWeightTransformerTest.class);

    public SAXPipelineComponent underTest;

    @Before
    public void beforeMethod() {

        underTest = new TermWeightTransformer();

        underTest.setConfiguration(new HashMap<String,Object>(){
            {
                put(TermWeightTransformer.PARAM_PARENT_ELEMENT,"results");
                put(TermWeightTransformer.PARAM_INDEX_ELEMENTS,"location,title");
            }
        });

        pipelineSetup = new HashMap<String, Object>() {
            {
                put(TermWeightTransformer.DB_CONNECTION, mockConnection());
            }
        };
    }

    private Connection mockConnection() {
        Connection mock = Mockito.mock(Connection.class);
        try {

            Statement mockedStatement = Mockito.mock(Statement.class);
            ResultSet mockedRs1 = Mockito.mock(ResultSet.class);

            Mockito.doReturn(true).doReturn(true).doReturn(true).doReturn(false).when(mockedRs1).next();
            Mockito.when(mockedRs1.getString("term"))
                    .thenReturn("StopWord")
                    .thenReturn("LowPriorityWord")
                    .thenReturn("HighPriorityWord");
            Mockito.when(mockedRs1.getString("weight"))
                    .thenReturn("0")
                    .thenReturn("0.5")
                    .thenReturn("1");

            Mockito.when(mockedStatement.executeQuery(Mockito.anyString())).thenReturn(mockedRs1);

            Mockito.when(mock.createStatement()).thenReturn(mockedStatement);
        } catch (SQLException e) {
            Assert.fail("unexpected SQLexception when mocking Connection" + e.getMessage());
        }
        return mock;
    }

    @Test
    public void weightsTermsCorrectlyOnLocation()
            throws Exception {

        final String basicXML = "<results><location>StopWord, LowPriorityWord, HighPriorityWord, UnknownWord</location></results>";

        final ByteArrayOutputStream baos = transformThroughPipeline(basicXML, underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff(
                "<results>"
                +"<location>StopWord, LowPriorityWord, HighPriorityWord, UnknownWord</location>"
                +"<weights>"
                +"<weight-term><term>LowPriorityWord</term><weight>0.5</weight></weight-term>"
                +"<weight-term><term>HighPriorityWord</term><weight>1</weight></weight-term>"
                +"<weight-term><term>StopWord</term><weight>0</weight></weight-term>"
                +"</weights>"
                +"</results>",
                actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @Test
    public void weightsTermsCorrectlyOnLocationAndTitle()
            throws Exception {

        final String basicXML = "<results><location>StopWord, LowPriorityWord</location>"
                +"<title>HighPriorityWord, UnknownWord</title></results>";

        final ByteArrayOutputStream baos = transformThroughPipeline(basicXML, underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff(
                "<results>"
                        +"<location>StopWord, LowPriorityWord</location>"
                        +"<title>HighPriorityWord, UnknownWord</title>"
                        +"<weights>"
                        +"<weight-term><term>LowPriorityWord</term><weight>0.5</weight></weight-term>"
                        +"<weight-term><term>HighPriorityWord</term><weight>1</weight></weight-term>"
                        +"<weight-term><term>StopWord</term><weight>0</weight></weight-term>"
                        +"</weights>"
                        +"</results>",
                actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }


    @Test
    public void weightsTermsCorrectlyWithEmptySource()
            throws Exception {

        final String basicXML = "<results><location></location></results>";

        final ByteArrayOutputStream baos = transformThroughPipeline(basicXML, underTest);
        final String actualDocument = new String(baos.toByteArray(), "UTF-8");

        final Diff diff = new Diff(
                "<results>"
                        +"<location></location>"
                        +"</results>",
                actualDocument);

        assertTrue("LinkRewrite transformation didn't work as expected " + diff,
                diff.identical());
    }

    @After
    public void tearDown() throws Exception {

    }

}