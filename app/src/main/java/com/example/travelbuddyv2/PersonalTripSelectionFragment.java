package com.example.travelbuddyv2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.TripSelectionAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.tripModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class PersonalTripSelectionFragment extends Fragment implements TripSelectionAdapter.TripSelectionAdapterCallback {

    private final String tag = "PERSONAL_SELECTION";
    List<tripModel> tripModelList;
    TripSelectionAdapter tripSelectionAdapter;
    RecyclerView rcvTrip;
    //This destination is used to pass to next activity
    Destination destination;


    public PersonalTripSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_personal_trip_selection, container, false);
        Bundle bundle = getArguments();
        String googleMapPlaceName = bundle.getString("googleMapPlaceName");
        String googleMapPlaceID = bundle.getString("googleMapPlaceID");
        double googleMapPlaceLat = bundle.getDouble("googleMapPlaceLat");
        double googleMapPlaceLong = bundle.getDouble("googleMapPlaceLong");
        Log.d(tag, googleMapPlaceName);
        Log.d(tag, googleMapPlaceID);
        Log.d(tag, String.valueOf(googleMapPlaceLat));
        Log.d(tag, String.valueOf(googleMapPlaceLong));

        destination = new Destination();

        destination.setName(googleMapPlaceName);
        destination.setPlaceId(googleMapPlaceID);
        destination.setLatitude(googleMapPlaceLat);
        destination.setLongitude(googleMapPlaceLong);

        tripModelList = new ArrayList<>();
        tripSelectionAdapter = new TripSelectionAdapter(tripModelList,this);
        rcvTrip = root.findViewById(R.id.rcvFragmentPersonalTripSelection);
        rcvTrip.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvTrip.setAdapter(tripSelectionAdapter);
        fillListWithTrip();


        return root;
    }


    private void fillListWithTrip() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    tripModel tmp = data.getValue(tripModel.class);
                    tripModelList.add(tmp);
                }
                tripSelectionAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onTripClicked(int position) {
        Intent i = new Intent(getContext(), DateSelectionActivity.class);

        tripModel trip = tripModelList.get(position);

        i.putExtra("tripStringId",trip.getStringID());
        i.putExtra("googleMapPlaceName",destination.getName());
        i.putExtra("googleMapPlaceID",destination.getPlaceId());
        i.putExtra("googleMapPlaceLat",destination.getLatitude());
        i.putExtra("googleMapPlaceLong",destination.getLongitude());
        i.putExtra("isFromPersonalTrip",true);
        startActivity(i);
    }
}