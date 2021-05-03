package com.example.travelbuddyv2.registrationPhrase;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.travelbuddyv2.Main2Activity;
import com.example.travelbuddyv2.R;
import com.example.travelbuddyv2.loginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class zLastOneActivityTest {

    ActivityScenario scenario;
    View decorView;

    Instrumentation.ActivityMonitor monitorLoginActivity = getInstrumentation().addMonitor(loginActivity.class.getName(), null, false);

    @Rule
    public ActivityScenarioRule mainActivityRule = new ActivityScenarioRule<Main2Activity>(Main2Activity.class);

    @Before
    public void setUp() throws Exception {
        scenario = mainActivityRule.getScenario();
        mainActivityRule.getScenario().onActivity(new ActivityScenario.ActivityAction() {
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
    public void clickOnLogOutButton(){
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
        onView(withId(R.id.tvUserLogOut)).perform(click());
        onView(withText("YES")).perform(click());
        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(monitorLoginActivity,10000);
        assertNotNull(loginActivity);
        loginActivity.finish();
    }
}