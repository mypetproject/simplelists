package com.appsverse.SimpleLists;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {


    public static final String PREF_DARK_THEME = "enable_dark_theme";
    public static final String PREF_HIDE_EMPTY_DEPARTMENT = "hide_empty_department";
    public static final String PREF_HISTORY_OF_CHANGES = "history_of_changes";
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.preferences);

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                switch (key) {
                    case PREF_DARK_THEME:
                        changeTheme(sharedPreferences);
                        break;
                    case PREF_HIDE_EMPTY_DEPARTMENT:
                        changeHideEmptyDepartmentStatus(sharedPreferences);
                        break;
                    case PREF_HISTORY_OF_CHANGES:
                        break;
                }
            }
        };

    }

    private void changeHideEmptyDepartmentStatus(SharedPreferences sharedPreferences) {
        MainActivity.setHideEmptyDepartmentPreference(sharedPreferences.getBoolean(PREF_HIDE_EMPTY_DEPARTMENT, false));
    }

    private void changeTheme(SharedPreferences sharedPreferences) {
        if (sharedPreferences.getBoolean(PREF_DARK_THEME, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
