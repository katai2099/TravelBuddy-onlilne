package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PermissionModifyActivity extends AppCompatActivity {

    TextView tvCanEdit,tvCanView, tvOptionDescription;
    boolean currentDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_modify);
        tvCanEdit = findViewById(R.id.tvCanEdit);
        tvCanView = findViewById(R.id.tvCanView);
        tvOptionDescription = findViewById(R.id.tvOptionDescription);
        currentDescription = false;
    }



    private void getCurrentGroupMemberPermission(){

    }

}