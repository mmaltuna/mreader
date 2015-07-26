package com.mmaltuna.mreader.model;

import java.util.Comparator;

/**
 * Created by miguel on 25/7/15.
 */
public class Subscription {
    private String id;
    private String url;
    private String title;
    private int unreadEntries;
    private int readEntries;

    public static Comparator<Subscription> comparatorMostUnread = new Comparator<Subscription>() {
        @Override
        public int compare(Subscription s1, Subscription s2) {
            return s2.getUnreadEntries() - s1.getUnreadEntries();
        }
    };

    public static Comparator<Subscription> comparatorLeastUnread = new Comparator<Subscription>() {
        @Override
        public int compare(Subscription s1, Subscription s2) {
            return s1.getUnreadEntries() - s2.getUnreadEntries();
        }
    };

    public Subscription() {
        unreadEntries = 0;
        readEntries = 0;
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

    public int getUnreadEntries() {
        return unreadEntries;
    }

    public void setUnreadEntries(int unreadEntries) {
        this.unreadEntries = unreadEntries;
    }

    public int getReadEntries() {
        return readEntries;
    }

    public void setReadEntries(int readEntries) {
        this.readEntries = readEntries;
    }

    public int getTotalEntries() {
        return unreadEntries + readEntries;
    }
}
