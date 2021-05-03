package com.example.travelbuddyv2.owner;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.addNewTrip;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

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
public class addNewTripTest {

    FirebaseAuth auth;
    long cnt,afterAdded;
    ActivityScenario scenario;
    View decorView;
    Instrumentation.ActivityMonitor monitorMainActivity =
            getInstrumentation().addMonitor(Main2Activity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule addNewTripActivityRule = new ActivityScenarioRule<addNewTrip>(addNewTrip.class);

    @Before
    public void setUp() throws Exception {
        scenario = addNewTripActivityRule.getScenario();
        addNewTripActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        }) ;
    }

    @Test
    public void emptyField() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                NetworkObserver.isNetworkConnected = true;
            }
        });

        onView(ViewMatchers.withId(R.id.optionSaveTrip)).perform(click());
        onView(withText("Please fill all detail")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }

    @Test
    public void startDateAfterEndDate() throws Exception{
        TimeUnit.SECONDS.sleep(2);

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                NetworkObserver.isNetworkConnected = true;
                EditText startDate = activity.findViewById(R.id.etDepartDate);
                EditText endDate = activity.findViewById(R.id.etArrivalDate);
                startDate.setText("2021-02-02");
                endDate.setText("2021-02-01");
                EditText tripName = activity.findViewById(R.id.etTripName);
                tripName.setText("katai");
            }
        });
        TimeUnit.SECONDS.sleep(2);

        onView(withId(R.id.optionSaveTrip)).perform(click());
        onView(withText("Start Date before EndDate")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }

    @Test
    public void enterValidData() throws Exception{
        TimeUnit.SECONDS.sleep(4);
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                NetworkObserver.isNetworkConnected = true;
                EditText startDate = activity.findViewById(R.id.etDepartDate);
                EditText endDate = activity.findViewById(R.id.etArrivalDate);
                startDate.setText("2021-02-02");
                endDate.setText("2021-02-03");
                EditText tripName = activity.findViewById(R.id.etTripName);
                tripName.setText("katai");

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3");

                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        cnt = dataSnapshot.getChildrenCount();
                    }
                });


            }
        });

        TimeUnit.SECONDS.sleep(4);

        onView(withId(R.id.optionSaveTrip)).perform(click());

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        afterAdded = dataSnapshot.getChildrenCount();
                    }
                });
            }
        });

        TimeUnit.SECONDS.sleep(3);

        assertEquals(cnt+1,afterAdded);
        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(monitorMainActivity, 10000);
        assertNotNull(mainActivity);
        mainActivity.finish();
    }


}