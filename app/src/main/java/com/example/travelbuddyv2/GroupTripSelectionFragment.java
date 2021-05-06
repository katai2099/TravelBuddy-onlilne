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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.TripSelectionAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupTripSelectionFragment extends Fragment implements TripSelectionAdapter.TripSelectionAdapterCallback {

    private final String tag = "GROUP_TRIP_SELECTION";
    List<tripModel> groupTripList;
    TripSelectionAdapter tripSelectionAdapter;
    RecyclerView rcvTrip;
    Destination destination;
    ProgressBar progressBar;

    public GroupTripSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_group_trip_selection, container, false);

        Bundle bundle = getArguments();
        String googleMapPlaceName = bundle.getString("googleMapPlaceName");
        String googleMapPlaceID = bundle.getString("googleMapPlaceID");
        double googleMapPlaceLat = bundle.getDouble("googleMapPlaceLat");
        double googleMapPlaceLong = bundle.getDouble("googleMapPlaceLong");
        String googleMapAddress = bundle.getString("googleMapAddress");
        Log.d(tag,googleMapPlaceName);
        Log.d(tag,googleMapPlaceID);
        Log.d(tag,String.valueOf(googleMapPlaceLat));
        Log.d(tag,String.valueOf(googleMapPlaceLong));

        destination = new Destination();

        destination.setName(googleMapPlaceName);
        destination.setPlaceId(googleMapPlaceID);
        destination.setLatitude(googleMapPlaceLat);
        destination.setLongitude(googleMapPlaceLong);
        destination.setAddress(googleMapAddress);

        groupTripList = new ArrayList<>();
        tripSelectionAdapter = new TripSelectionAdapter(groupTripList,this);
        rcvTrip = root.findViewById(R.id.rcvFragmentGroupTripSelection);
        progressBar = root.findViewById(R.id.simpleProgressBar);
        rcvTrip.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvTrip.setAdapter(tripSelectionAdapter);
        initializeGroupTripList();

        return root;

    }

    private void initializeGroupTripList(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupTripList.clear();
                for(DataSnapshot inviter:snapshot.getChildren()){
                    if(inviter!=null){
                        for(DataSnapshot tripList:inviter.getChildren() ){
                            tripModel tmp = tripList.getValue(tripModel.class);
                            groupTripList.add(tmp);
                        }
                    }
                }
                tripSelectionAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onTripClicked(int position) {

        tripModel trip = groupTripList.get(position);
        final Intent i = new Intent(getContext(), DateSelectionActivity.class);
        i.putExtra("tripStringId",trip.getStringID());
        i.putExtra("googleMapPlaceName",destination.getName());
        i.putExtra("googleMapPlaceID",destination.getPlaceId());
        i.putExtra("googleMapPlaceLat",destination.getLatitude());
        i.putExtra("googleMapPlaceLong",destination.getLongitude());
        i.putExtra("googleMapAddress",destination.getAddress());
        i.putExtra("isFromPersonalTrip",false);
        i.putExtra("tripOwnerUUID",trip.getOwner());

        //check if current user has permission to edit the trip
        DatabaseReference MemberNode = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(trip.getOwner())
                .child(trip.getStringID())
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        MemberNode.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Member currentUser = dataSnapshot.getValue(Member.class);
                if(currentUser.getPermission().equals("edit")){
                    startActivity(i);
                }else{
                    Toast.makeText(getContext(),"You do not have permission to edit",Toast.LENGTH_SHORT).show();
                }
            }
        });

       // startActivity(i);

    }


}