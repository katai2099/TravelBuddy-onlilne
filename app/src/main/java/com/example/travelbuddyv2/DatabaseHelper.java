package com.example.travelbuddyv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, "Trip.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTripTable = "CREATE TABLE TRIP (ID INTEGER PRIMARY KEY AUTOINCREMENT,TRIP_NAME TEXT,START_DATE DATE,END_DATE DATE,IS_NOTIFIED INTEGER)";
        String createTripDetailTable = "CREATE TABLE TRIP_DETAIL (ID INTEGER,TRIP_ID INTEGER PRIMARY KEY AUTOINCREMENT,TRIP_NAME TEXT,CUR_DATE DATE,START_TIME TIME,END_TIME TIME,DESTINATION TEXT)";
        db.execSQL(createTripTable);
        db.execSQL(createTripDetailTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //function to be called to add new trip in addNewTrip.class
    public boolean addNewTrip(tripModel trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("TRIP_NAME", trip.getTripName());
        cv.put("START_DATE", trip.getStartDate());
        cv.put("END_DATE", trip.getEndDate());
        cv.put("IS_NOTIFIED",trip.getIs_notified());

        long insert = db.insert("TRIP", null, cv);

        if (insert == -1) {
            return false;
        } else return true;
    }

    //function to be called to add trip detail in TripDetail.class
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


    public boolean updateTripDetail(tripModel tripModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("CUR_DATE", tripModel.getCurrentDate());
        cv.put("START_TIME", tripModel.getStartTime());
        cv.put("END_TIME", tripModel.getEndTime());
        cv.put("DESTINATION", tripModel.getDestination());

        String tmp = new StringBuilder().append(tripModel.getIdforListDetail()).toString();
        String whereClause = "TRIP_ID = ?";
        String args [] = new String [] {tmp};


        //It is a good solution to use where argument as well
        long insert = db.update("TRIP_DETAIL",cv,whereClause,args);


        if (insert == -1) {
            return false;
        } else return true;
    }

    public boolean updateIsNotifiedAfterNotificationShowed(int ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("IS_NOTIFIED",1); // better to make constant value of NOTIFIED & NOTYETNOTIFIED

        String tmp = new StringBuilder().append(ID).toString();
        String whereClause = "ID = ?";
        String args [] = new String [] {tmp};

        long insert = db.update("TRIP",cv,whereClause,args);

        if(insert == -1){
            return false;
        }  else return  true;

    }

    public Boolean DeleteTrip(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        String tmp = new StringBuilder().append(id).toString();
        String whereClause = "ID = ?";
        String [] whereArgs = new String[] {tmp};

        long res = db.delete("TRIP",whereClause,whereArgs);
        long res1 = db.delete("TRIP_DETAIL",whereClause,whereArgs);

        if(res == -1 || res1 == -1)return false;
        else return true;
    }

    public void DeleteTripDetail(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        //  String QueryString = "DELETE FROM TRIP_DETAIL WHERE TRIP_ID = " + id;

        String tmp = new StringBuilder().append(id).toString();
        String whereClause = "TRIP_ID = ?";
        String [] whereArgs = new String[] {tmp};

        long res = db.delete("TRIP_DETAIL",whereClause,whereArgs);
        //db.execSQL(QueryString);
    }





    //GETTER AND CHECKING AN EXISTING INFORMATION IN DATABASE



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

    // to be called when user try to edit an existing trip detail in EditTripDetailWithAdditionalData.class
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

    public int getID()
    {
        int res=0;
        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT MAX(ID) FROM TRIP" ;
        Cursor cursor = db.rawQuery(QueryString,null,null);
        if(cursor.moveToFirst()) res = cursor.getInt(0);
        cursor.close();
        db.close();
        return res;
    }

    public String getTripName(int id)
    {
        String res="";
        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT TRIP_NAME FROM TRIP WHERE ID = " + id ;
        Cursor cursor = db.rawQuery(QueryString,null,null);
        if(cursor.moveToFirst()) res = cursor.getString(0);
        cursor.close();
        db.close();
        return res;
    }

    //for setting the alarm after device restart
    public List<tripModel> getTripWhereNotificatonHasNotBeenFired()
    {
        List <tripModel> lists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
       // String QueryString = "SELECT ID,TRIP_NAME,START_DATE,END_DATE FROM TRIP WHERE START_DATE >=" + "'" + curdate + "'" ;
        String QueryString = "SELECT ID,TRIP_NAME,START_DATE,END_DATE FROM TRIP WHERE IS_NOTIFIED = " + 0;
        Cursor cursor  = db.rawQuery(QueryString,null,null);
        if(cursor.moveToFirst()){
            do{
                int tmpID = cursor.getInt(0);
                String tmpName = cursor.getString(1);
                String tmpStartDate = cursor.getString(2);
                String tmpEndDate = cursor.getString(3);

                tripModel tmpTrip = new tripModel(tmpID,tmpName,tmpStartDate,tmpEndDate);
                lists.add(tmpTrip);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lists;
    }

    public boolean checkIfTimeOverlappingExistingTrip(String time,int id,String curdate)
    {
        //We have to consider ID of TRIPNAME as well !!! FIX
        //consider currentDate too
        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT * FROM TRIP_DETAIL WHERE ID = " + id + " AND CUR_DATE = '"+ curdate+ "' AND START_TIME < '" + time + "' AND END_TIME > '" + time +"'";
        Cursor cursor = db.rawQuery(QueryString,null);
        return cursor.getCount() > 0;
    }

    public boolean checkIfTimeIntervalExist(String startTime,String endTime,int id,String curdate)
    {
        //we consider The ID of Trip and startTime and Endtime on the currentDate
        //Return true if there is an existing trip on that exact time

        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT * FROM TRIP_DETAIL WHERE ID = "+ id + " AND CUR_DATE ='" + curdate+ "' AND START_TIME = '" + startTime + "' AND END_TIME = '"+ endTime +"'";
        Cursor cursor = db.rawQuery(QueryString,null);
        return cursor.getCount() > 0 ;
    }




}
