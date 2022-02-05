package com.ajal.arsocialmessaging.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ajal.arsocialmessaging.Banner;
import com.ajal.arsocialmessaging.ui.home.common.VirtualMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostcodeHelper implements LocationListener {

    private Location location;
    private List<GPSObserver> observers = new ArrayList<>();

    private static PostcodeHelper instance;

    private PostcodeHelper() {}

    public static PostcodeHelper getInstance() {
        if (instance == null) {
            instance = new PostcodeHelper();
        }
        return instance;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.location = location;
        for (GPSObserver o : observers) {
            o.onLocationSuccess(location);
        }
    }

    public void registerObserver(GPSObserver observer) {
        this.observers.add(observer);
    }

    public void clearObservers() {
        this.observers.clear();
    }

    /**
     * getLocation returns the current location, which can extract the latitude and longitude to be used for getting a postcode
     * @return
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * getPostcode takes in the latitude and longitude of a given location and returns a postcode
     * @param ctx
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getPostCode(Context ctx, double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
            Address address = geocoder.getFromLocation(latitude, longitude, 1).get(0);

            // Return the postcode from the address
            return address.getPostalCode();
        }
        catch (IOException e) {
            return null;
        }
    }

    public static List<VirtualMessage> getLocalVirtualMessages(Context ctx, List<Banner> globalBanners, double latitude, double longitude) {
        List<VirtualMessage> result = new ArrayList<>();
        String currentPostcode = PostcodeHelper.getPostCode(ctx, latitude, longitude);
        for (Banner b : globalBanners) {
            if (b.getPostcode().equals(currentPostcode)) {
                VirtualMessage virtualMessage = new VirtualMessage(b);
                result.add(virtualMessage);
            }
        }
        return result;
    }
}
