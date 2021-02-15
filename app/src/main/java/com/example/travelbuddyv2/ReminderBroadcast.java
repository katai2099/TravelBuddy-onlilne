package com.example.travelbuddyv2;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;


public class ReminderBroadcast extends BroadcastReceiver {

    private static final String tag="ReminderBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseHelper db = new DatabaseHelper(context);

        Log.i(tag,"I trigger set alarm");
        Bundle bundle = intent.getExtras();

        String extraTripName = bundle.getString("Extra_tripName");
        int k = bundle.getInt("Extra_tripID");

        Intent [] intents = new Intent[2];
        intents[0] = new Intent(context, MainActivityOld.class);
        intents[0].setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intents[1] = new Intent(context,tripDetail.class);
        intents[1].setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intents[1].putExtra("TripID",k);
       // intents[1].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      //  Intent resultIntent = new Intent(context,myTrip.class);
       // TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
      //  stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent to the top of the stack
        //stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Gets a PendingIntent containing the entire back stack
      //  PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, 0);

      //  PendingIntent pendingIntent = PendingIntent.getActivities(context,0,intents,0);

        PendingIntent pendingIntent = PendingIntent.getActivities(context,k,intents,0);

  //      PendingIntent DeletePendingIntent = PendingIntent.getActivities(context,k,intents,pendingIntent.FLAG_UPDATE_CURRENT);

//        DeletePendingIntent.cancel();
        String toNotified="";
        Date d = new Date();
        Date startDate = Helper.stringToDate(db.getStartDateOfTrip(k));
        Date endDate = Helper.stringToDate(db.getEndDateOfTrip(k));
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.HOUR,23);
        Date ModifiedEndDate = cal.getTime();
        if(startDate.before(d) && ModifiedEndDate.before(d))
            toNotified = "You missed your " + extraTripName + " Trip !!";
        else if(d.before(startDate))
            toNotified = "You have " + extraTripName + " Trip Tomorrow";
        else if(startDate.before(d))
            toNotified = "You have " + extraTripName + " Trip Today";


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"NotifyTrip")
                .setSmallIcon(R.drawable.weirdicon)
                .setContentTitle("Trip Reminder")
                .setContentText(toNotified)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(k,builder.build());


        db.updateIsNotifiedAfterNotificationShowed(k);

    }




}
