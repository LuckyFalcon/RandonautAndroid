package com.randonautica.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.randonautica.app.Classes.DatabaseHelper;

public class AlarmReceiver extends BroadcastReceiver
{
    public static final String STATS = "stats";
    private static int limitanomalies;
    private static int limitattractors;
    private static int limitvoids;
    DatabaseHelper mDatabaseHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        loadData(context);
        if(intent.getAction() =="android.intent.action.TIME_SET"){
            //user changed time
        } else {
            mDatabaseHelper = new DatabaseHelper(context, "Points");
            Cursor data = mDatabaseHelper.getData("Points"); //here it gives up
            Boolean rowExists = false;
            if (data.moveToFirst()) {
                rowExists = true;
            } else {
                rowExists = false;
            }
            if (rowExists) {
                //Do nothing
            } else {
                //Reset points
                if (limitanomalies < 5) {
                    limitanomalies = 5;
                } else if (limitattractors < 5) {
                    limitattractors = 5;
                } else if (limitvoids < 5) {
                    limitvoids = 5;
                }
                saveDataDaily(context);

            }
        }

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
