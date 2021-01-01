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
    int tmpTripID,tmpTripDetailID;
    tripModel tmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edit_trip_detail_with_additional_data);
        setContentView(R.layout.activity_edit_trip);
        Bundle extra = getIntent().getExtras();

        if(extra!=null)
        {
            tmpTripID = extra.getInt("extra_trip_ID");
            tmpTripDetailID = extra.getInt("extra_tripDetail_ID");
        }

        databaseHelper = new DatabaseHelper(this);
        Toast.makeText(this,"ID of TRIP is "+String.valueOf(tmpTripID),Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"ID of TRIP_DETAIL is "+String.valueOf(tmpTripDetailID),Toast.LENGTH_SHORT).show();


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



        tmp = databaseHelper.getEditDetail(tmpTripDetailID);
        timePicker1.setText(tmp.getStartTime());
        timePicker2.setText(tmp.getEndTime());
        datePicker.setText(tmp.getCurrentDate());
        DestinationField.setText(tmp.getDestination());


        timePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DestinationField.clearFocus();
                calendar = Calendar.getInstance();
                Date t1 = Helper.stringToTime(timePicker1.getText().toString());
                calendar.setTime(t1);
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                timePickerDialogStartTime = new TimePickerDialog(EditTripDetailWithAdditionalData.this,3, new TimePickerDialog.OnTimeSetListener() {
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
                DestinationField.clearFocus();
                calendar = Calendar.getInstance();
                Date t2 = Helper.stringToTime(timePicker2.getText().toString());
                calendar.setTime(t2);
                int hour = calendar.get(Calendar.HOUR);
                int min = calendar.get(Calendar.MINUTE);

                timePickerDialogEndTime = new TimePickerDialog(EditTripDetailWithAdditionalData.this,3, new TimePickerDialog.OnTimeSetListener() {
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
                DestinationField.clearFocus();
                calendar = Calendar.getInstance();
                Date show = Helper.stringToDate(datePicker.getText().toString());
                calendar.setTime(show);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                datePickerDialog = new DatePickerDialog(EditTripDetailWithAdditionalData.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tmp_date = new StringBuilder().append(year).append('-').append(month+1).append('-').append(dayOfMonth).toString();
                        String end_res = Helper.changeInputDateFormat(tmp_date);
                        datePicker.setText(end_res);
                    }
                },year,month,date);


                String startDate = databaseHelper.getStartDateOfTrip(tmp.getId());
                String endDate = databaseHelper.getEndDateOfTrip(tmp.getId());
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

                tmp.setDestination(DestinationField.getText().toString());
                tmp.setCurrentDate(datePicker.getText().toString());
                tmp.setStartTime(timePicker1.getText().toString());
                tmp.setEndTime(timePicker2.getText().toString());

                String tmpStartTime = timePicker1.getText().toString();
                String tmpEndTime = timePicker2.getText().toString();
                String tmpDate = Helper.changeInputDateFormat(datePicker.getText().toString());

                if(Helper.isEditTextEmpty(DestinationField))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                }
                else if(Helper.isEditTextEmpty(datePicker))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                }
                else if(Helper.isEditTextEmpty(timePicker1))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                }
                else if(Helper.isEditTextEmpty(timePicker2))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Please fill all information",Toast.LENGTH_SHORT).show();
                }
                else if(tmpStartTime.equals(tmpEndTime))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Time cannot be the same",Toast.LENGTH_SHORT).show();
                }
                else if(!Helper.checkIfStartTimeBeforeEndTime(tmpStartTime,tmpEndTime))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Start time before End time",Toast.LENGTH_SHORT).show();
                }
                else if(databaseHelper.checkIfTimeOverlappingExistingTripInEditWithAdditionalData(tmpStartTime,tmpTripID,tmpTripDetailID,tmpDate)||
                        databaseHelper.checkIfTimeOverlappingExistingTripInEditWithAdditionalData(tmpEndTime,tmpTripID,tmpTripDetailID,tmpDate))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Your trip is in a time interval of another trip",Toast.LENGTH_SHORT).show();
                }
                else if(databaseHelper.checkIfTimeIntervalExistInEditWithAdditionalData(tmp.getStartTime(),tmp.getEndTime(),tmpTripID,tmpTripDetailID,tmp.getCurrentDate()))
                {
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"There is an Existing trip at that exact time",Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseHelper.updateTripDetail(tmp);
                    DestinationField.getText().clear();
                    datePicker.getText().clear();
                    timePicker1.getText().clear();
                    timePicker2.getText().clear();
                    Toast.makeText(EditTripDetailWithAdditionalData.this,"Edit success",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}