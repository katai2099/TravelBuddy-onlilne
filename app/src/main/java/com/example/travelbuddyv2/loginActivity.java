package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class loginActivity extends AppCompatActivity {

    Button btnSignUp , btnSignIn , btnPasswordReset;
    EditText etEmail, etPassword;

    FirebaseAuth auth;

    private final String tag = "LOGIN_ACTIVITY";

    private FirebaseAuth.AuthStateListener authStateListener;
    boolean toNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            toNotification = Boolean.parseBoolean(bundle.getString("changeToNotificationFragment")) ;
        }

       // authentication(toNotification);
        setContentView(R.layout.activity_login);
        btnSignIn = findViewById(R.id.btnLoginSignIn);
        btnSignUp = findViewById(R.id.btnLoginSignUp);
        btnPasswordReset = findViewById(R.id.btnLoginResetPassword);
        etEmail = findViewById(R.id.etLoginEmail);
        etPassword = findViewById(R.id.etLoginPassword);
        auth = FirebaseAuth.getInstance();




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


            }
        });

        btnPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(loginActivity.this,ResetPasswordActivity.class);
                startActivity(i);
            }
        });


    }

    private void loginUser(String email,String password){


        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(tag,"I am here logging in");
                    if(isEmailVerified()) {
//                        Intent i = new Intent(loginActivity.this, Main2Activity.class);
//                        startActivity(i);
//                        finish();

                    //    authentication(toNotification);

                        registerDeviceToken(toNotification);

                    }else{
                        Toast.makeText(loginActivity.this,"Please validate your email first",Toast.LENGTH_SHORT).show();
                        if(task.getResult().getUser()!=null){
                            task.getResult().getUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    auth.signOut();
                                }
                            });
                        }
                    }
                }
                else{
                    Toast.makeText(loginActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerDeviceToken(final boolean payload){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                        .child(user.getUid())
                        .child("deviceToken");

                reference.setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.appSharedPref),MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token",user.getUid());
                        editor.apply();

                        Intent intent = new Intent(loginActivity.this, Main2Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        if(payload){
                            intent.putExtra("changeToNotificationFragment",true);
                        }
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(loginActivity.this,"CANT GET DEVICE TOKEN",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private  void authentication(final boolean payload){
        Toast.makeText(loginActivity.this,"This is called",Toast.LENGTH_SHORT).show();
       authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user registered, check if user is verified
                    if (user.isEmailVerified()) {

                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User")
                                        .child(user.getUid())
                                        .child("deviceToken");

                                reference.setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(loginActivity.this, Main2Activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        if(payload){
                                            intent.putExtra("changeToNotificationFragment",true);
                                        }
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(loginActivity.this,"CANT GET DEVICE TOKEN",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
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
       // FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (authStateListener != null) {
//            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
//        }
    }

    private boolean isEmailVerified(){
        FirebaseUser user = auth.getCurrentUser();
        Log.d(tag,"I am here to check if email is verified");
        return user != null && user.isEmailVerified();
    }


}