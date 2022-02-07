package com.ajal.arsocialmessaging;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.ajal.arsocialmessaging.util.ConnectivityHelper;
import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.DBObserver;
import com.ajal.arsocialmessaging.util.database.DBResults;
import com.ajal.arsocialmessaging.util.database.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Database tests
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseTest implements DBObserver {

    private List<Message> messages;
    private List<Banner> banners;

    // Mutexes to ensure that results have been retrieved from database before running the tests
    private Semaphore messageMutex = new Semaphore(1);
    private Semaphore bannerMutex = new Semaphore(1);

    @Before
    public void init() throws InterruptedException {
        // Request the server to load the results from the database
        DBResults dbResults = DBResults.getInstance();
        DBResults.getInstance().clearObservers();
        dbResults.registerObserver(this);
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
    public void onMessageFailure() {
        this.messages = new ArrayList<>();
        messageMutex.release();
    }

    @Override
    public void onBannerSuccess(List<Banner> result) {
        this.banners = result;
        bannerMutex.release();
    }

    @Override
    public void onBannerFailure() {
        this.banners = new ArrayList<>();
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

        assertEquals(new Integer(1), this.messages.get(0).getId());
        assertEquals("happy birthday", this.messages.get(0).getMessage());
        assertEquals("happy-birthday.obj", this.messages.get(0).getObjfilename());

//        assertEquals("BS8 1UB", this.banners.get(0).getPostcode());
//        assertEquals(new Integer(2), this.banners.get(0).getMessage());
//        assertEquals("2022-02-02 18:40:52.476655", this.banners.get(0).getTimestamp());

        messageMutex.release();
        bannerMutex.release();
    }
}
