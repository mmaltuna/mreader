package com.mmaltuna.mreader;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mmaltuna.mreader.adapter.SubscriptionListAdapter;
import com.mmaltuna.mreader.model.Subscription;
import com.mmaltuna.mreader.utils.FeedlyUtils;

import java.util.ArrayList;

/**
 * Created by miguel on 25/7/15.
 */
public class SubscriptionList extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SubscriptionListAdapter adapter;
    private ProgressBar progressBar;

    private int syncProgress;

    public final static String SELECTED_FEED_ID = "selectedFeedId";
    public final static String SELECTED_FEED_TITLE = "selectedFeedTitle";

    private AdapterView.OnItemClickListener subscriptionListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), EntryList.class);
            intent.putExtra(SELECTED_FEED_ID, ((Subscription) adapter.getItem(position)).getId());
            intent.putExtra(SELECTED_FEED_TITLE, ((Subscription) adapter.getItem(position)).getTitle());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_list_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.subscriptionListDrawerRecycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        drawerLayout = (DrawerLayout) findViewById(R.id.subscriptionListActivityLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        syncProgress = 0;
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setProgress(syncProgress);

        getAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscription_list, menu);
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

    public void getAll() {
        final FeedlyUtils feedly = FeedlyUtils.getInstance(this);
        ListView subscriptionList = (ListView) findViewById(R.id.subscriptionList);
        subscriptionList.setOnItemClickListener(subscriptionListClickListener);
        final ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();
        adapter = new SubscriptionListAdapter(this, subscriptions);
        subscriptionList.setAdapter(adapter);

        syncProgress = 0;
        progressBar.setProgress(syncProgress);

        feedly.getSubscriptions(new FeedlyUtils.Callback() {
            @Override
            public void onComplete() {
                final int inc = 10000 / Data.getInstance().subscriptions.size();

                for (final Subscription s : Data.getInstance().subscriptions) {
                    feedly.getEntries(s.getId(), new FeedlyUtils.Callback() {
                        @Override
                        public void onComplete() {
                            subscriptions.add(s);
                            adapter.notifyDataSetChanged();

                            syncProgress++;
                            progressBar.setProgress(syncProgress * inc);
                            if (syncProgress == Data.getInstance().subscriptions.size()) {
                                progressBar.setProgress(0);
                            }
                        }
                    });
                }
            }
        });
    }

    public void updateAll(MenuItem menuItem) {
        final FeedlyUtils feedly = FeedlyUtils.getInstance(this);
        ListView subscriptionList = (ListView) findViewById(R.id.subscriptionList);
        subscriptionList.setOnItemClickListener(subscriptionListClickListener);
        adapter = new SubscriptionListAdapter(this, Data.getInstance().subscriptions);
        subscriptionList.setAdapter(adapter);

        syncProgress = 0;
        progressBar.setProgress(syncProgress);

        feedly.updateSubscriptions(new FeedlyUtils.Callback() {
            @Override
            public void onComplete() {
                final int inc = 10000 / Data.getInstance().subscriptions.size();
                for (int i = 0; i < Data.getInstance().subscriptions.size(); i++) {
                    final int index = i;
                    Subscription s = Data.getInstance().subscriptions.get(i);

                    feedly.updateEntries(s.getId(), new FeedlyUtils.Callback() {
                        @Override
                        public void onComplete() {
                            syncProgress++;
                            progressBar.setProgress(syncProgress * inc);

                            if (syncProgress == Data.getInstance().subscriptions.size()) {
                                adapter.notifyDataSetChanged();
                                progressBar.setProgress(0);
                            }
                        }
                    });
                }
            }
        });
    }
}
