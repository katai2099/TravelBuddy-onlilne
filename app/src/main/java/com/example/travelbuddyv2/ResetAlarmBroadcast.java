package com.example.travelbuddyv2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.travelbuddyv2.model.tripModel;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class ResetAlarmBroadcast extends BroadcastReceiver {

    private static final String BOOT_COMPLETED =
            "android.intent.action.BOOT_COMPLETED";
    private static final String UPDATE_COMPLETED = "android.intent.action.MY_PACKAGE_REPLACED";
    private static final String tag = "ResetAlarmBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentCode = intent.getAction();
        if(intentCode!=null){
        if(intentCode.equals(BOOT_COMPLETED) || intentCode.equals(UPDATE_COMPLETED)){
            Log.d(tag,"I trigger reset alarm from device restart");
            resetAlarmWhenDeviceReboot(context);
        }
        }
        else{
            Toast.makeText(context,"Just to try",Toast.LENGTH_SHORT).show();
            Log.d(tag,"I trigger reset daily alarm by alarmHandler");
            resetAlarmWhenDeviceReboot(context);
        }
    }



    public void resetAlarmWhenDeviceReboot(Context context){
        DatabaseHelper db = new DatabaseHelper(context);
        List<tripModel> trips;
        trips = db.getPendingNotificationList();
        for(int i=0;i<trips.size();i++){
            Intent intent = new Intent(context,ReminderBroadcast.class);
            Bundle extras = new Bundle();
            extras.putString("tripName",trips.get(i).getTripName());
            extras.putString("tripStringID",trips.get(i).getStringID());
            extras.putString("tripStartDate",trips.get(i).getStartDate());
            intent.putExtras(extras);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,Helper.tripStringIDToInt(trips.get(i).getStringID()),intent,0);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            long timeToFireAnAlarm = Helper.getStartDateInMilli(trips.get(i).getStartDate());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timeToFireAnAlarm,pendingIntent);
              //  alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000*30,pendingIntent); // Debuggin purpose
            }
        }
    }

}
