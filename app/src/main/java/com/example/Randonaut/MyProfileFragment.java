package com.example.Randonaut;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyProfileFragment extends Fragment {

    private long attractors;
    private long voids;
    private long psuedo;
    private long anomalies;
    private long entropy;
    private long reports;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Profile");
        loadData();

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set textviews
        TextView Attractors = (TextView)getActivity().findViewById(R.id.tv1);
        TextView Voids = (TextView)getActivity().findViewById(R.id.tv2);
        TextView Psuedo = (TextView)getActivity().findViewById(R.id.tv3);
        TextView Anomalies = (TextView)getActivity().findViewById(R.id.tv4);
        TextView Entropy = (TextView)getActivity().findViewById(R.id.tv5);
        TextView Reports = (TextView)getActivity().findViewById(R.id.tv6);

        //in your OnCreate() method
        String totalEntropyGenerated = Double.valueOf(entropy/1000000)+"M"; // FIX THIS!!

        Attractors.setText(Long.toString(attractors));
        Voids.setText(Long.toString(voids));
        Psuedo.setText(Long.toString(psuedo));
        Anomalies.setText(Long.toString(anomalies));
        Entropy.setText(totalEntropyGenerated);
        Reports.setText(Long.toString(reports));
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(RandonautFragment.STATS, Context.MODE_PRIVATE);
        attractors = sharedPreferences.getLong("ATTRACTORS", 0);
        voids = sharedPreferences.getLong("VOIDS", 0);
        entropy = sharedPreferences.getLong("ENTROPY", 0);
    }
}

