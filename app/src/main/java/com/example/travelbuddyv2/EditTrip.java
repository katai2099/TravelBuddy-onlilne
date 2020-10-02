package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTrip extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialogStartTime , timePickerDialogEndTime;
    EditText timePicker1 , timePicker2 , datePicker , DestinationField;
    Calendar calendar;
    Button btnSubmit;
    int tmpID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        Bundle extra = getIntent().getExtras();

        if(extra!=null)
        {
            tmpID = extra.getInt("TripIDfromTripDetail");
        }
        Toast.makeText(this,new StringBuilder().append(tmpID).toString(),Toast.LENGTH_SHORT).show();

        btnSubmit = findViewById(R.id.btnSubmitEdit);
        timePicker1 = findViewById(R.id.Timepicker1);
        timePicker1.setInputType(InputType.TYPE_NULL);
        timePicker2 = findViewById(R.id.Timepicker2);
        timePicker2.setInputType(InputType.TYPE_NULL);
        datePicker = findViewById(R.id.currentDatePicker);
        datePicker.setInputType(InputType.TYPE_NULL);
        DestinationField = findViewById(R.id.etDestination);

        timePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                timePickerDialogStartTime = new TimePickerDialog(EditTrip.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timePicker1.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).toString());
                    }
                },hour,min,true);
                timePickerDialogStartTime.show();
            }
        });

        timePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                timePickerDialogEndTime = new TimePickerDialog(EditTrip.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timePicker2.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).toString());
                    }
                },hour,min,true);
                timePickerDialogEndTime.show();
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                datePickerDialog = new DatePickerDialog(EditTrip.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datePicker.setText(new StringBuilder().append(year).append('-').append(month+1).append('-').append(dayOfMonth).toString());
                    }
                },year,month,date);
                datePickerDialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   databaseHelper = new DatabaseHelper(EditTrip.this);
                   String tmpDate = DatabaseHelper.changeDateFormat(datePicker.getText().toString());
                   tripModel tmp = new tripModel(tmpID,"Budapest",tmpDate,timePicker1.getText().toString(),timePicker2.getText().toString(),DestinationField.getText().toString());
                   databaseHelper.addTripDetail(tmp);
            }
        });


    }

    /*
    public static String changeDateFormat(String date)
    {
        String res ="";
        Date tmpDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tmpDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        res = simpleDateFormat.format(tmpDate);

        return res;
    } */

}