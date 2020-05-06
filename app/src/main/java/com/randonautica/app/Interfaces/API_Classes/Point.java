package com.randonautica.app.Interfaces.API_Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point {

    @SerializedName("GID")
    @Expose
    private Double gID;
    @SerializedName("TID")
    @Expose
    private Double tID;
    @SerializedName("LID")
    @Expose
    private Double lID;
    @SerializedName("Type")
    @Expose
    private Double type;
    @SerializedName("X")
    @Expose
    private Double x;
    @SerializedName("Y")
    @Expose
    private Double y;
    @SerializedName("Center")
    @Expose
    private Center center;
    @SerializedName("Side")
    @Expose
    private Double side;
    @SerializedName("DistanceErr")
    @Expose
    private Double distanceErr;
    @SerializedName("RadiusM")
    @Expose
    private Double radiusM;
    @SerializedName("N")
    @Expose
    private Double n;
    @SerializedName("Mean")
    @Expose
    private Double mean;
    @SerializedName("Rarity")
    @Expose
    private Double rarity;
    @SerializedName("Power_old")
    @Expose
    private Double powerOld;
    @SerializedName("Power")
    @Expose
    private Double power;
    @SerializedName("Z_score")
    @Expose
    private Double zScore;
    @SerializedName("Probability_single")
    @Expose
    private Double probabilitySingle;
    @SerializedName("Integral_score")
    @Expose
    private Double integralScore;
    @SerializedName("Significance")
    @Expose
    private Double significance;
    @SerializedName("Probability")
    @Expose
    private Double probability;

    public Double getGID() {
        return gID;
    }

    public void setGID(Double gID) {
        this.gID = gID;
    }

    public Double getTID() {
        return tID;
    }

    public void setTID(Double tID) {
        this.tID = tID;
    }

    public Double getLID() {
        return lID;
    }

    public void setLID(Double lID) {
        this.lID = lID;
    }

    public Double getType() {
        return type;
    }

    public void setType(Double type) {
        this.type = type;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Double getSide() {
        return side;
    }

    public void setSide(Double side) {
        this.side = side;
    }

    public Double getDistanceErr() {
        return distanceErr;
    }

    public void setDistanceErr(Double distanceErr) {
        this.distanceErr = distanceErr;
    }

    public Double getRadiusM() {
        return radiusM;
    }

    public void setRadiusM(Double radiusM) {
        this.radiusM = radiusM;
    }

    public Double getN() {
        return n;
    }

    public void setN(Double n) {
        this.n = n;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Double getRarity() {
        return rarity;
    }

    public void setRarity(Double rarity) {
        this.rarity = rarity;
    }

    public Double getPowerOld() {
        return powerOld;
    }

    public void setPowerOld(Double powerOld) {
        this.powerOld = powerOld;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public Double getZScore() {
        return zScore;
    }

    public void setZScore(Double zScore) {
        this.zScore = zScore;
    }

    public Double getProbabilitySingle() {
        return probabilitySingle;
    }

    public void setProbabilitySingle(Double probabilitySingle) {
        this.probabilitySingle = probabilitySingle;
    }

    public Double getIntegralScore() {
        return integralScore;
    }

    public void setIntegralScore(Double integralScore) {
        this.integralScore = integralScore;
    }

    public Double getSignificance() {
        return significance;
    }

    public void setSignificance(Double significance) {
        this.significance = significance;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

}