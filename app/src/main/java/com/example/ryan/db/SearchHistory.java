package com.example.ryan.db;

import org.litepal.crud.DataSupport;

/**
 * @author devonwong
 */
public class SearchHistory extends DataSupport {

    private String name = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
