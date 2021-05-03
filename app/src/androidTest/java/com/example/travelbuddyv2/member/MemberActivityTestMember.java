package com.example.travelbuddyv2.member;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.MemberActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

//we will user kataix2@gmail for testing

public class MemberActivityTestMember {

    public static Matcher<View> withViewAtPosition(final int position, final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo( Description description) {
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                final RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), MemberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fromWho", "groupTrip");
        bundle.putString("TripName","test1");
        bundle.putString("TripStringID","t0");
        bundle.putString("TripOwnerID","WFcvp08VpoS3MHdeRZXFojMtCUF3");
        intent.putExtras(bundle);
    }

    ActivityScenario scenario;
    View decorView;
    Instrumentation.ActivityMonitor monitorMainActivity=
            getInstrumentation().addMonitor(Main2Activity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule memberActivityRule = new ActivityScenarioRule<MemberActivity>(intent);

    @Before
    public void setUp() throws Exception {
        NetworkObserver.isNetworkConnected=true;
        scenario = memberActivityRule.getScenario();
        memberActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
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
    public void checkInviteFriendButton() throws Exception{
        onView(ViewMatchers.withId(R.id.tvInviteFriend)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkRemoveMemberButton() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvMemberActivity)).check(matches(withViewAtPosition(1,hasDescendant(allOf(withId(R.id.btnMemberDelete),not(isDisplayed()))))));
    }

    @Test
    public void checkLeaveGroupButton() throws Exception{
        onView(withId(R.id.optionLeaveGroup)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnLeaveGroupButton() throws Exception{
        onView(withId(R.id.optionLeaveGroup)).perform(click());
        onView(withText("Are you sure you want to leave this group?"))
                .inRoot(isDialog())
                .check(matches(withText("Are you sure you want to leave this group?")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void confirmLeaveGroupButton() throws Exception{
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.optionLeaveGroup)).perform(click());
        onView(withText("YES")).perform(click());
        Activity dashboardScreen = getInstrumentation().waitForMonitorWithTimeout(monitorMainActivity,10000);
        assertNotNull(dashboardScreen);
        dashboardScreen.finish();
    }

}