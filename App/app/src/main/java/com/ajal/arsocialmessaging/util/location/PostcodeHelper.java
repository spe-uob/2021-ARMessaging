package com.ajal.arsocialmessaging.util.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import com.ajal.arsocialmessaging.util.database.Banner;
import com.ajal.arsocialmessaging.ui.home.common.VirtualMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (location != null) {
            observer.onLocationSuccess(location);
        }
    }

    public void clearObservers() {
        this.observers.clear();
    }

    public void removeObserver(GPSObserver observer) {
        this.observers.remove(observer);
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

    public static String formatPostcode(String input) {
        String postcode = input.toUpperCase(Locale.ROOT);
        postcode = postcode.replace(" ", ""); // removes any spaces

        int x = postcode.length() - 3;
        String outcode = postcode.substring(0, x);
        String incode = postcode.substring(x);
        postcode = outcode + " " + incode;

        return postcode;
    }

    public static boolean checkPostcodeValid(String input) {
        // REFERENCE: https://howtodoinjava.com/java/regex/uk-postcode-validation/ 18/02/2022 16:46
        String regex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
