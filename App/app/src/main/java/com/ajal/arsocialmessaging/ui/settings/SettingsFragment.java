package com.ajal.arsocialmessaging.ui.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
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
        SharedPreferences toggleAudio = getContext().getSharedPreferences(
                getString(R.string.toggle_audio), Context.MODE_PRIVATE);

        final SeekBarPreference textSize = findPreference("text_size");
        final SwitchPreferenceCompat darkModeSwitch = findPreference("dark_mode");
        final SwitchPreferenceCompat toggleAudioSwitch = findPreference("toggle_audio");
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

        // Toggle audio
        toggleAudioSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            String state;
            Boolean newValueBool = (Boolean) newValue;
            if (newValueBool) {
                state = "On";
//                Toast.makeText(getContext(), "Toggle audio: On", Toast.LENGTH_SHORT).show();
            } else {
                state = "Off";
//                Toast.makeText(getContext(), "Toggle audio: Off", Toast.LENGTH_SHORT).show();
            }
            SharedPreferences.Editor editor = toggleAudio.edit();
            editor.putString(getString(R.string.toggle_audio), state);
            editor.apply();
            String tmp = toggleAudio.getString(getString(R.string.toggle_audio), "null");
            Toast.makeText(getContext(), "Toggle audio: "+tmp, Toast.LENGTH_SHORT).show();
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
