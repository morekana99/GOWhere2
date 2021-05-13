package com.example.ryan.db;

public class Landmarkdata {

    private String landmarkName;
    private int imageId;
    private String cityName;
    private String describe;
    public Landmarkdata(String landmarkName, int imageId,String cityName){
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
}
