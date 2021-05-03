package com.example.travelbuddyv2.registrationPhrase;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.confirmEmailActivity;
import com.example.travelbuddyv2.registerActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class registerActivityTest {

    ActivityScenario scenario;
    View decorView;
    Instrumentation.ActivityMonitor monitorConfirmEmailActivity =
            getInstrumentation().addMonitor(confirmEmailActivity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule registerActivityRule = new ActivityScenarioRule<registerActivity>(registerActivity.class);

    @Before
    public void setUp() throws Exception {
        scenario = registerActivityRule.getScenario();
        registerActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        }) ;
    }

    @After
    public void tearDown() throws Exception {
        scenario.close();
    }

    @Test
    public void enterEmptyField() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button signUp = activity.findViewById(R.id.btnRegisterSignUp);
                signUp.performClick();
                TextInputLayout textInputLayout = activity.findViewById(R.id.signUpEmailToggle);
                String error = textInputLayout.getError().toString();
                assertEquals(activity.getString(R.string.fieldRequired),error);
            }
        });
        scenario.close();
    }

    @Test
    public void signUpWithAnExistingAccount() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button signUp = activity.findViewById(R.id.btnRegisterSignUp);
                EditText email = activity.findViewById(R.id.etRegisterEmail);
                EditText password = activity.findViewById(R.id.etRegisterPassword);
                EditText confirmPassword = activity.findViewById(R.id.etRegisterConfirmPassword);
                email.setText("kataix2@outlook.com");
                password.setText("Welcome1@");
                confirmPassword.setText("Welcome1@");
                signUp.performClick();
            }
        });
        onView(withText("Something went wrong")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
        scenario.close();
    }

    @Test
    public void enterInvalidEmailFormat() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button signUp = activity.findViewById(R.id.btnRegisterSignUp);
                EditText email = activity.findViewById(R.id.etRegisterEmail);
                EditText password = activity.findViewById(R.id.etRegisterPassword);
                EditText confirmPassword = activity.findViewById(R.id.etRegisterConfirmPassword);
                email.setText("katai@outlook.m");
                password.setText("Welcome1@");
                confirmPassword.setText("Welcome1@");
                signUp.performClick();
                TextInputLayout textInputLayout = activity.findViewById(R.id.signUpEmailToggle);
                String error = textInputLayout.getError().toString();
                assertEquals("Please register with correct email address",error);
            }
        });
    }

    @Test
    public void enterWeakPassword() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button signUp = activity.findViewById(R.id.btnRegisterSignUp);
                EditText email = activity.findViewById(R.id.etRegisterEmail);
                EditText password = activity.findViewById(R.id.etRegisterPassword);
                EditText confirmPassword = activity.findViewById(R.id.etRegisterConfirmPassword);
                email.setText("kataix2@outlook.com");
                password.setText("We");
                confirmPassword.setText("Welcome1@");
                signUp.performClick();
                TextInputLayout textInputLayout = activity.findViewById(R.id.signUpPasswordToggle);
                String error = textInputLayout.getError().toString();
                assertEquals("Password is not strong enough",error);
            }
        });
    }

    @Test
    public void enterDifferentValueForPasswordAndConfirmPassword() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button signUp = activity.findViewById(R.id.btnRegisterSignUp);
                EditText email = activity.findViewById(R.id.etRegisterEmail);
                EditText password = activity.findViewById(R.id.etRegisterPassword);
                EditText confirmPassword = activity.findViewById(R.id.etRegisterConfirmPassword);
                email.setText("kataix2@outlook.com");
                password.setText("Welcome1@");
                confirmPassword.setText("Welcome");
                signUp.performClick();
                TextInputLayout textInputLayout = activity.findViewById(R.id.signUpConfirmPasswordToggle);
                String error = textInputLayout.getError().toString();
                assertEquals("Password does not match",error);
            }
        });
    }

    @Test
    public void signUpWithANewAccount() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button signUp = activity.findViewById(R.id.btnRegisterSignUp);
                EditText email = activity.findViewById(R.id.etRegisterEmail);
                EditText password = activity.findViewById(R.id.etRegisterPassword);
                EditText confirmPassword = activity.findViewById(R.id.etRegisterConfirmPassword);
                email.setText("katai123@outlook.com");
                password.setText("Welcome1@");
                confirmPassword.setText("Welcome1@");
                signUp.performClick();
            }
        });
        Activity confirmActivity = getInstrumentation().waitForMonitorWithTimeout(monitorConfirmEmailActivity, 10000);
        assertNotNull(confirmActivity);
        confirmActivity.finish();
    }


}