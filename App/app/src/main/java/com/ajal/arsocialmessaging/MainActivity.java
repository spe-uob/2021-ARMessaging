package com.ajal.arsocialmessaging;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ajal.arsocialmessaging.util.PermissionHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ajal.arsocialmessaging.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final List<Message> messages = new ArrayList<>();
    private final List<Banner> banners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check that SkyWrite has the correct permissions and if not, request them
        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.requestPermissions(this);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_message,
                R.id.navigation_notifications,
                R.id.navigation_settings,
                R.id.navigation_gallery)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);



//        // Set up connection for app to talk to database via rest controller
//        MessageService service = ServiceGenerator.createService(MessageService.class);
//
//        // Retrieve all messages stored in database
//        Call<List<Message>> callAsync = service.getAllMessages();
//        Log.d("MYTAG", "Call has been set up");
//        callAsync.enqueue(new Callback<List<Message>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
//                Log.d("MYTAG", "Got a response, error is "+response.errorBody());
//                List<Message> allMessages = response.body();
//                // NOTE: use allMessages.get([INDEX]).[ATTRIBUTE] to extract message data, as below
//                assert allMessages != null;
//                messages.addAll(allMessages);
//                Log.d("MYTAG", "Response: "+allMessages.get(0).id+" "+allMessages.get(0).objfilename+" "+allMessages.get(0).message);
//                Log.d("MYTAG", "Response: "+allMessages.get(4).id+" "+allMessages.get(4).objfilename+" "+allMessages.get(4).message);
//            }
//            @Override
//            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable throwable) {
//                Log.e("MYTAG", "Error " + throwable);
//            }
//        });
//
//        // Retrieve all banners stored in database
//        Call<List<Banner>> bannerCallAsync = service.getAllBanners();
//        Log.d("MYTAG", "Call has been set up");
//        bannerCallAsync.enqueue(new Callback<List<Banner>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Banner>> call, @NonNull Response<List<Banner>> response) {
//                Log.d("MYTAG", "Got a response "+response.message()+" "+response.errorBody());
//                List<Banner> allBanners = response.body();
//                assert allBanners != null;
//                banners.addAll(allBanners);
//                // NOTE: use allBanners.get([INDEX]).[ATTRIBUTE] to extract banner data, as below
//                Log.d("MYTAG", "Response: "+" "+allBanners.get(0).postcode+" "+allBanners.get(0).message+" "+allBanners.get(0).timestamp);
//                Log.d("MYTAG", "Response: "+" "+allBanners.get(8).postcode+" "+allBanners.get(8).message+" "+allBanners.get(8).timestamp);
//            }
//            @Override
//            public void onFailure(@NonNull Call<List<Banner>> call, @NonNull Throwable throwable) {
//                Log.e("MYTAG", "Error " + throwable);
//            }
//        });

    }

    /**
     * If the viewPager is opened, pressing back will "close" it
     * Otherwise, use super.onBackPressed()
     */
    @Override
    public void onBackPressed() {
        ViewPager viewPager = findViewById(R.id.viewPagerMain);
        RecyclerView rv = findViewById(R.id.rv);
        if (viewPager != null) { // it can be null when the Gallery fragment is not open
            if (viewPager.getVisibility() == View.VISIBLE) {
                rv.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.INVISIBLE);
            }
            else {
                super.onBackPressed();
            }
        }
        else {
            super.onBackPressed();
        }
    }

}
