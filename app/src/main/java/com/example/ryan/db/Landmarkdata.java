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


    public int getImageId(){
        return imageId;
    }

    public String getLandmarkName() {
        return landmarkName;
    }


    public String getCityName(){
        return cityName;
    }

    public String getDescribe() {
        return describe;
    }
}
