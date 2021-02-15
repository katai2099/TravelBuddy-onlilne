package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {

    Button btnSignUp , btnSignIn , btnPasswordReset;
    EditText etEmail, etPassword;

    FirebaseAuth auth;

    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignIn = findViewById(R.id.btnLoginSignIn);
        btnSignUp = findViewById(R.id.btnLoginSignUp);
        btnPasswordReset = findViewById(R.id.btnLoginResetPassword);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        auth = FirebaseAuth.getInstance();

        authentication();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(loginActivity.this,registerActivity.class);
                startActivity(i);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = etEmail.getText().toString();
                String txtPassword = etPassword.getText().toString();
                auth = FirebaseAuth.getInstance();
                loginUser(txtEmail,txtPassword);

                FirebaseUser user = auth.getCurrentUser();


                if( user!=null&&!user.isEmailVerified() ){
                    Toast.makeText(loginActivity.this,"Please validate your email first",Toast.LENGTH_SHORT).show();
                    sendValidationEmail();
                    auth.signOut();

                }



            }
        });

        btnPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                if(user!=null) {
                    Boolean check = user.isEmailVerified();
                    Toast.makeText(loginActivity.this, check.toString(), Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    user.reload();
                }


            }
        });


    }

    private void loginUser(String email,String password){


        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(loginActivity.this,"Log in successful",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(loginActivity.this,Main2Activity.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Toast.makeText(loginActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendValidationEmail(){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(loginActivity.this,"Verification email sent to " + user.getEmail(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(loginActivity.this,"Fail to send verification email",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private  void authentication(){
        Toast.makeText(loginActivity.this,"This is called",Toast.LENGTH_SHORT).show();
       authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user registered, check if user is verified
                    if (user.isEmailVerified()) {


                        Intent intent = new Intent(loginActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // user is registered, but not verified yet through email

                        FirebaseAuth.getInstance().signOut();
                    }
                } else {
                    // User is signed out

                }
            }
        };


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }


}