package com.mmaltuna.mreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mmaltuna.mreader.adapter.EntryListAdapter;
import com.mmaltuna.mreader.model.Data;

import java.util.ArrayList;

public class EntryList extends AppCompatActivity {

    public static final String SELECTED_ENTRY_ID = "selectedEntryId";

    private Toolbar toolbar;
    private EntryListAdapter adapter;

    private static String selectedFeedId;
    private static String selectedFeedTitle;
    private static int selectedView;

    private AdapterView.OnItemClickListener entryClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), Entry.class);
            intent.putExtra(EntryList.SELECTED_ENTRY_ID, adapter.getItemId(position));
            intent.putExtra(SubscriptionList.SELECTED_FEED_ID, selectedFeedId);
            intent.putExtra(SubscriptionList.SELECTED_FEED_TITLE, selectedFeedTitle);
            intent.putExtra(SubscriptionList.SELECTED_VIEW, selectedView);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_list_activity);

        Intent intent = getIntent();
        selectedFeedId = intent.getStringExtra(SubscriptionList.SELECTED_FEED_ID);
        selectedFeedTitle = intent.getStringExtra(SubscriptionList.SELECTED_FEED_TITLE);
        selectedView = intent.getIntExtra(SubscriptionList.SELECTED_VIEW, SubscriptionList.VIEW_UNREAD);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(selectedFeedTitle);

        ListView entryListView = (ListView) findViewById(R.id.entryList);

        ArrayList<com.mmaltuna.mreader.model.Entry> entryList = new ArrayList<com.mmaltuna.mreader.model.Entry>();
        switch (selectedView) {
            case SubscriptionList.VIEW_UNREAD:
                entryList = Data.getInstance().unreadEntries.get(selectedFeedId);
                break;
            case SubscriptionList.VIEW_READ:
                entryList = Data.getInstance().readEntries.get(selectedFeedId);
                break;
            case SubscriptionList.VIEW_SAVED:
                break;
        }

        adapter = new EntryListAdapter(this, entryList);
        entryListView.setAdapter(adapter);
        entryListView.setOnItemClickListener(entryClickListener);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
