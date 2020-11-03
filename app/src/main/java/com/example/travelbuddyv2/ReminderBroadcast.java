package com.example.travelbuddyv2;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Iterator;
import java.util.Set;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        Bundle bundle = intent.getExtras();


        String extraTripName = bundle.getString("Extra_tripName");
        int k = bundle.getInt("Extra_tripID");

        Intent [] intents = new Intent[2];
        intents[0] = new Intent(context,MainActivity.class);
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

        PendingIntent pendingIntent = PendingIntent.getActivities(context,k,intents,PendingIntent.FLAG_CANCEL_CURRENT);

  //      PendingIntent DeletePendingIntent = PendingIntent.getActivities(context,k,intents,pendingIntent.FLAG_UPDATE_CURRENT);

//        DeletePendingIntent.cancel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"NotifyTrip")
                .setSmallIcon(R.drawable.weirdicon)
                .setContentTitle("Trip Reminder")
                .setContentText("You have " + extraTripName + " Trip Tomorrow!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(k,builder.build());
    }
}
