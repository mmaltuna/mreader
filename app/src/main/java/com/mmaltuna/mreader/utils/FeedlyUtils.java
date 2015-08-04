package com.mmaltuna.mreader.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.mmaltuna.mreader.R;
import com.mmaltuna.mreader.model.Data;
import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.model.Subscription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 25/7/15.
 */
public class FeedlyUtils {

    private static FeedlyUtils instance = null;

    public final static String BASE_URL = "https://sandbox.feedly.com/v3";
    public final static String METHOD_SUBSCRIPTIONS = "/subscriptions";
    public final static String METHOD_STREAMS = "/streams";
    public final static String METHOD_CONTENTS = "/contents";

    public final static String FILE_SUBSCRIPTIONS = "subcriptions";
    public final static String FILE_ENTRIES = "entries";

    private Context context;

    private FeedlyUtils(Context context) {
        this.context = context;
    }

    public static FeedlyUtils getInstance(Context context) {
        if (instance == null)
            instance = new FeedlyUtils(context);
        return instance;
    }

    public void getSubscriptions(@Nullable final Callback callback) {
        if (Data.getInstance().subscriptions.size() == 0) {
            if (!getSubscriptionsFromFile())
                updateSubscriptions(callback);
            else
                callback.onComplete();
        }
        else
            callback.onComplete();
    }

    public void updateSubscriptions(@Nullable final Callback callback) {
        RestUtils.getInstance(context).get(BASE_URL + METHOD_SUBSCRIPTIONS, getHeaders(), null, new RestUtils.RequestCallback() {
            @Override
            public void invoke(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    loadSubscriptions(jsonResponse);
                    cacheSubscriptions(jsonResponse);
                    cacheFavicons(Data.getInstance().subscriptions);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                } finally {
                    if (callback != null)
                        callback.onComplete();
                }
            }
        });
    }

    public void getEntries(final String feedId, @Nullable final Callback callback) {
        Data data = Data.getInstance();
        if (data.unreadEntries.get(feedId).size() == 0 && data.readEntries.get(feedId).size() == 0) {
            if (!getEntriesFromFile(feedId))
                updateEntries(false, feedId, callback);
            else
                callback.onComplete();
        }
        else
            callback.onComplete();
    }

    public void updateEntries(boolean unreadOnly, final String feedId, @Nullable final Callback callback) {
        final String id = encodeString(feedId);

        String method = BASE_URL + METHOD_STREAMS + "/" + id + METHOD_CONTENTS;

        Map<String, String> params = new HashMap<String, String>();
        if (unreadOnly)
            params.put("unreadOnly", "true");

        RestUtils.getInstance(context).get(method, getHeaders(), params, new RestUtils.RequestCallback() {
            @Override
            public void invoke(String response) {
                try {
                    JSONArray entries = new JSONObject(response).getJSONArray("items");
                    loadEntries(entries, feedId);
                    cacheEntries(entries, id);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (callback != null)
                        callback.onComplete();
                }
            }
        });
    }

    public boolean getSubscriptionsFromFile() {
        JSONArray subscriptions = fileToJSONArray(FILE_SUBSCRIPTIONS);
        if (subscriptions != null) {
            loadSubscriptions(subscriptions);
            return true;
        }
        return false;
    }

    public boolean getEntriesFromFile(String feedId) {
        String id = encodeString(feedId);
        JSONArray entries = fileToJSONArray(FILE_ENTRIES + "_" + id);
        if (entries != null) {
            loadEntries(entries, feedId);
            return true;
        }
        return false;
    }

    private void loadSubscriptions(JSONArray subscriptions) {
        Data data = Data.getInstance();
        data.subscriptions.clear();

        try {
            for (int i = 0; i < subscriptions.length(); i++) {
                JSONObject o = subscriptions.getJSONObject(i);

                Subscription s = new Subscription(o);

                data.subscriptions.add(s);
                data.unreadEntries.put(s.getId(), new ArrayList<Entry>());
                data.readEntries.put(s.getId(), new ArrayList<Entry>());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadEntries(JSONArray entries, String feedId) {
        Data data = Data.getInstance();
        ArrayList<Entry> unreadEntries = data.unreadEntries.get(feedId);
        ArrayList<Entry> readEntries = data.readEntries.get(feedId);
        unreadEntries.clear();
        readEntries.clear();

        try {
            for (int i = 0; i < entries.length(); i++) {
                JSONObject o = entries.getJSONObject(i);
                Entry e = new Entry(o);

                if (e.isRead())
                    readEntries.add(e);
                else
                    unreadEntries.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cacheSubscriptions(JSONArray subscriptions) {
        try {
            jsonArrayToFile(subscriptions, FILE_SUBSCRIPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cacheEntries(JSONArray entries, String feedId) {
        try {
            jsonArrayToFile(entries, FILE_ENTRIES + "_" + feedId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cacheFavicons(List<Subscription> subscriptions) {
        for (Subscription s: subscriptions) {
            //CacheUtils.getInstance(context).downloadPicture(s.getIconUrl());
        }
    }

    private void jsonArrayToFile(JSONArray array, String fname) throws IOException {
        FileOutputStream file = context.openFileOutput(fname, Context.MODE_PRIVATE);
        file.write(array.toString().getBytes());
        file.close();
    }

    @Nullable
    private JSONArray fileToJSONArray(String fname) {
        try {
            FileInputStream file = context.openFileInput(fname);
            BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();

            String inputString = reader.readLine();
            while (inputString != null) {
                stringBuilder.append(inputString);
                inputString = reader.readLine();
            }

            file.close();

            return new JSONArray(stringBuilder.toString());
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File \"" + context.getFilesDir() + fname + "\" does not exist.");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return null;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth As8ai9d7ImEiOiJGZWVkbHkgc2FuZGJveCBjbGllbnQiLCJlIjoxNDM4Nzk4Njg4MDg0LCJpIjoiMDZhZTI2YjktMDExYS00OWMzLWFiY2YtN2M1ZDgzNWEyMDZkIiwicCI6NiwidCI6MSwidiI6InNhbmRib3giLCJ3IjoiMjAxNS4zMCIsIngiOiJzdGFuZGFyZCJ9:sandbox");
        return headers;
    }

    private String encodeString(String s) {
        String output = "";

        try {
            output = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return output;
        }
    }

    public static interface Callback {
        public void onComplete();
    }
}
