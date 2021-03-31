package com.example.travelbuddyv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordCompletedActivity extends AppCompatActivity {

    private final String tag  = "RESET_PASSWORD_COMPLETE";
    TextView tvResendLink;
    Button btnLogin;
    String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            email = bundle.getString("EMAIL");
        }

        setContentView(R.layout.activity_password_reset_completed);
        initField();
        behavior();

    }

    private void initField(){
        tvResendLink = findViewById(R.id.tvResendPasswordResetLink);
        btnLogin = findViewById(R.id.btnResetPasswordLogin);
    }

    private void behavior(){
        tvResendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetEmail(email);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLoginActivity();
            }
        });
    }

    private void sendPasswordResetEmail(final String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(tag, "onComplete: Password Reset Email sent.");
                            Toast.makeText(ResetPasswordCompletedActivity.this, R.string.password_reset_link_sent, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(tag, "onComplete: No user associated with that email.");
                            Toast.makeText(ResetPasswordCompletedActivity.this, R.string.no_user_associated_with_email, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void toLoginActivity(){
        Intent i = new Intent(ResetPasswordCompletedActivity.this,loginActivity.class);
        startActivity(i);
        finish();
    }

}
