package com.mmaltuna.mreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.toolbox.StringRequest;
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

public class EntryList extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

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
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.entry_list, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.entry_list, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((EntryList) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
