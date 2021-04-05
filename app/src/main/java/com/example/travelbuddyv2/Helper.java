package com.example.travelbuddyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.travelbuddyv2.model.Destination;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Helper {

    static int uniqueNotificationID = 0;
    static private final String tag = "HELPER";

    public static String changeInputTimeFormat(String time)
    {
        String res ="";

        Date tmpTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            tmpTime = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        res = simpleDateFormat.format(tmpTime);
        return res;
    }

    public static String changeInputDateFormat(String date)
    {
        String res ="";
        Date tmpDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tmpDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        res = simpleDateFormat.format(tmpDate);

        return res;
    }

    public static Date stringToTime(String time){
        Date res = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        try {
            res = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static Date stringToDate(String date){
        Date res = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            res = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean isEditTextEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public static void clearEdittext(EditText editText){
        editText.getText().clear();
    }

    public static boolean checkIfStartTimeBeforeEndTime(String startTime,String endTime)
    {
        Calendar startCal , endCal ;
        Date startDateTime = Helper.stringToTime(startTime);
        Date endDateTime = Helper.stringToTime(endTime);
        startCal = Calendar.getInstance();

        startCal.setTime(startDateTime);
        endCal = Calendar.getInstance();

         endCal.setTime(endDateTime);

        return startCal.before(endCal);
    }

    public static boolean checkIfStartDateBeforeEndDate(String startDate,String endDate)
    {
        Calendar startCal , endCal ;
        Date tmpStartDate = Helper.stringToDate(startDate);
        Date tmpEndDate = Helper.stringToDate(endDate);

        startCal = Calendar.getInstance();
        startCal.setTime(tmpStartDate);

        endCal = Calendar.getInstance();
        endCal.setTime(tmpEndDate);

        return startCal.before(endCal) ;
    }

    public static boolean checkIfStartDateSameDateAsEndDate(String startDate,String endDate)
    {
        Calendar startCal , endCal ;
        Date tmpStartDate = Helper.stringToDate(startDate);
        Date tmpEndDate = Helper.stringToDate(endDate);

        startCal = Calendar.getInstance();
        startCal.setTime(tmpStartDate);

        endCal = Calendar.getInstance();
        endCal.setTime(tmpEndDate);

        return startCal.get(Calendar.YEAR)==endCal.get(Calendar.YEAR) && startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH) && startCal.get(Calendar.DATE) == endCal.get(Calendar.DATE);
    }


    //to set notification alarm fired at 8 . minus one day and set hour to 8
    public static long getStartDateInMilli(String timeStartDate)
    {
        long res;//TimeUnit
        Date startDate = Helper.stringToDate(timeStartDate);
        Calendar epoch = Calendar.getInstance();
        epoch.setTime(startDate);
        epoch.add(Calendar.DATE,-1);
        epoch.set(Calendar.MINUTE, 0);
        epoch.set(Calendar.SECOND,0);
        epoch.set(Calendar.HOUR,8);
        Date dateInMilli = epoch.getTime();
        Log.d(tag, String.valueOf(dateInMilli));
        return dateInMilli.getTime();
    }




    public static long milliToSecond(long milli) {

        long res = TimeUnit.MILLISECONDS.toSeconds(milli) % 60; // if not modulo we will get the exact second difference of Time
        return res;
    }

    public static long milliToMinute(long milli){
        long res = TimeUnit.MILLISECONDS.toMinutes(milli) % 60;
        return res;
    }

    public static long milliToHour(long milli){
        long res = TimeUnit.MILLISECONDS.toHours(milli) ;
        return res;
    }

    public static long milliToDay(long milli){
        long res = TimeUnit.MILLISECONDS.toDays(milli) % 365;
        return res;
    }

    public static long milliToyear(long milli){
        long res = TimeUnit.MILLISECONDS.toDays(milli)/365l;
        return res;
    }

    public static int tripStringIDToInt(String ID){

        StringBuilder tmp = new StringBuilder();

        for(int i=1;i<ID.length();i++){
            tmp.append(ID.charAt(i));
        }

        return Integer.parseInt(tmp.toString());

    }

    public static int tripDetailStringIDToInt(String ID){

        StringBuilder tmp = new StringBuilder();

        for(int i=2;i<ID.length();i++){
            tmp.append(ID.charAt(i));
        }

        return Integer.parseInt(tmp.toString());

    }

    public static String getNextThirtyMinute(String time){
        Date date = stringToTime(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,30);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(cal.getTime());

    }

    public static int calculateExtraDay(String currentDate,String time,int numberOfExtraDayFromLastDestination){

        //get time
        Date currentUserTime = stringToTime(time);

        Calendar getMinHour = Calendar.getInstance();
        getMinHour.setTime(currentUserTime);

        //get date
        Date currentUserDate = stringToDate(currentDate);

        Calendar beforeAdding = Calendar.getInstance();

        beforeAdding.setTime(currentUserDate);
        beforeAdding.set(Calendar.HOUR_OF_DAY,getMinHour.get(Calendar.HOUR_OF_DAY));
        beforeAdding.set(Calendar.MINUTE,getMinHour.get(Calendar.MINUTE));
        beforeAdding.set(Calendar.SECOND,0);
        beforeAdding.set(Calendar.MILLISECOND,0);

        //add number of extra day in case there is one destination took more then one day
        beforeAdding.add(Calendar.DATE,numberOfExtraDayFromLastDestination);

        //get date
        Calendar afterAdding = Calendar.getInstance();

        afterAdding.setTime(currentUserDate);
        afterAdding.set(Calendar.HOUR_OF_DAY,getMinHour.get(Calendar.HOUR_OF_DAY));
        afterAdding.set(Calendar.MINUTE,getMinHour.get(Calendar.MINUTE));
        afterAdding.set(Calendar.SECOND,0);
        afterAdding.set(Calendar.MILLISECOND,0);

        //add number of extra day in case there is one destination took more then one day
        afterAdding.add(Calendar.DATE,numberOfExtraDayFromLastDestination);

        afterAdding.add(Calendar.MINUTE,30);

        List<String> dates = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        while(beforeAdding.getTime().before(afterAdding.getTime()) || beforeAdding.getTime().equals(afterAdding.getTime())  )
        {
            Date result = beforeAdding.getTime();
            String tmp = simpleDateFormat.format(result);
            dates.add(tmp);
            beforeAdding.add(Calendar.MINUTE, 30);
        }

        Date res = afterAdding.getTime();

        if(!( dates.get(0).equals(dates.get(1))) ){
            return 1;
        }

        //~ return res.toString();
        return 0 ;
    }

    public static int calculateExtraDay(String currentDate,String time,int numberOfExtraDayFromLastDestination,int hour,int min){

        //get time
        Date currentUserTime = stringToTime(time);

        Calendar getMinHour = Calendar.getInstance();
        getMinHour.setTime(currentUserTime);

        //get date
        Date currentUserDate = stringToDate(currentDate);

        Calendar beforeAdding = Calendar.getInstance();

        beforeAdding.setTime(currentUserDate);
        beforeAdding.set(Calendar.HOUR_OF_DAY,getMinHour.get(Calendar.HOUR_OF_DAY));
        beforeAdding.set(Calendar.MINUTE,getMinHour.get(Calendar.MINUTE));
        beforeAdding.set(Calendar.SECOND,0);
        beforeAdding.set(Calendar.MILLISECOND,0);

        //add number of extra day in case there is one destination took more then one day
        beforeAdding.add(Calendar.DATE,numberOfExtraDayFromLastDestination);

        //get date
        Calendar afterAdding = Calendar.getInstance();

        afterAdding.setTime(currentUserDate);
        afterAdding.set(Calendar.HOUR_OF_DAY,getMinHour.get(Calendar.HOUR_OF_DAY));
        afterAdding.set(Calendar.MINUTE,getMinHour.get(Calendar.MINUTE));
        afterAdding.set(Calendar.SECOND,0);
        afterAdding.set(Calendar.MILLISECOND,0);

        //add number of extra day in case there is one destination took more then one day
        afterAdding.add(Calendar.DATE,numberOfExtraDayFromLastDestination);

        afterAdding.add(Calendar.MINUTE,min);
        afterAdding.add(Calendar.HOUR_OF_DAY,hour);

        List<String> dates = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


        if(hour == 0 && min == 0){
            return 0;
        }

        while(beforeAdding.getTime().before(afterAdding.getTime()) || beforeAdding.getTime().equals(afterAdding.getTime())  )
        {
            Date result = beforeAdding.getTime();
            String tmp = simpleDateFormat.format(result);
            dates.add(tmp);
            beforeAdding.add(Calendar.MINUTE, min);
            beforeAdding.add(Calendar.HOUR_OF_DAY,hour);
        }

        Date res = afterAdding.getTime();

        if(!( dates.get(0).equals(dates.get(1))) ){
            return 1;
        }

        //~ return res.toString();
        return 0 ;
    }

    public static void changeStayPeriodOfDestination(String currentDate, String startTime, int hour, int minute, Destination destination){

        //need to calculate new extraDay , get new EndDate , modify i+1 list

        SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat toTime = new SimpleDateFormat("HH:mm");

        Date date = new Date();


        try{
            date = toDate.parse(currentDate);
        }catch(ParseException e){
            System.out.println(e);
        }

        Calendar curDate = Calendar.getInstance();
        curDate.setTime(date);

        Date time = new Date();

        try{
            time = toTime.parse(startTime);
        }catch(ParseException e){
            System.out.println(e);
        }

        Calendar curTime = Calendar.getInstance();
        curTime.setTime(time);

        curDate.set(Calendar.HOUR_OF_DAY, curTime.get(Calendar.HOUR_OF_DAY));
        curDate.set(Calendar.MINUTE,curTime.get(Calendar.MINUTE));
        curDate.set(Calendar.MILLISECOND,0);

        curDate.add(Calendar.HOUR_OF_DAY, hour);
        curDate.add(Calendar.MINUTE,minute);

        Date res = curDate.getTime();

        String finalTime = toTime.format(res);

        int extraDay = destination.getExtraDay();

        int  extraDayAfterPeriodChanged = calculateExtraDay(currentDate,startTime,extraDay,hour,minute);

        Log.d(tag,"Extra day " + extraDayAfterPeriodChanged);

        if(extraDayAfterPeriodChanged==1){

            if(!destination.isIncreased() && destination.isDecreased()){
                extraDayAfterPeriodChanged += extraDay;
            }else{
                extraDayAfterPeriodChanged = extraDay;
            }

            destination.setIncreased(true);
            destination.setDecreased(false);

        }else if(extraDayAfterPeriodChanged==0){

            if(!destination.isDecreased() && destination.isIncreased()){
                if(extraDay==0){
                    extraDayAfterPeriodChanged = 0;
                }else {
                    extraDayAfterPeriodChanged = extraDay - 1;
                }

                destination.setIncreased(false);
                destination.setDecreased(true);

            }else{
                extraDayAfterPeriodChanged = extraDay;
            }

        }

      /*  if(extraDayAfterPeriodChanged==1 ) {
            if(!destination.isIncreased()) {
                Log.d(tag,"!isIncrease and extraDay == 1");
                extraDayAfterPeriodChanged += extraDay;
            }
            else{
                Log.d(tag,"isIncrease and extraDay == 1");
                extraDayAfterPeriodChanged = extraDay;
            }
            destination.setIncreased(true);
            destination.setDecreased(false);
        }else {
            if(extraDayAfterPeriodChanged!=0 && !destination.isDecreased()) {
                Log.d(tag,"!isDecease and extraDay != 0");
                if(extraDay == 0){
                    extraDayAfterPeriodChanged = 0;
                }else{
                    extraDayAfterPeriodChanged = (extraDay - 1);
                }
                destination.setIncreased(false);
                destination.setDecreased(true);
            }
            else if(destination.isDecreased() && extraDayAfterPeriodChanged==0){
                Log.d(tag,"isDecease and extraDay == 0");
                extraDayAfterPeriodChanged = extraDay;
                destination.setIncreased(false);
                destination.setDecreased(true);
            }
            else {
                extraDayAfterPeriodChanged = extraDay;
                destination.setIncreased(false);
                destination.setDecreased(true);
            }
        }*/

//        extraDayAfterPeriodChanged += extraDay;

        Log.d(tag,"Extra day after if else " + extraDayAfterPeriodChanged);

        destination.setEndTime( finalTime) ;
        destination.setDuration((hour*60) + minute);
        destination.setExtraDay(extraDayAfterPeriodChanged);

    }

    public static boolean isPdf(String fileName){
        if(fileName!=null){
            String fileExtension = fileName.substring(fileName.length()-3,fileName.length());
            if(fileExtension.toUpperCase().equals("PDF")){
                return true;
            }
        }
        return false;
    }

}
