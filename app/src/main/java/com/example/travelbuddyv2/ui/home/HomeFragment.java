package com.example.travelbuddyv2.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.addNewTrip;
import com.example.travelbuddyv2.loginActivity;
import com.example.travelbuddyv2.myTrip;
import com.example.travelbuddyv2.tripModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FloatingActionButton fbtnAddNewTrip;

    ListView lvTrip;

    List<tripModel> trips  ;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        setHasOptionsMenu(true);

        fbtnAddNewTrip = root.findViewById(R.id.fbtnFragmentHomeAddNewTrip);

        lvTrip = root.findViewById(R.id.lvFragmentHomeTriplist);

        

        fbtnAddNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getActivity(),addNewTrip.class);
                startActivity(i);
            }
        });



        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionSignout:
                signOut(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut(final Activity activity){
        Intent i = new Intent(getActivity(), loginActivity.class);
        startActivity(i);
        activity.finishAffinity();
        FirebaseAuth.getInstance().signOut();

    }}

