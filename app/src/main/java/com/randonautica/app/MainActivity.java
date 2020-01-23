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
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements RandonautFragment.SendMessage, MyAttractorsListFragment.SendMessage, NavigationView.OnNavigationItemSelectedListener  {


    private DrawerLayout drawer;
    private String tag;

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


        //UserID
        String uniqueID = UUID.randomUUID().toString();

        Log.d("test", ""+uniqueID);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new RandonautFragment(), "randonaut")
                .addToBackStack("randonaut")
                .commit();
        navigationView.setCheckedItem(R.id.nav_randonaut);
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

}