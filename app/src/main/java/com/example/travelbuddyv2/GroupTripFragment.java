package com.example.travelbuddyv2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.GroupTripAdapter;
import com.example.travelbuddyv2.model.User;
import com.example.travelbuddyv2.model.tripModel;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GroupTripFragment extends Fragment implements GroupTripAdapter.GroupTripAdapterCallback {

    List<tripModel> groupTripList;
    RecyclerView rcvGroupTripView;
    GroupTripAdapter groupTripAdapter;
    static final String tag = "GROUP_TRIP_FRAGMENT";
    SwipeRefreshLayout swipeRefreshLayout;
    ValueEventListener groupTripListener;
    View placeholder;
    public GroupTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_group_trip, container, false);
        groupTripList = new ArrayList<>();
        groupTripAdapter = new GroupTripAdapter(groupTripList,this,getActivity());
        rcvGroupTripView = root.findViewById(R.id.rcvFragmentGroupTrip);
        swipeRefreshLayout = root.findViewById(R.id.groupTripRefreshLayout);
        placeholder = root.findViewById(R.id.emptyListPlaceholder);
        rcvGroupTripView.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvGroupTripView.setAdapter(groupTripAdapter);
        initializeGroupTripListener();
        initializeGroupTripList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                detachGroupTripListener();
                initializeGroupTripList();
            }
        });
        return root ;
    }

    private void initializeGroupTripList(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(groupTripListener);
    }


    @Override
    public void onResume() {
        super.onResume();
        initializeGroupTripList();
    }


    @Override
    public void onLeaveGroupClicked(int position) {
        final tripModel currentTrip = groupTripList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to leave this group?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(NetworkObserver.isNetworkConnected){
                    leaveGroup(currentTrip);
                    toGroupTripFragment();
                }else{
                      Helper.showSnackBar(getActivity().findViewById(R.id.nav_view),getString(R.string.noInternet));
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void leaveGroup(final tripModel trip){
        String currentUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //remove user from Group Node
        DatabaseReference userGroupNode = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(currentUserUUID)
                .child(trip.getOwner())
                .child(trip.getStringID());
        //remove user from memberNode
        final DatabaseReference userMemberNode = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(trip.getOwner())
                .child(trip.getStringID())
                .child(currentUserUUID);
        DatabaseReference userNode; // we need to get deletingMemberDeviceToken;
        final DatabaseReference upcomingNotificationNode = FirebaseDatabase.getInstance().getReference().child("upcomingTripNotification")
                .child(trip.getOwner())
                .child(trip.getStringID());
        userNode = FirebaseDatabase.getInstance().getReference().child("User")
                .child(currentUserUUID);
        userNode.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User deleteMember = dataSnapshot.getValue(User.class);
                if(deleteMember!=null){
                    upcomingNotificationNode.child(deleteMember.getDeviceToken()).removeValue();
                    Log.d(tag,deleteMember.getDeviceToken());
                }
            }
        });
        userGroupNode.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(tag,"Done remove from group NODE ");
                userMemberNode.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(tag,"Done remove from Member NODE");
                    }
                });
            }
        });
    }

    private void toGroupTripFragment(){
        Intent i = new Intent(getContext(), Main2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("changeToGroup",true);
        startActivity(i);
        getActivity().finish();
    }

    private void detachGroupTripListener(){
        groupTripList.clear();
        groupTripAdapter.notifyDataSetChanged();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.removeEventListener(groupTripListener);
    }

    private void initializeGroupTripListener(){
         groupTripListener = new ValueEventListener() {
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
                Collections.sort(groupTripList, new Comparator<tripModel>() {
                    @Override
                    public int compare(tripModel trip1, tripModel trip2) {
                        return trip2.getStartDate().compareTo(trip1.getStartDate());
                    }
                });
                groupTripAdapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (groupTripList.isEmpty()) {
                    placeholder.setVisibility(View.VISIBLE);
                } else {
                    placeholder.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

}