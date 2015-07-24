package com.mmaltuna.mreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mmaltuna.mreader.adapter.EntryListAdapter;
import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.utils.RestUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntryList extends AppCompatActivity {

    private CharSequence mTitle;
    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DrawerLayout drawerLayout;

    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        final String url = "https://sandbox.feedly.com/v3";
        final Activity activity = this;

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth Apj6A-x7ImEiOiJGZWVkbHkgc2FuZGJveCBjbGllbnQiLCJlIjoxNDM4MTA4MTU1OTA5LCJpIjoiMDZhZTI2YjktMDExYS00OWMzLWFiY2YtN2M1ZDgzNWEyMDZkIiwicCI6NiwidCI6MSwidiI6InNhbmRib3giLCJ3IjoiMjAxNS4zMCIsIngiOiJzdGFuZGFyZCJ9:sandbox");

        RestUtils.getInstance(this).get(url + "/subscriptions", headers, null, new RestUtils.RequestCallback() {
            @Override
            public void invoke(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);

                        String feedId = null;

                        try {
                            feedId = URLEncoder.encode((String) o.get("id"), "UTF-8");
                        } catch (UnsupportedEncodingException e) {

                        }

                        if (feedId != null) {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", "OAuth Apj6A-x7ImEiOiJGZWVkbHkgc2FuZGJveCBjbGllbnQiLCJlIjoxNDM4MTA4MTU1OTA5LCJpIjoiMDZhZTI2YjktMDExYS00OWMzLWFiY2YtN2M1ZDgzNWEyMDZkIiwicCI6NiwidCI6MSwidiI6InNhbmRib3giLCJ3IjoiMjAxNS4zMCIsIngiOiJzdGFuZGFyZCJ9:sandbox");

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("unreadOnly", "true");
                            RestUtils.getInstance(getApplicationContext()).get(url + "/streams/" + feedId + "/contents", headers, params, new RestUtils.RequestCallback() {
                                @Override
                                public void invoke(String response) {

                                    ListView mEntryList = (ListView) findViewById(R.id.entryList);
                                    ArrayList<Entry> entries = new ArrayList<Entry>();
                                    EntryListAdapter adapter = new EntryListAdapter(activity, entries);
                                    mEntryList.setAdapter(adapter);

                                    try {
                                        JSONArray items = new JSONObject(response).getJSONArray("items");
                                        for (int i = 0; i < items.length(); i++) {
                                            JSONObject o = items.getJSONObject(i);
                                            Entry e = new Entry();
                                            e.setTitle((String) o.get("title"));
                                            //e.setSummary((String) (o.getJSONObject("content").get("content")));
                                            if (o.has("summary")) {
                                                String summary = Jsoup.parse((String) (o.getJSONObject("summary").get("content"))).text();
                                                int length = 0;
                                                if (summary != null) {
                                                    length = summary.length();
                                                    summary = summary.substring(0, Math.min(length, 140));

                                                    if (length > 140)
                                                        summary += "...";
                                                }

                                                System.out.println("summary[from callback!] = " + summary);
                                                e.setSummary(summary);
                                            }

                                            entries.add(e);
                                        }

                                        adapter.notifyDataSetChanged();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }


                    }

                } catch (JSONException e) {

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
