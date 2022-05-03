package com.ajal.arsocialmessaging.util.database.server;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.Message;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerDBHelper {
    private static final String TAG = "SkyWrite";
    private List<Message> messages;
    private List<Banner> banners;
    private List<ServerDBObserver> observers = new ArrayList<>();
    private static ServerDBHelper instance = new ServerDBHelper();

    public static ServerDBHelper getInstance() {
        return instance;
    }

    // Private so it cannot be instantiated outside of getInstance
    private ServerDBHelper() {
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
                assert allMessages != null;
                ServerDBHelper.getInstance().setMessages(true, allMessages);
            }
            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable throwable) {
                // Sends an empty list of messages to observers
                ServerDBHelper.getInstance().setMessages(false, new ArrayList<>());
            }
        });

        // Retrieve all banners stored in database
        Call<List<Banner>> bannerCallAsync = service.getAllBanners();
        bannerCallAsync.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(@NonNull Call<List<Banner>> call, @NonNull Response<List<Banner>> response) {
                List<Banner> allBanners = response.body();
                assert allBanners != null;
                ServerDBHelper.getInstance().setBanners(true, allBanners);
            }

            @Override
            public void onFailure(@NonNull Call<List<Banner>> call, @NonNull Throwable throwable) {
                // Sends an empty array list of banners to observers
                ServerDBHelper.getInstance().setBanners(false, new ArrayList<>());
            }
        });
    }

    public void registerObserver(ServerDBObserver observer) {
        observers.add(observer);
        if (this.messages != null) {
            observer.onMessageSuccess(messages);
        }
        if (this.banners != null) {
            observer.onBannerSuccess(banners);
        }
    }

    public void setMessages(boolean success, List<Message> messages) {
        this.messages = messages;
        if (success) {
            for (ServerDBObserver o : observers) {
                try {
                    o.onMessageSuccess(messages);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    observers.remove(o);
                }
            }
        }
        else {
            for (ServerDBObserver o : observers) {
                try {
                    o.onMessageFailure();
                } catch (Exception e) {
                    observers.remove(o);
                }
            }
        }
    }

    public void setBanners(boolean success, List<Banner> banners) {
        this.banners = banners;
        if (success) {
            for (ServerDBObserver o : observers) {
                try {
                    o.onBannerSuccess(banners);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    observers.remove(o);
                }
            }
        }
        else {
            for (ServerDBObserver o : observers) {
                try {
                    o.onBannerFailure();
                } catch (Exception e) {
                    observers.remove(o);
                }
            }
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    // Note: in theory, this should be safe as you register one observer and remove it every time
    // you switch between fragments that implement ServerDBObserver
    public void clearObservers() {
        this.observers.clear();
    }

    public void removeObserver(ServerDBObserver observer) {
        this.observers.remove(observer);
    }

    public int getNumOfObservers() {
        return this.observers.size();
    }
}