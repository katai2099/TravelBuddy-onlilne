package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.UserAdapter;
import com.example.travelbuddyv2.model.Request;
import com.example.travelbuddyv2.model.User;
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
                if(TextUtils.isEmpty(etInviteFriend.getText().toString())){
                    Toast.makeText(getBaseContext(),"Please fill",Toast.LENGTH_SHORT).show();
                }
                else{
                    //check if User has Receiver in his/her known lists
                    if(!checkIfEmailExistInKnownList(etInviteFriend.getText().toString()))
                    {
                        fillList(etInviteFriend.getText().toString());
                    }


                }
            }
        });
    }

    //This will fill the list with user known contact from Firebase database =
    public void fillKnownList(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Known_lists")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    User tmp = data.getValue(User.class);
                    suggestedUsers.add(tmp);
                }
                suggestedUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void fillList(final String email){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   // users.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        Log.d(tag, user.toString());
                        if (user.getEmail().equals(email) && !(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(email))) {
                            users.clear();
                            users.add(user);
                            rcvFriendList.setVisibility(View.VISIBLE);
                        }
                        else{
                            Toast.makeText(getBaseContext(),"COULD NOT FIND USER",Toast.LENGTH_SHORT).show();
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                   // suggestedUserAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    public boolean checkIfEmailExistInKnownList(final String email){

        for(int i=0;i<suggestedUsers.size();i++) {
            if (suggestedUsers.get(i).getEmail().equals(email)){
                Log.d(tag,"There exist user in knownlist");
                return true;
            }
        }
        Log.d(tag,"THERE IS NO USER IN KNOWNLIST");
        return false;

    }



}