package com.example.travelbuddyv2.owner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.travelbuddyv2.InviteFriendActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.networkManager.NetworkObserver;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
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
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.travelbuddyv2.owner.TripDetailActivityTest.atPosition;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.asm.tree.InsnList.check;

@RunWith(AndroidJUnit4.class)
public class InviteFriendActivityTest {

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), InviteFriendActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("TripName","test1");
        bundle.putString("TripStringID","t0");
        intent.putExtras(bundle);
    }

    ActivityScenario scenario;
    View decorView;
    FirebaseAuth auth;
    @Rule
    public ActivityScenarioRule inviteFriendActivityRule = new ActivityScenarioRule<InviteFriendActivity>(intent);

    @Before
    public void setUp() throws Exception {
        NetworkObserver.isNetworkConnected=true;
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("kataix2@outlook.com","Welcome1@");
        scenario = inviteFriendActivityRule.getScenario();
        inviteFriendActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
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
        onView(ViewMatchers.withId(R.id.btnSearchForFriend)).perform(click());
        onView(withText("Please fill user email")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
        TimeUnit.SECONDS.sleep(2);
    }
    @Test
    public void randomEmail() throws Exception{
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.etFindFriendByEmail)).perform(typeText("kkk"));
        onView(withId(R.id.btnSearchForFriend)).perform(click());
        onView(withText("COULD NOT FIND USER")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void enterEmailThatAlreadyExistOnKnownList() throws Exception{
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.etFindFriendByEmail)).perform(typeText("kataix2@gmail.com"));
        onView(withId(R.id.btnSearchForFriend)).perform(click());
        onView(withText("User already on known lists")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
        TimeUnit.SECONDS.sleep(3);
    }
    @Test
    public void enterValidEmail() throws Exception{
        onView(withId(R.id.etFindFriendByEmail)).perform(typeText("ohayotesting2@gmail.com"));
        onView(withId(R.id.btnSearchForFriend)).perform(click());
        TimeUnit.SECONDS.sleep(3);
        scenario = inviteFriendActivityRule.getScenario();
        inviteFriendActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                RecyclerView view = activity.findViewById(R.id.rcvInviteFriend);
                int numberOfFriend = view.getAdapter().getItemCount();
                assertEquals(1,numberOfFriend);
            }
        }) ;
    }
    @Test
    public void validEmailAndClickOnInviteButton() throws Exception{
        onView(withId(R.id.etFindFriendByEmail)).perform(typeText("ohayokatai@gmail.com"));
        onView(withId(R.id.btnSearchForFriend)).perform(click());
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.rcvInviteFriend)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.btnInviteFriend)));
        onView(withId(R.id.rcvInviteFriend))
                .check(matches(atPosition(0, hasDescendant(withText("Pending")))));
        TimeUnit.SECONDS.sleep(3);
    }
    @Test
    public void inviteUserWhoseAlreadyAMemberOfTheGroup() throws Exception{
        TimeUnit.SECONDS.sleep(3);
        onView(withId(R.id.rcvSuggestedFriend)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.btnInviteFriend)));
        onView(withText("This user is already a member of the group")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
        TimeUnit.SECONDS.sleep(3);
    }

}