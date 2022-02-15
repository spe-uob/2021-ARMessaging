package com.ajal.arsocialmessaging.ui.notifications;

import static androidx.core.app.NotificationCompat.VISIBILITY_PRIVATE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.ajal.arsocialmessaging.MainActivity;
import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentNotificationsBinding;


public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        super.onCreate(savedInstanceState);
        createNotificationChannel();

//        Button notifyBtn = (Button) root.findViewById(R.id.test_btn);

        // Direct to home page when clicking on notification.
        // TODO: Try directing to notification page when clicking.
        Intent intent = new Intent(this.getContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getContext());
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        @SuppressLint("WrongConstant") NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext(), "New Message")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("New message")
                .setContentText("New banners have been sent to nearby post code. Check it out!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
//                .setVisibility(VISIBILITY_PRIVATE)
                .setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this.getContext());


//        notifyBtn.setOnClickListener(v -> {
//            managerCompat.notify(1, builder.build());
//        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New Message";
            String description = "Manage notification for new messages.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("New Message", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = this.getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}