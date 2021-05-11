package com.example.travelbuddyv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.travelbuddyv2.model.tripModel;

import java.util.ArrayList;
import java.util.List;

//offline database is used to store pending notification (trip notification that has not been fired yet)
public class DatabaseHelper extends SQLiteOpenHelper {
    private final String tripName = "TRIP_NAME";
    private final String tripStringID = "TRIP_STRING_ID";
    private final String tripStareDate = "START_DATE";
    private final String tripNotificationTable = "TRIP_NOTIFICATION" ;
    public DatabaseHelper(@Nullable Context context) {
        super(context, "Trip.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUpcomingTripNotification = "CREATE TABLE TRIP_NOTIFICATION (ID INTEGER PRIMARY KEY AUTOINCREMENT, TRIP_NAME TEXT,TRIP_STRING_ID NAME,START_DATE TIME)";
        db.execSQL(createUpcomingTripNotification);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addNewPendingNotification(String tripName,String tripStringID,String startDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(this.tripName,tripName);
        cv.put(this.tripStringID,tripStringID);
        cv.put(this.tripStareDate,startDate);
        long insert = db.insert(tripNotificationTable,null,cv);
        return insert != -1;
    }


    public List<tripModel> getPendingNotificationList() {
        List<tripModel> returnlist = new ArrayList<>();
        String queryString = "SELECT * FROM " + tripNotificationTable;
        //get data from database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null, null);
        if (cursor.moveToFirst()) {
            do {
                String tmpTripName = cursor.getString(1);
                String tmpTripStringID = cursor.getString(2);
                String tmpStartDate = cursor.getString(3);
                tripModel tmp = new tripModel(tmpTripName, tmpTripStringID, tmpStartDate);
                returnlist.add(tmp);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnlist;
    }

    public boolean deletePendingNotification(String tripStringID){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = this.tripStringID + " = ?";
        String [] whereArgs = new String[] {tripStringID};
        long res = db.delete(tripNotificationTable,whereClause,whereArgs);
        return res != -1;
    }

    public boolean isAllHasBeenNotified()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String QueryString = "SELECT * FROM " + tripNotificationTable ;
        Cursor cursor  = db.rawQuery(QueryString,null,null);
        int count = cursor.getCount();
        cursor.close();
        return count == 0;
    }

    public boolean deleteAllPendingNotification(){
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(tripNotificationTable,null,null);
        return res != -1;
    }





}
