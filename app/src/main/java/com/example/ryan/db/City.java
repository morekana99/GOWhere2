package com.example.ryan.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    private int id;
    private  String cityName;
    private int cityCode;
    private int countryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String countyName) {
        this.cityName = countyName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int countryCode) {
        this.cityCode = countryCode;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }
}