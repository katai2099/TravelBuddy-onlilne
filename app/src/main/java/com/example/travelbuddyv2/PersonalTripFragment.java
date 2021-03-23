package com.example.travelbuddyv2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.TripAdapter;
import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PersonalTripFragment extends Fragment {

    int ID = 0;

    final String tag = "PERSONAL_TRIP_FRAGMENT";

    FloatingActionButton fbtnAddNewTrip;

    RecyclerView rcvTriplist;

    TripAdapter tripAdapter;

    List<tripModel> tripLists  = new ArrayList<>();


    public PersonalTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_personal_trip, container, false);


        fbtnAddNewTrip = root.findViewById(R.id.fbtnFragmentPersonalTrip);


        rcvTriplist = root.findViewById(R.id.rcvFragmentPersonalTrip);
        rcvTriplist.setLayoutManager(new LinearLayoutManager(getActivity()));
        tripAdapter = new TripAdapter(tripLists);

        rcvTriplist.setAdapter(tripAdapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripLists.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key = dataSnapshot.getKey();
                    Log.d(tag,key);
                    tripModel trip = dataSnapshot.getValue(tripModel.class);
                    tripLists.add(trip);
                }
                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        rcvTriplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        fbtnAddNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getActivity(),addNewTrip.class);
                startActivity(i);
                /*Intent i = new Intent(getActivity(), TripDetail.class);
                startActivity(i);*/
            }
        });

        return root;

    }


}