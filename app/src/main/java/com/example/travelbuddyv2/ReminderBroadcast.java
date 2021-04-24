package com.example.travelbuddyv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.travelbuddyv2.retrofit.Client;
import com.example.travelbuddyv2.retrofit.MyResponse;
import com.example.travelbuddyv2.retrofit.NotificationAPI;
import com.example.travelbuddyv2.retrofit.NotificationData;
import com.example.travelbuddyv2.retrofit.PushNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Date;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReminderBroadcast extends BroadcastReceiver {
    private static final String tag="ReminderBroadcast";
    private NotificationAPI notificationAPI;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationAPI = Client.getClient("https://fcm.googleapis.com/").create(NotificationAPI.class);
        Log.i(tag,"I trigger set alarm");
        Bundle bundle = intent.getExtras();
        String tripName = bundle.getString("tripName");
        String tripStringID = bundle.getString("tripStringID");
        String tripStartDate = bundle.getString("tripStartDate");
        String toNotified="";
        Date d = new Date();
        Date startDate = Helper.stringToDate(tripStartDate);
        if(d.before(startDate))
            toNotified = "You have " + tripName + " Trip Tomorrow";
        else if(startDate.before(d))
            toNotified = "You have " + tripName + " Trip Today";
        final String endResult = toNotified;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("upcomingTripNotification")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(tripStringID);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot mobileToken: dataSnapshot.getChildren()){
                        sendNotification(endResult,mobileToken.getKey());
                    }
                }
            }
        });
        DatabaseHelper db = new DatabaseHelper(context);
        db.deletePendingNotification(tripStringID);
    }

    private void sendNotification(String body,String recipient){
        NotificationData data = new NotificationData("Trip Upcoming!",body);
        PushNotification pushNotification = new PushNotification(data,recipient);
        notificationAPI.sendNotification(pushNotification).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.code()==200){
                    assert response.body() != null;
                    if(response.body().success!=1){
                       Log.d(tag,"FAIL TO SEND REQUEST");
                    }
                }
                Log.d(tag, String.valueOf(response.code()));
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }


}
