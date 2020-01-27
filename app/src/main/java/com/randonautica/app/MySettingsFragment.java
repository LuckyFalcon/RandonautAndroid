package com.randonautica.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;


public class MySettingsFragment extends PreferenceFragmentCompat {

    //set preference call string
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWTICHEnableDarkMode = "enableDarkMode";
    public static final String SWTICHEnableWaterPoints = "enableWaterPoints";

    // Get the preference widgets reference
    private SwitchPreferenceCompat enableDarkMode;
    private SwitchPreferenceCompat enableWaterPoints;

    //setting actual values
    private boolean switchOnOff;
    private boolean waterSwitchOnOff;

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

        enableWaterPoints.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object isVibrateOnObject) {
                if(enableWaterPoints.isChecked()){
                    waterSwitchOnOff = false;
                    saveData();
                } else {
                    waterSwitchOnOff = true;
                    saveData();
                }
                return true;
            }
        });



    }

    public void saveData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SWTICHEnableDarkMode, switchOnOff);
        editor.putBoolean(SWTICHEnableWaterPoints, waterSwitchOnOff);

        editor.apply();

    }
    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(SWTICHEnableDarkMode, false);
        waterSwitchOnOff = sharedPreferences.getBoolean(SWTICHEnableWaterPoints, false);

    }

    public void updateViews() {
        enableDarkMode = (SwitchPreferenceCompat) findPreference("enableDarkMode");
        enableDarkMode.setChecked(switchOnOff);

        enableWaterPoints = (SwitchPreferenceCompat) findPreference("enableWaterPoints");
        enableWaterPoints.setChecked(waterSwitchOnOff);

    }
}

