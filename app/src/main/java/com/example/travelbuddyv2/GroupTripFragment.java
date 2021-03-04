package com.example.travelbuddyv2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.GroupTripAdapter;
import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class GroupTripFragment extends Fragment implements GroupTripAdapter.AdapterCallback {

    List<tripModel> groupTripList;
    RecyclerView rcvGroupTripView;
    GroupTripAdapter groupTripAdapter;

    public GroupTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_group_trip, container, false);

        groupTripList = new ArrayList<>();
        groupTripAdapter = new GroupTripAdapter(groupTripList,this);
        rcvGroupTripView = root.findViewById(R.id.rcvFragmentGroupTrip);
        rcvGroupTripView.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvGroupTripView.setAdapter(groupTripAdapter);

        initializeGroupTripList();


        return root ;
    }

    private void initializeGroupTripList(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    for(DataSnapshot inviter:dataSnapshot.getChildren()){
                       if(inviter!=null){
                           for(DataSnapshot tripList:inviter.getChildren() ){
                               tripModel tmp = tripList.getValue(tripModel.class);
                               groupTripList.add(tmp);
                           }
                       }
                    }
                }
                groupTripAdapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public void onMethodCallback(int position) {
        Toast.makeText(getContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();
    }
}