package com.example.travelbuddyv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, "Trip.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTripTable = "CREATE TABLE TRIP (ID INTEGER PRIMARY KEY AUTOINCREMENT,TRIP_NAME TEXT,START_DATE TEXT,END_DATE TEXT)";
        String createTripDetailTable = "CREATE TABLE TRIP_DETAIL (ID INTEGER,TRIP_ID INTEGER PRIMARY KEY AUTOINCREMENT,TRIP_NAME TEXT,CUR_DATE TEXT,START_TIME TEXT,END_TIME TEXT,DESTINATION TEXT)";
        db.execSQL(createTripTable);
        db.execSQL(createTripDetailTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addNewTrip(tripModel trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("TRIP_NAME", trip.getTripName());
        cv.put("START_DATE", trip.getStartDate());
        cv.put("END_DATE", trip.getEndDate());

        long insert = db.insert("TRIP", null, cv);

        if (insert == -1) {
            return false;
        } else return true;
    }


    public boolean addTripDetail(tripModel trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("ID", trip.getId());
        cv.put("TRIP_NAME", trip.getTripName());
        cv.put("CUR_DATE", trip.getCurrentDate());
        cv.put("START_TIME", trip.getStartTime());
        cv.put("END_TIME", trip.getEndTime());
        cv.put("DESTINATION", trip.getDestination());

        long insert = db.insert("TRIP_DETAIL", null, cv);

        if (insert == -1) {
            return false;
        } else return true;
    }

    public List<tripModel> getTripList() {
        List<tripModel> returnlist = new ArrayList<>();

        String queryString = "SELECT * FROM TRIP";
        //get data from database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String tmpTripName = cursor.getString(1);
                String tmpStartDate = cursor.getString(2);
                String tmpEndDate = cursor.getString(3);
                tripModel tmp = new tripModel(id, tmpTripName, tmpStartDate, tmpEndDate);
                returnlist.add(tmp);
            } while (cursor.moveToNext());
        } else {

        }
        cursor.close();
        db.close();

        return returnlist;
    }

    public List<tripModel> getTripListOnACertainDate(String date,int iden) {
        List<tripModel> returnlist = new ArrayList<>();

        String queryString = "SELECT TRIP_ID, TRIP_NAME,CUR_DATE,START_TIME,END_TIME,DESTINATION FROM TRIP_DETAIL WHERE CUR_DATE ='" + date + "' AND ID =" + iden;
        //get data from database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String tmpTripName = cursor.getString(1);
                String curDate = cursor.getString(2);
                String tmpStartTime = cursor.getString(3);
                String tmpEndTime = cursor.getString(4);
                String tmpDestination = cursor.getString(5);
                tripModel tmp = new tripModel(id, tmpTripName, curDate, tmpStartTime, tmpEndTime, tmpDestination);
                returnlist.add(tmp);
            } while (cursor.moveToNext());
        } else {

        }
        cursor.close();
        db.close();

        return returnlist;
    }

    public String getStartDateOfTrip(int id)
    {
        String res="";
        String queryString = "SELECT START_DATE FROM TRIP WHERE ID =" + id ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null,null);
        if (cursor.moveToFirst()) res = cursor.getString(0);
        cursor.close();
        db.close();

        return res;
    }

    public String getEndDateOfTrip(int id)
    {
        String res="";
        String queryString = "SELECT END_DATE FROM TRIP WHERE ID =" + id ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null,null);
        if (cursor.moveToFirst()) res = cursor.getString(0);
        cursor.close();
        db.close();

        return res;
    }

    public static String changeDateFormat(String date)
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


}
