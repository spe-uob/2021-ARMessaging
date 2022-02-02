package com.ajal.arsocialmessaging.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostcodeHelper {

    /**
     * getLocation returns the current location, which can extract the latitude and longitude to be used for getting a postcode
     * @param ctx
     * @return
     */
    public static Location getLocation(Context ctx) {
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        // As permissions are checked several times, in theory this should always return true
        // However getLastKnownLocation() requires you to check that there are permissions
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location;
    }

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
}
