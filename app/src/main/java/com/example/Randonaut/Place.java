package com.example.Randonaut;

import com.mapbox.mapboxsdk.geometry.LatLng;

class Place {

    private LatLng coordinate;

    private int type;

    private double radiusm;

    private double power;

    private double z_score;

    public LatLng getCoordinate() {
        return coordinate;
    }

    public int getType() {
        return type;
    }

    public double getRadiusm() {
        return radiusm;
    }

    public double getPower() {
        return power;
    }

    public double getZ_score() {
        return z_score;
    }

    public Place(LatLng coordinate, int type, double radiusm, double power, double z_score) {
        this.coordinate = coordinate;
        this.type = type;
        this.radiusm = radiusm;
        this.power = power;
        this.z_score = z_score;
    }
}

