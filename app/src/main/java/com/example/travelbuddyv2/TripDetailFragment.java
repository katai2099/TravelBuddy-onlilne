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

import com.example.travelbuddyv2.adapter.ParentTripDetailAdapter;
import com.example.travelbuddyv2.adapter.TripDetailAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.TripSection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TripDetailFragment extends Fragment {

    private final String tag = "TRIP_DETAIL_FRAGMENT" ;

    List<TripSection> tripSectionList;
    RecyclerView rcvTripDetail;
    ParentTripDetailAdapter parentTripDetailAdapter;
    FloatingActionButton floatingActionButton;

    String tripID ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View root = inflater.inflate(R.layout.fragment_trip_detail, container, false);

        Bundle bundle = getArguments();
        if(bundle!=null){
            tripID = bundle.getString("TRIP_STRING_ID");
           //  Toast.makeText(getContext(),bundle.getString("TRIP_STRING_ID"),Toast.LENGTH_SHORT).show();
        }else{
             Toast.makeText(getContext(),"NULL",Toast.LENGTH_SHORT).show();
        }





        tripSectionList = new ArrayList<>();
        //fillList();
        fillDateInterval();
        rcvTripDetail = root.findViewById(R.id.rcvFragmentTripDetailList);
        floatingActionButton = root.findViewById(R.id.katai);
        rcvTripDetail.setLayoutManager(new LinearLayoutManager(getActivity()));

        parentTripDetailAdapter = new ParentTripDetailAdapter(tripSectionList,tripID);

        rcvTripDetail.setAdapter(parentTripDetailAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(),MapsActivity.class);
                startActivity(i);

            }
        });

        return root;
    }


    private void fillDateInterval(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripSectionList.clear();
                for(DataSnapshot data: snapshot.getChildren()){
                    String tmp = data.getKey();
                    Log.d(tag,tmp);
                    List<Destination> tmpDestination = new ArrayList<>();
                   for(DataSnapshot childData: data.getChildren())
                    {
                        Log.d(tag,childData.toString());
                        Destination destination = childData.getValue(Destination.class);
                        tmpDestination.add(destination);
                    }
                    TripSection tripSection = new TripSection(tmp,tmpDestination);
                    tripSectionList.add(tripSection);
                }
                parentTripDetailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


   /* private void fillList(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                destinationList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Log.d(tag,snapshot.getValue().toString());
                    Destination destination = dataSnapshot.getValue(Destination.class);
                   destinationList.add(destination);
                }
                tripDetailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

}