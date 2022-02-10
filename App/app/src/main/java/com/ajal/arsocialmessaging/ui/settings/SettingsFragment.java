package com.ajal.arsocialmessaging.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.ajal.arsocialmessaging.BuildConfig;
import com.ajal.arsocialmessaging.R;


public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        final SeekBarPreference textSize = findPreference("text_size");
        final SwitchPreferenceCompat darkModeSwitch = findPreference("dark_mode");
        Preference manageNotification = findPreference("manage_notification");

        //TODO: Font size change
//        textSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
//                return false;
//            }
//        });

        //Dark Mode
        darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                if (darkModeSwitch.isChecked()){
                    Toast.makeText(getContext(), "dark mode off", Toast.LENGTH_SHORT).show();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                return true;
            }
        });

        //TODO: Direct to system setting page
        manageNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID);
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, "New Message");
                startActivity(intent);
                Toast.makeText(getContext(),"you clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }
}
