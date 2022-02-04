package com.ajal.arsocialmessaging.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.widget.Toast;


import androidx.preference.PreferenceFragmentCompat;

import com.ajal.arsocialmessaging.MainActivity;
import com.ajal.arsocialmessaging.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
