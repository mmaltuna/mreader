package com.mmaltuna.mreader.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Map;

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

    public void get(String url, final Map<String, String> headers, final Map<String, String> params, final RequestCallback callback) {
        request(Request.Method.GET, url, headers, params, callback);
    }

    public void post(String url, final Map<String, String> headers, final JSONObject params, final RequestCallback callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (callback != null)
                            callback.invoke(response.toString());
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

        requestQueue.add(jsonObjectRequest);
    }

    private void request(int method, String url, final Map<String, String> headers,
                     final Map<String, String> params, final RequestCallback callback) {

        if (method == Request.Method.GET && params != null && params.size() > 0)
            url = addQueryString(url, params);

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
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

            @Override
            public Map<String, String> getParams() {
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private String addQueryString(String url, Map<String, String> params) {
        String queryString = "?";

        for (String key: params.keySet())
            queryString += key + "=" + params.get(key) + "&";

        return url + queryString.substring(0, queryString.length() - 1);
    }

    public interface RequestCallback {
        void invoke(String response);
    }
}
