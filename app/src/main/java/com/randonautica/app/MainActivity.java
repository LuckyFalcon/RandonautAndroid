package com.randonautica.app;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.amplitude.api.Amplitude;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;
import com.randonautica.app.Interfaces.MainActivityMessage;

import java.io.InputStream;
import java.util.Calendar;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements MainActivityMessage, MyAttractorsListFragment.SendMessage, MyCamRngFragment.SendMessage, NavigationView.OnNavigationItemSelectedListener {
    public static NavigationView navigationView;
    Dialog privacyPolicyDialog;
    private static int limitanomalies;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String STATS = "stats";

    private String userId;
    private String tag;

    private androidx.fragment.app.FragmentManager fragmentManager;
    public static  DrawerLayout drawer;

    public static ActionBarDrawerToggle toggle;

    private boolean darkModeSwitch;
    private Boolean privacyPolicyAccepted;

    Toolbar toolbar;

    public static final String SWTICHEnableDarkMode = "enableDarkMode";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        Amplitude.getInstance().initialize(this, "0bd7e7711d1c3a30578d365260d2d166").enableForegroundTracking(getApplication());

        setContentView(R.layout.activity_main);

        //Set the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Enable drawer menu within the toolbar
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Load user data
        loadData();

        //Check for userId
        if (userId == null) {
            //userId
            userId = UUID.randomUUID().toString();
            saveData();
            Intent receiverIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, 123456789, receiverIntent, 0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 58);

            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, sender);
        }

        //Set Navigation header username equal to userId
        View headerView = navigationView.getHeaderView(0);
        TextView text = headerView.findViewById(R.id.textViewuserId);
        text.setText(userId.substring(0, 8));

        //Check if dark mode is enabled
        if (darkModeSwitch) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //Check if Privacy and Policy was accepted
        if (privacyPolicyAccepted == false) {
            showPrivacyPolicyAlertDialog(navigationView);

        } else {

          //  if(limitanomalies != Integer.MAX_VALUE) {

                //Continue loading the app
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RandonautFragment(), "randonaut")
                        .addToBackStack("randonaut")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_randonaut);
//            } else {
                //Continue loading the app
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new RandonautFragment(), "randonaut")
//                        .addToBackStack("randonaut")
//                        .commit();
//                navigationView.setCheckedItem(R.id.nav_randonaut);
           // }


        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_randonaut:
                fragmentManager = getSupportFragmentManager();
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                tag = "randonaut";

                Fragment randonautfragment = (Fragment) fragmentManager.findFragmentByTag(tag);

                //Check for existing randonaut fragment
                if (randonautfragment == null) {
                    randonautfragment = new RandonautFragment();
                }

                //Add the fragment in stack with the corresponding tag and start the fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, randonautfragment, tag)
                        .addToBackStack(tag)
                        .commit();

                break;
            case R.id.nav_attractors:
                fragmentManager = getSupportFragmentManager();
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                tag = "bot";

                //Check for existing bot fragment
                Fragment botfragment = (Fragment) fragmentManager.findFragmentByTag(tag);
                if (botfragment == null) {
                    botfragment = new MyBotFragment();
                }

                //Add the fragment in stack with the corresponding tag and start the fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, botfragment, tag)
                        .addToBackStack(tag)
                        .commit();
                break;
            case R.id.nav_upgrade:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyUpgradeFragment()).commit();
                break;
            case R.id.nav_slideshow:
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyListFragment()).commit();
                break;
            case R.id.nav_tools:
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MySettingsFragment()).commit();
                break;
            case R.id.nav_profile:
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyProfileFragment()).commit();
                break;
            case R.id.nav_share:
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                onCreateDialog();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public Dialog onCreateDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Coming soon!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void showPrivacyPolicyAlertDialog(final NavigationView navigationView) {

        privacyPolicyDialog = new Dialog(this);
        privacyPolicyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        privacyPolicyDialog.setContentView(R.layout.dialog_privacy);

        privacyPolicyDialog.setCancelable(false);
        privacyPolicyDialog.setCanceledOnTouchOutside(false);

        Button yesAnwserButton = (Button) privacyPolicyDialog.findViewById(R.id.agreeButton);
        Button noAnwserButton = (Button) privacyPolicyDialog.findViewById(R.id.disagreeBtuton);

        TextView privacyPolicyTextView = (TextView) privacyPolicyDialog.findViewById(R.id.privacyTextView);
        privacyPolicyTextView.setMovementMethod(new ScrollingMovementMethod());

        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.term);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            privacyPolicyTextView.setText(new String(b));
        } catch (Exception e) {
            privacyPolicyTextView.setText("https://bot.randonauts.com/privacy-and-terms.html");
        }

        //Button listener for yes
        yesAnwserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save the the users choice
                privacyPolicyAccepted = true;
                privacyPolicyDialog.dismiss();
                saveData();
                //Continue loading the app
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RandonautFragment(), "randonaut")
                        .addToBackStack("randonaut")
                        .commit();
                navigationView.setCheckedItem(R.id.nav_randonaut);
            }
        });

        //Button listener for no
        noAnwserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ask until the user presses the agree button
                privacyPolicyDialog.dismiss();
                showPrivacyPolicyAlertDialog(navigationView);

            }
        });


        privacyPolicyDialog.show();

        //End privacy and policy

    }

    //Send Coordinates from one of the Attractor lists to randonaut fragment
    public void sendData(int type, double power, double x, double y, double radiusm, double z_score, double pseudo) {
        tag = "randonaut";
        FragmentManager fragmentManager = getSupportFragmentManager();
        RandonautFragment randonautfragment = (RandonautFragment) fragmentManager.findFragmentByTag(tag);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_randonaut);

        //Check if ram doesn't double each time and this actually resets the randonauts fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, randonautfragment, tag)
                .addToBackStack(tag)
                .commit();

        randonautfragment.onShowProfileAttractors(type, power, x, y, radiusm, z_score, pseudo);
    }

    //Send Entropy from Camera RNG Fragment
    public void sendEntropyObj(int size, String entropy) {
        tag = "randonaut";
        FragmentManager fragmentManager = getSupportFragmentManager();
        RandonautFragment randonautfragment = (RandonautFragment) fragmentManager.findFragmentByTag(tag);

        //Checks if size was 0, which means it was cancelled
        if (size != 0) {
            randonautfragment.setQuantumEntropy(size, entropy, "Camera");
        }

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, randonautfragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    //Start the CameraRNG dialog from within a dialog
    public void rng(int distance) {
        tag = "camrng";
        FragmentManager fragmentManager = getSupportFragmentManager();
        MyCamRngFragment MyCamRngFragment = new MyCamRngFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("VALUE1", distance);
        MyCamRngFragment.setArguments(arguments);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyCamRngFragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    //Enable the on back press key to open previous fragment from the stack
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Save data to Shared Preferences
    private void saveData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(STATS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putBoolean("PRIVACY", privacyPolicyAccepted);
        editor.putInt("LIMITVOID", 5);
        editor.putInt("LIMITANOMALY", 5);
        editor.putInt("LIMITATTRACTORS", 5);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(STATS, Context.MODE_PRIVATE);

        userId = sharedPreferences.getString("userId", null);
        privacyPolicyAccepted = sharedPreferences.getBoolean("PRIVACY", false);

        //Check for dark mode in SHARED_PREFS
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        darkModeSwitch = sharedPreferences.getBoolean(SWTICHEnableDarkMode, false);
        limitanomalies = sharedPreferences.getInt("LIMITATTRACTORS", 0);

    }



}
