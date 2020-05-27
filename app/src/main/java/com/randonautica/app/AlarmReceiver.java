package com.randonautica.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{
    public static final String STATS = "stats";
    private static int limitanomalies;
    private static int limitattractors;
    private static int limitvoids;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        loadData(context);

        if(limitanomalies < 5){
            limitanomalies = 5;
        }
        else if(limitattractors < 5){
            limitattractors = 5;
        }
        else if(limitvoids < 5){
            limitvoids = 5;
        }
        saveDataDaily(context);

    }

    //Save data to Shared Preferences
    protected void saveDataDaily(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STATS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("LIMITVOID", limitvoids);
        editor.putInt("LIMITANOMALY", limitanomalies);
        editor.putInt("LIMITATTRACTORS", limitattractors);

        editor.apply();
    }

    public void loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STATS, Context.MODE_PRIVATE);
        limitattractors = sharedPreferences.getInt("LIMITATTRACTORS", 0);
        limitanomalies = sharedPreferences.getInt("LIMITANOMALY", 0);
        limitvoids = sharedPreferences.getInt("LIMITVOID", 0);
    }
}
