package com.randonautica.app.Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.randonautica.app.Interfaces.ItemClickListener;
import com.randonautica.app.MyRandonautFragment;
import com.randonautica.app.R;

import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class LocationRecyclerViewAdapter extends
        RecyclerView.Adapter<LocationRecyclerViewAdapter.MyViewHolder> {

    private List<SingleRecyclerViewLocation> locationList;
    private MapboxMap map;

    public LocationRecyclerViewAdapter(Context activity,
                                       List<SingleRecyclerViewLocation> locationList,
                                       MapboxMap mapBoxMap) {
        this.locationList = locationList;
        this.map = mapBoxMap;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_on_top_of_map_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        SingleRecyclerViewLocation singleRecyclerViewLocation = locationList.get(position);
        String type = "Attractor";
        final Double latitude = singleRecyclerViewLocation.getLocationCoordinates().getLatitude();
        final Double longitude = singleRecyclerViewLocation.getLocationCoordinates().getLongitude();

        String radiusm = "Radius: " + (int) singleRecyclerViewLocation.getRadiusm();
        String power = "Power: " + String.format("%.2f", singleRecyclerViewLocation.getPower());
        String z_score = "Z Score: " + String.format("%.2f", singleRecyclerViewLocation.getZ_score());

        if (singleRecyclerViewLocation.getType() == 0) {
            type = "Pseudo Point";
            radiusm = "Radius: Unknown";
            power = "Power: Unknown";
            z_score = "Z Score: Unknown";
        } else if (singleRecyclerViewLocation.getType() == 2) {
            type = "Void";
        }
        //Check for pseudo
        if (singleRecyclerViewLocation.isPsuedo()) {
            type = "Pseudo Attractor";
            if (singleRecyclerViewLocation.getType() == 2) {
                type = "Pseudo Void";
            }
        }

        holder.type.setText(type);
        holder.radiusm.setText(radiusm);
        holder.power.setText(power);
        holder.z_score.setText(z_score);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                LatLng selectedLocationLatLng = locationList.get(position).getLocationCoordinates();
                CameraPosition newCameraPosition = new CameraPosition.Builder()
                        .target(selectedLocationLatLng)
                        .build();
                map.easeCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
                Button newbutton = (Button) MyRandonautFragment.reportButton;
                newbutton.setVisibility(View.VISIBLE);

                newbutton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = null;
                        try {
                            // Google maps app
                            getApplicationContext().getPackageManager().getPackageInfo("com.google.android.apps.maps", 0);
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/" + latitude + "+" + longitude + "/@" + latitude + "+" + longitude + ",14z"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (Exception e) {
                            // Maps in Browser
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/" + latitude + "+" + longitude + "/@" + latitude + "+" + longitude + ",14z"));
                        }
                        getApplicationContext().startActivity(intent);

                    }
                });
            }


        });

    }


    @Override
    public int getItemCount() {
        return locationList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView type;
        TextView radiusm;
        TextView power;
        TextView z_score;
        CardView singleCard;
        ItemClickListener clickListener;

        MyViewHolder(View view) {
            super(view);
            type = view.findViewById(R.id.type);
            radiusm = view.findViewById(R.id.radiusm);
            power = view.findViewById(R.id.power);
            z_score = view.findViewById(R.id.z_score);

            singleCard = view.findViewById(R.id.single_location_cardview);
            singleCard.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getLayoutPosition());
        }
    }
}
