package com.mmaltuna.mreader.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.mmaltuna.mreader.utils.FeedlyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by miguel on 25/7/15.
 */
public class Subscription {
    private String id;
    private String url;
    private String iconUrl;
    private String title;
    private int unreadEntries;
    private int readEntries;
    private Bitmap favicon;

    public static Comparator<Subscription> comparatorMostUnread = new Comparator<Subscription>() {
        @Override
        public int compare(Subscription s1, Subscription s2) {
            return s2.getUnreadEntries() - s1.getUnreadEntries();
        }
    };

    public static Comparator<Subscription> comparatorAToZ = new Comparator<Subscription>() {
        @Override
        public int compare(Subscription s1, Subscription s2) {
            return s1.getTitle().compareTo(s2.getTitle());
        }
    };

    public Subscription() {
        unreadEntries = 0;
        readEntries = 0;
        favicon = null;
    }

    public Subscription(JSONObject o) {
        unreadEntries = 0;
        readEntries = 0;

        try {
            id = o.getString("id");
            url = o.getString("website");
            title = o.getString("title");

            iconUrl = o.has("iconUrl") ? o.getString("iconUrl") : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public String getIconUrl() { return iconUrl; }

    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

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

    public Bitmap getFavicon() {
        return favicon;
    }

    public void setFavicon(Bitmap favicon) {
        this.favicon = favicon;
    }
}
