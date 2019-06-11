package com.example.ryan.db;

public class Landmarkdata {

    private String landmarkName;
    private int imageId;
    private String cityName;
    public Landmarkdata(String landmarkName, int imageId,String cityName){
        this.landmarkName = landmarkName;
        this.imageId = imageId;
        this.cityName = cityName;
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


}
