package com.ajal.arsocialmessaging;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseTest {
    @Test
    public void test_retrievesFromDB() {
        DBResults results = DBResults.getInstance();
        results.retrieveDBResults();
        assertNotEquals(null, results.getMessages());
        assertEquals("happy birthday", results.getMessages().get(0).message);
    }
}
