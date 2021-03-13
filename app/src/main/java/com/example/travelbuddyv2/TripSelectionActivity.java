package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.travelbuddyv2.adapter.TripSelectionAdapter;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.tripModel;
import com.example.travelbuddyv2.ui.main.TripSelectionPagerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TripSelectionActivity extends AppCompatActivity {

    RecyclerView rcvTrip;
    TripSelectionAdapter tripSelectionAdapter;
    List<tripModel> tripModelList;
    Destination destination ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_selection);


        destination = new Destination();

        Bundle bundle = getIntent().getExtras();
/*
        if(bundle!=null){
            destination.setName(bundle.getString("googleMapPlaceName"));
            destination.setPlaceId(bundle.getString("googleMapPlaceID"));
            destination.setLatitude(bundle.getDouble("googleMapPlaceLat"));
            destination.setLongitude(bundle.getDouble("googleMapPlaceLong"));
        }

        tripModelList = new ArrayList<>();
        tripSelectionAdapter = new TripSelectionAdapter(tripModelList,destination);
        rcvTrip = findViewById(R.id.rcvTripSelection);
        rcvTrip.setLayoutManager(new LinearLayoutManager(this));
        rcvTrip.setAdapter(tripSelectionAdapter);
        fillListWithTrip();*/

        TripSelectionPagerAdapter tripSelectionPagerAdapter = new TripSelectionPagerAdapter(this,getSupportFragmentManager(),bundle);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(tripSelectionPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


    }






}