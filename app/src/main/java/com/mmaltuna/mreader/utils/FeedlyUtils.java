package com.mmaltuna.mreader.utils;

import android.content.Context;
import android.support.annotation.Nullable;

import com.mmaltuna.mreader.Data;
import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.model.Subscription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    public void getSubscriptions() {
        RestUtils.getInstance(context).get(BASE_URL + METHOD_SUBSCRIPTIONS, getHeaders(), null, new RestUtils.RequestCallback() {
            @Override
            public void invoke(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    loadSubscriptions(jsonResponse);
                    cacheSubscriptions(jsonResponse);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        });
    }

    public void getEntries(final String feedId) {
        final String id = encodeString(feedId);

        String method = BASE_URL + METHOD_STREAMS + "/" + id + METHOD_CONTENTS;

        Map<String, String> params = new HashMap<String, String>();
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
                }
            }
        });
    }

    public void getSubscriptionsFromFile() {
        JSONArray subscriptions = fileToJSONArray(FILE_SUBSCRIPTIONS);
        if (subscriptions != null) {
            loadSubscriptions(subscriptions);
        }
    }

    public void getEntriesFromFile(String feedId) {
        String id = encodeString(feedId);
        JSONArray entries = fileToJSONArray(FILE_ENTRIES + "_" + id);
        if (entries != null) {
            loadEntries(entries, feedId);
        }
    }

    private void loadSubscriptions(JSONArray subscriptions) {
        Data data = Data.getInstance();

        try {
            for (int i = 0; i < subscriptions.length(); i++) {
                JSONObject o = subscriptions.getJSONObject(i);

                Subscription s = new Subscription();
                s.setId(o.getString("id"));
                s.setUrl(o.getString("website"));
                s.setTitle(o.getString("title"));

                data.subscriptions.add(s);
                data.entries.put(s.getId(), new ArrayList<Entry>());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadEntries(JSONArray entries, String feedId) {
        Data data = Data.getInstance();

        try {
            for (int i = 0; i < entries.length(); i++) {
                JSONObject o = entries.getJSONObject(i);

                Entry e = new Entry();
                e.setTitle(o.getString("title"));
                e.setSummary(o.has("summary") ? o.getJSONObject("summary").getString("contentt") : "");
                e.setDate(new Date(o.getLong("published")));

                ArrayList<Entry> feedEntries = data.entries.get(feedId);
                if (feedEntries != null) {
                    feedEntries.add(e);
                }
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
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        return null;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth Apj6A-x7ImEiOiJGZWVkbHkgc2FuZGJveCBjbGllbnQiLCJlIjoxNDM4MTA4MTU1OTA5LCJpIjoiMDZhZTI2YjktMDExYS00OWMzLWFiY2YtN2M1ZDgzNWEyMDZkIiwicCI6NiwidCI6MSwidiI6InNhbmRib3giLCJ3IjoiMjAxNS4zMCIsIngiOiJzdGFuZGFyZCJ9:sandbox");
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
}
