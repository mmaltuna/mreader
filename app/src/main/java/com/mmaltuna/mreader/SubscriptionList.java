package com.mmaltuna.mreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mmaltuna.mreader.adapter.SubscriptionListAdapter;
import com.mmaltuna.mreader.model.*;
import com.mmaltuna.mreader.utils.CacheUtils;
import com.mmaltuna.mreader.utils.FeedlyUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by miguel on 25/7/15.
 */
public class SubscriptionList extends AppCompatActivity {

    public final static String SELECTED_FEED_ID = "selectedFeedId";
    public final static String SELECTED_FEED_TITLE = "selectedFeedTitle";
    public final static String SELECTED_VIEW = "selectedView";

    public final static int VIEW_UNREAD = 0;
    public final static int VIEW_READ = 1;
    public final static int VIEW_SAVED = 2;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SubscriptionListAdapter subscriptionListAdapter;
    private ArrayList<Subscription> subscriptionList;
    private Comparator<Subscription> selectedComparator;
    private ListView listView;
    private NavigationView navigationView;

    private Data data;
    private FeedlyUtils feedly;
    private CacheUtils cache;

    private com.mmaltuna.mreader.view.ProgressBar progressBar;

    private int currentView;
    private Toast currentToast;

    private AdapterView.OnItemClickListener subscriptionListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Subscription subscription = (Subscription) subscriptionListAdapter.getItem(position);
            Intent intent = new Intent(getApplicationContext(), EntryList.class);
            intent.putExtra(SELECTED_FEED_ID, subscription.getId());
            intent.putExtra(SELECTED_FEED_TITLE, subscription.getTitle());
            intent.putExtra(SELECTED_VIEW, currentView);
            startActivity(intent);
        }
    };

    private NavigationView.OnNavigationItemSelectedListener navigationItemListener =
            new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            if (menuItem.getGroupId() == R.id.navigationGroup) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.navDrawerUnread:
                        currentView = VIEW_UNREAD;
                        break;
                    case R.id.navDrawerRead:
                        currentView = VIEW_READ;
                        break;
                    case R.id.navDrawerSaved:
                        break;
                }

                loadView(currentView, false);
            } else {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_list_activity);

        listView = (ListView) findViewById(R.id.subscriptionList);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.subscriptionListActivityLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.navigationDrawer);
        navigationView.setNavigationItemSelectedListener(navigationItemListener);

        data = Data.getInstance();
        feedly = FeedlyUtils.getInstance(this);
        cache = CacheUtils.getInstance(this);

        subscriptionList = new ArrayList<Subscription>();
        listView.setOnItemClickListener(subscriptionListClickListener);

        progressBar = new com.mmaltuna.mreader.view.ProgressBar(
                (ProgressBar) findViewById(R.id.progressbar));

        selectedComparator = Subscription.comparatorMostUnread;
        currentView = VIEW_UNREAD;
        loadView(currentView, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscription_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadView(int viewType, boolean update) {
        subscriptionList.clear();
        subscriptionListAdapter = new SubscriptionListAdapter(this, subscriptionList, viewType == VIEW_UNREAD);
        listView.setAdapter(subscriptionListAdapter);

        switch (viewType) {
            case VIEW_UNREAD:
                loadData(update, true);
                break;
            case VIEW_READ:
                loadData(update, false);
                break;
            case VIEW_SAVED:
                break;
        }
    }

    public void loadData(final boolean update, final boolean unreadOnly) {
        FeedlyUtils.Callback subscriptionsCallback = new FeedlyUtils.Callback() {
            @Override
            public void onComplete() {
                if (update) {
                    currentToast.setText("Updating entries...");
                    progressBar.init(data.subscriptions.size());
                    progressBar.show();
                }

                for (final Subscription s: data.subscriptions) {
                    if (update)
                        feedly.updateEntries(unreadOnly, s.getId(), new FeedlyUtils.Callback() {
                            @Override
                            public void onComplete() {
                                s.setUnreadEntries(data.unreadEntries.get(s.getId()).size());
                                s.setReadEntries(data.readEntries.get(s.getId()).size());

                                if (unreadOnly && s.getUnreadEntries() > 0)
                                    subscriptionList.add(s);
                                else if (!unreadOnly && s.getReadEntries() > 0)
                                    subscriptionList.add(s);

                                Collections.sort(subscriptionList, selectedComparator);
                                subscriptionListAdapter.notifyDataSetChanged();

                                progressBar.step();
                                if (progressBar.isFinished())
                                    progressBar.hide();
                            }
                        });
                    else
                        feedly.getEntries(s.getId(), new FeedlyUtils.Callback() {
                            @Override
                            public void onComplete() {
                                s.setUnreadEntries(data.unreadEntries.get(s.getId()).size());
                                s.setReadEntries(data.readEntries.get(s.getId()).size());

                                if (unreadOnly && s.getUnreadEntries() > 0)
                                    subscriptionList.add(s);
                                else if (!unreadOnly && s.getReadEntries() > 0)
                                    subscriptionList.add(s);

                                Collections.sort(subscriptionList, selectedComparator);
                                subscriptionListAdapter.notifyDataSetChanged();
                            }
                        });
                }
            }
        };

        if (update) {
            currentToast = Toast.makeText(this, "Updating subscriptions...", Toast.LENGTH_SHORT);
            currentToast.show();
            feedly.updateSubscriptions(subscriptionsCallback);
        }
        else
            feedly.getSubscriptions(subscriptionsCallback);
    }

    public void updateAll(MenuItem menuItem) {
        loadView(currentView, true);
    }
}
