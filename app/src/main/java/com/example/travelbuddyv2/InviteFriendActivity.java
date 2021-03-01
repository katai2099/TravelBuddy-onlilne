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
    RecyclerView rcvFriendList;
    Button btnSearchForFriend;
    List<User> users;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        Bundle extra = getIntent().getExtras();


        String tripName = extra.getString("TripName");
        String tripStringID = extra.getString("TripStringID");

        Log.d(tag,"Information pass by Intent " + tripName + " " + tripStringID);


        users = new ArrayList<>();
        etInviteFriend = findViewById(R.id.etFindFriendByEmail);
        rcvFriendList = findViewById(R.id.rcvInviteFriend);
        btnSearchForFriend = findViewById(R.id.btnSearchForFriend);

        rcvFriendList.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(users,tripName,tripStringID);



        rcvFriendList.setAdapter(userAdapter);

        btnSearchForFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etInviteFriend.getText().toString())){
                    Toast.makeText(getBaseContext(),"Please fill",Toast.LENGTH_SHORT).show();
                }
                else{
                    fillList(etInviteFriend.getText().toString());
                }
            }
        });
    }

    public void fillKnownList(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Known_lists")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    public void fillList(final String email){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                   User user = dataSnapshot.getValue(User.class);
                    Log.d(tag,user.getName() + " " + user.getEmail());
                    if(user.getEmail().equals(email) &&  !(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(email) ) ){
                        users.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}