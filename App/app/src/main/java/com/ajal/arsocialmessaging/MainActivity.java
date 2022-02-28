package com.ajal.arsocialmessaging;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ajal.arsocialmessaging.util.ConnectivityHelper;
import com.ajal.arsocialmessaging.util.PermissionHelper;
import com.ajal.arsocialmessaging.util.database.client.ClientDBHelper;
import com.ajal.arsocialmessaging.util.location.PostcodeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ajal.arsocialmessaging.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SkyWrite";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        // Check that SkyWrite has the correct permissions and if not, request them
        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.requestPermissions(this);
        }
        else {
            // Start the FCM notification service
            Intent intent = new Intent(this, NotificationFCMService.class);
            startService(intent);

            ConnectivityHelper.getInstance().setMainActivity(this);
            // Initiate the location updates request if location is available
            Context ctx = this.getApplicationContext();
            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            PostcodeHelper postcodeHelper = PostcodeHelper.getInstance();
            // Permissions check
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lm.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 10, postcodeHelper);

            // Preferences
            SharedPreferences theme = getSharedPreferences(getString(R.string.theme_id), Context.MODE_PRIVATE);
            SharedPreferences darkM = getSharedPreferences(getString(R.string.dark_mode), Context.MODE_PRIVATE);

            // Dark mode
            if (darkM.getString("darkMode", "On").equals("On")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(theme.getInt("themeID", R.style.FontSizeMedium));
            }

            theme.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    setTheme(sharedPreferences.getInt(key, R.style.FontSizeMedium));
                }
            });
            
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Set up navbar
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
        }

        return;
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

    @Override
    public void onDestroy() {
        ClientDBHelper clientDBHelper = new ClientDBHelper(this);
        clientDBHelper.resetTable(); // reset the table after the user has left the app
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if Permissions aren't granted despite requesting for them, open the device's Settings page
        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.requestPermissionsIfDenied(this);
            return;
        }

        // Set up app once permissions have been granted
        ConnectivityHelper.getInstance().setMainActivity(this);
        // Initiate the location updates request if location is available
        Context ctx = this.getApplicationContext();
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        PostcodeHelper postcodeHelper = PostcodeHelper.getInstance();
        // Permissions check - required for lm.requestLocationUpdates
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 10, postcodeHelper);

        // Preferences
        SharedPreferences theme = getSharedPreferences(getString(R.string.theme_id), Context.MODE_PRIVATE);
        SharedPreferences darkM = getSharedPreferences(getString(R.string.dark_mode), Context.MODE_PRIVATE);

        // Dark mode
        if (darkM.getString("darkMode", "On").equals("On")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setTheme(theme.getInt("themeID", R.style.FontSizeMedium));
        }

        theme.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                setTheme(sharedPreferences.getInt(key, R.style.FontSizeMedium));
            }
        });

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

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
    }

}
