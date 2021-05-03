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
import com.example.travelbuddyv2.ResetPasswordActivity;
import com.example.travelbuddyv2.ResetPasswordCompletedActivity;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.android.material.textfield.TextInputLayout;
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
public class ResetPasswordActivityTest {

    ActivityScenario scenario;
    View decorView;
    Instrumentation.ActivityMonitor monitorCompletePasswordResetActivity =
            getInstrumentation().addMonitor(ResetPasswordCompletedActivity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule resetPasswordActivityRule = new ActivityScenarioRule<ResetPasswordActivity>(ResetPasswordActivity.class);

    @Before
    public void setUp() throws Exception {
        scenario = resetPasswordActivityRule.getScenario();
        resetPasswordActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
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
    public void emptyField() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                Button send = activity.findViewById(R.id.btnResetPasswordSend);
                send.performClick();
                TextInputLayout textInputLayout = activity.findViewById(R.id.resetPasswordEmailToggle);
                String error = textInputLayout.getError().toString();
                assertEquals(activity.getString(R.string.fieldRequired),error);
            }
        });
        scenario.close();
    }

    @Test
    public void enterInvalidEmail() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                EditText email = activity.findViewById(R.id.etResetPasswordEmail);
                email.setText("ohayokatai@g");
                Button send = activity.findViewById(R.id.btnResetPasswordSend);
                send.performClick();
            }
        });
        onView(withText("No User Associated with the Email")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }

    @Test
    public void enterValidEmail() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                EditText email = activity.findViewById(R.id.etResetPasswordEmail);
                email.setText("ohayokatai@gmail.com");
                Button send = activity.findViewById(R.id.btnResetPasswordSend);
                send.performClick();
            }
        });
        Activity completeResetPasswordActivity = getInstrumentation().waitForMonitorWithTimeout(monitorCompletePasswordResetActivity, 5000);
        assertNotNull(completeResetPasswordActivity);
        completeResetPasswordActivity.finish();
    }

}