package com.example.travelbuddyv2.owner;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.MemberActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.addNewTrip;
import com.example.travelbuddyv2.loginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class Main2ActivityTest {

    FirebaseAuth auth;
    ActivityScenario scenario;
    View decorView;
    long tripNumberBeforeAccepted, tripNumberAfterAccepted ;
    int numberOfTripBeforeRemoved, numberOfTripAfterRemoved;
    Instrumentation.ActivityMonitor monitorAddNewTripActivity = getInstrumentation().addMonitor(addNewTrip.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitorMemberActivity = getInstrumentation().addMonitor(MemberActivity.class.getName(),null,false);
    @Rule
    public ActivityScenarioRule resetPasswordActivityRule = new ActivityScenarioRule<Main2Activity>(Main2Activity.class);

    @Before
    public void setUp() throws Exception {
        scenario = resetPasswordActivityRule.getScenario();
        resetPasswordActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                decorView = activity.getWindow().getDecorView();
            }
        }) ;
        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        scenario.close();
        Intents.release();
    }

    @Test
    public void clickOnMapBottomTab() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_map);
                String actual = navController.getCurrentDestination().getLabel().toString();
                assertEquals("Map",actual);
            }
        });
    }
    @Test
    public void clickOnNotificationBottomTab(){
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_notifications);
                String actual = navController.getCurrentDestination().getLabel().toString();
                assertEquals("Notifications",actual);
            }
        });
    }

    @Test
    public void clickOnUserBottomTab(){
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_user_profile);
                String actual = navController.getCurrentDestination().getLabel().toString();
                assertEquals("User",actual);
            }
        });
    }
    //this is user profile fragment test
    //name should not be test test
    @Test
    public void clickOnEditUserProfileName() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_user_profile);

            }
        });
        onView(withId(R.id.tvProfileName)).perform(click());
        onView(withId(R.id.bottomSheetContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnSaveAfterNameIsEdit(){
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_user_profile);

            }
        });
        onView(withId(R.id.tvProfileName)).perform(click());
        onView(withId(R.id.etEditProfileName)).perform(replaceText("testtest"));
        onView(withId(R.id.btnEditProfileSave)).perform(click());
        onView(withId(R.id.tvProfileName)).check(matches(withText("testtest")));
    }

        @Test
    public void clickOnCancelAfterNameIsEdit(){
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_user_profile);

            }
        });
        onView(withId(R.id.tvProfileName)).perform(click());
        onView(withId(R.id.etEditProfileName)).perform(typeText("name"));
        onView(withId(R.id.btnEditProfileCancel)).perform(click());
        onView(withId(R.id.tvProfileName)).check(matches(not(withText("name"))));
    }

    @Test
    public void clickOnProfileImage(){
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_user_profile);
            }
        });
        onView(withId(R.id.imgProfilePic)).perform(click());
        intended(hasAction(equalTo(Intent.ACTION_CHOOSER)));
    }



    // notification fragment

    //kataix2@gmail.com to invite first
    @Test
    public void rejectFriendRequest() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {

                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_notifications);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("2FH4HJFVbKgJCEXIep7w5g5P2q13");

                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        tripNumberBeforeAccepted = dataSnapshot.getChildrenCount();
                    }
                });

            }
        });

        TimeUnit.SECONDS.sleep(4);
        onView(withId(R.id.rcvFragmentNotificationList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.btnReject)));
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("2FH4HJFVbKgJCEXIep7w5g5P2q13");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        tripNumberAfterAccepted = dataSnapshot.getChildrenCount();
                    }
                });
                assertEquals(tripNumberBeforeAccepted,tripNumberAfterAccepted);
            }
        });
    }


//    //ohayotesting@gmail.com to invite first
    @Test
    public void acceptFriendRequest() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                BottomNavigationView navView = activity.findViewById(R.id.nav_view);
                AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.navigation_user_profile, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_map)
                        .build();
                NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
                NavigationUI.setupActionBarWithNavController((AppCompatActivity) activity, navController, appBarConfiguration);
                NavigationUI.setupWithNavController(navView, navController);
                navController.navigate(R.id.navigation_notifications);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("64jXUXyQBMONjpBlATSijKUVFJt1");

                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        tripNumberBeforeAccepted = dataSnapshot.getChildrenCount();
                    }
                });
            }
        });
        TimeUnit.SECONDS.sleep(4);
        onView(withId(R.id.rcvFragmentNotificationList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.btnAccept)));
        TimeUnit.SECONDS.sleep(3);
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Group")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("64jXUXyQBMONjpBlATSijKUVFJt1");

                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        tripNumberAfterAccepted = dataSnapshot.getChildrenCount();
                        assertEquals(tripNumberBeforeAccepted+1,tripNumberAfterAccepted);
                    }
                });
            }
        });
    }

    // dashboard fragment
    @Test
    public void clickOnAddButton() throws Exception{
        onView(withId(R.id.fbtnFragmentPersonalTrip)).perform(click());
        Activity addNewTrip = getInstrumentation().waitForMonitorWithTimeout(monitorAddNewTripActivity,10000);
        assertNotNull(addNewTrip);
        addNewTrip.finish();
    }

    @Test
    public void clickOnInviteFriendButton() throws Exception{
        onView(withId(R.id.rcvFragmentPersonalTrip)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.tripInviteFriend)));
        Activity member = getInstrumentation().waitForMonitorWithTimeout(monitorMemberActivity,10000);
        assertNotNull(member);
        member.finish();
    }

    @Test
    public void clickOnDeleteTripButton() throws Exception{
        onView(withId(R.id.rcvFragmentPersonalTrip)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.tripRemove)));
        onView(withText("Are you sure you want to delete this trip?"))
                .inRoot(isDialog())
                .check(matches(withText("Are you sure you want to delete this trip?")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void confirmDeleteTrip() throws Exception{

        TimeUnit.SECONDS.sleep(4);

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                RecyclerView view = activity.findViewById(R.id.rcvFragmentPersonalTrip);
                numberOfTripBeforeRemoved = view.getAdapter().getItemCount();

            }
        });
        onView(withId(R.id.rcvFragmentPersonalTrip)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.tripRemove)));
        TimeUnit.SECONDS.sleep(1);
        onView(withText("YES")).perform(click());
        TimeUnit.SECONDS.sleep(2);
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                RecyclerView view = activity.findViewById(R.id.rcvFragmentPersonalTrip);
                numberOfTripAfterRemoved = view.getAdapter().getItemCount();
                assertEquals(numberOfTripBeforeRemoved-1,numberOfTripAfterRemoved);
            }
        });
    }







}