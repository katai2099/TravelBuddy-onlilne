package com.example.travelbuddyv2;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

     //   Bundle bundle = intent.getExtras();
     //   int k = bundle.getInt("ALARM");



        //Intent [] intents = new Intent[2];
       // intents[0] = new Intent(context,MainActivity.class);
       // intents[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // intents[1] = new Intent(context,myTrip.class);
       // intents[1].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent resultIntent = new Intent(context,myTrip.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
      //  stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Gets a PendingIntent containing the entire back stack
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                0);

      //  PendingIntent pendingIntent = PendingIntent.getActivities(context,0,intents,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"NotifyTrip")
                .setSmallIcon(R.drawable.weirdicon)
                .setContentTitle("Trip Reminder")
                .setContentText("First Notification of TravelBuddy App ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1,builder.build());

    }
}
