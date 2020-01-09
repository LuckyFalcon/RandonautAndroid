package com.randonautica.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MyList extends Fragment implements
        ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles

    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view, container, false);

        setHasOptionsMenu(true);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initilization
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);

        mAdapter = new TabsPagerAdapter(getChildFragmentManager());

        // add your fragments
        mAdapter.addFrag(new MyAttractorsListFragment(), "Attractors");
        mAdapter.addFrag(new MyVoidsListFragment(), "Voids");
        mAdapter.addFrag(new MyAnomalyListFragment(), "Anomalies");

        // set adapter on viewpager
        viewPager.setAdapter(mAdapter);

        //Set title of screen
        getActivity().setTitle("Webbot");

    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, androidx.fragment.app.FragmentTransaction ft) {

    }
}