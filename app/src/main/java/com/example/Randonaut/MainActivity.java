package com.example.Randonaut;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private DrawerLayout drawer;
    private String tag;

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
                    Toast.makeText(this, "null", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "null", Toast.LENGTH_LONG).show();
                    botfragment = new MyBotFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, botfragment, tag)
                        .addToBackStack(tag)
                        .commit();
                break;
            case R.id.nav_slideshow:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyAttractorsListFragment()).commit();
                break;
            case R.id.nav_tools:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MySettingsFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MyProfileFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
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