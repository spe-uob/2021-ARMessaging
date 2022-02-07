package com.ajal.arsocialmessaging.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ajal.arsocialmessaging.MainActivity;

public class ConnectivityHelper {
    private boolean networkAvailable;
    private boolean locationAvailable;
    private MainActivity mainActivity;
    private static ConnectivityHelper instance;

    private ConnectivityHelper() {}

    public static ConnectivityHelper getInstance() {
        if (instance == null) {
            instance = new ConnectivityHelper();
        }
        return instance;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isLocationAvailable() {
        LocationManager lm = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled;
        boolean networkEnabled;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            gpsEnabled = false;
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception e) {
            networkEnabled = false;
        }
        return gpsEnabled && networkEnabled;
    }
}
