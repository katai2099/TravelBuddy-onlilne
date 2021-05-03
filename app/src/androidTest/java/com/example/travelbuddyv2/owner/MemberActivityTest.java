package com.example.travelbuddyv2.owner;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.travelbuddyv2.InviteFriendActivity;
import com.example.travelbuddyv2.MemberActivity;
import com.example.travelbuddyv2.PermissionModificationActivity;
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
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.CursorMatchers.withRowString;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.not;

class MyViewAction {

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
public class MemberActivityTest {

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
        bundle.putString("fromWho", "personalTrip");
        bundle.putString("TripName","test1");
        bundle.putString("TripStringID","t0");
        bundle.putString("TripOwnerID","WFcvp08VpoS3MHdeRZXFojMtCUF3");
        intent.putExtras(bundle);
    }


    ActivityScenario scenario;
    long memberCount,afterRemove;
    View decorView;
    Instrumentation.ActivityMonitor monitorInviteFriendActivity =
            getInstrumentation().addMonitor(InviteFriendActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor monitorPermissionModificationActivity =
            getInstrumentation().addMonitor(PermissionModificationActivity.class.getName(), null, false);

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
    public void checkInviteFriendButton() throws Exception {
        onView(ViewMatchers.withId(R.id.tvInviteFriend)).check(matches(isDisplayed()));
    }

    @Test
    public void checkLeaveGroupButton() throws Exception{
        onView(withId(R.id.optionLeaveGroup)).check(doesNotExist());
    }

    @Test
    public void clickOnOwner() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvMemberActivity)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText("Owner permission cannot be changed")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));

    }

    @Test
    public void clickOnInviteFriendButton() throws Exception{
        onView(withId(R.id.tvInviteFriend)).perform(click());
        Activity inviteFriendActivity = getInstrumentation().waitForMonitorWithTimeout(monitorInviteFriendActivity, 10000);
        assertNotNull(inviteFriendActivity);
        inviteFriendActivity.finish();
    }

    @Test
    public void clickOnMemberOnTheList() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvMemberActivity)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        Activity modification = getInstrumentation().waitForMonitorWithTimeout(monitorPermissionModificationActivity, 10000);
        assertNotNull(modification);
        modification.finish();
    }



    @Test
    public void checkRemoveMemberButton() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvMemberActivity)).check(matches(withViewAtPosition(1,hasDescendant(allOf(withId(R.id.btnMemberDelete),isDisplayed())))));
    }

    @Test
    public void clickOnRemoveMemberButton() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvMemberActivity)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnMemberDelete)));
        onView(withText("Are you sure you want to remove this member from group?"))
                .inRoot(isDialog())
                .check(matches(withText("Are you sure you want to remove this member from group?")))
                .check(matches(isDisplayed()));
    }


    //need to add ohayotesting to trip first
    @Test
    public void ConfirmRemoveMemberFromGroup() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Member")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0");
                databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        memberCount = dataSnapshot.getChildrenCount();
                    }
                });
            }
        });
        TimeUnit.SECONDS.sleep(4);
        onView(withId(R.id.rcvMemberActivity)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnMemberDelete)));
        TimeUnit.SECONDS.sleep(1);
        onView(withText("YES")).perform(click());
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Member")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0");
                databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        afterRemove = dataSnapshot.getChildrenCount();
                        assertEquals(memberCount-1,afterRemove);
                    }
                });
            }
        });



    }



}