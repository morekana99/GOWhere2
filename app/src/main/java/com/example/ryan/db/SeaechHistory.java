package com.example.ryan.db;

import org.litepal.crud.DataSupport;

public class SeaechHistory extends DataSupport {
    private int id = 0;
    private String name = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
