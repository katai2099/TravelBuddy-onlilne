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

import com.example.travelbuddyv2.adapter.ChildTripDetailAdapter;
import com.example.travelbuddyv2.adapter.DayAdapter;
import com.example.travelbuddyv2.adapter.ParentTripDetailAdapter;
import com.example.travelbuddyv2.adapter.TripDetailAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.TripSection;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;


public class TripDetailFragment extends Fragment implements DayAdapter.DayAdapterCallback, ChildTripDetailAdapter.ChildTripDetailAdapterCallBack {

    private final String tag = "TRIP_DETAIL_FRAGMENT" ;

    List<TripSection> tripSectionList;
    List<String> dayList;
    RecyclerView rcvTripDetail , rcvDays;
    ParentTripDetailAdapter parentTripDetailAdapter;
    DayAdapter dayAdapter;



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

        rcvTripDetail = root.findViewById(R.id.rcvFragmentTripDetailList);

        rcvTripDetail.setLayoutManager(new LinearLayoutManager(getActivity()));

        parentTripDetailAdapter = new ParentTripDetailAdapter(tripSectionList,tripID,this);
        rcvTripDetail.setAdapter(parentTripDetailAdapter);

        //Day adapter initialization
        dayList = new ArrayList<>();
        rcvDays = root.findViewById(R.id.rcvFragmentTripDetailToSelectedList);
        rcvDays.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));

        dayAdapter = new DayAdapter(dayList,this);

        rcvDays.setAdapter(dayAdapter);

        fillDateInterval();

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
                dayList.clear();
                int dayCount = 0 ;
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
                   //add to TripSection
                    TripSection tripSection = new TripSection(tmp,tmpDestination);
                    tripSectionList.add(tripSection);

                    //add to DayList
                    String day = "Day " + (dayCount+1);
                    dayList.add(day);
                    dayCount++;
                }
                parentTripDetailAdapter.notifyDataSetChanged();
                dayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onListClicked(int position) {
       // rcvTripDetail.scrollToPosition(position);
        rcvTripDetail.smoothScrollToPosition(position);
    }


    @Override
    public void onDeleteDestinationClick(final String date, String destinationStringID) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID)
                .child(date)
                .child(destinationStringID);

        //first check if it is the last child of the node

        final DatabaseReference checkNumberOfChild = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripID)
                .child(date);

        checkNumberOfChild.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>1){
                    reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),"Delete success",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    checkNumberOfChild.setValue("");
                }
            }
        });

    }


}