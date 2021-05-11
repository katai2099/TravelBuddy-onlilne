package com.example.travelbuddyv2;

import android.os.Bundle;

import com.example.travelbuddyv2.ui.main.GroupSectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.travelbuddyv2.ui.main.SectionsPagerAdapter;

//Group trip detail Activity as a tabbed activity holding two fragment, groupTripDetail Fragment and inventory Fragment.
public class GroupTripDetailActivity extends AppCompatActivity {
    private final String tag = "GROUP_DETAIL_ACTIVITY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_trip_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extra = getIntent().getExtras();
        Log.d(tag,extra.getString("TRIP_STRING_ID"));
        Log.d(tag,extra.getString("TRIP_OWNER"));
        GroupSectionsPagerAdapter groupSectionsPagerAdapter = new GroupSectionsPagerAdapter(this, getSupportFragmentManager(),extra);
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(groupSectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }
}