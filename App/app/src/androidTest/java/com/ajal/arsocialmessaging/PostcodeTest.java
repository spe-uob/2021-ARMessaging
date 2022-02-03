package com.ajal.arsocialmessaging;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.location.Location;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ajal.arsocialmessaging.ui.home.common.Banner;
import com.ajal.arsocialmessaging.util.PostcodeHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 * Note: the Android device needs to be unlocked for it to work
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 * @see <a href="https://developer.android.com/training/testing/espresso/basics">Espresso Basics</a>
 */
@RunWith(AndroidJUnit4.class)
public class PostcodeTest {

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
    public void test_GetCorrectPostCode() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String postcode = PostcodeHelper.getPostCode(appContext, 51.4559275, -2.6031669);
        assertEquals("BS8 1UB", postcode);
    }

    @Test
    public void test_DisplayCorrectPostcode() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        onView(withId(R.id.navigation_settings)).perform(click());
        Location location = PostcodeHelper.getLocation(appContext);
        String postcode = PostcodeHelper.getPostCode(appContext, location.getLatitude(), location.getLongitude());
        onView(withId(R.id.text_currentPostcode)).check(matches(withText(containsString(postcode))));
    }

    @Test
    public void test_ReturnsZeroLocalBanners() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        List<Banner> globalBanners = new ArrayList<>();
        Banner banner1 = new Banner(0, "BS8 1LN"); // Richmond Building (Bristol SU)
        Banner banner2 = new Banner(0, "BS8 1LN"); // Richmond Building (Bristol SU)
        globalBanners.add(banner1);
        globalBanners.add(banner2);

        List<Banner> localBanners = PostcodeHelper.getLocalBanners(appContext, globalBanners, 51.4559275, -2.6031669);
        assertEquals(0, localBanners.size());
    }

    @Test
    public void test_ReturnsOneLocalBanner() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String postcode = PostcodeHelper.getPostCode(appContext, 51.4559275, -2.6031669);
        List<Banner> globalBanners = new ArrayList<>();
        Banner banner1 = new Banner(0, "BS8 1LN"); // Richmond Building (Bristol SU)
        Banner banner2 = new Banner(0, "BS8 1UB"); // Merchant Venturer's Building (University of Bristol)
        globalBanners.add(banner1);
        globalBanners.add(banner2);

        List<Banner> localBanners = PostcodeHelper.getLocalBanners(appContext, globalBanners, 51.4559275, -2.6031669);
        assertEquals(1, localBanners.size());
        assertEquals(postcode, localBanners.get(0).getPostCode());
    }

    @Test
    public void test_ReturnsMultipleLocalBanners() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String postcode = PostcodeHelper.getPostCode(appContext, 51.4559275, -2.6031669);
        List<Banner> globalBanners = new ArrayList<>();
        Banner banner1 = new Banner(0, "BS8 1LN"); // Richmond Building (Bristol SU)
        Banner banner2 = new Banner(0, "BS8 1UB"); // Merchant Venturer's Building (University of Bristol)
        Banner banner3 = new Banner(0, "BS8 1UB"); // Merchant Venturer's Building (University of Bristol)
        globalBanners.add(banner1);
        globalBanners.add(banner2);
        globalBanners.add(banner3);

        List<Banner> localBanners = PostcodeHelper.getLocalBanners(appContext, globalBanners, 51.4559275, -2.6031669);
        assertEquals(2, localBanners.size());
        assertEquals(postcode, localBanners.get(0).getPostCode());
        assertEquals(postcode, localBanners.get(1).getPostCode());
    }

}