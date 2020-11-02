package com.gng2101groupb32.pathfindr.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.gng2101groupb32.pathfindr.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}