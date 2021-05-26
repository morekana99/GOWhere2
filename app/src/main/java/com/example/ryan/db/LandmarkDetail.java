package com.example.ryan.db;

import org.litepal.crud.DataSupport;

public class LandmarkDetail extends DataSupport {
    private String name ;
    private String detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
