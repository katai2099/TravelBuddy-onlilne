package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class addNewTrip extends AppCompatActivity {

    EditText tripName , startDate , endDate, setAlarmTime;
    Button btnSave;
    DatePickerDialog datePickerDialogStartDate,datePickerDialogEndDate;
    Calendar calendar ;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        this.setTitle("New Trip");
        tripName = findViewById(R.id.etTripName);

       // setAlarmTime = findViewById(R.id.etSetAlarmTime);

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

        /*
        setAlarmTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        }); */

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

                    FirebaseDatabase.getInstance().getReference()
                            .child("Trips")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Trip id " + 2)
                            .setValue(tmpTripModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(addNewTrip.this,"Adding complete",Toast.LENGTH_SHORT).show();
                                }
                            });

                   // databaseHelper = new DatabaseHelper(addNewTrip.this);
                   // databaseHelper.addNewTrip(tmpTripModel);
                   // int Time = Integer.parseInt(setAlarmTime.getText().toString());
                   // int ID = databaseHelper.getID();
                   // Toast.makeText(addNewTrip.this, String.valueOf(ID),Toast.LENGTH_SHORT).show();
                   // tmpTripModel.setId(ID);
                   // setNotificationTime(0,tmpTripModel);
                   //  Intent i = new Intent(addNewTrip.this, myTrip.class);
                   // startActivity(i);
                   // finish();
                }
            }
        });



        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripName.clearFocus();
             //   setAlarmTime.clearFocus();
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                if(Helper.isEditTextEmpty(startDate))
                {
                datePickerDialogStartDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String tmp = String.valueOf(year) + '-' + (month + 1) + '-' + dayOfMonth;
                        String res = Helper.changeInputDateFormat(tmp);
                        startDate.setText(res);
                    }
                },year,month,date);
                }
                else{
                    Date d = Helper.stringToDate(startDate.getText().toString());
                    calendar.setTime(d);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    date = calendar.get(Calendar.DATE);
                    datePickerDialogStartDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String tmp = String.valueOf(year) + '-' + (month + 1) + '-' + dayOfMonth;
                            String res = Helper.changeInputDateFormat(tmp);
                            startDate.setText(res);
                        }
                    },year,month,date);

                }

                Calendar tmpcal = Calendar.getInstance();
                // comment here is meant for debugging purpose (notification) , uncomment to deploy the app
                datePickerDialogStartDate.getDatePicker().setMinDate(tmpcal.getTimeInMillis());

                datePickerDialogStartDate.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripName.clearFocus();
             //   setAlarmTime.clearFocus();
                calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                if(!Helper.isEditTextEmpty(startDate) && Helper.isEditTextEmpty(endDate)){

                    Date d = Helper.stringToDate(startDate.getText().toString());
                    calendar.setTime(d);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    date = calendar.get(Calendar.DATE);

                    datePickerDialogEndDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String tmp = new StringBuilder().append(year).append('-').append(month + 1).append('-').append(dayOfMonth).toString();
                            String res = Helper.changeInputDateFormat(tmp);
                            endDate.setText(res);
                        }
                    }, year, month, date);

                }
                else if(Helper.isEditTextEmpty(endDate)) {

                    datePickerDialogEndDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String tmp = new StringBuilder().append(year).append('-').append(month + 1).append('-').append(dayOfMonth).toString();
                            String res = Helper.changeInputDateFormat(tmp);
                            endDate.setText(res);
                        }
                    }, year, month, date);

                }else{

                    Date d = Helper.stringToDate(endDate.getText().toString());
                    calendar.setTime(d);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    date = calendar.get(Calendar.DATE);
                    datePickerDialogEndDate = new DatePickerDialog(addNewTrip.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String tmp = new StringBuilder().append(year).append('-').append(month + 1).append('-').append(dayOfMonth).toString();
                            String res = Helper.changeInputDateFormat(tmp);
                            endDate.setText(res);
                        }
                    }, year, month, date);

                }

                    Calendar tmpcal = Calendar.getInstance();
                // comment here is meant for debugging purpose (notification) , uncomment to deploy the app
                    datePickerDialogEndDate.getDatePicker().setMinDate(tmpcal.getTimeInMillis());

                datePickerDialogEndDate.show();

            }
        });




    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //Toast.makeText(this,"I hide keyboard",Toast.LENGTH_SHORT).show();
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
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timeToFireAnAlarm,pendingIntent);
          // alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+milli*1000,pendingIntent); //for debugging purpose
           // Toast.makeText(getApplicationContext(),"I AM USING NEW VERSION ALARM",Toast.LENGTH_SHORT).show();
        } else{
            alarmManager.set(AlarmManager.RTC_WAKEUP,timeToFireAnAlarm,pendingIntent);
          // alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+milli*1000,pendingIntent);// for debugging purpose
           // Toast.makeText(getApplicationContext(),"I AM USING OLD VERSION ALARM",Toast.LENGTH_SHORT).show();
        }

        /*Date alarmFiredDate = new Date(System.currentTimeMillis() + milli*1000);
        Log.d("ADD NEW TRIP", "Time alarm will fired: " + alarmFiredDate.toString());
        long whatever = timeToFireAnAlarm-System.currentTimeMillis();
        Log.d("ADD NEW TRIP", "Time in miili "+whatever);
        long reminder = Helper.milliToHour(timeToFireAnAlarm-System.currentTimeMillis());
        Log.d("ADD NEW TRIP", "Time in Hour: "+reminder);
        Toast.makeText(this,"Send Notification in " + reminder + " Hour",Toast.LENGTH_SHORT).show(); */
    }



}