package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MemberActivity extends AppCompatActivity {

    private final String tag = "MEMBER_ACTIVITY";

    View ownerLayout;
    String tripName,tripStringID,fromWho;
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
}