package com.example.travelbuddyv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    boolean toNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            toNotification = Boolean.parseBoolean(bundle.getString("changeToNotificationFragment")) ;
        }
        userLogIn(toNotification);
    }

    private void userLogIn(boolean payload){
        sharedPreferences = getSharedPreferences(getString(R.string.appSharedPref),MODE_PRIVATE);
        if(!sharedPreferences.getString("token", "").equals("")){
            Intent i = new Intent(SplashScreenActivity.this,Main2Activity.class);
            if(payload)
                i.putExtra("changeToNotificationFragment",true);
            startActivity(i);
            finish();
        }else{
            Intent i  = new Intent(SplashScreenActivity.this,loginActivity.class);
            if(payload)
                i.putExtra("changeToNotificationFragment",true);
            startActivity(i);
            finish();
        }
    }
}