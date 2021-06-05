package com.example.ryan.db;

import org.litepal.crud.DataSupport;

/**
 * @author devonwong
 */
public class LandmarkRate extends DataSupport {

    private String landmarkName;
    private float rate;

    public String getLandmarkName() {
        return landmarkName;
    }

    public void setLandmarkName(String landmarkName) {
        this.landmarkName = landmarkName;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
