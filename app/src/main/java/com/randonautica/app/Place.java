package com.randonautica.app;

import com.mapbox.mapboxsdk.geometry.LatLng;

class Place implements Comparable<Place> {

    private LatLng coordinate;

    private String GID;

    private String TID;

    private String LID;

    private double x;

    private double y;

    private double distance;
    private double initialBearing;
    private double finalBearing;

    private int side;

    private double distanceErr;

    private double radiusM;

    private int N;

    private double mean;

    private int rarity;

    private double power_old;

    private double probability_single;

    private double integral_score;

    private double significance;

    private double probability;

    private int FILTERING_SIGNIFICANCE;

    private int type;

    private double radiusm;

    private double power;

    private double z_score;

    public String getGID() {
        return GID;
    }

    public String getTID() {
        return TID;
    }

    public String getLID() {
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

    public int getSide() {
        return side;
    }

    public double getDistanceErr() {
        return distanceErr;
    }

    public double getRadiusM() {
        return radiusM;
    }

    public int getN() {
        return N;
    }

    public double getMean() {
        return mean;
    }

    public int getRarity() {
        return rarity;
    }

    public double getPower_old() {
        return power_old;
    }

    public double getProbability_single() {
        return probability_single;
    }

    public double getIntegral_score() {
        return integral_score;
    }

    public double getSignificance() {
        return significance;
    }

    public double getProbability() {
        return probability;
    }

    public int getFILTERING_SIGNIFICANCE() {
        return FILTERING_SIGNIFICANCE;
    }

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

    public Place(LatLng coordinate, String GID, String TID, String LID, double x, double y, double distance, double initialBearing, double finalBearing, int side, double distanceErr, double radiusM, int n, double mean, int rarity, double power_old, double probability_single, double integral_score, double significance, double probability, int FILTERING_SIGNIFICANCE, int type, double radiusm, double power, double z_score) {
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
        this.integral_score = integral_score;
        this.significance = significance;
        this.probability = probability;
        this.FILTERING_SIGNIFICANCE = FILTERING_SIGNIFICANCE;
        this.type = type;
        this.radiusm = radiusm;
        this.power = power;
        this.z_score = z_score;
    }

    @Override
    public int compareTo(Place o) {
        return 0;
    }
}

