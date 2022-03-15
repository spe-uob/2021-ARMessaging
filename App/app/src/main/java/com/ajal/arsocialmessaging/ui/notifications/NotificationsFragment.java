package com.ajal.arsocialmessaging.ui.notifications;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.ajal.arsocialmessaging.R;
import com.ajal.arsocialmessaging.databinding.FragmentNotificationsBinding;
import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.util.database.client.ClientDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NotificationsFragment extends Fragment {

    private static final String TAG = "SkyWrite";
    private FragmentNotificationsBinding binding;
    private ClientDBHelper clientDBHelper;
    private List<Banner> notificationBanners = new ArrayList<>();

    private Timer timer;
    private TimerTask timerTask;
    private Handler timerHandler = new Handler();


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        super.onCreate(savedInstanceState);

        clientDBHelper = new ClientDBHelper(this.getContext());
        notificationBanners = clientDBHelper.getAllNewBanners();
        displayNewBanners();

        startTimer();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        stopTimer();
    }

    private void displayNewBanners() {

        View root = binding.getRoot();

        // Hide the text view if there are new notifications to be displayed
        TextView textView = root.findViewById(R.id.text_notifications_none);
        if (notificationBanners.size() == 0) {
            return;
        }
        else {
            textView.setVisibility(View.INVISIBLE);
        }

        // Fills the ListView with messages
        ListView listView = root.findViewById(R.id.list_notifications);
        NotificationListAdapter adapter = new NotificationListAdapter(this.getContext(), R.layout.notification_list_item, notificationBanners);
        listView.setAdapter(adapter);

        // Sets a listener to figure out what item was clicked in list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Position of notification in list: "+position);
                // TODO
            }
        });
    }

    private void stopTimer() {
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                timerHandler.post(new Runnable() {
                    public void run(){
                        // Repeatedly check to see if there are any new notifications to be added
                        List<Banner> newBanners = clientDBHelper.getAllNewBanners();
                        if (newBanners.size() > notificationBanners.size()) {
                            notificationBanners = newBanners;
                            displayNewBanners();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000, 2000);
    }
}