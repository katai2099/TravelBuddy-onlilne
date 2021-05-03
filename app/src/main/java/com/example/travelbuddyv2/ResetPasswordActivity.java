package com.example.travelbuddyv2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private final String tag = "RESET_PASSWORD_ACTIVITY" ;
    Button btnSend;
    EditText etEmail;
    TextInputLayout toggleEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initField();
    }

    private void initField(){
        btnSend = findViewById(R.id.btnResetPasswordSend);
        etEmail = findViewById(R.id.etResetPasswordEmail);
        toggleEmail = findViewById(R.id.resetPasswordEmailToggle);
        behavior();
    }

    private void behavior(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                if(!TextUtils.isEmpty(email)){
                    sendPasswordResetEmail(email);
                }else{
                    toggleEmail.setError(getString(R.string.fieldRequired));
                }
            }
        });
        onTextChangedBehavior();
    }

    private void onTextChangedBehavior(){
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // Will send a password reset link to the email provided by the user
    private void sendPasswordResetEmail(final String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(tag, "onComplete: Password Reset Email sent.");
                            Toast.makeText(ResetPasswordActivity.this, R.string.password_reset_link_sent, Toast.LENGTH_SHORT).show();
                            toResetPasswordCompletedActivity(email);
                        } else {
                            Log.d(tag, "onComplete: No user associated with that email.");
                            Toast.makeText(ResetPasswordActivity.this, R.string.no_user_associated_with_email, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void toResetPasswordCompletedActivity(String email){
        Intent i = new Intent(ResetPasswordActivity.this,ResetPasswordCompletedActivity.class);
        i.putExtra("EMAIL",email);
        startActivity(i);
        finish();
    }

}
