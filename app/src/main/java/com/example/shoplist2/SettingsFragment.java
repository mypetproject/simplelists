package com.example.shoplist2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {


    public static final String PREF_DARK_THEME = "enable_dark_theme";
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.preferences);

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(PREF_DARK_THEME)) {
                 //   Preference darkThemeKey = findPreference(key);
                 //   darkThemeKey.setSummary(sharedPreferences.getString(key + " xxx", " darkThemeKey changed"));
                //   changeTheme(sharedPreferences);
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
            }
        };

    }

    private void changeTheme(SharedPreferences sharedPreferences) {
        if (sharedPreferences.getBoolean(PREF_DARK_THEME,false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
           // Log.d("myLogs", "PREF_DARK_THEME true");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
          //  Log.d("myLogs", "PREF_DARK_THEME false");
        }
        //startActivity(new Intent(getContext(), MainActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceChangeListener);

       // Preference darkThemeKey = findPreference(PREF_DARK_THEME);
       // darkThemeKey.setSummary(getPreferenceScreen().getSharedPreferences().getString(PREF_DARK_THEME + " xxx", " darkThemeKey changed"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
