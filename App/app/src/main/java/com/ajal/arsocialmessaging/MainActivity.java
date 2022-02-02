package com.ajal.arsocialmessaging;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.ajal.arsocialmessaging.databinding.ActivityMainBinding;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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


        // Set up connection for app to talk to database via rest controller
        MessageService service = ServiceGenerator.createService(MessageService.class);


        // Retrieve all messages stored in database
        Call<List<Message>> callAsync = service.getAllMessages();
        Log.d("MYTAG", "Call has been set up");
        callAsync.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                Log.d("MYTAG", "Got a response "+response.message()+" "+response.errorBody());
                List<Message> allMessages = response.body();
                assert allMessages != null;
                Log.d("MYTAG", "We got a response! "+allMessages.get(2).id+" "+allMessages.get(2).objfilename+" "+allMessages.get(2).message);
            }
            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable throwable) {
                Log.e("MYTAG", "Error " + throwable);
            }
        });

        /*

        // Retrieve all banners stored in database
        Call<List<Banner>> callAsync = service.getAllBanners();
        Log.d("MYTAG", "Call has been set up");
        callAsync.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(@NonNull Call<List<Banner>> call, @NonNull Response<List<Banner>> response) {
                Log.d("MYTAG", "Got a response "+response.message()+" "+response.errorBody());
                List<Banner> allBanners = response.body();
                assert allBanners != null;
                Log.d("MYTAG", "We got a response! "+allBanners.get(0).id+" "+allBanners.get(0).postcode+" "+allBanners.get(0).message+" "+allBanners.get(0).timestamp);
            }
            @Override
            public void onFailure(@NonNull Call<List<Banner>> call, @NonNull Throwable throwable) {
                Log.e("MYTAG", "Error " + throwable);
            }
        });
        
         */

    }

}