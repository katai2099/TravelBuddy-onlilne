package com.example.travelbuddyv2;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class HelperClassUnitTest {

    @Test
    public void checkIfStartTimeBeforeEndTime(){
        Boolean checkTrue = Helper.checkIfStartTimeBeforeEndTime("12:00","12:12");
        assertTrue(checkTrue);
        Boolean checkFalse = Helper.checkIfStartTimeBeforeEndTime("12:12","12:00");
        assertFalse(checkFalse);
    }
    @Test
    public void checkIfStartDateBeforeEndDate(){
        Boolean checkTrue = Helper.checkIfStartDateBeforeEndDate("12-12-2020","14-12-2020");
        assertTrue(checkTrue);
        Boolean checkFalse = Helper.checkIfStartDateBeforeEndDate("14-12-2020","12-12-2020");
        assertFalse(checkFalse);
    }
    @Test
    public void checkIfStartDateSameAsEndDate(){
        Boolean checkTrue = Helper.checkIfStartDateSameDateAsEndDate("12-12-2020","12-12-2020");
        assertTrue(checkTrue);
        Boolean checkFalse = Helper.checkIfStartDateSameDateAsEndDate("12-12-2020","28-12-2020");
        assertFalse(checkFalse);
    }
    @Test
    public void getStartDateInMilliSec(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,2020);
        cal.set(Calendar.MONTH,12-1); // january is 0
        cal.set(Calendar.DATE,1);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        float checkTrue = Helper.getStartDateInMilli("2020-12-2");
        assertEquals(1606802400000f,checkTrue,0); // 1606802400000f is millsec of 2020-12-1 at 7 am
    }
    @Test
    public void stringToDate(){
        Date d = new Date();
        d.setTime(Helper.stringToDate("2020-12-1").getTime());
        assertEquals("Tue Dec 01 00:00:00 CET 2020",d.toString());

        Date d2 = new Date();
        d2.setTime(Helper.stringToDate("2020-12-3").getTime());
        String tmp = "Tue Dec 04 00:00:00 CET 2020";
        Boolean checkFalse =  tmp.equals(d2.toString());
        assertFalse(checkFalse);
    }
    //we only care about time here
    @Test
    public void stringToTime(){
        Date d = new Date();
        d.setTime(Helper.stringToTime("23:45").getTime());
        assertEquals("Thu Jan 01 23:45:00 CET 1970",d.toString());

        Date d2 = new Date();
        d2.setTime(Helper.stringToTime("10:00").getTime());
        String tmp = "Thu Jan 01 10:01:00 CET 1970";
        Boolean checkFalse =  tmp.equals(d2.toString());
        assertFalse(checkFalse);

    }
    @Test
    public void changeInputDateformat(){
        String checkTrue = Helper.changeInputDateFormat("2020-12-1");
        assertEquals("2020-12-01",checkTrue);

        String tmp = Helper.changeInputDateFormat("2020-12-2");
        String tmp2 = "2020-12-2" ;
        Boolean checkFalse = tmp2.equals(tmp);
        assertFalse(checkFalse);

    }
    @Test
    public void changeInputTimeFormat(){
        String checkTrue = Helper.changeInputTimeFormat("1:23");
        assertEquals("01:23",checkTrue);

        String tmp = Helper.changeInputTimeFormat("2:44");
        String tmp2 = "2:44";
        Boolean checkFalse = tmp2.equals(tmp);
        assertFalse(checkFalse);
    }




}
