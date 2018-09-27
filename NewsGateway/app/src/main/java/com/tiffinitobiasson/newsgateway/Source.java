package com.tiffinitobiasson.newsgateway;

import java.io.Serializable;

/**
 * Created by tiffi on 4/21/2018.
 */

public class Source implements Serializable{
    private String ID, name, url, category;

    public Source(String ID, String name, String url, String category) {
        this.ID = ID;
        this.name = name;
        this.url = url;
        this.category = category;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }
}
