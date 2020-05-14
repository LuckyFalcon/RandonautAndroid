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

import com.google.android.gms.maps.GoogleMap;
import com.randonautica.app.Interfaces.ItemClickListener;
import com.randonautica.app.R;
import com.randonautica.app.RandonautFragment;

import java.util.List;

public class LocationRecyclerViewAdapter extends
        RecyclerView.Adapter<LocationRecyclerViewAdapter.MyViewHolder> {

    private List<SingleRecyclerViewLocation> locationList;
    private GoogleMap map;
    private static final float DEFAULT_ZOOM = 15f;
    private Context context;

    public LocationRecyclerViewAdapter(Context activity,
                                       List<SingleRecyclerViewLocation> locationList,
                                       GoogleMap mapBoxMap) {
        this.locationList = locationList;
        this.map = mapBoxMap;
        this.context = activity;
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
        final Double latitude = singleRecyclerViewLocation.getLocationCoordinates().latitude;
        final Double longitude = singleRecyclerViewLocation.getLocationCoordinates().longitude;

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

                map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(latitude, longitude), DEFAULT_ZOOM));

                Button newbutton = (Button) RandonautFragment.reportButton;
                newbutton.setVisibility(View.VISIBLE);

                newbutton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = null;
                        try {
                            // Google maps app
                            context.getApplicationContext().getPackageManager().getPackageInfo("com.google.android.apps.maps", 0);
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/" + latitude + "+" + longitude + "/@" + latitude + "+" + longitude + ",14z"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        } catch (Exception e) {
                            // Maps in Browser
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/place/" + latitude + "+" + longitude + "/@" + latitude + "+" + longitude + ",14z"));
                        }
                        context.getApplicationContext().startActivity(intent);

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
