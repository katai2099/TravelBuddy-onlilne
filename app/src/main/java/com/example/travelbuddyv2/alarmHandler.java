package com.example.travelbuddyv2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class alarmHandler {
    Context context;
    static private String tag = "ALARMHANDLER";
    alarmHandler(Context context){
        this.context = context;
        setRepeatingAlarm();
    }

    //repeat the alarm when application is launched (in case alarm is killed by system)
    public void setRepeatingAlarm(){
        DatabaseHelper db = new DatabaseHelper(context);
        Intent intent = new Intent(context,ResetAlarmBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(alarmManager!=null && !db.isAllHasBeenNotified()){
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
            Log.d(tag,"Set because some of them yet need to be notified");
        }
        else
        {
            cancelRepeatingAlarm();
            Log.d(tag,"Cancel because all has been notified");
        }
    }

    public void cancelRepeatingAlarm(){
        Intent intent = new Intent(context,ResetAlarmBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(alarmManager!=null){
           alarmManager.cancel(pendingIntent);
        }
    }

}
