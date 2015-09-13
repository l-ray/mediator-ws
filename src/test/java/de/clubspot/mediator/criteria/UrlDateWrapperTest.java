package de.clubspot.mediator.criteria;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class UrlDateWrapperTest {

    UrlDateWrapper _underTest;
    Date _currentDate;

    @Before
    public void setUp() throws Exception {
       _currentDate = new GregorianCalendar(2015, Calendar.NOVEMBER, 30).getTime();
       _underTest =new UrlDateWrapper(_currentDate);
    }

    @Test
    public void testGetUrlForSingleParameter() throws Exception {
        assertEquals(
                "adds day correctly",
                "http://test.de/?day=30&whatever",
                _underTest.getUrl("http://test.de/?day={startDay}&whatever")
        );

        assertEquals(
                "adds month correctly",
                "http://test.de/?month=11&whatever",
                _underTest.getUrl("http://test.de/?month={startMonth}&whatever")
        );

        assertEquals(
                "adds year correctly",
                "http://test.de/?year=2015&whatever",
                _underTest.getUrl("http://test.de/?year={startYear}&whatever")
        );
    }

    @Test
    public void testGetUrlForMultipleParameter() throws Exception {
        assertEquals(
                "adds day correctly",
                "http://test.de/?day=30&month=11&year=2015",
                _underTest.getUrl("http://test.de/?day={startDay}&month={startMonth}&year={startYear}")
        );

        assertEquals(
                "adds day correctly",
                "/events.aspx?ai=43&v=day&mn=11&yr=2015&dy=30",
                _underTest.getUrl("/events.aspx?ai=43&v=day&mn={startMonth}&yr={startYear}&dy={startDay}")
        );


    }

}