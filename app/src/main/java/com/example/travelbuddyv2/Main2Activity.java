package com.example.travelbuddyv2;

import android.os.Bundle;
import android.widget.Toast;

import com.example.travelbuddyv2.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

    String changeToGroupTripFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Bundle bundle = getIntent().getExtras();

        changeToGroupTripFragment = "";
        if(bundle!=null){
            changeToGroupTripFragment = bundle.getString("changeToGroup");
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

        if(changeToGroupTripFragment.length()>1)
        {
            navController.navigate(R.id.navigation_dashboard,bundle);
           // NavGraph  navGraph = navController.getGraph();
           // navGraph.setStartDestination();

        }

    }

    public interface MyInterface{
        public void onClick(int position);
    }

}