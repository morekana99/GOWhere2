package com.example.ryan.db;

import org.litepal.crud.DataSupport;

/**
 * @author devonwong
 */
public class SearchHistory extends DataSupport {

    private int cityCode = 0;

    private String name = null;

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
