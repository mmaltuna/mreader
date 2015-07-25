package com.mmaltuna.mreader;

import android.app.Activity;
import android.content.Intent;
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
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<Entry> shownEntries;
    private EntryListAdapter adapter;

    private static String selectedFeedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_list);

        Intent intent = getIntent();
        selectedFeedId = intent.getStringExtra(SubscriptionList.SELECTED_FEED_ID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.entryListDrawerRecycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        drawerLayout = (DrawerLayout) findViewById(R.id.entryListActivityLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        ListView entryListView = (ListView) findViewById(R.id.entryList);
        shownEntries = new ArrayList<Entry>();
        adapter = new EntryListAdapter(this, shownEntries);
        entryListView.setAdapter(adapter);

        Data data = Data.getInstance();
        FeedlyUtils feedly = FeedlyUtils.getInstance(this);
        feedly.getEntries(selectedFeedId, new FeedlyUtils.Callback() {
            @Override
            public void onComplete() {
                updateEntries();
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

    public void updateEntries() {
        ArrayList<Entry> entries = Data.getInstance().entries.get(selectedFeedId);
        if (entries != null) {
            shownEntries.addAll(entries);
            adapter.notifyDataSetChanged();
        }
    }
}
