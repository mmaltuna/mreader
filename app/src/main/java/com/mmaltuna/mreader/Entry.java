package com.mmaltuna.mreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mmaltuna.mreader.model.Data;
import com.mmaltuna.mreader.utils.FeedlyUtils;

import java.util.ArrayList;


public class Entry extends AppCompatActivity {

    private static int selectedEntryId;
    private static String selectedFeedId;
    private static String selectedFeedTitle;
    private static int selectedView;

    private ArrayList<com.mmaltuna.mreader.model.Entry> entries;
    private com.mmaltuna.mreader.model.Entry currentEntry;
    private Data data;
    private FeedlyUtils feedly;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

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
        feedly = FeedlyUtils.getInstance(this);

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

        currentEntry = entries.get(selectedEntryId);

        viewPager = (ViewPager) findViewById(R.id.entryPager);
        pagerAdapter = new EntrySlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(selectedEntryId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.toggleUnread) {
            currentEntry = entries.get(viewPager.getCurrentItem());
            if (currentEntry.isRead()) {
                item.setIcon(R.drawable.ic_unread_white_24dp);
            } else {
                item.setIcon(R.drawable.ic_read_white_24dp);
            }

            feedly.toggleUnread(currentEntry);
        }

        return super.onOptionsItemSelected(item);
    }

    private class EntrySlidePagerAdapter extends FragmentStatePagerAdapter {
        public EntrySlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle arguments = new Bundle();
            arguments.putString("feedId", selectedFeedId);
            arguments.putInt("selectedView", selectedView);
            arguments.putInt("position", position);

            feedly.markAsRead(entries.get(position));

            EntryFragment entry = new EntryFragment();
            entry.setArguments(arguments);

            return entry;
        }

        @Override
        public int getCount() {
            return entries.size();
        }
    }
}
