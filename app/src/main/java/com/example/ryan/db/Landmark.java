package com.example.ryan.db;

import org.litepal.crud.DataSupport;

public class Landmark extends DataSupport {
    private int id;
    private String landmarkName;
    private String cityName;
    private int countryId;
    private int cityId;
    private int imageId;
    private String describe;

    public Landmark(){
    };
    public Landmark(String landmarkName, int imageId){
        this.landmarkName = landmarkName;
        this.imageId = imageId;
    }
    public int getImageId(){
        return imageId;
    }
    public void setImageId(int imageId){
        this.imageId = imageId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getLandmarkName() {
        return landmarkName;
    }

    public void setLandmarkName(String landmarkName) {
        this.landmarkName = landmarkName;
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
