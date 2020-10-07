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
        String createTripTable = "CREATE TABLE TRIP (ID INTEGER PRIMARY KEY AUTOINCREMENT,TRIP_NAME TEXT,START_DATE DATE,END_DATE DATE)";
        String createTripDetailTable = "CREATE TABLE TRIP_DETAIL (ID INTEGER,TRIP_ID INTEGER PRIMARY KEY AUTOINCREMENT,TRIP_NAME TEXT,CUR_DATE DATE,START_TIME TIME,END_TIME TIME,DESTINATION TEXT)";
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

    public void DeleteTrip(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String QueryString1 = "DELETE FROM TRIP WHERE ID = " + id ;
        String QueryString2 = "DELETE FROM TRIP_DETAIL WHERE ID = " +id;
        db.execSQL(QueryString1);
        db.execSQL(QueryString2);
    }

    public void DeleteTripDetail(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String QueryString = "DELETE FROM TRIP_DETAIL WHERE TRIP_ID = " + id;
        db.execSQL(QueryString);
    }

    public boolean checkIfTimeOverlappingExistingTrip(String time,int id)
    {
        //We have to consider ID of TRIPNAME as well !!! FIX
        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT * FROM TRIP_DETAIL WHERE ID = " + id + " AND START_TIME < '" + time + "' AND END_TIME > '" + time +"'";
        Cursor cursor = db.rawQuery(QueryString,null);
        return cursor.getCount() > 0;
    }


/*
    public int getIdOfTripName(int id)
    {
        int res=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT ID FROM TRIP_DETAIL WHERE TRIP_ID = " + id;
        Cursor cursor = db.rawQuery(QueryString,null,null);
        if (cursor.moveToFirst()) res = cursor.getInt(0);
        cursor.close();
        db.close();
        return res;
    } */

    public tripModel getEditDetail(int id)
    {
        int id1=0,id2=0;
        String cur_date="" , destination="" , start_time="" , end_time="" ;
        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT ID , TRIP_ID  ,CUR_DATE , DESTINATION , START_TIME , END_TIME FROM TRIP_DETAIL WHERE TRIP_ID = " + id;
        Cursor cursor = db.rawQuery(QueryString,null,null);
        if (cursor.moveToFirst())
        {
            id1=cursor.getInt(0);
            id2=cursor.getInt(1);
            cur_date=cursor.getString(2);
            destination=cursor.getString(3);
            start_time=cursor.getString(4);
            end_time=cursor.getString(5);
        }
        tripModel tmp = new tripModel(id1,id2,cur_date,destination,start_time,end_time);
        cursor.close();
        db.close();
        return tmp;
    }


    //Still need to handle user input with aphosthophie
    public void updateTripDetail(tripModel tripModel){
        SQLiteDatabase db = this.getWritableDatabase();
        String QueryString = "UPDATE TRIP_DETAIL SET CUR_DATE ='" + tripModel.getCurrentDate() +"', START_TIME = '" + tripModel.getStartTime() +
                "' , END_TIME = '"+tripModel.getEndTime()+"', DESTINATION = '" + tripModel.getDestination() + "' WHERE TRIP_ID = " + tripModel.getIdforListDetail();
        db.execSQL(QueryString);

    }


}
