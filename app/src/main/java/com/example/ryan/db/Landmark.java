package com.example.ryan.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

/**
 * @author devonwong
 */
public class Landmark extends DataSupport {
    private int id;
    @SerializedName("name")
    private String landmarkName;
    private String cityName;
    private int countryId;
    private int cityId;
    private int imageId;
    private float rate;
    private String time;
    private String detail;

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

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    @Override
    public String toString() {
        return "LandmarkInfo{" +
                "id='" + id + '\'' +
                "cityName='" + cityName + '\'' +
                "landmarkName='" + landmarkName + '\'' +
                ", imageId='" + imageId + '\'' +
                ", rate=" + rate +
                ", time=" + time +
                ", detail=" + detail +
                '}';
    }

}
