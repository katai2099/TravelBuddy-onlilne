package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.travelbuddyv2.adapter.MemberAdapter;
import com.example.travelbuddyv2.model.Member;
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
        //Log.d(tag,tripName+'\n'+tripStringID+'\n'+fromWho);
       // Toast.makeText(getBaseContext(),tripName,Toast.LENGTH_SHORT).show();
       // Toast.makeText(getBaseContext(),tripStringID,Toast.LENGTH_SHORT).show();
       // Toast.makeText(getBaseContext(),fromWho,Toast.LENGTH_SHORT).show();

        memberList = new ArrayList<>();
        memberAdapter = new MemberAdapter(memberList,this);
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
        if(!isFromPersonalTripFragment())
            ownerLayout.setVisibility(View.GONE);


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

                        memberList.get(0).setEmail(tmp.getEmail());
                        memberList.get(0).setID(tmp.getID());
                        memberList.get(0).setPermission(tmp.getPermission());
                        memberList.get(0).setName(tmp.getName());

                        tmp.setName(ownerTmp.getName());
                        tmp.setPermission(ownerTmp.getPermission());
                        tmp.setID(ownerTmp.getID());
                        tmp.setEmail(ownerTmp.getEmail());

                    }
                    memberList.add(tmp);
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();
                for(DataSnapshot member: snapshot.getChildren()){
                    Member tmp = member.getValue(Member.class);
                    memberList.add(tmp);
                }
                memberAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

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
        i.putExtra("changeToGroup","changeToGroup");
        startActivity(i);
        finish();
    }

    @Override
    public void onMemberListClicked(int position) {
      //  Toast.makeText(getBaseContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();
        if(position==0){
            Toast.makeText(getBaseContext(),"Owner permission cannot be changed",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent i = new Intent(this, PermissionModificationActivity.class);
            i.putExtra("TripOwnerID", tripOwnerID);
            i.putExtra("TripStringID", tripStringID);
            i.putExtra("MemberID", memberList.get(position).getID());
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag,"MemberActivity onResume called");
        Log.d(tag,tripName+'\n'+tripStringID+'\n'+fromWho);
        fillMemberList();
    }
}