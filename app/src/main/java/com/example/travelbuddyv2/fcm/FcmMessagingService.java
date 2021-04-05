package com.example.travelbuddyv2.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FcmMessagingService extends FirebaseMessagingService {

    private final String tag = "FCM_MESSAGING_SERVICE";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        String title = "";
        String message = "";


        title = remoteMessage.getData().get("title");
        message = remoteMessage.getData().get("body");
        Log.d(tag,title==null ? "null":title);
        Log.d(tag,message==null ? "null":title);

        if(TextUtils.isEmpty(title)){
            title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
            message = remoteMessage.getNotification().getBody();
        }

        Intent intent = new Intent(this, Main2Activity.class);
        assert title != null;
        if(title.trim().equals("New Trip Invitation Request")) {
            intent.putExtra("changeToNotificationFragment", true);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"NotifyTripInvitationRequest")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDeleteIntent(pendingIntent)
                ;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0,builder.build());

    }



}
