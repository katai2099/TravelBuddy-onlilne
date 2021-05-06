package com.example.travelbuddyv2;

import com.example.travelbuddyv2.model.Destination;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class HelperClassUnitTest {

    @Test
    public void checkIfStartTimeBeforeEndTime(){
        boolean checkTrue = Helper.checkIfStartTimeBeforeEndTime("12:00","12:12");
        assertTrue(checkTrue);
        boolean checkFalse = Helper.checkIfStartTimeBeforeEndTime("12:12","12:00");
        assertFalse(checkFalse);
    }
    @Test
    public void checkIfStartDateBeforeEndDate(){
        boolean checkTrue = Helper.checkIfStartDateBeforeEndDate("12-12-2020","14-12-2020");
        assertTrue(checkTrue);
        boolean checkFalse = Helper.checkIfStartDateBeforeEndDate("14-12-2020","12-12-2020");
        assertFalse(checkFalse);
    }
    @Test
    public void checkIfStartDateSameAsEndDate(){
        boolean checkTrue = Helper.checkIfStartDateSameDateAsEndDate("12-12-2020","12-12-2020");
        assertTrue(checkTrue);
        boolean checkFalse = Helper.checkIfStartDateSameDateAsEndDate("12-12-2020","28-12-2020");
        assertFalse(checkFalse);
    }
    @Test
    public void checkIfPdfFile(){
        boolean checkTrue = Helper.isPdf("katai.pdf");
        assertTrue(checkTrue);
        boolean checkFalse = Helper.isPdf("katai.png");
        assertFalse(checkFalse);
    }

    @Test
    public void stringToDate(){
        Date date = new Date();
        date.setTime(Helper.stringToDate("2020-12-01").getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(1,cal.get(Calendar.DATE));
        assertEquals(11,cal.get(Calendar.MONTH));
        assertEquals(2020,cal.get(Calendar.YEAR));

        Date date2 = new Date();
        date2.setTime(Helper.stringToDate("2020-12-3").getTime());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        assertNotEquals(1,cal2.get(Calendar.DATE));
        assertNotEquals(10,cal2.get(Calendar.MONTH));
        assertNotEquals(2021,cal2.get(Calendar.YEAR));
    }

    //this not use anymore need to be gone

/*    //we only care about time here
    @Test
    public void stringToTime(){
        Date d = new Date();
        d.setTime(Helper.stringToTime("23:45").getTime());
        Calendar cal = Calendar.getInstance();
        assertEquals(23,cal.get(Calendar.HOUR_OF_DAY));

//        Date d2 = new Date();
//        d2.setTime(Helper.stringToTime("10:00").getTime());
//        d2.setSeconds(0);
//        String tmp = "Thu Jan 01 10:01:00 CET 1970";
//        Boolean checkFalse =  tmp.equals(d2.toString());
//        assertFalse(checkFalse);

    }*/
    @Test
    public void changeInputDateformat(){
        String checkTrue = Helper.changeInputDateFormat("2020-12-1");
        assertEquals("2020-12-01",checkTrue);

        String tmp = Helper.changeInputDateFormat("2020-12-2");
        String tmp2 = "2020-12-2" ;
        boolean checkFalse = tmp2.equals(tmp);
        assertFalse(checkFalse);

    }
    @Test
    public void changeInputTimeFormat(){
        String checkTrue = Helper.changeInputTimeFormat("1:23");
        assertEquals("01:23",checkTrue);
        String tmp = Helper.changeInputTimeFormat("2:44");
        String tmp2 = "2:44";
        boolean checkFalse = tmp2.equals(tmp);
        assertFalse(checkFalse);
    }
    @Test
    public void changeDateFormatSuitableForTripScreen(){
        String checkTrue = Helper.changeDateFormatSuitableForTripScreen("2012-03-22");
        assertEquals("Mar 22, 2012",checkTrue);
    }

    @Test
    public void calculateExtraDay(){
        int checkTrue = Helper.calculateExtraDay("2020-12-1","09:30",1);
        assertEquals(0,checkTrue);
    }

    @Test
    public void calculateExtraDayWithMoreParameter(){
        int secondCheckTrue = Helper.calculateExtraDay("2021-12-1","23:30",1,1,0);
        assertEquals(1,secondCheckTrue);
    }

    @Test
    public void changeDateFormatSuitableForTripDetailScreen(){
        String checkTrue = Helper.changeDateFormatSuitableForTripDetailScreen("2012-03-22");
        assertEquals("Thu, Mar 22, 2012",checkTrue);
    }

    @Test
    public void tripStringIDtoInt(){
        int checkTrue = Helper.tripStringIDToInt("t2");
        assertEquals(2,checkTrue);
    }

    @Test
    public void tripDetailStringIDtoInt(){
        int checkTrue = Helper.tripDetailStringIDToInt("td03");
        assertEquals(3,checkTrue);
    }

    @Test
    public void getNextThirtyMinute(){
        String checkTrue = Helper.getNextThirtyMinute("09:30");
        assertEquals("10:00",checkTrue);
    }

    @Test
    public void changeStayPeriodOfDestination(){
        Destination test = new Destination();
        Helper.changeStayPeriodOfDestination("2012-02-02","09:00",2,30,test);
        assertEquals("11:30",test.getEndTime());
    }
    @Test
    public void changeStayPeriodOfDestinationWithExtraDayIncreased(){
        Destination test = new Destination();
        Helper.changeStayPeriodOfDestination("2012-02-02","23:00",2,30,test);
        assertEquals(1,test.getExtraDay());
    }
    @Test
    public void changeStayPeriodOfDestinationWithExtraDayDecreased(){
        Destination test = new Destination();
        test.setExtraDay(1);
        test.setIncreased(true);
        test.setDecreased(false);
        Helper.changeStayPeriodOfDestination("2012-02-02","09:00",2,30,test);
        assertEquals(0,test.getExtraDay());
    }
    @Test
    public void changeStayPeriodCheckOnStayPeriod(){
        Destination test = new Destination();
        test.setExtraDay(1);
        test.setIncreased(true);
        test.setDecreased(false);
        Helper.changeStayPeriodOfDestination("2012-02-02","09:00",2,30,test);
        assertEquals(150,test.getDuration());
    }

    @Test
    public void minuteToInt(){
        int actual = Helper.minuteToInt("00:59");
        assertEquals(59,actual);
    }

    @Test
    public void hourToInt(){
        int actual = Helper.hourToInt("01:11");
        assertEquals(1,actual);
    }

    @Test
    public void failMinuteToInt(){
        int actual = Helper.minuteToInt("00:21");
        assertNotEquals(1,actual);
    }

    @Test
    public void failHourToInt(){
        int actual = Helper.hourToInt("12:21");
        assertNotEquals(13,actual);
    }

    @Test
    public void minuteToHour(){
        int actual = Helper.minutesToHour(70);
        assertEquals(1,actual);
        int actual2 = Helper.minutesToHour(120);
        assertEquals(2,actual2);
    }

    @Test
    public void minuteToMinute(){
        int actual = Helper.minutesToMinute(70);
        assertEquals(10,actual);
        int actual2 = Helper.minutesToMinute(70);
        assertNotEquals(70,actual2);
    }

    @Test
    public void durationToTextWithBothHourAndMin(){
        String actual = Helper.changeDurationToText(2,1);
        assertEquals("2 hours 1 min",actual);
        String actual2 = Helper.changeDurationToText(1,12);
        assertEquals("1 hour 12 mins",actual2);
        String actual3 = Helper.changeDurationToText(1,1);
        assertEquals("1 hour 1 min",actual3);
        String actual4 = Helper.changeDurationToText(2,59);
        assertEquals("2 hours 59 mins",actual4);
    }

    @Test
    public void durationToTextOnlyHour(){
        String actual = Helper.changeDurationToText(1,0);
        assertEquals("1 hour",actual);
        String actual2 = Helper.changeDurationToText(2,0);
        assertEquals("2 hours",actual2);
    }

    @Test
    public void durationToTextOnlyMin(){
        String actual = Helper.changeDurationToText(0,1);
        assertEquals("1 min",actual);
        String actual2 = Helper.changeDurationToText(0,12);
        assertEquals("12 mins",actual2);
    }


}
