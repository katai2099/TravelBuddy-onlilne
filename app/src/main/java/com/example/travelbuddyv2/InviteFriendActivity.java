package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.UserAdapter;
import com.example.travelbuddyv2.model.Request;
import com.example.travelbuddyv2.model.User;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.example.travelbuddyv2.utils.Snack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendActivity extends AppCompatActivity {

    private final String tag = "INVITE_FRIEND_ACTIVITY";
    EditText etInviteFriend;
    RecyclerView rcvFriendList , rcvSuggestionFriendList;
    Button btnSearchForFriend;
    List<User> users, suggestedUsers;
    UserAdapter userAdapter, suggestedUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        Bundle extra = getIntent().getExtras();
        String tripName = extra.getString("TripName");
        String tripStringID = extra.getString("TripStringID");
        Log.d(tag,"Information pass by Intent " + tripName + " " + tripStringID);
        suggestedUsers = new ArrayList<>();
        users = new ArrayList<>();
        etInviteFriend = findViewById(R.id.etFindFriendByEmail);
        rcvFriendList = findViewById(R.id.rcvInviteFriend);
        rcvSuggestionFriendList = findViewById(R.id.rcvSuggestedFriend);
        btnSearchForFriend = findViewById(R.id.btnSearchForFriend);
        rcvFriendList.setLayoutManager(new LinearLayoutManager(this));
        rcvSuggestionFriendList.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(users,tripName,tripStringID);
        suggestedUserAdapter = new UserAdapter(suggestedUsers,tripName,tripStringID);
        rcvFriendList.setAdapter(userAdapter);
        rcvSuggestionFriendList.setAdapter(suggestedUserAdapter);
        rcvFriendList.setVisibility(View.GONE);
        fillKnownList();
        btnSearchForFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              searchForFriend();
            }
        });

        etInviteFriend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    Log.d(tag,"ACTION SEARCH TRIGGERED");
                    searchForFriend();
                    return true;
                }
                return false;
            }
        });

    }

    private void searchForFriend(){
        if(NetworkObserver.isNetworkConnected) {
            if (TextUtils.isEmpty(etInviteFriend.getText().toString())) {
                Toast.makeText(getBaseContext(), "Please fill user email", Toast.LENGTH_SHORT).show();
            } else {
                //check if User has Receiver in his/her known lists
                if (!checkIfEmailExistInKnownList(etInviteFriend.getText().toString().trim())) {
                    fillSearchedFriendList(etInviteFriend.getText().toString().trim());
                }else{
                    Toast.makeText(InviteFriendActivity.this,"User already on known lists",Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Helper.showSnackBar(rcvSuggestionFriendList,getString(R.string.noInternet));
        }
    }
    //This will fill the list with user known contact from Firebase database =
    public void fillKnownList(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Known_lists")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    User tmp = data.getValue(User.class);
                    suggestedUsers.add(tmp);
                }
                suggestedUserAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InviteFriendActivity.this,getString(R.string.unexpectedBehavior),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fillSearchedFriendList(final String email){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");
            reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    boolean userDoesNotExist = true;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if (user.getEmail().equals(email) && !(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(email))) {
                            users.clear();
                            users.add(user);
                            rcvFriendList.setVisibility(View.VISIBLE);
                            userDoesNotExist = false;
                        }
                    }
                    if(userDoesNotExist)
                        Toast.makeText(getBaseContext(),"COULD NOT FIND USER",Toast.LENGTH_SHORT).show();
                    userAdapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(InviteFriendActivity.this,getString(R.string.unexpectedBehavior),Toast.LENGTH_SHORT).show();
                }
            });


    }

    public boolean checkIfEmailExistInKnownList(final String email){
        for(int i=0;i<suggestedUsers.size();i++) {
            if (suggestedUsers.get(i).getEmail().equals(email)){
                Log.d(tag,"There exist user in knownList");
                return true;
            }
        }
        Log.d(tag,"THERE IS NO USER IN KNOWNLIST");
        return false;
    }



}