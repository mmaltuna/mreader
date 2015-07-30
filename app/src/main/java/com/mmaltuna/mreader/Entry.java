package com.mmaltuna.mreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.mmaltuna.mreader.model.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Entry extends AppCompatActivity {

    private static int selectedEntryId;
    private static String selectedFeedId;
    private static String selectedFeedTitle;
    private static int selectedView;

    private ArrayList<com.mmaltuna.mreader.model.Entry> entries;
    private com.mmaltuna.mreader.model.Entry entry;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity);

        Intent intent = getIntent();
        selectedEntryId = (int) intent.getLongExtra(EntryList.SELECTED_ENTRY_ID, 0);
        selectedFeedId = intent.getStringExtra(SubscriptionList.SELECTED_FEED_ID);
        selectedFeedTitle = intent.getStringExtra(SubscriptionList.SELECTED_FEED_TITLE);
        selectedView = intent.getIntExtra(SubscriptionList.SELECTED_VIEW, SubscriptionList.VIEW_UNREAD);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(selectedFeedTitle);

        data = Data.getInstance();

        switch (selectedView) {
            case SubscriptionList.VIEW_UNREAD:
                entries = data.unreadEntries.get(selectedFeedId);
                break;
            case SubscriptionList.VIEW_READ:
                entries = data.readEntries.get(selectedFeedId);
                break;
            case SubscriptionList.VIEW_SAVED:
                break;
        }

        String header = getHeader(entries.get(selectedEntryId));

        String content = entries.get(selectedEntryId).getContent();
        content = "".compareTo(content) == 0 ? entries.get(selectedEntryId).getSummary() : content;
        content = header + content;

        String style = "<style>" + getStyle() + "</style>";

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(style + content, "text/html;charset=utf-8", null);
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

    private String getHeader(com.mmaltuna.mreader.model.Entry entry) {
        String header = "";

        header += "<div class='header'>";
        header += "<h1 class='header-title'><a href='" + entry.getUrl() + "'>" + entry.getTitle() + "</a></h1>";

        String author = entry.getAuthor();
        if (author != null)
            header += "<span class='header-author'>" + entry.getAuthor() + "</span>";

        header += "<span class='header-date'>" + formatDate(entry.getDate()) + "</span>";
        header += "</div>";

        return header;
    }

    private String getStyle() {
        InputStream is = getResources().openRawResource(R.raw.entry_style);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy, HH:mm").format(date);
    }
}
