package com.ajal.arsocialmessaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.DBHelper;
import com.ajal.arsocialmessaging.util.database.DBObserver;
import com.ajal.arsocialmessaging.util.database.Message;
import com.ajal.arsocialmessaging.util.location.PostcodeHelper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Database tests
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PostcodeTest {

    @Test
    public void test_postcodeFormatting() {
        String test1 = "BS8 1UB";
        String test2 = "BS81UB";
        String test3 = "bs8 1ub";
        String test4 = "bs81ub";
        String[] tests = {test1, test2, test3, test4};
        String expected = "BS8 1UB";

        for (String test : tests) {
            assertEquals(expected, PostcodeHelper.formatPostcode(test));
        }
    }

    @Test
    public void test_postcodeValid() {
        // UK University postcodes
        String test0 = "BS8 1TH";  // University of Bristol
        String test1 = "BS16 1QY"; // University of West England (UWE)
        String test2 = "OX1 4BH";  // University of Oxford
        String test3 = "CB2 1TN";  // University of Cambridge
        String test4 = "CV4 7AL";  // University of Warwick
        String test5 = "L1 8JX";   // University of Liverpool
        String test6 = "B15 2SQ";  // University of Birmingham
        String test7 = "WC2A 2AE"; // London School of Economics
        String test8 = "SW7 2BX";  // Imperial College London
        String test9 = "WC1E 6BT"; // University College London
        String[] tests = {test0, test1, test2, test3, test4, test5, test6, test7, test8, test9};

        for (String test : tests) {
            assert PostcodeHelper.checkPostcodeValid(test);
        }
    }

    @Test
    public void test_postcodeInvalid() {
        // Incorrectly typed UK postcodes
        String test0 = "BS8 1T";
        String test1 = "BS16 11QY";
        String test2 = "4BC";
        String test3 = "CWC1E 6BT!";
        String test4 = "<SW7 2BX";
        String test5 = "L1";
        String test6 = "WC2A AE";
        // SQL injections
        String test7 = "SELECT * FROM messages";
        String test8 = "DROP TABLE banners";
        // Other invalid strings
        String test9 = "<:>./##[";
        String test10 = "Hello, World!";
        String[] tests = {test0, test1, test2, test3, test4, test5, test6, test7, test8, test9, test10};

        for (String test : tests) {
            assert !PostcodeHelper.checkPostcodeValid(test);
        }
    }
}
