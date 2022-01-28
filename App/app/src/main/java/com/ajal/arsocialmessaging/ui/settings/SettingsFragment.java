package com.ajal.arsocialmessaging.ui.settings;

import android.os.Bundle;


import androidx.preference.PreferenceFragmentCompat;

import com.ajal.arsocialmessaging.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
