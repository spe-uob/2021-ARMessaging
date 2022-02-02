package com.ajal.arsocialmessaging;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 * Note: the Android device needs to be unlocked for it to work
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @see <a href="https://developer.android.com/training/testing/espresso/basics">Espresso Basics</a>
 */
@RunWith(AndroidJUnit4.class)
public class FragmentsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void init() {
        activityRule.getScenario().onActivity(activity -> {
            activity.getSupportFragmentManager().beginTransaction();
        });
    }

    @Test
    public void test_launchFragments() {
        // Clicks through every fragment to check they can be launched
        onView(withId(R.id.navigation_gallery)).perform(click());
        onView(withId(R.id.navigation_notifications)).perform(click());
        onView(withId(R.id.navigation_home)).perform(click());
        onView(withId(R.id.navigation_message)).perform(click());
        onView(withId(R.id.navigation_settings)).perform(click());

    }

}