package com.example.ryan.db;

import org.litepal.crud.DataSupport;

/**
 * @author devonwong
 */
public class Country extends DataSupport {
    private int id;
    private  String countyName;
    private int countryCode;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {

        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }
}
