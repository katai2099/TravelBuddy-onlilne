package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.MemberAdapter;
import com.example.travelbuddyv2.model.Member;
import com.example.travelbuddyv2.model.User;
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

public class MemberActivity extends AppCompatActivity implements MemberAdapter.MemberAdapterCallBack {

    private final String tag = "MEMBER_ACTIVITY";

    View ownerLayout;
    String tripName,tripStringID,fromWho,tripOwnerID;
    RecyclerView rcvMemberView;

    List<Member> memberList;
    MemberAdapter memberAdapter;
    MenuItem leave ;
    boolean isMember = false;
    String ownerUUID ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        Log.d(tag,"On create called");
        Bundle extra = getIntent().getExtras();
        if(extra!=null){
            tripName = extra.getString("TripName");
            tripStringID = extra.getString("TripStringID");
            fromWho = extra.getString("fromWho");
            tripOwnerID = extra.getString("TripOwnerID");
        }
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
        if(!isFromPersonalTripFragment()){
            ownerLayout.setVisibility(View.GONE);
            isMember = true;
        }
        ownerUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        memberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(memberList,this,isMember);
        rcvMemberView = findViewById(R.id.rcvMemberActivity);
        rcvMemberView.setLayoutManager(new LinearLayoutManager(this));
        rcvMemberView.setAdapter(memberAdapter);
        fillMemberList();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        leave = menu.findItem(R.id.optionLeaveGroup);
        if(fromWho.equals("personalTrip"))
            leave.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionLeaveGroup:
                AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
                builder.setMessage("Are you sure you want to leave this group?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       leaveGroup();
                       toGroupTripFragment();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MemberActivity.this,"Guess I still want to be here",Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }




    void fillMemberList(){


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(tripOwnerID)
                .child(tripStringID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();
                for(DataSnapshot member : snapshot.getChildren()){
                    Member tmp = member.getValue(Member.class);
                    Log.d(tag,tmp.toString());
                    Log.d(tag,String.valueOf(memberList.size()));
                    if(memberList.size() == 1 && tmp.getPermission().equals("owner")){
                        Member ownerTmp = new Member();
                        ownerTmp.setName(memberList.get(0).getName());
                        ownerTmp.setPermission(memberList.get(0).getPermission());
                        ownerTmp.setID(memberList.get(0).getID());
                        ownerTmp.setEmail(memberList.get(0).getEmail());
                        ownerTmp.setProfileImg(memberList.get(0).getProfileImg());

                        memberList.get(0).setEmail(tmp.getEmail());
                        memberList.get(0).setID(tmp.getID());
                        memberList.get(0).setPermission(tmp.getPermission());
                        memberList.get(0).setName(tmp.getName());
                        memberList.get(0).setProfileImg(tmp.getProfileImg());

                        tmp.setName(ownerTmp.getName());
                        tmp.setPermission(ownerTmp.getPermission());
                        tmp.setID(ownerTmp.getID());
                        tmp.setEmail(ownerTmp.getEmail());
                        tmp.setProfileImg(ownerTmp.getProfileImg());

                    }
                    memberList.add(tmp);
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private boolean isFromPersonalTripFragment(){
        return fromWho.equals("personalTrip");
    }

    private void leaveGroup(){

        String currentUserUUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //remove user from Group Node
        DatabaseReference userGroupNode = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(currentUserUUID)
                .child(tripOwnerID)
                .child(tripStringID);

        //remove user from memberNode
        final DatabaseReference userMemberNode = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(tripOwnerID)
                .child(tripStringID)
                .child(currentUserUUID);

        DatabaseReference userNode; // we need to get deletingMemberDeviceToken;
        final DatabaseReference upcomingNotificationNode = FirebaseDatabase.getInstance().getReference().child("upcomingTripNotification")
                .child(tripOwnerID)
                .child(tripStringID);
        userNode = FirebaseDatabase.getInstance().getReference().child("User")
                .child(currentUserUUID);
        userNode.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User deleteMember = dataSnapshot.getValue(User.class);
                if(deleteMember!=null){
                    upcomingNotificationNode.child(deleteMember.getDeviceToken()).removeValue();
                    Log.d(tag,deleteMember.getDeviceToken());
                }
            }
        });


        userGroupNode.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(tag,"Done remove from group NODE ");
                userMemberNode.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(tag,"Done remove from Member NODE");
                        Toast.makeText(MemberActivity.this,"Done leaving",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void toGroupTripFragment(){
        Intent i = new Intent(MemberActivity.this,Main2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("changeToGroup",true);
        startActivity(i);
        finish();
    }

    @Override
    public void onMemberListClicked(int position) {

        if(isFromPersonalTripFragment()) {
            if (position == 0) {
                Toast.makeText(getBaseContext(), "Owner permission cannot be changed", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(this, PermissionModificationActivity.class);
                i.putExtra("TripOwnerID", tripOwnerID);
                i.putExtra("TripStringID", tripStringID);
                i.putExtra("MemberID", memberList.get(position).getID());
                startActivity(i);
            }
        }
    }

    @Override
    public void onMemberDeleteClicked(int position) {
        if(isFromPersonalTripFragment()){
            if(position==0){
                Toast.makeText(MemberActivity.this,"Owner cannot be deleted from group",Toast.LENGTH_SHORT).show();
            }else{
                showAskingForDeleteDialog(position);
            }
        }
    }

    private void showAskingForDeleteDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
        builder.setMessage("Are you sure you want to remove this member from group?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeFromGroup(position);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MemberActivity.this,"Well, he/she can be here then",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeFromGroup(int position){

        removeFromMemberNode(position);
        removeGroupNode(position);
        removeFromUpcomingNotificationNode(position);

    }

    private void removeGroupNode(int position){
        Member deletingMember = memberList.get(position);
        DatabaseReference groupNode ;
        groupNode = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(deletingMember.getID())
                .child(ownerUUID)
                .child(tripStringID);
        groupNode.removeValue();
    }

    private void removeFromMemberNode(int position){
        Member deletingMember = memberList.get(position);
        DatabaseReference memberNode ;
        memberNode = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(ownerUUID)
                .child(tripStringID)
                .child(deletingMember.getID());
        memberNode.removeValue();
    }

    private void removeFromUpcomingNotificationNode(int position){
        final Member deletingMember = memberList.get(position);
        DatabaseReference userNode; // we need to get deletingMemberDeviceToken;
        final DatabaseReference upcomingNotificationNode = FirebaseDatabase.getInstance().getReference().child("upcomingTripNotification")
                .child(ownerUUID)
                .child(tripStringID);
        userNode = FirebaseDatabase.getInstance().getReference().child("User")
                .child(deletingMember.getID());
        userNode.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User deleteMember = dataSnapshot.getValue(User.class);
                if(deleteMember!=null){
                    upcomingNotificationNode.child(deleteMember.getDeviceToken()).removeValue();
                    Log.d(tag,deleteMember.getDeviceToken());
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag,"MemberActivity onResume called");
        Log.d(tag,tripName+'\n'+tripStringID+'\n'+fromWho);
        fillMemberList();
    }
}