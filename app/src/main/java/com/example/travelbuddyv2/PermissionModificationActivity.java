package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbuddyv2.model.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PermissionModificationActivity extends AppCompatActivity {

    private final String tag = "PERMISSION_ACTIVITY";

    TextView tvCanEdit,tvCanView, tvOptionDescription;
    String tripOwnerID, tripStringID , memberID ;
    ImageView tick1 , tick2;
    boolean editTextViewFocused, viewTextViewFocused;
    DatabaseReference memberReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_modification);
        tvCanEdit = findViewById(R.id.tvCanEdit);
        tvCanView = findViewById(R.id.tvCanView);
        tvOptionDescription = findViewById(R.id.tvOptionDescription);
        tick1 = findViewById(R.id.imgTick1);
        tick2 = findViewById(R.id.imgTick2);

        Bundle extra = getIntent().getExtras();
        if(extra!=null){
            tripOwnerID = extra.getString("TripOwnerID");
            tripStringID = extra.getString("TripStringID");
            memberID = extra.getString("MemberID");
        }
        //Focus means what permission does owner give to member
        editTextViewFocused = false;
        viewTextViewFocused = false;
        memberReference = FirebaseDatabase.getInstance().getReference().child("Member")
                .child(tripOwnerID)
                .child(tripStringID)
                .child(memberID);
        Toast.makeText(getBaseContext(),tripStringID,Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(),tripOwnerID,Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(),memberID,Toast.LENGTH_SHORT).show();
        getCurrentGroupMemberPermission();
        Log.d(tag,String.valueOf(editTextViewFocused));
        Log.d(tag,String.valueOf(viewTextViewFocused));
        editTextViewBehavior();
        viewTextViewBehavior();


    }



    private void getCurrentGroupMemberPermission(){
        memberReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member currentMember = snapshot.getValue(Member.class);
                if(currentMember != null && currentMember.getPermission().equals("edit"))
                {
                    tick1.setVisibility(View.VISIBLE);
                    tick2.setVisibility(View.INVISIBLE);
                    editTextViewFocused = true;
                    viewTextViewFocused = false;
                }
                else{
                    tick1.setVisibility(View.INVISIBLE);
                    tick2.setVisibility(View.VISIBLE);
                    editTextViewFocused = false;
                    viewTextViewFocused = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editTextViewBehavior(){
        tvCanEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextViewFocused) {
                    memberReference.child("permission").setValue("edit");
                   // Toast.makeText(getBaseContext(), "Clicked " + editTextViewFocused, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Test if both textView are being focused or not
    private void viewTextViewBehavior(){
        tvCanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewTextViewFocused) {
                    memberReference.child("permission").setValue("view");
                   // Toast.makeText(getBaseContext(), "Clicked " + viewTextViewFocused, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}