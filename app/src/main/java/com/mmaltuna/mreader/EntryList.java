package com.mmaltuna.mreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.mmaltuna.mreader.adapter.EntryListAdapter;
import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.model.Subscription;
import com.mmaltuna.mreader.utils.FeedlyUtils;

import java.util.ArrayList;

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

        ListView mEntryList = (ListView) findViewById(R.id.entryList);
        ArrayList<Entry> allEntries = new ArrayList<Entry>();
        EntryListAdapter adapter = new EntryListAdapter(this, allEntries);
        mEntryList.setAdapter(adapter);

        final String url = "https://sandbox.feedly.com/v3";
        final Activity activity = this;

        Data data = Data.getInstance();
        FeedlyUtils feedly = FeedlyUtils.getInstance(this);
        feedly.getSubscriptions();

        for (Subscription s: data.subscriptions) {
            feedly.getEntries(s.getId());

            ArrayList<Entry> entries = data.entries.get(s.getId());

            if (entries != null) {
                for (Entry e: entries) {
                    Entry ecopy = new Entry();
                    ecopy.setTitle(e.getTitle());
                    ecopy.setDate(e.getDate());
                    ecopy.setSummary(e.getSummary(140));
                    allEntries.add(ecopy);
                }
            }
        }

        adapter.notifyDataSetChanged();
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
