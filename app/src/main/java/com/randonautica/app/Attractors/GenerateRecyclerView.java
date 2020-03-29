package com.randonautica.app.Attractors;

import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.randonautica.app.Classes.LocationRecyclerViewAdapter;
import com.randonautica.app.Classes.SingleRecyclerViewLocation;
import com.randonautica.app.R;

import java.util.List;

public class GenerateRecyclerView {

    public void initRecyclerView(List<SingleRecyclerViewLocation> locationList, View view, MapboxMap mapboxMap) {

        RecyclerView recyclerView = view.findViewById(R.id.rv_on_top_of_map);
        recyclerView.setOnFlingListener(null);
        LocationRecyclerViewAdapter locationAdapter =
                new LocationRecyclerViewAdapter(view.getContext(), createRecyclerViewLocations(locationList), mapboxMap);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, true));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(locationAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

    }

    public void removeRecyclerView(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.rv_on_top_of_map);
        recyclerView.setAdapter(null);

    }

    private List<SingleRecyclerViewLocation> createRecyclerViewLocations(List<SingleRecyclerViewLocation> locationList) {

        return locationList;
    }



}
