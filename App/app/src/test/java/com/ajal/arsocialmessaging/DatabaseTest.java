package com.ajal.arsocialmessaging;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.server.ServerDBObserver;
import com.ajal.arsocialmessaging.util.database.server.ServerDBHelper;
import com.ajal.arsocialmessaging.util.database.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Database tests
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseTest implements ServerDBObserver {

    private List<Message> messages;
    private List<Banner> banners;

    // Mutexes to ensure that results have been retrieved from database before running the tests
    private Semaphore messageMutex = new Semaphore(1);
    private Semaphore bannerMutex = new Semaphore(1);

    @Before
    public void init() throws InterruptedException {
        // Request the server to load the results from the database
        ServerDBHelper serverDbHelper = ServerDBHelper.getInstance();
        ServerDBHelper.getInstance().clearObservers();
        serverDbHelper.registerObserver(this);
        serverDbHelper.retrieveDBResults();

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
        assertEquals("Happy birthday", this.messages.get(0).getMessage());
        assertEquals("happy-birthday", this.messages.get(0).getObjfilename());

        // Note: not testing the other two values as the database will remove banners after a day

        messageMutex.release();
        bannerMutex.release();
    }
}
