package com.randonautica.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;


public class MySettingsFragment extends PreferenceFragmentCompat {

    //set preference call string
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWTICHEnableDarkMode = "enableDarkMode";

    // Get the preference widgets reference
    private SwitchPreferenceCompat enableDarkMode;

    //setting actual values
    private boolean switchOnOff;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getActivity().setTitle("Settings");
        setPreferencesFromResource(R.xml.preferences, rootKey);
        loadData();
        updateViews();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        enableDarkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object isVibrateOnObject) {
                if(enableDarkMode.isChecked()){
                    switchOnOff = false;
                    saveData();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    switchOnOff = true;
                    saveData();
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                return true;
            }
        });


    }

    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWTICHEnableDarkMode, switchOnOff);

        editor.apply();

    }
    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(SWTICHEnableDarkMode, false);

    }

    public void updateViews() {
        enableDarkMode = (SwitchPreferenceCompat) findPreference("enableDarkMode");
        enableDarkMode.setChecked(switchOnOff);


    }
}

