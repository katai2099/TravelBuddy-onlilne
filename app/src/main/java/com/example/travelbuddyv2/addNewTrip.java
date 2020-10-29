package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class addNewTrip extends AppCompatActivity {

    EditText tripName , startDate , endDate;
    Button btnSave;
    DatePickerDialog datePickerDialogStartDate,datePickerDialogEndDate;
    Calendar calendar ;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        tripName = findViewById(R.id.etTripName);

        startDate = findViewById(R.id.etDepartDate);
        startDate.setInputType(InputType.TYPE_NULL);

        endDate = findViewById(R.id.etArrivalDate);
        endDate.setInputType(InputType.TYPE_NULL);

        btnSave = findViewById(R.id.btnSaveTrip);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmpStartDate = Helper.changeInputDateFormat(startDate.getText().toString());
                String tmpEndDate = Helper.changeInputDateFormat(endDate.getText().toString());

                if(Helper.isEditTextEmpty(tripName))
                {
                    Toast.makeText(addNewTrip.this,"Please fill all detail",Toast.LENGTH_SHORT).show();
                }
                else if(Helper.isEditTextEmpty(startDate))
                {
                    Toast.makeText(addNewTrip.this,"Please fill all detail",Toast.LENGTH_SHORT).show();
                }
                else if(Helper.isEditTextEmpty(endDate))
                {
                    Toast.makeText(addNewTrip.this,"Please fill all detail",Toast.LENGTH_SHORT).show();
                }
                else if(!Helper.checkIfStartDateBeforeEndDate(tmpStartDate,tmpEndDate) && !Helper.checkIfStartDateSameDateAsEndDate(tmpStartDate,tmpEndDate))
                {
                    Toast.makeText(addNewTrip.this,"Start Date before EndDate",Toast.LENGTH_SHORT).show();
                }
                else {
                    tripModel tmp = new tripModel(tripName.getText().toString(), tmpStartDate, tmpEndDate);
                    //  System.out.println(tmp.toString());
                    databaseHelper = new DatabaseHelper(addNewTrip.this);
                    databaseHelper.addNewTrip(tmp);
                    // finish();
                    Intent i = new Intent(addNewTrip.this, myTrip.class);
                    startActivity(i);
                }
            }
        });

        tripName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                datePickerDialogStartDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tmp = new StringBuilder().append(year).append('-').append(month+1).append('-').append(dayOfMonth).toString();
                        String res = Helper.changeInputDateFormat(tmp);
                        startDate.setText(res);
                    }
                },year,month,date);

                Calendar tmpcal = Calendar.getInstance();
                datePickerDialogStartDate.getDatePicker().setMinDate(tmpcal.getTimeInMillis());

                datePickerDialogStartDate.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                datePickerDialogEndDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tmp = new StringBuilder().append(year).append('-').append(month+1).append('-').append(dayOfMonth).toString();
                        String res = Helper.changeInputDateFormat(tmp);
                        endDate.setText(res);
                    }
                },year,month,date);

                if(!Helper.isEditTextEmpty(startDate))
                {
                    String tmpStartDate = Helper.changeInputDateFormat(startDate.getText().toString());
                    Date tmpdate = Helper.stringToDate(tmpStartDate);
                    int yeartmp = tmpdate.getYear()+1900;
                    System.out.println(yeartmp);
                    int monthtmp = tmpdate.getMonth();
                    int daytmp = tmpdate.getDate();
                    Calendar tmpcal = Calendar.getInstance();
                    tmpcal.set(yeartmp,monthtmp,daytmp,0,0,0);
                    datePickerDialogEndDate.getDatePicker().setMinDate(tmpcal.getTimeInMillis());
                }
                else{

                    Calendar tmpcal = Calendar.getInstance();
                    datePickerDialogEndDate.getDatePicker().setMinDate(tmpcal.getTimeInMillis());

                }
                datePickerDialogEndDate.show();

            }
        });




    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}