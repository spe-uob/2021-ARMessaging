package com.ajal.arsocialmessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ajal.arsocialmessaging.ui.settings.SettingsFragment;

public class NotificationActivity extends AppCompatActivity {

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel1";
            String description = "test notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);
        createNotificationChannel();

        Button notifyBtn = (Button) findViewById(R.id.test_btn);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationActivity.this, "Channel1");
                builder.setSmallIcon(R.drawable.ic_message_black_24dp);
                builder.setContentTitle("Notification!!!");
                builder.setContentText("You have successfully sent yourself a notification yayy.");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationActivity.this);


        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button working", Toast.LENGTH_SHORT).show();
                managerCompat.notify(1, builder.build());
            }
        });



    }
}