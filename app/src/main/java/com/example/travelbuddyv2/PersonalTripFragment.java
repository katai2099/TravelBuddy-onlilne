package com.example.travelbuddyv2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.TripAdapter;
import com.example.travelbuddyv2.model.Inventory;
import com.example.travelbuddyv2.model.tripModel;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.example.travelbuddyv2.utils.Snack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;


public class PersonalTripFragment extends Fragment implements TripAdapter.TripAdapterCallBack{

    final String tag = "PERSONAL_TRIP_FRAGMENT";
    FloatingActionButton fbtnAddNewTrip;
    RecyclerView rcvTripList;
    TripAdapter tripAdapter;
    List<tripModel> tripLists  = new ArrayList<>();
    String userUUID ;
    View currentView ;
    SwipeRefreshLayout swipeRefreshLayout;
    public PersonalTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_personal_trip, container, false);
        currentView = root.findViewById(R.id.personalTripLayout);
        swipeRefreshLayout = root.findViewById(R.id.personalTripRefreshLayout);
        userUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fbtnAddNewTrip = root.findViewById(R.id.fbtnFragmentPersonalTrip);
        rcvTripList = root.findViewById(R.id.rcvFragmentPersonalTrip);
        rcvTripList.setLayoutManager(new LinearLayoutManager(getActivity()));
        tripAdapter = new TripAdapter(tripLists,this);
        rcvTripList.setAdapter(tripAdapter);
        initializeList();
        fbtnAddNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getContext(),addNewTrip.class);
                startActivity(i);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeList();
            }
        });
        return root;
    }

    private void initializeList(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                tripLists.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    tripModel trip = data.getValue(tripModel.class);
                    tripLists.add(trip);
                }
                tripAdapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
               Helper.showSnackBar(getActivity().findViewById(R.id.nav_view),getString(R.string.unexpectedBehavior));
            }
        });
    }

    @Override
    public void onInviteFriendClicked(int position) {
        tripModel currentTrip = tripLists.get(position);
        Intent i = new Intent(getContext(), MemberActivity.class);
        i.putExtra("TripName",currentTrip.getTripName());
        i.putExtra("TripStringID",currentTrip.getStringID());
        i.putExtra("fromWho","personalTrip");
        i.putExtra("TripOwnerID",currentTrip.getOwner());
        startActivity(i);
    }

    @Override
    public void onDeleteTripClicked(int position) {

        final tripModel currentTrip = tripLists.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this trip?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(NetworkObserver.isNetworkConnected){
                    deleteTrip(currentTrip);
                }else{
                    Helper.showSnackBar(getActivity().findViewById(R.id.nav_view),getString(R.string.noInternet));
                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Nah, I am not leaving",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onTripClicked(int position) {
        tripModel tmp = tripLists.get(position);
        Intent i = new Intent(getContext(), TripDetailActivity.class);
        i.putExtra("TRIP_STRING_ID",tmp.getStringID());
        i.putExtra("isPersonal",true);
        startActivity(i);
    }

    private void removeMemberNode(final tripModel currentTrip){
        final DatabaseReference memberNodeReference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(userUUID)
                .child(currentTrip.getStringID());
        memberNodeReference.removeValue();
    }

    private void removeTripDetail(final tripModel currentTrip){
        DatabaseReference tripDetailNodeReference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                .child(userUUID)
                .child(currentTrip.getStringID());
        tripDetailNodeReference.removeValue();
    }

    private void removeInventoryNode(final tripModel currentTrip){

        final String tripStringID = currentTrip.getStringID();

        DatabaseReference inventoryNodeReference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                .child(userUUID)
                .child(tripStringID);

        inventoryNodeReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot file:dataSnapshot.getChildren()){
                    Inventory tmp = file.getValue(Inventory.class);
                    removeFileInCloudStorage(tripStringID,tmp.getFileName()); // here we have to get reference to file name and remove them one by one from google cloud storage
                }                                                              // we cannot remove the whole directory
            }
        });

        inventoryNodeReference.removeValue();
    }

    private void removeFileInCloudStorage(String tripStringID,String fileName){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("trip_file")
                .child(userUUID)
                .child(tripStringID)
                .child(fileName);
        storageRef.delete();
    }

    private void removeTripNode(final tripModel currentTrip){
        final DatabaseReference tripNodeReference = FirebaseDatabase.getInstance().getReference().child("Trips")
                .child(userUUID)
                .child(currentTrip.getStringID());
        tripNodeReference.removeValue();
    }

    private void removeFromGroupNode(final tripModel currentTrip){
        final DatabaseReference groupNodeReference = FirebaseDatabase.getInstance().getReference().child("Group");
        groupNodeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()){ //go through every user
                    for (DataSnapshot data : user.getChildren()) {
                        if (data.getKey().equals(currentTrip.getOwner())) { // find if user is a member of currentTrip
                            for (DataSnapshot trip : data.getChildren()) {
                                if (trip.getKey().equals(currentTrip.getStringID()))  // check for trip that has the same StringID has current so we can proceed with remove user from group
                                    trip.getRef().removeValue();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void removePendingIntent(tripModel currentTrip){
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getContext(),ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),Helper.tripStringIDToInt(currentTrip.getStringID()),intent,0);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }

    private void removePendingNotificationFromOfflineDatabase(tripModel currentTrip){
        DatabaseHelper db = new DatabaseHelper(getContext());
        db.deletePendingNotification(currentTrip.getStringID());
    }

    private void removeUpcomingTripNotificationNode(tripModel currentTrip){
        DatabaseReference upcomingNodeReference = FirebaseDatabase.getInstance().getReference().child("upcomingTripNotification")
                .child(userUUID)
                .child(currentTrip.getStringID());
        upcomingNodeReference.removeValue();
    }

    private void deleteTrip(final tripModel currentTrip){
        removeFromGroupNode(currentTrip);
        removeInventoryNode(currentTrip);
        removeMemberNode(currentTrip);
        removePendingIntent(currentTrip);
        removePendingNotificationFromOfflineDatabase(currentTrip);
        removeTripNode(currentTrip);
        removeTripDetail(currentTrip);
        removeUpcomingTripNotificationNode(currentTrip);
    }
}