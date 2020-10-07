package com.example.travelbuddyv2;

import androidx.annotation.ContentView;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class EditTripDetailWithAdditionalData extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialogStartTime , timePickerDialogEndTime;
    EditText timePicker1 , timePicker2 , datePicker , DestinationField;
    Calendar calendar;
    Button btnSubmit;
    int tmpID;
    tripModel tmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_trip_detail_with_additional_data);
        setContentView(R.layout.activity_edit_trip);
        Bundle extra = getIntent().getExtras();

        /* Not a good solution to hide keyboard when click on layout
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.k);
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });

         */

        if(extra!=null)
        {
            tmpID = extra.getInt("extra");
        }

        databaseHelper = new DatabaseHelper(this);
        Toast.makeText(this,new StringBuilder().append(tmpID).toString(),Toast.LENGTH_SHORT).show();


        btnSubmit = findViewById(R.id.btnSubmitEdit);
        timePicker1 = findViewById(R.id.Timepicker1);
        timePicker1.setInputType(InputType.TYPE_NULL);
        timePicker2 = findViewById(R.id.Timepicker2);
        timePicker2.setInputType(InputType.TYPE_NULL);
        datePicker = findViewById(R.id.currentDatePicker);
        datePicker.setInputType(InputType.TYPE_NULL);
        DestinationField = findViewById(R.id.etDestination);

        DestinationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideKeyboard(v);
                }
            }
        });



        tmp = databaseHelper.getEditDetail(tmpID);
        timePicker1.setText(tmp.getStartTime());
        timePicker2.setText(tmp.getEndTime());
        datePicker.setText(tmp.getCurrentDate());
        DestinationField.setText(tmp.getDestination());


        timePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                timePickerDialogStartTime = new TimePickerDialog(EditTripDetailWithAdditionalData.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String tmp_time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
                        String end_res = Helper.changeInputTimeFormat(tmp_time);
                        timePicker1.setText(end_res);
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

                timePickerDialogEndTime = new TimePickerDialog(EditTripDetailWithAdditionalData.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String tmp_time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
                        String end_res = Helper.changeInputTimeFormat(tmp_time);
                        timePicker2.setText(end_res);
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

                datePickerDialog = new DatePickerDialog(EditTripDetailWithAdditionalData.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        datePicker.setText(new StringBuilder().append(year).append('-').append(month+1).append('-').append(dayOfMonth).toString());
                    }
                },year,month,date);


                String startDate = databaseHelper.getStartDateOfTrip(tmp.getId());
                String endDate = databaseHelper.getEndDateOfTrip(tmp.getId());
                Date tmpdate = Helper.stringToDate(startDate);
                int yeartmp = tmpdate.getYear()+1900;
                System.out.println(yeartmp);
                int monthtmp = tmpdate.getMonth();
                int daytmp = tmpdate.getDate();
                Calendar tmpcal = Calendar.getInstance();
                tmpcal.set(yeartmp,monthtmp,daytmp,0,0,0);
                datePickerDialog.getDatePicker().setMinDate(tmpcal.getTimeInMillis());
                Date tmpdate2 = Helper.stringToDate(endDate);
                int yeartmp2 = tmpdate2.getYear()+1900;
                int monthtmp2 = tmpdate2.getMonth();
                int daytmp2 = tmpdate2.getDate();
                Calendar tmpcal2 = Calendar.getInstance();
                tmpcal2.set(yeartmp2,monthtmp2,daytmp2,0,0,0);
                datePickerDialog.getDatePicker().setMaxDate(tmpcal2.getTimeInMillis());


                datePickerDialog.show();
            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmp.setDestination(DestinationField.getText().toString());
                tmp.setCurrentDate(datePicker.getText().toString());
                tmp.setStartTime(timePicker1.getText().toString());
                tmp.setEndTime(timePicker2.getText().toString());
                databaseHelper.updateTripDetail(tmp);
            }
        });





    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}