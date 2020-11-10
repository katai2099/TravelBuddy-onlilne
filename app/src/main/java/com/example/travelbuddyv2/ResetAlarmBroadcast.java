package com.example.travelbuddyv2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class ResetAlarmBroadcast extends BroadcastReceiver {

    private static final String BOOT_COMPLETED =
            "android.intent.action.BOOT_COMPLETED";
    private static final String tag = "ResetAlarmBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        String ja = intent.getAction();
        System.out.println(ja);
        if(ja.equals(BOOT_COMPLETED)){
            Log.d(tag,"I trigger reset alarm from device restart");
            Date d = new Date();
            String time = Helper.dateToString(d);
            resetAlarmWhenDeviceReboot(context,time);
            System.out.println("It does work tho");
        }
    }



    public void resetAlarmWhenDeviceReboot(Context context,String time){

        DatabaseHelper db = new DatabaseHelper(context);
        List<tripModel> trips;
        trips = db.getTripWhereStartDateIsGreaterThanCurrentDate(time);

        for(int i=0;i<trips.size();i++){
            Intent intent = new Intent(context,ReminderBroadcast.class);
            Bundle extras = new Bundle();
            extras.putString("Extra_tripName",trips.get(i).getTripName());
            extras.putInt("Extra_tripID",trips.get(i).getId());
            intent.putExtras(extras);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,trips.get(i).getId(),intent,0);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000*30,pendingIntent);
            }

        }

    }
}
