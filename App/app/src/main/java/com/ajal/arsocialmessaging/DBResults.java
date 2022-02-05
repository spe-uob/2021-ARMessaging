package com.ajal.arsocialmessaging;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DBResults {
    private static final String TAG = "SkyWrite";
    private List<Message> messages;
    private List<Banner> banners;
    private List<ApiCallback> callbacks = new ArrayList<>();
    private static volatile DBResults instance = new DBResults();

    public static DBResults getInstance() {
        return instance;
    }

    // Private so it cannot be instantiated outside of getInstance
    private DBResults() {
    }

    public void retrieveDBResults() {
        // Set up connection for app to talk to database via rest controller
        MessageService service = ServiceGenerator.createService(MessageService.class);

        // Retrieve all messages stored in database
        Call<List<Message>> callAsync = service.getAllMessages();
        callAsync.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                List<Message> allMessages = response.body();
                // NOTE: use allMessages.get([INDEX]).[ATTRIBUTE] to extract message data, as below
                assert allMessages != null;
                DBResults.getInstance().setMessages(allMessages);
            }
            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });

        // Retrieve all banners stored in database
        Call<List<Banner>> bannerCallAsync = service.getAllBanners();
        bannerCallAsync.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(@NonNull Call<List<Banner>> call, @NonNull Response<List<Banner>> response) {
                List<Banner> allBanners = response.body();
                assert allBanners != null;
                DBResults.getInstance().setBanners(allBanners);
            }

            @Override
            public void onFailure(@NonNull Call<List<Banner>> call, @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
            }
        });
    }

    public void registerCallback(ApiCallback callback) {
        callbacks.add(callback);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        for (ApiCallback c : callbacks) {
            c.onMessageSuccess(messages);
        }
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
        for (ApiCallback c : callbacks) {
            c.onBannerSuccess(banners);
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    // Note: in theory, this should be safe as you register one ApiCallback and remove it every time
    // you switch between fragments that implement ApiCallback
    public void clearCallbacks() {
        this.callbacks.clear();
    }
}