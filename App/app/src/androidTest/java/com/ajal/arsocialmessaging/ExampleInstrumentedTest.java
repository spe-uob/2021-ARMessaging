package com.ajal.arsocialmessaging;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.ajal.arsocialmessaging.util.PostcodeHelper;

//import com.ajal.arsocialmessaging.util.PostcodeHelper;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ajal.arsocialmessaging", appContext.getPackageName());
    }

    @Test
    public void test_GetCorrectPostCode() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(PostcodeHelper.getPostCode(appContext, 51.4559275, -2.6031669), "BS8 1UB");
    }
}