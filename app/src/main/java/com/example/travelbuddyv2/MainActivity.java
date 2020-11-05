package com.example.travelbuddyv2;


import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    Button newTrip,myTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        createNotificationChannel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  String time="23:12";
    //    Date date = Helper.stringToTime(time);
     //   Toast.makeText(MainActivity.this,date.toString(),Toast.LENGTH_SHORT).show();
        newTrip = findViewById(R.id.btnAddNewTrip);
        myTrip = findViewById(R.id.btnMyTrip);
        //Helper.checkIfStartTimeBeforeEndTime("23:50","23:40");

        newTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,addNewTrip.class);
                startActivity(i);
            }
        });

        myTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,myTrip.class);
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
            Toast.makeText(MainActivity.this,"Function Trigger!",Toast.LENGTH_SHORT).show();
        }
    }

}