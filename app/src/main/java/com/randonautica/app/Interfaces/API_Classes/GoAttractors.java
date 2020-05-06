package com.randonautica.app.Interfaces.API_Classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoAttractors {

    @SerializedName("points")
    @Expose
    private List<Point> points = null;

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

}
