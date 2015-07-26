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
}
