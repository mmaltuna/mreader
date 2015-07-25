package com.mmaltuna.mreader;

import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.model.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 25/7/15.
 */
public class Data {

    private static Data instance = null;

    public ArrayList<Subscription> subscriptions;
    public Map<String, ArrayList<Entry>> entries;

    private Data() {
        subscriptions = new ArrayList<Subscription>();
        entries = new HashMap<String, ArrayList<Entry>>();
    }

    public static Data getInstance() {
        if (instance == null)
            instance = new Data();
        return instance;
    }
}
