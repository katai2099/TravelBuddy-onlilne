package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.List;


public class myTrip extends AppCompatActivity implements myTripAdapter.OnListListener {

    RecyclerView rcVTripList;
    DatabaseHelper databaseHelper;
    myTripAdapter myTripadapter;
    List<tripModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);
        rcVTripList = findViewById(R.id.rcvTripList);
        rcVTripList.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(myTrip.this);
        list = databaseHelper.getTripList();
        myTripadapter = new myTripAdapter(this,list,this);
        rcVTripList.setAdapter(myTripadapter);
        rcVTripList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcVTripList);

    }

    @Override
    public void onListClick(int position) {

        Intent i = new Intent(myTrip.this,tripDetail.class);
        i.putExtra("TripID",list.get(position).getId());
        startActivity(i);
    }

    ItemTouchHelper.SimpleCallback simpleCallback =
            new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Toast.makeText(myTrip.this,"You have swipe",Toast.LENGTH_SHORT).show();
            myTripadapter.notifyDataSetChanged();
        }


    };

}