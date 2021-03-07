package com.example.travelbuddyv2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.DayAdapter;
import com.example.travelbuddyv2.adapter.GroupTripAdapter;
import com.example.travelbuddyv2.adapter.ParentGroupTripDetailAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.TripSection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupTripDetailFragment extends Fragment implements DayAdapter.DayAdapterCallback {

    private final String tag = "GROUPDETAILFRAGMENT";

    private String tripID, tripOwner;

    List<TripSection> tripSectionList;
    ParentGroupTripDetailAdapter parentGroupTripDetailAdapter;
    RecyclerView rcvGroupTripDetailView,rcvDays;
    List<String> dayList;
    DayAdapter dayAdapter;




   // private GroupTripAdapter.AdapterCallBack adapterCallBack;

    public GroupTripDetailFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = getArguments();
        if(bundle!=null){

            tripID = bundle.getString("TRIP_STRING_ID");
            tripOwner = bundle.getString("TRIP_OWNER");

        }else{
            Log.d(tag,"NULL");
        }

        checkUserPermission();

        View root = inflater.inflate(R.layout.fragment_group_trip_detail, container, false);
        tripSectionList = new ArrayList<>();

        parentGroupTripDetailAdapter = new ParentGroupTripDetailAdapter(tripSectionList,tripID);

        rcvGroupTripDetailView = root.findViewById(R.id.rcvFragmentGroupTripDetail);
        rcvGroupTripDetailView.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvGroupTripDetailView.setAdapter(parentGroupTripDetailAdapter);

        //
        dayList = new ArrayList<>();
        rcvDays = root.findViewById(R.id.rcvFragmentGroupTripDetailToSelectedList);
        rcvDays.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));

        dayAdapter = new DayAdapter(dayList,this);

        rcvDays.setAdapter(dayAdapter);

        fillDateInterval();

        return root;
    }


    private void fillDateInterval(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(tripOwner)
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
                    //Add to tripSection
                    TripSection tripSection = new TripSection(tmp,tmpDestination);
                    tripSectionList.add(tripSection);

                    //add to DayList
                    String day = "Day " + (dayCount+1);
                    dayList.add(day);
                    dayCount++;
                }
               parentGroupTripDetailAdapter.notifyDataSetChanged();
                dayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void checkUserPermission(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(tripOwner)
                .child(tripID)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member member = snapshot.getValue(Member.class);
                if (member!=null&&member.getPermission().equals("edit")) {
                    parentGroupTripDetailAdapter.updateUserPermission(true);
                } else {
                    parentGroupTripDetailAdapter.updateUserPermission(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onListClicked(int position) {
        rcvGroupTripDetailView.scrollToPosition(position);
    }
}