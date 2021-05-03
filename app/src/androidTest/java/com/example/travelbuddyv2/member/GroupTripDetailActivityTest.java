package com.example.travelbuddyv2.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.travelbuddyv2.GroupTripDetailActivity;
import com.example.travelbuddyv2.R;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class MyViewAction2 {

    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }
}
@RunWith(AndroidJUnit4.class)
public class GroupTripDetailActivityTest {

    static Intent intent;
    static{
        intent = new Intent(ApplicationProvider.getApplicationContext(), GroupTripDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("TRIP_STRING_ID","t0");
        bundle.putString("TRIP_OWNER","WFcvp08VpoS3MHdeRZXFojMtCUF3");
        intent.putExtras(bundle);
    }

    ActivityScenario scenario;
    View decorView;

    //There must be at least one attraction on database
    @Rule
    public ActivityScenarioRule tripDetailActivityRule = new ActivityScenarioRule<GroupTripDetailActivity>(intent);

    @Before
    public void setUp() throws Exception {
        scenario = tripDetailActivityRule.getScenario();
        tripDetailActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
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
    public void clickOnAttractionButtonWhenPermissionIsView() throws Exception{
        TimeUnit.SECONDS.sleep(3);
        onView(ViewMatchers.withId(R.id.rcvFragmentGroupTripDetail)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction2.clickChildViewWithId(R.id.sectionRowAddAttraction)));
        onView(withText("You do not have permission to edit")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }
    @Test
    public void clickOnClockIconWhenPermissionIsView() throws Exception{
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.childRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction2.clickChildViewWithId(R.id.layoutEditDuration)));
        onView(withText("You do not have permission to edit")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }
    @Test
    public void clickOnDeleteButtonWhenPermissionIsView() throws Exception{
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.childRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction2.clickChildViewWithId(R.id.btnDeleteDestination)));
        onView(withText("You do not have permission to edit")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }
    @Test
    public void clickOnStartTimeButtonWhenPermissionIsView() throws Exception{
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.rcvFragmentGroupTripDetail)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction2.clickChildViewWithId(R.id.sectionStartTime)));
        onView(withText("You do not have permission to edit")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }


}