package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.travelbuddyv2.adapter.TripSelectionAdapter;
import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TripSelectionActivity extends AppCompatActivity {

    RecyclerView rcvTrip;
    TripSelectionAdapter tripSelectionAdapter;
    List<tripModel> tripModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_selection);

        tripModelList = new ArrayList<>();
        tripSelectionAdapter = new TripSelectionAdapter(tripModelList);
        rcvTrip = findViewById(R.id.rcvTripSelection);
        rcvTrip.setLayoutManager(new LinearLayoutManager(this));
        rcvTrip.setAdapter(tripSelectionAdapter);
        fillListWithTrip();


    }

    private void fillListWithTrip(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    tripModel tmp = data.getValue(tripModel.class);
                    tripModelList.add(tmp);
                }
                tripSelectionAdapter.notifyDataSetChanged();
            }
        });

    }



}