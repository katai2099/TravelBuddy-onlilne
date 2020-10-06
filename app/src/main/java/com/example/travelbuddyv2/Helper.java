package com.example.travelbuddyv2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

}
