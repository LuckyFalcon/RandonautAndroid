package com.randonautica.app.Interfaces.API_Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bearing {

    @SerializedName("Distance")
    @Expose
    private Double distance;
    @SerializedName("InitialBearing")
    @Expose
    private Double initialBearing;
    @SerializedName("FinalBearing")
    @Expose
    private Double finalBearing;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getInitialBearing() {
        return initialBearing;
    }

    public void setInitialBearing(Double initialBearing) {
        this.initialBearing = initialBearing;
    }

    public Double getFinalBearing() {
        return finalBearing;
    }

    public void setFinalBearing(Double finalBearing) {
        this.finalBearing = finalBearing;
    }

}