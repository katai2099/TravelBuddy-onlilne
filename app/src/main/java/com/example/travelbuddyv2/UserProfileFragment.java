package com.example.travelbuddyv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UserProfileFragment extends Fragment {

    TextView tvUserEmail;
    EditText etUserName;
    Button btnLogOut , btnEditUserName;
    ImageView imgUserProfileImage;
    User user;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);

        etUserName = root.findViewById(R.id.etProfileName);
        tvUserEmail = root.findViewById(R.id.tvProfileEmail);
        btnLogOut = root.findViewById(R.id.btnUserLogOut);
        imgUserProfileImage = root.findViewById(R.id.imgProfilePic);
        btnEditUserName = root.findViewById(R.id.btnEditProfileName);
        user = new User();
        getCurrentUserInformation();

        imgUserProfileImage.setImageResource(R.drawable.ic_baseline_person_24);


        btnEditUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserName.setEnabled(true);
                etUserName.setFocusable(true);
                btnEditUserName.setVisibility(View.GONE);
                etUserName.setSelection(etUserName.getText().toString().length());
                etUserName.requestFocus();
                getContext();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    btnEditUserName.setVisibility(View.VISIBLE);
                    etUserName.setEnabled(false);
                }

            }
        });




      /*  root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEditUserName.setVisibility(View.VISIBLE);
                etUserName.setEnabled(false);
            }
        });*/

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

    private void getCurrentUserInformation(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User tmp = dataSnapshot.getValue(User.class);
                user.setName(tmp.getName());
                user.setEmail(tmp.getEmail());
                user.setUser_id(tmp.getUser_id());

                etUserName.setText(user.getName());

                tvUserEmail.setText(user.getEmail());
            }
        });

    }

}