package com.ajal.arsocialmessaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ajal.arsocialmessaging.util.ConnectivityHelper;
import com.ajal.arsocialmessaging.util.database.MessageService;
import com.ajal.arsocialmessaging.util.database.ServiceGenerator;
import com.ajal.arsocialmessaging.util.location.GPSObserver;
import com.ajal.arsocialmessaging.util.location.PostcodeHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFCMService extends FirebaseMessagingService implements GPSObserver {

    private static final String TAG = "SkyWrite";
    private String token;
    private String postcode;
    private Semaphore postcodeMutex = new Semaphore(1); // used to ensure that the postcode is retrieved before checking postcode

    @Override
    public void onCreate() {
        boolean sendToServer = true;
        retrieveToken(sendToServer);
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
        Log.d(TAG, "Refreshed token: " + token);
        this.token = token;
        // Check if network is available
        if (ConnectivityHelper.getInstance().isNetworkAvailable()) {
            sendRegistrationToServer(token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If no location is available, return
        if (!ConnectivityHelper.getInstance().isLocationAvailable()) {
            return;
        }

        PostcodeHelper.getInstance().registerObserver(this);
        try {
            postcodeMutex.acquire();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                String postcode = remoteMessage.getData().get("postcode");

                try {
                    postcodeMutex.acquire();
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                // if the user is in the postcode of the newly added message, display a notification
                if (postcode.equals(this.postcode)) {
                    sendNotification(remoteMessage.getNotification());
                }
                postcodeMutex.release();
            }
        }

        PostcodeHelper.getInstance().removeObserver(this);
    }

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

    private void sendNotification(RemoteMessage.Notification notification) {
        String title = notification.getTitle();
        String body = notification.getBody();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Note: FCM comes with a default notification channel
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_skywrite_logo_vector)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onLocationSuccess(Location location) {
        this.postcode = PostcodeHelper.getPostCode(this, location.getLatitude(), location.getLongitude());
        postcodeMutex.release();
    }
}
