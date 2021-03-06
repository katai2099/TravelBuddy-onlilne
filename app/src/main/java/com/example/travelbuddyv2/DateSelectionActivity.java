package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.DateSelectionAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_selection);

        destination = new Destination();
        idList = new ArrayList<>();
        dateAndItsIdPair = new HashMap<>();

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            tripStringID = bundle.getString("tripStringId");
            destination.setName(bundle.getString("googleMapPlaceName"));
            destination.setPlaceId(bundle.getString("googleMapPlaceID"));
            destination.setLatitude(bundle.getDouble("googleMapPlaceLat"));
            destination.setLongtitude(bundle.getDouble("googleMapPlaceLong"));
        }



        Toast.makeText(this,tripStringID,Toast.LENGTH_SHORT).show();

        dates = new ArrayList<>();

        dateSelectionAdapter = new DateSelectionAdapter(dates,this);

        rcvDate = findViewById(R.id.rcvDateSelection);
        rcvDate.setLayoutManager(new LinearLayoutManager(this));
        rcvDate.setAdapter(dateSelectionAdapter);

        fillListWithDate();
        getEachDateLatestTripDetailID();
        Log.d(tag,"Oncreate " + dateAndItsIdPair.toString());

    }

    private void fillListWithDate(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
            }
        });


    }

    private void getEachDateLatestTripDetailID(){

         DatabaseReference tripDetailStringID = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                 Log.d(tag,"This is inside of one time GET()" + dateAndItsIdPair);
             }
         });


         tripDetailStringID.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 //Log.d(tag,"This is called");
                 for(DataSnapshot date:snapshot.getChildren()){
                     //Log.d(tag,"date from snapshot " + date.getKey());
                     idList.clear();
                     for(DataSnapshot id:date.getChildren()){
                       //  Log.d(tag,"trip detail id from snapshot " + id.getKey());
                         idList.add(Helper.tripDetailStringIDToInt(id.getKey()));
                     }
                     if(idList.size()!=0) {
                         Collections.sort(idList);
                         int size = idList.get(idList.size() - 1);
                     //    Log.d(tag,"latest trip detail ID is " + size);
                         dateAndItsIdPair.put(date.getKey(),size+1);
                     }
                 }
                Log.d(tag,"This is inside on dataChange " + dateAndItsIdPair.toString());
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
        Log.d(tag,"This is outside on dataChange " + dateAndItsIdPair.toString());
    }

    private void addTripDetailToDatabase(int position){

      //  Toast.makeText(DateSelectionActivity.this,String.valueOf(ID),Toast.LENGTH_SHORT).show();

        Log.d(tag,"This is after user decide to add " + dateAndItsIdPair.toString());

        final DatabaseReference userTripDetailNode = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripStringID)
                .child(dates.get(position))
                .child("td"+dateAndItsIdPair.get(dates.get(position)));

        userTripDetailNode.setValue(destination).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DateSelectionActivity.this,"ADD SUCCESS",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onListClicked(final int position) {

       addTripDetailToDatabase(position);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getEachDateLatestTripDetailID();
        Log.d(tag,"onResume " + dateAndItsIdPair);
    }
}