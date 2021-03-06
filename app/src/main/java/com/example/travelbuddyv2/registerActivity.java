package com.example.travelbuddyv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travelbuddyv2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity {

    EditText etEmail, etPassword , etConfirmPassword;
    Button btnSignUp;

    private static final String email_regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String password_regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
    private static final String userName_Regex = "^[\\w-\\.]+";

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnSignUp = findViewById(R.id.btnRegisterSignUp);


        auth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = etEmail.getText().toString();
                String txtPassword = etPassword.getText().toString();
                String txtConfirmPassword = etConfirmPassword.getText().toString();

                //check if any field is empty
                if(!isEmptyString(txtEmail)
                && !isEmptyString(txtPassword)
                && !isEmptyString(txtConfirmPassword)){

                    if(isEmailCorrect(txtEmail)){

                        if(isPasswordStrongEnough(txtPassword)){

                            if(stringCheckEqual(txtPassword,txtConfirmPassword)){
                                registerUser(txtEmail,txtPassword);
                            }else{
                                Toast.makeText(registerActivity.this,"Password does not match",Toast.LENGTH_SHORT).show();
                                etConfirmPassword.setError("Password does not match");
                            }

                        }else{

                            Toast.makeText(registerActivity.this,"Password is not strong enough",Toast.LENGTH_SHORT).show();
                            etPassword.setError("Password is not strong enough@");

                        }


                    }else{
                        Toast.makeText(registerActivity.this,"Please register with correct address",Toast.LENGTH_SHORT).show();
                        etEmail.setError("Please register with correct address");
                    }



                }else{
                    Toast.makeText(registerActivity.this,"Please fill all the field",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(registerActivity.this,"Verification email sent to " + user.getEmail(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(registerActivity.this,"Fail to send verification email",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(final String email, String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    sendValidationEmail();
                    User user = new User();
                    user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    user.setName(getLocalPartOfEmail(email));
                    FirebaseDatabase.getInstance().getReference()
                            .child("User")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    toConfirmEmailScreen();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(registerActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    Toast.makeText(registerActivity.this,"Incomplete Registration",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Returns True if the user's email format is correct
    private boolean isEmailCorrect(String email) {
        Pattern pattern = Pattern.compile(email_regex);

        Matcher matcher = pattern.matcher(email);

        return ((Matcher) matcher).matches();
    }

    private boolean isPasswordStrongEnough(String password) {
        Pattern pattern = Pattern.compile(password_regex);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private String getLocalPartOfEmail(String email){
        Pattern pattern = Pattern.compile(userName_Regex);

        Matcher matcher = pattern.matcher(email);

        if(matcher.find()){
            return matcher.group(0);
        }
        return "";
    }

    //Redirects the user to the login screen
    private void toConfirmEmailScreen() {
        Toast.makeText(registerActivity.this,"Register Successful",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(registerActivity.this,confirmEmailActivity.class);
        startActivity(i);
        finish();
    }

    // return true if strings match
    private boolean stringCheckEqual(String s1, String s2) {
        return s1.equals(s2);
    }

    // return true if the string is null
    private boolean isEmptyString(String string) {
        return TextUtils.isEmpty(string);
    }

}