package com.example.travelbuddyv2;


import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivityOld extends AppCompatActivity {

    Button newTrip,myTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        createNotificationChannel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmHandler alarm = new alarmHandler(this);

       // String time = Helper.changeInputDateFormat("2020-12-1");
      //  System.out.println(time);

      //  String time="23:12";
    //    Date date = Helper.stringToTime(time);
     //   Toast.makeText(MainActivity.this,date.toString(),Toast.LENGTH_SHORT).show();
        newTrip = findViewById(R.id.btnAddNewTrip);
        myTrip = findViewById(R.id.btnMyTrip);
        //Helper.checkIfStartTimeBeforeEndTime("23:50","23:40");

        newTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityOld.this,addNewTrip.class);
                startActivity(i);
            }
        });

        myTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivityOld.this,myTrip.class);
                startActivity(i);
            }
        });

    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notify Trip";
            String description = "Channel for Trip Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NotifyTrip",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        //    Toast.makeText(MainActivity.this,"Function Trigger!",Toast.LENGTH_SHORT).show();
        }
    }

}