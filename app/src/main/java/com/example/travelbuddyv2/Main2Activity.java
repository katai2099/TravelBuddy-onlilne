package com.example.travelbuddyv2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.travelbuddyv2.model.tripModel;
import com.example.travelbuddyv2.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class Main2Activity extends AppCompatActivity {

    private final String tag = "MAIN_2_ACTIVITY";

    alarmHandler handler;

    boolean changeToGroupTripFragment=false;
    boolean changeToMapFragment=false;
    boolean changeToNotificationFragment=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.appSharedPref),MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isFirstRetrievePendingNotification",true)){
            retrievePendingNotificationFromFirebaseDatabase();
        }else{
            handler = new alarmHandler(Main2Activity.this);
            Log.d(tag,"SKIP RETRIEVE PENDING NOTIFICATION");
        }
        createNotificationChannel();

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            changeToGroupTripFragment = bundle.getBoolean("changeToGroup");
            changeToMapFragment = bundle.getBoolean("changeToMapFragment");
            changeToNotificationFragment = bundle.getBoolean("changeToNotificationFragment");
        }


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if(changeToGroupTripFragment)
        {
            navController.navigate(R.id.navigation_dashboard,bundle);
           // NavGraph  navGraph = navController.getGraph();
           // navGraph.setStartDestination();
        }
        else if(changeToMapFragment)
        {
            navController.navigate(R.id.navigation_map);
        }
        else if(changeToNotificationFragment){
            navController.navigate(R.id.navigation_notifications);
        }


    }


    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notify trip invitation request";
            String description = "Channel for Trip invitation request";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NotifyTripInvitationRequest",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            //    Toast.makeText(MainActivity.this,"Function Trigger!",Toast.LENGTH_SHORT).show();
        }
    }

    private void retrievePendingNotificationFromFirebaseDatabase(){
        Log.d(tag,"RETRIEVE_PENDING_NOTIFICATION");
        final DatabaseHelper db = new DatabaseHelper(Main2Activity.this);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Pending_notification_backup")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot notificationData: dataSnapshot.getChildren()){
                    tripModel currentTripModel = notificationData.getValue(tripModel.class);
                    db.addNewPendingNotification(currentTripModel.getTripName(),currentTripModel.getStringID(),currentTripModel.getStartDate());
                }
                handler = new alarmHandler(Main2Activity.this);
                SharedPreferences sharedPreferences =  getSharedPreferences(getString(R.string.appSharedPref),MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isFirstRetrievePendingNotification",false).apply();
                deletePendingNotificationNode();
            }
        });
    }

    private void deletePendingNotificationNode(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Pending_notification_backup")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.removeValue();
    }

}