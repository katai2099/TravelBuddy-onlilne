package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.DateSelectionAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DateSelectionActivity extends AppCompatActivity {

    String tripStringID;
    List<String> dates;
    DateSelectionAdapter dateSelectionAdapter;
    RecyclerView rcvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_selection);

        tripStringID = getIntent().getExtras().getString("tripStringId");

        Toast.makeText(this,tripStringID,Toast.LENGTH_SHORT).show();

        dates = new ArrayList<>();

        dateSelectionAdapter = new DateSelectionAdapter(dates);

        rcvDate = findViewById(R.id.rcvDateSelection);
        rcvDate.setLayoutManager(new LinearLayoutManager(this));
        rcvDate.setAdapter(dateSelectionAdapter);

        fillListWithDate();

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
                }
                dateSelectionAdapter.notifyDataSetChanged();
            }
        });

    }


}