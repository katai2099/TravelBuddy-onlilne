package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Intent;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;


public class myTrip extends AppCompatActivity implements myTripAdapter.OnListListener {

    RecyclerView rcVTripList;
    DatabaseHelper databaseHelper;
    myTripAdapter myTripadapter;
    List<tripModel> list;
    FloatingActionButton addNewTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);
        this.setTitle("Trip List");
        rcVTripList = findViewById(R.id.rcvTripList);
        rcVTripList.setLayoutManager(new LinearLayoutManager(this));
        addNewTrip = findViewById(R.id.fbtAddNewTrip);
        databaseHelper = new DatabaseHelper(myTrip.this);
        list = databaseHelper.getTripList();
        Collections.sort(list,new tripModel.SortbystartDate());

        myTripadapter = new myTripAdapter(this,list,this);
        rcVTripList.setAdapter(myTripadapter);
        rcVTripList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcVTripList);

        addNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(myTrip.this,addNewTrip.class);
                startActivity(i);
                finish();
            }
        });

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
        //    Toast.makeText(myTrip.this,"You have swipe " + new StringBuilder().append(viewHolder.getAdapterPosition()).toString(),Toast.LENGTH_SHORT).show();
            databaseHelper = new DatabaseHelper(myTrip.this);
            databaseHelper.DeleteTrip(list.get(viewHolder.getAdapterPosition()).getId());
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(myTrip.this,ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(myTrip.this,list.get(viewHolder.getAdapterPosition()).getId(),intent,0);
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
            list.remove(viewHolder.getAdapterPosition());
            myTripadapter.notifyDataSetChanged();

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.options_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionSignout:
                signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut(final Activity activity){
        Intent i = new Intent(myTrip.this,loginActivity.class);
        startActivity(i);
        activity.finishAffinity();
        FirebaseAuth.getInstance().signOut();

    }}