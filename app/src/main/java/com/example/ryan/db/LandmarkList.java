package com.example.ryan.db;

/**
 * @author devonwong
 */
public class LandmarkList {
    private String landmarkName;
    private int imageId;
    private String cityName;
    private int countyId;
    public LandmarkList(int countyId, String landmarkName, int imageId, String cityName){
        this.countyId = countyId;
        this.landmarkName = landmarkName;
        this.imageId = imageId;
        this.cityName = cityName;
    }

    public String getLandmarkName() {
        return landmarkName;
    }

    public void setLandmarkName(String landmarkName) {
        this.landmarkName = landmarkName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }
}
