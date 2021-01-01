package com.example.travelbuddyv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Helper {

    static int uniqueNotificationID = 0;

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


    //to set notification alarm fired at 7 . minus one day and set hour to 7
    public static long getStartDateInMilli(String timeStartDate)
    {
        long res;//TimeUnit
        Date startDate = Helper.stringToDate(timeStartDate);
        Calendar epoch = Calendar.getInstance();
        epoch.setTime(startDate);
        epoch.add(Calendar.DATE,-1);
        epoch.set(Calendar.MINUTE, 0);
        epoch.set(Calendar.SECOND,0);
        epoch.set(Calendar.HOUR,7);
        Date dateInmilli = epoch.getTime();
        return dateInmilli.getTime();
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

}
