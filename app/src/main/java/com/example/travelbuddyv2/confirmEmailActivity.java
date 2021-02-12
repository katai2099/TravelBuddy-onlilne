package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class confirmEmailActivity extends AppCompatActivity {

    TextView tvUserEmail;
    Button btnResendConfirmationEmail;
    String userEmail;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnResendConfirmationEmail = findViewById(R.id.btnResendConfirmationEMail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        userEmail = user.getEmail();

        tvUserEmail.setText(userEmail);

        btnResendConfirmationEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendValidationEmail();
            }
        });


    }

    private void sendValidationEmail(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(confirmEmailActivity.this,"Verification email sent to " + user.getEmail(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(confirmEmailActivity.this,"Fail to send verification email",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}