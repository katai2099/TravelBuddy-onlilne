package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.MemberAdapter;
import com.example.travelbuddyv2.model.Member;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity {

    private final String tag = "MEMBER_ACTIVITY";

    View ownerLayout;
    String tripName,tripStringID,fromWho;
    RecyclerView rcvMemberView;

    List<Member> memberList;
    MemberAdapter memberAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Bundle extra = getIntent().getExtras();
        if(extra!=null){
            tripName = extra.getString("TripName");
            tripStringID = extra.getString("TripStringID");
            fromWho = extra.getString("fromWho");
        }
        Log.d(tag,tripName+'\n'+tripStringID+'\n'+fromWho);
        Toast.makeText(getBaseContext(),tripName,Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(),tripStringID,Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(),fromWho,Toast.LENGTH_SHORT).show();

        memberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(memberList);
        rcvMemberView = findViewById(R.id.rcvMemberActivity);
        rcvMemberView.setLayoutManager(new LinearLayoutManager(this));
        rcvMemberView.setAdapter(memberAdapter);
        fillMemberList();

        ownerLayout = findViewById(R.id.layoutForOwner);
        ownerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(),InviteFriendActivity.class);
                i.putExtra("TripName",tripName);
                i.putExtra("TripStringID",tripStringID);
                startActivity(i);

            }
        });
    }


    void fillMemberList(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(tripStringID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot member: snapshot.getChildren()){
                    Member tmp = member.getValue(Member.class);
                    memberList.add(tmp);
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}