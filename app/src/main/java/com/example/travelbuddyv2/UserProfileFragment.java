package com.example.travelbuddyv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class UserProfileFragment extends Fragment {

    TextView tvUserName;
    Button btnLogOut;
    ImageView imgUserProfileImage;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);

        tvUserName = root.findViewById(R.id.tvProfileName);
        btnLogOut = root.findViewById(R.id.btnUserLogOut);
        imgUserProfileImage = root.findViewById(R.id.imgProfilePic);

        imgUserProfileImage.setImageResource(getResources().getIdentifier("@drawable/ic_baseline_person_24",null,getContext().getPackageName()));

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(getActivity());
            }
        });

        return root;
    }

    private void signOut(final Activity activity){
        Intent i = new Intent(getActivity(), loginActivity.class);
        startActivity(i);
        activity.finishAffinity();
        FirebaseAuth.getInstance().signOut();

    }

}