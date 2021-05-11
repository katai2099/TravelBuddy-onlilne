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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.User;
import com.example.travelbuddyv2.model.tripModel;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.example.travelbuddyv2.retrofit.Client;
import com.example.travelbuddyv2.retrofit.MyResponse;
import com.example.travelbuddyv2.retrofit.NotificationAPI;
import com.example.travelbuddyv2.retrofit.NotificationData;
import com.example.travelbuddyv2.retrofit.PushNotification;
import com.example.travelbuddyv2.utils.Snack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class addNewTrip extends AppCompatActivity {
    private final String tag = "ADD_NEW_TRIP";
    private int ID = 0;
    List<Integer> tmp;
    EditText tripName , startDate , endDate;
    DatePickerDialog datePickerDialogStartDate,datePickerDialogEndDate;
    Calendar calendar ;
    DatabaseHelper databaseHelper;
    User currentUserInfo;
    ProgressBar progressBar;
    MenuItem save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_trip);
        this.setTitle("Create a new trip");
        getCurrentIdFromFirebaseDatabase();
        getCurrentUserInfo();
        tripName = findViewById(R.id.etTripName);
        progressBar = findViewById(R.id.simpleProgressBar);
        startDate = findViewById(R.id.etDepartDate);
        startDate.setInputType(InputType.TYPE_NULL);
        endDate = findViewById(R.id.etArrivalDate);
        endDate.setInputType(InputType.TYPE_NULL);
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
                tripName.clearFocus();
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
                    datePickerDialogEndDate.getDatePicker().setMinDate(tmpcal.getTimeInMillis());
                datePickerDialogEndDate.show();
            }
        });
    }
    private void savePendingNotificationToOfflineDatabase(tripModel model){
        databaseHelper = new DatabaseHelper(addNewTrip.this);
        databaseHelper.addNewPendingNotification(model.getTripName(),model.getStringID(),model.getStartDate());
    }
    private void saveBehavior(){
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
            //disable save button until trip is done/fail saving
            save.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            final tripModel tmpTripModel = new tripModel();
            tmpTripModel.setTripName(tripName.getText().toString());
            tmpTripModel.setStartDate(tmpStartDate);
            tmpTripModel.setEndDate(tmpEndDate);
            tmpTripModel.setStringID("t" + ID);
            tmpTripModel.setOwner(FirebaseAuth.getInstance().getCurrentUser().getUid());
            //adding date to TripDetail NODE
            final List<String> dateList = getDateInterval(tmpStartDate,tmpEndDate);
            final HashMap<String,String> res = new HashMap<>();
            for(int i=0;i<dateList.size();i++){
                res.put(dateList.get(i),"");
            }
            final int lastInsertedID = ID;
            //adding trip to Trips Node
            FirebaseDatabase.getInstance().getReference()
                    .child("Trips")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("t" + ID)
                    .setValue(tmpTripModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("t" + lastInsertedID)
                                    .setValue(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    addOwnerToMemberNode(lastInsertedID);
                                    setNotificationTime(tmpTripModel);
                                    subscribeToUpcomingTripNotification(tmpTripModel.getStringID());
                                    savePendingNotificationToOfflineDatabase(tmpTripModel);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    save.setEnabled(true);
                                    toTripFragment();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    save.setEnabled(true);
                    progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(addNewTrip.this,getString(R.string.unexpectedBehavior),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // subscribe user to upcoming trip notification node so that after alarm fired, the application will get device token(s) from this node
    private void subscribeToUpcomingTripNotification(final String tripStringID){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("upcomingTripNotification")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(tripStringID)
                        .child(s);
                reference.setValue("");
            }
        });
    }

    //we called this function to get the current user information. We need this information to store them into member node
    private void getCurrentUserInfo() {
        currentUserInfo = new User();
        DatabaseReference currentUserReference = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User tmp = dataSnapshot.getValue(User.class);
                Log.d(tag,tmp.toString());
                currentUserInfo.setProfile_image(tmp.getProfile_image());
                currentUserInfo.setEmail(tmp.getEmail());
                currentUserInfo.setName(tmp.getName());
                currentUserInfo.setUser_id(tmp.getUser_id());
            }
        });
    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    //set alarm time so that when upcoming trip is approching the alarm will fired and FCM will send push notification to each device whose in the group
    public void setNotificationTime(tripModel passingData)
    {
        Intent intent = new Intent(addNewTrip.this,ReminderBroadcast.class);
        Bundle extras = new Bundle();
        extras.putString("tripStringID",passingData.getStringID());
        extras.putString("tripName",passingData.getTripName());
        extras.putString("tripStartDate",passingData.getStartDate());
        intent.putExtras(extras);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(addNewTrip.this,Helper.tripStringIDToInt(passingData.getStringID()),intent,0);
        long timeToFireAnAlarm = Helper.getStartDateInMilli(passingData.getStartDate());
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,timeToFireAnAlarm,pendingIntent);
        } else{
            alarmManager.set(AlarmManager.RTC_WAKEUP,timeToFireAnAlarm,pendingIntent);
        }
    }


    //get current trip ID of current user in Trips node
    private void getCurrentIdFromFirebaseDatabase(){
        tmp = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Trips")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    tmp.add(Helper.tripStringIDToInt(data.getKey()));
                    Log.d(tag,"KEY FROM FIREBASE " + data.getKey());
                }
                if(tmp.size()!=0){
                    Collections.sort(tmp);
                int size = tmp.get(tmp.size()-1);
                Log.d(tag,"latest trip ID " + size);
                ID = size+1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //date interval between start date and end date
    private List<String> getDateInterval(String startDate , String endDate)  {
        SimpleDateFormat simpleDateFormat;
        Date start , end ;
        Calendar calendar;
        start = new Date();
        end = new Date();
        calendar = new GregorianCalendar();
        List<String> listOfdate = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            start = simpleDateFormat.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end = simpleDateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(start);
        while(calendar.getTime().before(end))
        {
            Date result = calendar.getTime();
            String tmp = simpleDateFormat.format(result);
            listOfdate.add(tmp);
            calendar.add(Calendar.DATE, 1);
        }
        String tmp = simpleDateFormat.format(calendar.getTime());
        listOfdate.add(tmp);
        return listOfdate;
    }

    //when trip is created, the owner will be added to member node to keep track of number of member
    private void addOwnerToMemberNode(final int id){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("t"+id)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final Member member = new Member();
        member.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        member.setID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        member.setName(currentUserInfo.getName());
        member.setPermission("owner");
        member.setProfileImg(currentUserInfo.getProfile_image());
        reference.setValue(member);
    }

    private void toTripFragment(){
        Intent i = new Intent(addNewTrip.this,Main2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu,menu);
        save = menu.findItem(R.id.optionSaveTrip);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.optionSaveTrip){
            if(NetworkObserver.isNetworkConnected){
                saveBehavior();
            }else{
                Helper.showSnackBar(getCurrentFocus(),getString(R.string.noInternet));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}