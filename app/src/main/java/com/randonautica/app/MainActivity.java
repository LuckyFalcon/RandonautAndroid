package com.randonautica.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;
import com.randonautica.app.Interfaces.MainActivityMessage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements MainActivityMessage, PurchasesUpdatedListener, MyAttractorsListFragment.SendMessage, MyCamRngFragment.SendMessage, NavigationView.OnNavigationItemSelectedListener {
    public static NavigationView navigationView;
    Dialog privacyPolicyDialog;

    //Amount of IDAs
    private long attractors;
    private long voids;
    private long anomalies;

    private BillingClient billingClient;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String STATS = "stats";

    private String userId;
    private String tag;

    private androidx.fragment.app.FragmentManager fragmentManager;
    private DrawerLayout drawer;

    private boolean darkModeSwitch;
    private Boolean privacyPolicyAccepted;

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

        setContentView(R.layout.activity_main);

        //Set the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Enable drawer menu within the toolbar
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
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

            //Continue loading the app
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new RandonautFragment(), "randonaut")
                    .addToBackStack("randonaut")
                    .commit();
            navigationView.setCheckedItem(R.id.nav_randonaut);

//            if(attractors > 5 || voids > 5 || anomalies > 5){
//                showUpgradeDialog();
//            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_randonaut:
                fragmentManager = getSupportFragmentManager();

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
            case R.id.nav_slideshow:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyListFragment()).commit();
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

    public Dialog showUpgradeDialog() {
        final Dialog upgradeDialog = new Dialog(this);
        upgradeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        upgradeDialog.setContentView(R.layout.dialog_crowdfunding);

        //Buttons
        Button upgrade = (Button) upgradeDialog.findViewById(R.id.upgradeAccept);
        Button cancel = (Button) upgradeDialog.findViewById(R.id.upgradeCancel);

        upgradeDialog.show();



        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upgradeDialog.cancel();

            }

        });




        return upgradeDialog;
    }

    public void test(){
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    List<String> skuList = new ArrayList<> ();
                    skuList.add("premium_upgrade");
                    skuList.add("gas");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {

                                    for (SkuDetails skuDetails : skuDetailsList) {
                                        String sku = skuDetails.getSku();
                                        String price = skuDetails.getPrice();
                                        if ("premium_upgrade".equals(sku)) {
                                            test2(skuDetails);
                                        } else if ("gas".equals(sku)) {
                                            test2(skuDetails);
                                        }
                                    }
                                }
                            });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });



    }

    public void test2(SkuDetails skuDetails){

        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        BillingResult responseCode = billingClient.launchBillingFlow(this, flowParams);

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

        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(STATS, Context.MODE_PRIVATE);

        userId = sharedPreferences.getString("userId", null);
        privacyPolicyAccepted = sharedPreferences.getBoolean("PRIVACY", false);

        attractors = sharedPreferences.getLong("ATTRACTORS", 0);
        voids = sharedPreferences.getLong("VOID", 0);
        anomalies = sharedPreferences.getLong("ANOMALIES", 0);

        //Check for dark mode in SHARED_PREFS
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        darkModeSwitch = sharedPreferences.getBoolean(SWTICHEnableDarkMode, false);

    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}
