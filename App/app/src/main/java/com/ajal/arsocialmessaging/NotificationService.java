package com.ajal.arsocialmessaging;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.DBHelper;
import com.ajal.arsocialmessaging.util.database.DBObserver;
import com.ajal.arsocialmessaging.util.database.Message;
import com.ajal.arsocialmessaging.util.location.GPSObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

// REFERENCE: https://www.androidtonight.com/2019/07/run-android-sevice-in-background.html 25/02/2022
public class NotificationService extends Service {

    private static final String CHANNEL_ID = "SkyWrite Notifications";
    private static final String channelName = "Background Service";
    public int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        createNotificationChannel();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("RestartService");
        broadcastIntent.setClass(this, RestartNotificationService.class);
        this.sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Timer-related code
    private class NotificationListener implements DBObserver, GPSObserver {

        private List<Banner> globalBanners = new ArrayList<>();
        private Location location;

        @Override
        public void onMessageSuccess(List<Message> result) {

        }

        @Override
        public void onMessageFailure() {

        }

        @Override
        public void onBannerSuccess(List<Banner> result) {
            this.globalBanners = result;
        }

        @Override
        public void onBannerFailure() {

        }

        @Override
        public void onLocationSuccess(Location location) {
            this.location = location;
        }

        public List<Banner> getGlobalBanners() {
            return this.globalBanners;
        }

        public Location getLocation() {
            return this.location;
        }
    }

    private Timer timer;
    private TimerTask timerTask;
    private List<Banner> previousBanners = new ArrayList<>();
    private List<Banner> currentBanners = new ArrayList<>();
    NotificationListener listener = new NotificationListener();

    public void startTimer() {

        // Request the server to load the results from the database
        DBHelper dbHelper = DBHelper.getInstance();
        dbHelper.setNotificationObserver(listener);
        dbHelper.retrieveDBResults();

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                currentBanners = listener.getGlobalBanners();
                if (counter == 0) {
                    if (currentBanners.size() > 0) {
                        previousBanners = currentBanners;
                        counter++;
                    }
                }
                else {
                    if (previousBanners.size() < currentBanners.size()) {
                        sendNotification();
                        previousBanners = currentBanners;
                    }
                    counter++;
                }

                Log.i("Count", "=========  "+ counter+","
                        +previousBanners.size()+","+currentBanners.size()
                        +":"
                        +DBHelper.getInstance().getNumOfObservers()
                );
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            DBHelper.getInstance().setNotificationObserver(null);
            timer = null;
        }
    }

    // Notification code
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "SkyWrite Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification() {
        // Set up intent to open SkyWrite app
        Intent appIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(appIntent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set up notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("SkyWrite")
                .setContentText("You have a new message! Click here to view it!")
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        startForeground(1, notification);

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(69, builder.build());
    }

}
