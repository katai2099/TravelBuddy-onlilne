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
import com.example.travelbuddyv2.loginActivity;
import com.google.firebase.auth.FirebaseAuth;

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
public class confirmEmailActivityTest {

    ActivityScenario scenario;
    View decorView;
    Instrumentation.ActivityMonitor monitorLoginActivity =
            getInstrumentation().addMonitor(loginActivity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule registerActivityRule = new ActivityScenarioRule<confirmEmailActivity>(confirmEmailActivity.class);

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
    public void clickOnLoginButton() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button login = activity.findViewById(R.id.btnDone);
                login.performClick();
            }
        });
        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(monitorLoginActivity, 5000);
        assertNotNull(loginActivity);
        loginActivity.finish();
    }

    @Test
    public void resendValidationEmail() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithEmailAndPassword("kataix2@outlook.com","Welcome1@");
                Button login = activity.findViewById(R.id.btnResendConfirmationEMail);
                login.performClick();
            }
        });
        onView(withText("Verification email sent to kataix2@outlook.com")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
        scenario.close();
    }

}