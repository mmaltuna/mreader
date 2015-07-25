package com.mmaltuna.mreader.model;

/**
 * Created by miguel on 25/7/15.
 */
public class Subscription {
    private String id;
    private String url;
    private String title;

    public Subscription() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
