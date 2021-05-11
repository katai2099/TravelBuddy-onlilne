package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.DateSelectionAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class DateSelectionActivity extends AppCompatActivity implements DateSelectionAdapter.DateSelectionAdapterCallBack {

    private final String tag ="DATE_SELECTION_ACTIVITY";
    String tripStringID;
    List<String> dates;
    DateSelectionAdapter dateSelectionAdapter;
    RecyclerView rcvDate;
    Destination destination;
    List<Integer> idList;
    HashMap<String,Integer> dateAndItsIdPair;
    boolean isFromPersonalTrip;
    String tripOwnerUUID;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_selection);
        destination = new Destination();
        idList = new ArrayList<>();
        dateAndItsIdPair = new HashMap<>();
        Bundle bundle = getIntent().getExtras();
        //extra information from tripSelection activity
        if(bundle!=null){
            tripStringID = bundle.getString("tripStringId");
            destination.setName(bundle.getString("googleMapPlaceName"));
            destination.setPlaceId(bundle.getString("googleMapPlaceID"));
            destination.setLatitude(bundle.getDouble("googleMapPlaceLat"));
            destination.setLongitude(bundle.getDouble("googleMapPlaceLong"));
            destination.setAddress(bundle.getString("googleMapAddress"));
            isFromPersonalTrip = bundle.getBoolean("isFromPersonalTrip");
        }
        //if user decided to add attraction to group trip, we will retrieve Owner's UUID so that we could store attraction there
        if(!isFromPersonalTrip){
            tripOwnerUUID = bundle.getString("tripOwnerUUID");
        }
        dates = new ArrayList<>();
        dateSelectionAdapter = new DateSelectionAdapter(dates,this);
        rcvDate = findViewById(R.id.rcvDateSelection);
        progressBar = findViewById(R.id.simpleProgressBar);
        rcvDate.setLayoutManager(new LinearLayoutManager(this));
        rcvDate.setAdapter(dateSelectionAdapter);
        if(isFromPersonalTrip){
            String userUUID=FirebaseAuth.getInstance().getCurrentUser().getUid();
            fillListWithDate(userUUID);
            getEachDateLatestTripDetailID(userUUID);
        }else{
            fillListWithDate(tripOwnerUUID);
            getEachDateLatestTripDetailID(tripOwnerUUID);
        }
    }

    //get date interval of the selected trip
    private void fillListWithDate(String owner){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(owner)
                .child(tripStringID);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    dates.add(data.getKey());
                    dateAndItsIdPair.put(data.getKey(),0);
                }
                Log.d(tag,dateAndItsIdPair.toString());
                dateSelectionAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //get latest destincationStringID of each date
    private void getEachDateLatestTripDetailID(String owner){
         DatabaseReference tripDetailStringID = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(owner)
                .child(tripStringID);
         tripDetailStringID.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
             @Override
             public void onSuccess(DataSnapshot dataSnapshot) {
                 for(DataSnapshot date:dataSnapshot.getChildren()){
                     idList.clear();
                     for(DataSnapshot id:date.getChildren()){
                         idList.add(Helper.tripDetailStringIDToInt(id.getKey()));
                     }
                     if(idList.size()!=0) {
                         Collections.sort(idList);
                         int size = idList.get(idList.size() - 1);
                         dateAndItsIdPair.put(date.getKey(),size+1);
                     }
                 }
             }
         });
         tripDetailStringID.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for(DataSnapshot date:snapshot.getChildren()){
                     idList.clear();
                     for(DataSnapshot id:date.getChildren()){
                         idList.add(Helper.tripDetailStringIDToInt(id.getKey()));
                     }
                     if(idList.size()!=0) {
                         Collections.sort(idList);
                         int size = idList.get(idList.size() - 1);
                         dateAndItsIdPair.put(date.getKey(),size+1);
                     }
                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }

    //add destination to trip_detail node based on the selected date
    private void addTripDetailToDatabase(int position,String owner){
        Log.d(tag,"This is after user decide to add " + dateAndItsIdPair.toString());

        int latestID = dateAndItsIdPair.get(dates.get(position));

        DatabaseReference userTripDetailNode;

        //to sort firebase database tripDetailID
        if(latestID<10 ){
            userTripDetailNode = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                    .child(owner)
                    .child(tripStringID)
                    .child(dates.get(position))
                    .child("td0"+dateAndItsIdPair.get(dates.get(position)));
            destination.setDestinationStringID("td0"+dateAndItsIdPair.get(dates.get(position)));

        }else{
            userTripDetailNode = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                    .child(owner)
                    .child(tripStringID)
                    .child(dates.get(position))
                    .child("td"+dateAndItsIdPair.get(dates.get(position)));
            destination.setDestinationStringID("td"+dateAndItsIdPair.get(dates.get(position)));
        }
        destination.setStartDate(dates.get(position));
        checkLatestEndTime(position,owner,userTripDetailNode);
    }


    @Override
    public void onListClicked(final int position) {
        if(isFromPersonalTrip){
            addTripDetailToDatabase(position,FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        else{
            addTripDetailToDatabase(position,tripOwnerUUID);
        }
    }

    private void goBackToMapFragment(){
        Intent i = new Intent(DateSelectionActivity.this,Main2Activity.class);
        i.putExtra("changeToMapFragment",true);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    //this is to check the latest end time of the selected date, in case this is the first attraction, it will be set to 8:00 by default
    //otherwise we want to use the last attraction end time as a start time of the new attraction
    private void checkLatestEndTime(final int position, String owner, final DatabaseReference userTripDetailNode){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(owner)
                .child(tripStringID)
                .child(dates.get(position));
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()==0){
                    destination.setStartTime("08:00");
                    destination.setEndTime("08:30");
                    userTripDetailNode.setValue(destination).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(DateSelectionActivity.this,"ADD SUCCESS",Toast.LENGTH_SHORT).show();
                            goBackToMapFragment();
                        }
                    });
                }else{
                    int cnt = 0;
                    Log.d(tag,dataSnapshot.toString());
                    Destination lastDestination = new Destination();
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        if(cnt==dataSnapshot.getChildrenCount()-1){
                            lastDestination =  data.getValue(Destination.class);
                        }
                       cnt++;
                    }
                    Log.d(tag,lastDestination.toString());
                    destination.setStartTime(lastDestination.getEndTime());
                    destination.setEndTime(Helper.getNextThirtyMinute(lastDestination.getEndTime()));
                    //check for number Of extra Day in case destination took more than one day
                        int extraDayFromLastDestination = lastDestination.getExtraDay();
                        int endResult = Helper.calculateExtraDay(dates.get(position),lastDestination.getEndTime(),extraDayFromLastDestination);
                        endResult += extraDayFromLastDestination;
                        destination.setExtraDay(endResult);
                            userTripDetailNode.setValue(destination).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(DateSelectionActivity.this,"ADD SUCCESS",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    goBackToMapFragment();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DateSelectionActivity.this,getString(R.string.unexpectedBehavior),Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //getEachDateLatestTripDetailID();
    }
}