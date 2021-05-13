package com.example.travelbuddyv2.owner;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.MapsActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCurrentUserAMember", false);
        bundle.putString("dateOfTrip","2021-02-02");
        bundle.putString("tripStringID","t0");
        intent.putExtras(bundle);
    }

    ActivityScenario scenario;
    View decorView;
    Instrumentation.ActivityMonitor monitorTripDetailActivity=
            getInstrumentation().addMonitor(Main2Activity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule mapActivityRule = new ActivityScenarioRule<MapsActivity>(intent);

    @Before
    public void setUp() throws Exception {
        NetworkObserver.isNetworkConnected=true;
        scenario = mapActivityRule.getScenario();
        mapActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
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
    public void clickOnSearchForAttractionButton() throws Exception{
        onView(withId(R.id.btnSearchForAttraction)).perform(click());
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.btnHideAttraction)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnHideAttractionButton() throws Exception{
        onView(withId(R.id.btnSearchForAttraction)).perform(click());
        onView(withId(R.id.btnHideAttraction)).perform(click());
        onView(withId(R.id.btnSearchForAttraction)).check(matches(isDisplayed()));

    }

    @Test
    public void zclickOnMarker() throws Exception{
        TimeUnit.SECONDS.sleep(2);
        onView(ViewMatchers.withId(R.id.btnSearchForAttraction)).perform(click());
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector()
        .descriptionContains("Google Map")
        .childSelector(new UiSelector().instance(0)));

        marker.click();
        try {
            Thread.sleep(3000);
        }catch (InterruptedException e){

        }
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                SlidingUpPanelLayout googleMapInformationLayout = activity.findViewById(R.id.sliding_layout);
                assertEquals(SlidingUpPanelLayout.PanelState.COLLAPSED,googleMapInformationLayout.getPanelState());
            }
        });

    }
//
    @Test
    public void clickOnBottomLayout() throws Exception{
        onView(withId(R.id.btnSearchForAttraction)).perform(click());
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector()
                .descriptionContains("Lao Textile Museum"));
        marker.click();
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withId(R.id.layoutForTesting)).perform(click());
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){

        }
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                SlidingUpPanelLayout googleMapInformationLayout = activity.findViewById(R.id.sliding_layout);
                assertEquals(SlidingUpPanelLayout.PanelState.ANCHORED,googleMapInformationLayout.getPanelState());
            }
        });

    }

    //this will kill activity. On real device it would go back to tripDetailActivity,
    // but for testing it would kill the app because it could not go back
    @Test
    public void clickOnAddAttractionButton() throws Exception{
        onView(withId(R.id.btnSearchForAttraction)).perform(click());

        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector()
                .descriptionContains("Google Map")
                .childSelector(new UiSelector().instance(0)));
        marker.click();

        try {
            Thread.sleep(3000);
        }catch (InterruptedException e){

        }
        onView(withId(R.id.mapFragmentBtnAddTripDetail)).perform(click());
        onView(withText("ADD SUCCESS")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
        Activity activity = getInstrumentation().waitForMonitorWithTimeout(monitorTripDetailActivity,1000);
        assertNull(activity);
    }

}