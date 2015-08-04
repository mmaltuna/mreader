package com.mmaltuna.mreader.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 25/7/15.
 */
public class Data {

    private static Data instance = null;

    public ArrayList<Subscription> subscriptions;
    public Map<String, ArrayList<Entry>> unreadEntries;
    public Map<String, ArrayList<Entry>> readEntries;

    private Data() {
        subscriptions = new ArrayList<Subscription>();
        unreadEntries = new HashMap<String, ArrayList<Entry>>();
        readEntries = new HashMap<String, ArrayList<Entry>>();
    }

    public static Data getInstance() {
        if (instance == null)
            instance = new Data();
        return instance;
    }

    public int getNumberOfPics(Boolean read) {
        int pics = 0;
        for (Subscription s: subscriptions) {
            pics += getNumberOfPics(s.getId(), read);
        }

        return pics;
    }

    public int getNumberOfPics(String feedId, Boolean read) {
        int picsUnread = 0;
        int picsRead = 0;

        if (read == null || read) {
            for (Entry e: readEntries.get(feedId)) {
                picsRead += e.getPictures().size();
            }
        }

        if (read == null || !read) {
            for (Entry e: unreadEntries.get(feedId)) {
                picsUnread += e.getPictures().size();
            }
        }

        return picsRead + picsUnread;
    }
}
