package com.mmaltuna.mreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.mmaltuna.mreader.adapter.EntryListAdapter;
import com.mmaltuna.mreader.utils.FeedlyUtils;

public class EntryList extends AppCompatActivity {

    private CharSequence mTitle;

    private Toolbar toolbar;
    private EntryListAdapter adapter;

    private static String selectedFeedId;
    private static String selectedFeedTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_list_activity);

        Intent intent = getIntent();
        selectedFeedId = intent.getStringExtra(SubscriptionList.SELECTED_FEED_ID);
        selectedFeedTitle = intent.getStringExtra(SubscriptionList.SELECTED_FEED_TITLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(selectedFeedTitle);

        ListView entryListView = (ListView) findViewById(R.id.entryList);
        adapter = new EntryListAdapter(this, Data.getInstance().entries.get(selectedFeedId));
        entryListView.setAdapter(adapter);

        Data data = Data.getInstance();
        FeedlyUtils feedly = FeedlyUtils.getInstance(this);
        feedly.getEntries(selectedFeedId, new FeedlyUtils.Callback() {
            @Override
            public void onComplete() {
                adapter.notifyDataSetChanged();
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
