package com.example.travelbuddyv2;


import androidx.appcompat.app.AppCompatActivity;

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
}