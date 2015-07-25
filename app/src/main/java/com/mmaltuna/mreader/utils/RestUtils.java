package com.mmaltuna.mreader.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by miguel on 22/7/15.
 */
public class RestUtils {

    private RequestQueue requestQueue;

    private static RestUtils instance = null;
    private RestUtils(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static RestUtils getInstance(Context context) {
        if (instance == null)
            instance = new RestUtils(context);
        return instance;
    }

    public void get(String url) {
        get(url, null, null);
    }

    public void get(String url, final RequestCallback callback) {
        get(url, null, null, callback);
    }

    public void get(String url, final Map<String, String> headers, final Map<String, String> params) {
        get(url, headers, params, null);
    }

    public JSONArray get(String url, final Map<String, String> headers, final Map<String, String> params, final RequestCallback callback) {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (callback != null)
                    callback.invoke(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        requestQueue.add(stringRequest);
        return null;
    }

    public static interface RequestCallback {
        public void invoke(String response);
    }
}
