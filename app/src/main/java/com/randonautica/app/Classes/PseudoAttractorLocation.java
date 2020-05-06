package com.randonautica.app.Classes;


import com.mapbox.mapboxsdk.geometry.LatLng;

public class PseudoAttractorLocation {

    private LatLng coordinate;

    private double GID;

    private double TID;

    private double LID;

    private double x;

    private double y;

    private double distance;
    private double initialBearing;
    private double finalBearing;

    private double side;

    private double distanceErr;

    private double radiusM;

    private double N;

    private double mean;

    private double rarity;

    private double power_old;

    private double probability_single;

    private double doubleegral_score;

    private double significance;

    private double probability;

    private double FILTERING_SIGNIFICANCE;

    private double type;

    private double radiusm;

    private double power;

    private double z_score;

    public double getGID() {
        return GID;
    }

    public double getTID() {
        return TID;
    }

    public double getLID() {
        return LID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDistance() {
        return distance;
    }

    public double getInitialBearing() {
        return initialBearing;
    }

    public double getFinalBearing() {
        return finalBearing;
    }

    public double getSide() {
        return side;
    }

    public double getDistanceErr() {
        return distanceErr;
    }

    public double getRadiusM() {
        return radiusM;
    }

    public double getN() {
        return N;
    }

    public double getMean() {
        return mean;
    }

    public double getRarity() {
        return rarity;
    }

    public double getPower_old() {
        return power_old;
    }

    public double getProbability_single() {
        return probability_single;
    }

    public double getdoubleegral_score() {
        return doubleegral_score;
    }

    public double getSignificance() {
        return significance;
    }

    public double getProbability() {
        return probability;
    }

    public double getFILTERING_SIGNIFICANCE() {
        return FILTERING_SIGNIFICANCE;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public double getType() {
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

    public PseudoAttractorLocation(LatLng coordinate, double GID, double TID, double LID, double x, double y, double distance, double initialBearing, double finalBearing, double side, double distanceErr, double radiusM, double n, double mean, double rarity, double power_old, double probability_single, double doubleegral_score, double significance, double probability, double FILTERING_SIGNIFICANCE, double type, double radiusm, double power, double z_score) {
        this.coordinate = coordinate;
        this.GID = GID;
        this.TID = TID;
        this.LID = LID;
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.initialBearing = initialBearing;
        this.finalBearing = finalBearing;
        this.side = side;
        this.distanceErr = distanceErr;
        this.radiusM = radiusM;
        N = n;
        this.mean = mean;
        this.rarity = rarity;
        this.power_old = power_old;
        this.probability_single = probability_single;
        this.doubleegral_score = doubleegral_score;
        this.significance = significance;
        this.probability = probability;
        this.FILTERING_SIGNIFICANCE = FILTERING_SIGNIFICANCE;
        this.type = type;
        this.radiusm = radiusm;
        this.power = power;
        this.z_score = z_score;
    }


}


