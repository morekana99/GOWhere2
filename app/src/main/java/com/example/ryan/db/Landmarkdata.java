package com.example.ryan.db;

public class Landmarkdata {

    private String landmarkName;
    private int imageId;
    private String cityName;
    private String describe;
    private int countyId;
    public Landmarkdata(int countyId,String landmarkName, int imageId,String cityName){
        this.countyId = countyId;
        this.landmarkName = landmarkName;
        this.imageId = imageId;
        this.cityName = cityName;
        this.describe = describe;
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }
}
