package com.example.travelbuddyv2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.admin.SystemUpdateInfo;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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

        tripName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }

            }
        });

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
                    tripModel tmpTripModel = new tripModel(tripName.getText().toString(), tmpStartDate, tmpEndDate);

                    databaseHelper = new DatabaseHelper(addNewTrip.this);
                    databaseHelper.addNewTrip(tmpTripModel);
                  //   Calendar cal = Calendar.getInstance();
                   //  cal.set(Calendar.HOUR,0);
                   //  cal.set(Calendar.MINUTE,0);
                   //  cal.set(Calendar.SECOND,0);
                  //   Date tmpDateNow = cal.getTime();
                  //  System.out.println(tmpDateNow);
                   // System.out.println(tmp.getStartDate());
                   //  long res = Helper.calculateDifferenceTimeInMilli(tmpDateNow,tmp.getStartDate());
                  //   Toast.makeText(addNewTrip.this,new StringBuilder().append(res).toString(),Toast.LENGTH_SHORT).show();

                    int ID = databaseHelper.getID();
                    Toast.makeText(addNewTrip.this, String.valueOf(ID),Toast.LENGTH_SHORT).show();
                    tmpTripModel.setId(ID);
                    setNotificationTime(60*1000,tmpTripModel);
                     Intent i = new Intent(addNewTrip.this, myTrip.class);
                    startActivity(i);
                }
            }
        });



        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripName.clearFocus();
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                datePickerDialogStartDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tmp = String.valueOf(year) + '-' + (month + 1) + '-' + dayOfMonth;
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
                tripName.clearFocus();
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
        Toast.makeText(this,"I hide keyboard",Toast.LENGTH_SHORT).show();
    }




    public void setNotificationTime(long milli, tripModel passingData) // milli needed just for debug
    {
        Intent intent = new Intent(addNewTrip.this,ReminderBroadcast.class);
        Bundle extras = new Bundle();
        extras.putString("Extra_tripName",passingData.getTripName());
        extras.putInt("Extra_tripID",passingData.getId());
        intent.putExtras(extras);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(addNewTrip.this,passingData.getId(),intent,0);
        long timeToFireAnAlarm = Helper.getStartDateInMilli(passingData.getStartDate());
        Date tmp = new Date(timeToFireAnAlarm);
        Log.d("ADD NEW TRIP",tmp.toString());
        //Toast.makeText(addNewTrip.this,tmp.toString(),Toast.LENGTH_SHORT).show();
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        long timeAtButtonClicked = System.currentTimeMillis();
        //alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClicked+milli,pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           // alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timeAtButtonClicked+milli,pendingIntent); //for debugging purpose
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (timeToFireAnAlarm-System.currentTimeMillis()),pendingIntent);
        } else{
          //  alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClicked+milli,pendingIntent);// for debugging purpose
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (timeToFireAnAlarm-System.currentTimeMillis()),pendingIntent);
        }
        Date alarmFiredDate = new Date(System.currentTimeMillis() + (timeToFireAnAlarm-System.currentTimeMillis()));
        Log.d("ADD NEW TRIP", "Time alarm will fired: " + alarmFiredDate.toString());
        long whatever = timeToFireAnAlarm-System.currentTimeMillis();
        Log.d("ADD NEW TRIP", "Time in miili "+whatever);
        long reminder = Helper.milliToHour(timeToFireAnAlarm-System.currentTimeMillis());
        Log.d("ADD NEW TRIP", "Time in Hour: "+reminder);
        Toast.makeText(this,"Send Notification in " + reminder + " Hour",Toast.LENGTH_SHORT).show();
    }


}