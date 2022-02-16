package com.ajal.arsocialmessaging.ui.settings;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        SharedPreferences fontSize = getContext().getSharedPreferences(
                getString(R.string.theme_id), Context.MODE_PRIVATE);
        SharedPreferences darkMode = getContext().getSharedPreferences(
                getString(R.string.dark_mode), Context.MODE_PRIVATE);

        final SeekBarPreference textSize = findPreference("text_size");
        final SwitchPreferenceCompat darkModeSwitch = findPreference("dark_mode");
        final Preference manageNotification = findPreference("manage_notification");
        final SwitchPreferenceCompat showPreviewSwitch = findPreference("show_preview");

        //TODO: Font size change
        textSize.setUpdatesContinuously(true);
        textSize.setOnPreferenceChangeListener((preference, newValue) -> {
            Integer newValueInt = (Integer) newValue;;
            int themeId = R.style.FontSizeMedium;
            if (newValueInt == 0){
                themeId = R.style.FontSizeSmall;
            }else if(newValueInt == 1){
                themeId = R.style.FontSizeMedium;
            }else if(newValueInt == 2){
                themeId = R.style.FontSizeLarge;
            }
            SharedPreferences.Editor editor = fontSize.edit();
            editor.putInt("themeID", themeId);
            editor.apply();
            return true;
        });

        //Dark Mode
        darkModeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            String dm;
            Boolean newValueBool = (Boolean) newValue;
            if (newValueBool){
                Toast.makeText(getContext(), "Dark mode On", Toast.LENGTH_SHORT).show();
                dm = "On";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                Toast.makeText(getContext(), "Dark mode off", Toast.LENGTH_SHORT).show();
                dm = "Off";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            SharedPreferences.Editor editor = darkMode.edit();
            editor.putString("darkMode", dm);
            editor.apply();
            return true;
        });

        //Direct to system setting page
        manageNotification.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID);
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, "New Message");
            startActivity(intent);
            return true;
        });

        //TODO: Notification Preview
        showPreviewSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            if (showPreviewSwitch.isChecked()){
                Toast.makeText(getContext(), "Message preview off.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Message will be displayed on lock screen.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        //TODO: Vibrate mode.

    }
}
