package com.ajal.arsocialmessaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ajal.arsocialmessaging.util.ConnectivityHelper;
import com.ajal.arsocialmessaging.util.HashCreator;
import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.client.ClientDBHelper;
import com.ajal.arsocialmessaging.util.database.server.MessageService;
import com.ajal.arsocialmessaging.util.database.server.ServerDBHelper;
import com.ajal.arsocialmessaging.util.database.server.ServiceGenerator;
import com.ajal.arsocialmessaging.util.location.GPSObserver;
import com.ajal.arsocialmessaging.util.location.PostcodeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Semaphore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFCMService extends FirebaseMessagingService implements GPSObserver {

    private static final String TAG = "SkyWrite";
    private String token;
    private String postcode = "";
    private Semaphore postcodeMutex = new Semaphore(1); // used to ensure that the postcode is retrieved before checking postcode

    private ClientDBHelper clientDBHelper;

    @Override
    public void onCreate() {
        this.clientDBHelper = new ClientDBHelper(this);
        boolean sendToServer = true;
        if (ConnectivityHelper.getInstance().isNetworkAvailable()) {
            retrieveToken(sendToServer);
        }
        else {
            Toast.makeText(this.getApplicationContext(), "Network error. Please try again.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "R/efreshed token: " + token);
        this.token = token;
        sendRegistrationToServer(token);
    }

    // Note: if the user has the app open then it will filter the notifications to only their postcode,
    // otherwise the user will receive every notification, which is why there are different notification titles and bodies
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        PostcodeHelper.getInstance().registerObserver(this);
        try {
            postcodeMutex.acquire();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String postcode = remoteMessage.getData().get("postcode");
            // If there is a postcode value,
            if (postcode != null) {
                try {
                    postcodeMutex.acquire();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                // if the user is in the postcode of the newly added message:
                // display a notification and store it in a database to be displayed in the Notification Fragment
                if (postcode.equals(HashCreator.createSHAHash(this.postcode))) {
                    // Notifies the relevant observers to request for the banners from server again
                    ServerDBHelper.getInstance().retrieveDBResults();

                    // Send notification
                    String title = "New message at: " + this.postcode;
                    String body = "Click here to open the app";
                    sendNotification(title, body); // send notification to the notification channel
                    saveNotification(remoteMessage.getData()); // save notification to the SQLite database
                }
                postcodeMutex.release();
            }
            else {
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                sendNotification(title, body);
            }
        }

        PostcodeHelper.getInstance().removeObserver(this);
    }

    // Retrieve token from FCM
    public void retrieveToken(boolean sendToServer) {
        final String[] result = new String[1];
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration result
                    result[0] = task.getResult();
                    String msg = "Instance ID: "+ result[0];
                    Log.d(TAG, msg);
                    token = result[0];

                    if (sendToServer) {
                        sendRegistrationToServer(token);
                    }
                }
            });
    }

    // Send token to Spring boot server
    public static void sendRegistrationToServer(String token) {
        // Send registration token to server
        MessageService service = ServiceGenerator.createService(MessageService.class);
        String tokenData = token;
        Call<String> addTokenCall = service.addToken(tokenData);
        addTokenCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    Log.d(TAG, "Got a response, error is: "+response.errorBody().string()+", message is: "+response.message());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String postResponse = response.body();
                Log.d(TAG, "Send token to server. Response: "+postResponse);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                call.cancel();
            }
        });
    }

    // Displays the notification - sends it down the FCM default notification channel
    private void sendNotification(String title, String body) {
        // Note: FCM comes with a default notification channel
        String channelId = getString(R.string.default_notification_channel_id);

        // Set up notification channel
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "SkyWrite Notification Title",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        // Set up notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_skywrite_logo_vector)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        // Send notification down channel
        notificationManager.notify(0 , notificationBuilder.build());
    }

    // Saves the notification to into the local SQLite Database on the Android device
    private void saveNotification(Map<String, String> remoteMessageData) {
        int messageId = Integer.parseInt(remoteMessageData.get("message"));
        String timestamp = remoteMessageData.get("timestamp");
        Banner banner = new Banner(this.postcode, messageId, timestamp);

        clientDBHelper.insertNewBanner(banner);
    }

    @Override
    public void onLocationSuccess(Location location) {
        this.postcode = PostcodeHelper.getPostCode(this, location.getLatitude(), location.getLongitude());
        postcodeMutex.release();
    }
}
