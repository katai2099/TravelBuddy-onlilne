package com.example.travelbuddyv2;

import android.os.Bundle;

import com.example.travelbuddyv2.ui.main.GroupSectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.travelbuddyv2.ui.main.SectionsPagerAdapter;

public class GroupTripDetailActivity extends AppCompatActivity {

    private final String tag = "GROUP_DETAIL_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_trip_detail);
        Bundle extra = getIntent().getExtras();
        Log.d(tag,extra.getString("TRIP_STRING_ID"));
        Log.d(tag,extra.getString("TRIP_OWNER"));
        GroupSectionsPagerAdapter groupSectionsPagerAdapter = new GroupSectionsPagerAdapter(this, getSupportFragmentManager(),extra);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(groupSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
     /*   FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}