package com.example.travelbuddyv2;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;

public class addNewTripUnitTest {

    private List<String> getDateInterval(String startDate , String endDate)  {
        SimpleDateFormat simpleDateFormat;
        Date start , end ;
        Calendar calendar;
        start = new Date();
        end = new Date();
        calendar = new GregorianCalendar();
        List<String> listOfdate = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            start = simpleDateFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end = simpleDateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(start);
        while(calendar.getTime().before(end))
        {
            Date result = calendar.getTime();
            String tmp = simpleDateFormat.format(result);
            listOfdate.add(tmp);
            calendar.add(Calendar.DATE, 1);
        }
        String tmp = simpleDateFormat.format(calendar.getTime());
        listOfdate.add(tmp);
        return listOfdate;
    }
    List<String> actual;
    List<String> expected;
    @Before
    public void initialize(){
        actual = new ArrayList<>();
        expected = new ArrayList<>();
        actual = getDateInterval("2021-08-12","2021-08-14");
        String first = "2021-08-12";
        String second = "2021-08-13";
        String third = "2021-08-14";
        expected.add(first);
        expected.add(second);
        expected.add(third);
    }
    @Test
    public void getDateInterval(){
        assertFalse(actual.isEmpty());
        assertEquals(3,actual.size());
        assertEquals(actual.get(0),expected.get(0));
        assertEquals(actual.get(1),expected.get(1));
        assertEquals(actual.get(2),expected.get(2));
    }

    @Test
    public void getSameDateInterval(){
        assertEquals(1,getDateInterval("2021-08-12","2021-08-12").size());
    }

    

}