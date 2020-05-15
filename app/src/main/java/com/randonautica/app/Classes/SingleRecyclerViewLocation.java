package com.randonautica.app.Classes;


import com.google.android.gms.maps.model.LatLng;

public class SingleRecyclerViewLocation {

    private double type;

    private double radiusm;
    private double  power;
    private double z_score;
    private boolean isPsuedo;

    private LatLng locationCoordinates;

    private Double lan;

    private Double lon;

    public Double getLan() {
        return lan;
    }

    public void setLan(Double lan) {
        this.lan = lan;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public double getType() {
        return type;
    }

    public void setType(double name) {
        this.type = name;
    }

    public boolean isPsuedo() {
        return isPsuedo;
    }

    public void setPsuedo(boolean psuedo) {
        isPsuedo = psuedo;
    }

    public double getRadiusm() {
        return radiusm;
    }

    public void setRadiusm(double radiusm) {
        this.radiusm = radiusm;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getZ_score() {
        return z_score;
    }

    public void setZ_score(double z_score) {
        this.z_score = z_score;
    }

    public LatLng getLocationCoordinates() {
        return locationCoordinates;
    }

    public void setLocationCoordinates(LatLng locationCoordinates) {
        this.locationCoordinates = locationCoordinates;
    }

}
