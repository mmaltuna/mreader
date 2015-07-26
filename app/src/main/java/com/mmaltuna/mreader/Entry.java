package com.mmaltuna.mreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.mmaltuna.mreader.model.Data;


public class Entry extends AppCompatActivity {

    private static int selectedEntryId;
    private static String selectedFeedId;
    private static String selectedFeedTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);

        Intent intent = getIntent();
        selectedEntryId = (int) intent.getLongExtra(EntryList.SELECTED_ENTRY_ID, 0);
        selectedFeedId = intent.getStringExtra(SubscriptionList.SELECTED_FEED_ID);
        selectedFeedTitle = intent.getStringExtra(SubscriptionList.SELECTED_FEED_TITLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(selectedFeedTitle);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadData(Data.getInstance().unreadEntries.get(selectedFeedId).get(selectedEntryId).getSummary(), "text/html", "UTF-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_menu, menu);
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
