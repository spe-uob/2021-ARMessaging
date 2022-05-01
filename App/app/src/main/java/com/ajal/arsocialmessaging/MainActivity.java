package com.ajal.arsocialmessaging;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import com.ajal.arsocialmessaging.util.ConnectivityHelper;
import com.ajal.arsocialmessaging.util.PermissionHelper;
import com.ajal.arsocialmessaging.util.database.server.ServerDBHelper;
import com.ajal.arsocialmessaging.util.location.PostcodeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ajal.arsocialmessaging.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SkyWrite";
    private ActivityMainBinding binding;
    ServerDBHelper serverDBHelper = ServerDBHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        ConnectivityHelper.getInstance().setMainActivity(this);

        super.onCreate(savedInstanceState);

        // Start the FCM notification service
        Intent intent = new Intent(this, NotificationFCMService.class);
        startService(intent);

        // Check that SkyWrite has the correct permissions and if not, request them
        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.requestPermissions(this);
        }
        else {
            if (ConnectivityHelper.getInstance().isNetworkAvailable()) {
                serverDBHelper.retrieveDBResults();
            }
            loadApp();
        }

        return;
    }

    @Override
    public void onDestroy() {
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
        this.recreate();
    }

    public void loadApp() {
        // Initiate the location updates request if location is available
        Context ctx = this.getApplicationContext();
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        PostcodeHelper postcodeHelper = PostcodeHelper.getInstance();
        // Permissions check
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0, 0, postcodeHelper);

        // Preferences
        SharedPreferences theme = getSharedPreferences(getString(R.string.theme_id), Context.MODE_PRIVATE);
        SharedPreferences darkM = getSharedPreferences(getString(R.string.dark_mode), Context.MODE_PRIVATE);

        // Dark mode
        if (darkM.getString("darkMode", "On").equals("On")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            setTheme(theme.getInt("themeID", R.style.FontSizeMedium));
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

        return;
    }
}
