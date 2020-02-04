package com.randonautica.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements RandonautFragment.SendMessage, MyAttractorsListFragment.SendMessage, NavigationView.OnNavigationItemSelectedListener  {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String STATS = "stats";
    private String userid;
    private Boolean privacyPolicyAccepted;
    private DrawerLayout drawer;
    private String tag;
    private boolean switchOnOff;
    public static final String SWTICHEnableDarkMode = "enableDarkMode";

    Dialog privacyPolicyDialog;

    private NavigationView navigationView;

    private FragmentManager fragmentManager;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Load user data
        loadData();

        //Check for userid
        if(userid == null){
            //UserID
            userid = UUID.randomUUID().toString();
            saveData();
        }

        //Check for dark mode enabled
        if(switchOnOff){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //Check for Privacy and Policy
        if(privacyPolicyAccepted == false){
            showPrivacyPolicyAlertDialog(navigationView);
        } else {
            //Continue loading the app
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RandonautFragment(), "randonaut")
                    .addToBackStack("randonaut")
                    .commit();
            navigationView.setCheckedItem(R.id.nav_randonaut);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_randonaut:

                fragmentManager = getSupportFragmentManager();
                tag = "randonaut";

                Fragment randonautfragment = (Fragment) fragmentManager.findFragmentByTag(tag);
                if (randonautfragment == null) {
                    randonautfragment = new RandonautFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, randonautfragment, tag)
                        .addToBackStack(tag)
                        .commit();
                break;
            case R.id.nav_attractors:

                fragmentManager = getSupportFragmentManager();
                tag = "bot";

                Fragment botfragment = (Fragment) fragmentManager.findFragmentByTag(tag);
                if (botfragment == null) {
                    botfragment = new MyBotFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, botfragment, tag)
                        .addToBackStack(tag)
                        .commit();
                break;
            case R.id.nav_slideshow:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyList()).commit();
                break;
            case R.id.nav_tools:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MySettingsFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyProfileFragment()).commit();
                break;
            case R.id.nav_share:
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

    public void showPrivacyPolicyAlertDialog(final NavigationView navigationView){

        privacyPolicyDialog = new Dialog(this);
        privacyPolicyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        privacyPolicyDialog.setContentView(R.layout.dialog_privacy);

        Button yesAnwserButton = (Button) privacyPolicyDialog.findViewById(R.id.agreeButton);
        Button noAnwserButton = (Button) privacyPolicyDialog.findViewById(R.id.disagreeBtuton);
        TextView privacyPolicyTextView = (TextView) privacyPolicyDialog.findViewById(R.id.privacyTextView);
        privacyPolicyTextView.setMovementMethod(new ScrollingMovementMethod());

        privacyPolicyTextView.setText("1. By using the Randonautica app you do so at your own risk and assume sole responsiblity!  \n" +
                "2. Using it means you agree and will abide to this privacy policy and terms of use.  \n" +
                "3. In no way are we responsible or will be held liable for any positive or adverse affects or consequences from the use of this platform.  \n" +
                "4. Use common sense, do not put yourself or others in harms way. Don't tresspass. \n" +
                "5. Don't steal, don't damage property, don't litter, don't do anything illegal nor anything that would otherwise cause trouble or danger. \n" +
                "6. All your data is anonymized. However, we don't have control of what goes through Telegram and any other social media platforms so if you want to be absolutely anonymous we suggest you use the webbot.  \n" +
                "7. Any location data that you send is automatically deleted after 24 hours.  \n" +
                "8. We do store anonymized data of points generated for internal analysis (afterall this is an experiment, and experiments need data to analyze).  \n" +
                "9. All your other data like your settings - besides the anonymized point data stored for analysis - will automatically be deleted after 1 month, including your saved agreement.  \n" +
                "10. If you chose to write up a trip report, the report will be posted anonymously on the /r/randonaut_reports subreddit. Don't worry, we will never post your location, just the point generated.\n" +
                " \n " +
                "Do you agree to the terms of use and privacy policy, and to be a well behaved Randonaut?");

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

    public void sendData(int type, double power, double x, double y, double radiusm, double z_score, double pseudo) {
        tag = "randonaut";
        FragmentManager fragmentManager = getSupportFragmentManager();
        RandonautFragment randonautfragment = (RandonautFragment) fragmentManager.findFragmentByTag(tag);
        randonautfragment.onShowProfileAttractors(type, power, x, y, radiusm, z_score, pseudo);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_randonaut);
        //Check if ram doesn't double each time and this actually resets the randonauts fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, randonautfragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    public void rng() {
        tag = "camrng";
        FragmentManager fragmentManager = getSupportFragmentManager();
        camRngDialog camRngDialog = new camRngDialog();
        camRngDialog.show(fragmentManager, tag);

    }


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(STATS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERID", userid);
        editor.putBoolean("PRIVACY", privacyPolicyAccepted);

        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(STATS, Context.MODE_PRIVATE);
        userid = sharedPreferences.getString("USERID", null);
        privacyPolicyAccepted = sharedPreferences.getBoolean("PRIVACY", false);

        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switchOnOff = sharedPreferences.getBoolean(SWTICHEnableDarkMode, false);

    }

}