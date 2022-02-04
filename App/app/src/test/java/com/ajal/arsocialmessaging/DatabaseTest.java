package com.ajal.arsocialmessaging;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Database tests
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseTest implements ApiCallback {

    private List<Message> messages;
    private List<Banner> banners;

    // Mutexes to ensure that results have been retrieved from database before running the tests
    private Semaphore messageMutex = new Semaphore(1);
    private Semaphore bannerMutex = new Semaphore(1);

    @Before
    public void init() throws InterruptedException {
        // Request the server to load the results from the database
        DBResults dbResults = DBResults.getInstance();
        DBResults.getInstance().clearCallbacks();
        dbResults.registerCallback(this);
        dbResults.retrieveDBResults();

        messageMutex.acquire();
        bannerMutex.acquire();
    }

    @Override
    public void onMessageSuccess(List<Message> result) {
        this.messages = result;
        messageMutex.release();
    }

    @Override
    public void onBannerSuccess(List<Banner> result) {
        this.banners = result;
        bannerMutex.release();
    }

    @Test
    public void test_resultsFromDBNotNull() throws InterruptedException {
        messageMutex.acquire();
        bannerMutex.acquire();

        assertNotEquals(null, this.messages);
        assertNotEquals(null, this.banners);

        messageMutex.release();
        bannerMutex.release();
    }

    @Test
    public void test_resultsFromDBAreValid() throws InterruptedException {
        messageMutex.acquire();
        bannerMutex.acquire();

        assertEquals(new Integer(1), this.messages.get(0).id);
        assertEquals("happy birthday", this.messages.get(0).message);
        assertEquals("happy-birthday.obj", this.messages.get(0).objfilename);

//        assertEquals("BS8 1UB", this.banners.get(0).postcode);
//        assertEquals(new Integer(2), this.banners.get(0).message);
//        assertEquals("2022-02-02 18:40:52.476655", this.banners.get(0).timestamp);

        messageMutex.release();
        bannerMutex.release();
    }
}
