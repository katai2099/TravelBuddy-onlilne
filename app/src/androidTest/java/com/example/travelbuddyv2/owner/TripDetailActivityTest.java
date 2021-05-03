package com.example.travelbuddyv2.owner;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.viewpager.widget.ViewPager;

import com.example.travelbuddyv2.AttractionDetailActivity;
import com.example.travelbuddyv2.ClickedItemActivity;
import com.example.travelbuddyv2.InviteFriendActivity;
import com.example.travelbuddyv2.MapsActivity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.TripDetailActivity;
import com.example.travelbuddyv2.model.Destination;
import com.example.travelbuddyv2.model.Inventory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setTime;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;



@RunWith(AndroidJUnit4.class)
public class TripDetailActivityTest {

    final String tag = "TRIPDETAILTEST";

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    Instrumentation.ActivityMonitor monitorMapActivity =
            getInstrumentation().addMonitor(MapsActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor monitorAttractionDetail =
            getInstrumentation().addMonitor(AttractionDetailActivity.class.getName(),null,false);
    Instrumentation.ActivityMonitor monitorEnlargeActivity =
            getInstrumentation().addMonitor(ClickedItemActivity.class.getName(),null,false);

    ActivityScenario scenario;
    View decorView;
    String endTime,startTime;
    long attractionCount,attractionCountAfterDeleted , itemCount, itemCountAfterDeleted;

    static Intent intent;
    static{
        intent = new Intent(ApplicationProvider.getApplicationContext(), TripDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("TRIP_STRING_ID","t0");
        bundle.putBoolean("isPersonal",true);
        intent.putExtras(bundle);
    }


    @Rule
    public ActivityScenarioRule tripDetailActivityRule = new ActivityScenarioRule<TripDetailActivity>(intent);

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
    }


//    tripDetail fragment
//    There should be at least one attraction on the list

    @Test
    public void clickOnInventoryTab() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);

            }
        });
    }

    @Test
    public void clickOnClockIcon() throws Exception{

        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.childRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.layoutEditDuration)));
        onView(withText("CANCEL"))
                .inRoot(isDialog())
                .check(matches(withText("CANCEL")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void changeStayPeriod() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.childRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.layoutEditDuration)));
        onView(isAssignableFrom(TimePicker.class)).perform(setTime(5, 0));
        onView(withText("OK")).perform(click());

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0")
                        .child("2021-02-02");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Destination firstDestination = snapshot.getValue(Destination.class);
                            endTime = firstDestination.getEndTime();
                            break;
                        }
                    }
                });
            }
        });
        TimeUnit.SECONDS.sleep(4);
        onView(withId(R.id.childRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(endTime)))));
    }

    @Test
    public void clickOnStartTimeButton() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvFragmentTripDetailList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.sectionStartTime)));
        onView(withText("CANCEL"))
                .inRoot(isDialog())
                .check(matches(withText("CANCEL")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void changeStartTime() throws Exception{
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.sectionStartTime)).perform(click());
        onView(isAssignableFrom(TimePicker.class)).perform(setTime(5, 0));
        onView(withText("OK")).perform(click());

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0")
                        .child("2021-02-02");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Destination firstDestination = snapshot.getValue(Destination.class);
                            startTime = firstDestination.getStartTime();
                            break;
                        }
                    }
                });
            }
        });

        TimeUnit.SECONDS.sleep(2);
        onView(withId(R.id.childRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(startTime)))));

    }

    @Test
    public void clickOnAttractionButton() throws Exception{
        TimeUnit.SECONDS.sleep(4);
        onView(withId(R.id.rcvFragmentTripDetailList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.sectionRowAddAttraction)));
        Activity mapActivity = getInstrumentation().waitForMonitorWithTimeout(monitorMapActivity,10000);
        assertNotNull(mapActivity);
        mapActivity.finish();
    }

    @Test
    public void clickOnAttractionOnTheList() throws Exception{
        TimeUnit.SECONDS.sleep(4);
        onView(withId(R.id.childRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        Activity attractionDetail = getInstrumentation().waitForMonitorWithTimeout(monitorAttractionDetail,10000);
        assertNotNull(attractionDetail);
        attractionDetail.finish();
    }


    @Test
    public void clickOnDeleteAttractionButton() throws Exception{
        TimeUnit.SECONDS.sleep(3);

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0")
                        .child("2021-02-02");

                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        attractionCount = dataSnapshot.getChildrenCount();
                    }
                });
            }
        });
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.childRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnDeleteDestination)));
        TimeUnit.SECONDS.sleep(3);
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trip_detail")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0")
                        .child("2021-02-02");

                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        attractionCountAfterDeleted = dataSnapshot.getChildrenCount();
                        assertEquals(attractionCount-1,attractionCountAfterDeleted);
                    }
                });
            }
        });
    }

    //inventory fragment
    //this test required two items. First item belongs to member. Second one belongs to user

    @Test
    public void clickOnItemOnTheList() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);
            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvItems)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        Activity enlarge = getInstrumentation().waitForMonitorWithTimeout(monitorEnlargeActivity,10000);
        assertNotNull(enlarge);
        enlarge.finish();
    }

    @Test
    public void clickOnPrivacyButton() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);

            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnInventoryOption)));
        onView(withText("Privacy")).perform(click());
        onView(withText("Please Select Privacy"))
                .inRoot(isDialog())
                .check(matches(withText("Please Select Privacy")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void selectPrivatePrivacy() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);

            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnInventoryOption)));
        onView(withText("Privacy")).perform(click());
        onView(withText("Private")).perform(click());
        onView(withText("YES")).perform(click());
        TimeUnit.SECONDS.sleep(1);


        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        int cnt = 0;
                        for(DataSnapshot data:dataSnapshot.getChildren()){
                            if(cnt ==1){
                                Inventory inventory = data.getValue(Inventory.class);
                                assertEquals("Private",inventory.getPermission());
                                break;
                            }
                            cnt++;
                        }
                    }
                });
            }
        });

    }

    @Test
    public void selectSharedPrivacy() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);

            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnInventoryOption)));
        onView(withText("Privacy")).perform(click());
        onView(withText("Shared")).perform(click());
        onView(withText("YES")).perform(click());
        TimeUnit.SECONDS.sleep(3);

        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        int cnt = 0;
                        for(DataSnapshot data:dataSnapshot.getChildren()){
                            if(cnt ==1){
                                Inventory inventory = data.getValue(Inventory.class);
                                assertEquals("Shared",inventory.getPermission());
                                break;
                            }
                            cnt++;
                        }
                    }
                });
            }
        });

    }

    @Test
    public void deleteItemThatDoesNotBelongToUser() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);

            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.btnInventoryOption)));
        onView(withText("Delete")).perform(click());
        onView(withText("This file does not belong to you")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }

    @Test
    public void setPrivacyOnItemThatDoesNotBelongToUser() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);

            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.btnInventoryOption)));
        onView(withText("Privacy")).perform(click());
        onView(withText("You cannot set Permission group member's item")).inRoot(withDecorView(not(is(decorView))) ).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnGridView() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);

            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.btnGridView)).perform(click());
        onView(withId(R.id.btnListView)).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnDeleteButton() throws Exception{
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);
            }
        });
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.rcvItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnInventoryOption)));
        TimeUnit.SECONDS.sleep(1);
        onView(withText("Delete")).perform(click());
        onView(withText("are you sure you want to delete this item?"))
                .inRoot(isDialog())
                .check(matches(withText("are you sure you want to delete this item?")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void confirmDelete() throws Exception{
        TimeUnit.SECONDS.sleep(4);
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                final ViewPager viewPager = activity.findViewById(R.id.view_pager);
                TabLayout tabs = activity.findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
                viewPager.setCurrentItem(1);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        itemCount = dataSnapshot.getChildrenCount();
                    }
                });
            }
        });
        onView(withId(R.id.rcvItems)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildViewWithId(R.id.btnInventoryOption)));
        onView(withText("Delete")).perform(click());
        onView(withText("YES")).perform(click());
        TimeUnit.SECONDS.sleep(4);
        scenario.onActivity(new ActivityScenario.ActivityAction() {
            @Override
            public void perform(Activity activity) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                        .child("WFcvp08VpoS3MHdeRZXFojMtCUF3")
                        .child("t0");
                reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        itemCountAfterDeleted = dataSnapshot.getChildrenCount();
                        assertEquals(itemCount-1,itemCountAfterDeleted);
                    }
                });
            }
        });


    }

}