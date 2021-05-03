package com.example.travelbuddyv2.registrationPhrase;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.ResetPasswordActivity;
import com.example.travelbuddyv2.loginActivity;
import com.example.travelbuddyv2.registerActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class loginActivityTest {

    private CountDownLatch authSignal = null;
    private FirebaseAuth auth;
    ActivityScenario scenario;
    View decorView;
    Instrumentation.ActivityMonitor monitorRegisterActivity =
            getInstrumentation().addMonitor(registerActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor monitorResetPasswordActivity =
            getInstrumentation().addMonitor(ResetPasswordActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor monitorMainActivity =
            getInstrumentation().addMonitor(Main2Activity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule loginActivityRule = new ActivityScenarioRule<loginActivity>(loginActivity.class);

    @Before
    public void setUp() throws Exception {
        authSignal = new CountDownLatch(1);
        auth = FirebaseAuth.getInstance();
        scenario = loginActivityRule.getScenario();
        loginActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        }) ;
    }

    @After
    public void tearDown() throws Exception {
        if(auth!=null){
            auth.signOut();
            auth = null;
        }
        scenario.close();
    }

    @Test
    public void enterEmptyField() throws  Exception{

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
                public void perform(Activity activity) {
                Button signIn = activity.findViewById(R.id.btnLoginSignIn);
                signIn.performClick();
                TextInputLayout textInputLayout = activity.findViewById(R.id.emailToggle);
                String error = textInputLayout.getError().toString();
                assertEquals(activity.getString(R.string.fieldRequired),error);
            }
        });
        scenario.close();
    }

    @Test
    public void clickOnSignUpButton() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button signUp = activity.findViewById(R.id.btnLoginSignUp);
                signUp.performClick();
            }
        });
        Activity registerPillActivity = getInstrumentation().waitForMonitorWithTimeout(monitorRegisterActivity, 5000);
        assertNotNull(registerPillActivity);
        registerPillActivity.finish();
    }

    @Test
    public void clickOnResetPasswordButton() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button resetPassword = activity.findViewById(R.id.btnLoginResetPassword);
                resetPassword.performClick();
            }
        });
        Activity resetPasswordActivity = getInstrumentation().waitForMonitorWithTimeout(monitorResetPasswordActivity, 5000);
        assertNotNull(resetPasswordActivity);
        resetPasswordActivity.finish();
    }

    @Test
    public void clickOnSignInWithCorrectDetail() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                EditText email = activity.findViewById(R.id.etLoginEmail);
                email.setText("kataix2@gmail.com");
                EditText password = activity.findViewById(R.id.etLoginPassword);
                password.setText("Welcome1@");
                Button signIn = activity.findViewById(R.id.btnLoginSignIn);
                signIn.performClick();
            }
        });
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitorMainActivity, 10000);
        assertNotNull(mainActivity);
        mainActivity.finish();
    }

    @Test
    public void clickOnSignInWithIncorrectDetail() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                EditText email = activity.findViewById(R.id.etLoginEmail);
                email.setText("random");
                EditText password = activity.findViewById(R.id.etLoginPassword);
                password.setText("random");
                Button signIn = activity.findViewById(R.id.btnLoginSignIn);
                signIn.performClick();
            }
        });
      onView(withText("Email or Password incorrect")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnSignInWithUnverifiedEmail() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                EditText email = activity.findViewById(R.id.etLoginEmail);
                email.setText("ohayokatai@gmail.com");
                EditText password = activity.findViewById(R.id.etLoginPassword);
                password.setText("Welcome1@");
                Button signIn = activity.findViewById(R.id.btnLoginSignIn);
                signIn.performClick();
            }
        });
        onView(withText("Please validate your email first")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }

}