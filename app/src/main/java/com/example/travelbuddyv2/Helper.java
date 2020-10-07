package com.example.travelbuddyv2;

import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {

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

    public static boolean checkIfStartTimeBeforeEndTime(String startTime,String endTime)
    {
        Calendar startCal , endCal ;
        Date startDateTime = Helper.stringToTime(startTime);
        Date endDateTime = Helper.stringToTime(endTime);
        startCal = Calendar.getInstance();
       // startCal.set(Calendar.YEAR,2000);
       // startCal.set(Calendar.MONTH,12);
      //  startCal.set(Calendar.DATE,28);
        startCal.setTime(startDateTime);
        endCal = Calendar.getInstance();
        //endCal.set(Calendar.YEAR,3000);
        //endCal.set(Calendar.MONTH,12);
        //endCal.set(Calendar.DATE,28);
         endCal.setTime(endDateTime);
       // String msg=(startCal.get(Calendar.AM_PM)==Calendar.AM) ? "am" : "pm";
        //String msg2=(endCal.get(Calendar.AM_PM)==Calendar.AM) ? "am" : "pm";
        //System.out.println(startCal.get(Calendar.HOUR)+" "+startCal.get(Calendar.MINUTE)+" "+msg);
       // System.out.println(endCal.get(Calendar.HOUR) +" "+endCal.get(Calendar.MINUTE)+" "+  msg2  );
       // System.out.println(startCal.before(endCal) ? "true" : "false");
        return startCal.before(endCal);
    }

}
