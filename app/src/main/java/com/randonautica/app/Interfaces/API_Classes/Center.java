package com.randonautica.app.Interfaces.API_Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Center {

    @SerializedName("Point")
    @Expose
    private Point_ point;
    @SerializedName("Bearing")
    @Expose
    private Bearing bearing;

    public Point_ getPoint() {
        return point;
    }

    public void setPoint(Point_ point) {
        this.point = point;
    }

    public Bearing getBearing() {
        return bearing;
    }

    public void setBearing(Bearing bearing) {
        this.bearing = bearing;
    }

}