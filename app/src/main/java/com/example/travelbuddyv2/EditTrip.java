package com.example.travelbuddyv2;

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

        this.setTitle("Edit Trip");

        Bundle extra = getIntent().getExtras();

        if(extra!=null)
        {
            tmpID = extra.getInt("TripIDfromTripDetail");
        }
        //Toast.makeText(this, "ID of Trip is "+ String.valueOf(tmpID),Toast.LENGTH_SHORT).show();


        databaseHelper = new DatabaseHelper(EditTrip.this);
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

        timePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DestinationField.clearFocus();
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                if(Helper.isEditTextEmpty(timePicker1)) {

                    timePickerDialogStartTime = new TimePickerDialog(EditTrip.this, 3, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String tmp_time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
                            String end_res = Helper.changeInputTimeFormat(tmp_time);
                            timePicker1.setText(end_res);
                        }
                    }, hour, min, true);

                }else{
                    Date tmp = Helper.stringToTime(timePicker1.getText().toString());
                    calendar.setTime(tmp);
                    hour = calendar.get(Calendar.HOUR);
                    min = calendar.get(Calendar.MINUTE);

                    timePickerDialogStartTime = new TimePickerDialog(EditTrip.this, 3, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String tmp_time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
                            String end_res = Helper.changeInputTimeFormat(tmp_time);
                            timePicker1.setText(end_res);
                        }
                    }, hour, min, true);

                }
                timePickerDialogStartTime.show();
            }
        });

        timePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DestinationField.clearFocus();
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                if(Helper.isEditTextEmpty(timePicker2)) {

                    timePickerDialogEndTime = new TimePickerDialog(EditTrip.this, 3, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String tmp_time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
                            String end_res = Helper.changeInputTimeFormat(tmp_time);
                            timePicker2.setText(end_res);
                        }
                    }, hour, min, true);

                }
                else{
                    Date tmp = Helper.stringToTime(timePicker2.getText().toString());
                    calendar.setTime(tmp);
                    hour = calendar.get(Calendar.HOUR);
                    min = calendar.get(Calendar.MINUTE);

                    timePickerDialogEndTime = new TimePickerDialog(EditTrip.this, 3, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String tmp_time = new StringBuilder().append(hourOfDay).append(":").append(minute).toString();
                            String end_res = Helper.changeInputTimeFormat(tmp_time);
                            timePicker2.setText(end_res);
                        }
                    }, hour, min, true);

                }
                timePickerDialogEndTime.show();
            }
        });



        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DestinationField.clearFocus();
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                if(Helper.isEditTextEmpty(datePicker)) {
                    datePickerDialog = new DatePickerDialog(EditTrip.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String tmpDate = new StringBuilder().append(year).append('-').append(month + 1).append('-').append(dayOfMonth).toString();
                            String end_res = Helper.changeInputDateFormat(tmpDate);
                            datePicker.setText(end_res);
                        }
                    }, year, month, date);
                }else{
                    Date tmp = Helper.stringToDate(datePicker.getText().toString());
                    calendar.setTime(tmp);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    date = calendar.get(Calendar.DATE);

                    datePickerDialog = new DatePickerDialog(EditTrip.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String tmpDate = new StringBuilder().append(year).append('-').append(month + 1).append('-').append(dayOfMonth).toString();
                            String end_res = Helper.changeInputDateFormat(tmpDate);
                            datePicker.setText(end_res);
                        }
                    }, year, month, date);

                }


                String startDate = databaseHelper.getStartDateOfTrip(tmpID);
                String endDate = databaseHelper.getEndDateOfTrip(tmpID);
                Date tmpdate = Helper.stringToDate(startDate);
                Calendar tmpcal = Calendar.getInstance();
                tmpcal.setTime(tmpdate);
                datePickerDialog.getDatePicker().setMinDate(tmpcal.getTimeInMillis());
                Date tmpdate2 = Helper.stringToDate(endDate);
                Calendar tmpcal2 = Calendar.getInstance();
                tmpcal2.setTime(tmpdate2);
                datePickerDialog.getDatePicker().setMaxDate(tmpcal2.getTimeInMillis());


                datePickerDialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    DestinationField.clearFocus();
                   String tmpStartTime = timePicker1.getText().toString();
                   String tmpEndTime = timePicker2.getText().toString();
                   String tmpDate = Helper.changeInputDateFormat(datePicker.getText().toString());

                   if(Helper.isEditTextEmpty(timePicker1))
                   {
                       Toast.makeText(EditTrip.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                   }
                   else if(Helper.isEditTextEmpty(timePicker2))
                   {
                       Toast.makeText(EditTrip.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                   }
                   else if(Helper.isEditTextEmpty(DestinationField))
                   {
                       Toast.makeText(EditTrip.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                   }
                   else if(Helper.isEditTextEmpty(datePicker))
                   {
                       Toast.makeText(EditTrip.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                   }
                   else if(tmpStartTime.equals(tmpEndTime))
                   {
                       Toast.makeText(EditTrip.this,"Time cannot be the same",Toast.LENGTH_SHORT).show();
                   }
                   else if(!Helper.checkIfStartTimeBeforeEndTime(tmpStartTime,tmpEndTime))
                   {
                       Toast.makeText(EditTrip.this,"Start time before End time",Toast.LENGTH_SHORT).show();
                   }
                   else if(databaseHelper.checkIfTimeOverlappingExistingTrip(tmpStartTime,tmpID,tmpDate)||databaseHelper.checkIfTimeOverlappingExistingTrip(tmpEndTime,tmpID,tmpDate))
                   {
                       Toast.makeText(EditTrip.this,"Your trip is in a time interval of another trip",Toast.LENGTH_SHORT).show();
                   }
                   else if(databaseHelper.checkIfTimeIntervalExist(tmpStartTime,tmpEndTime,tmpID,tmpDate))
                   {
                       Toast.makeText(EditTrip.this,"There is an Existing trip at that exact time",Toast.LENGTH_SHORT).show();
                   }
                   else {
                       String tripName = databaseHelper.getTripName(tmpID);
                       tripModel tmp = new tripModel(tmpID, tripName, tmpDate, tmpStartTime, tmpEndTime, DestinationField.getText().toString());
                       databaseHelper.addTripDetail(tmp);
                       Toast.makeText(EditTrip.this,"Add Success",Toast.LENGTH_SHORT).show();
                       Helper.clearEdittext(timePicker1);
                       Helper.clearEdittext(timePicker2);
                       Helper.clearEdittext(datePicker);
                       Helper.clearEdittext(DestinationField);
                   }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }






}