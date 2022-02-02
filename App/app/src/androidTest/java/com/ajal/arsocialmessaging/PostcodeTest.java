package com.ajal.arsocialmessaging;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.ajal.arsocialmessaging.util.PostcodeHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PostcodeTest {

    @Test
    public void test_GetCorrectPostCode() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals(PostcodeHelper.getPostCode(appContext, 51.4559275, -2.6031669), "BS8 1UB");
    }
}