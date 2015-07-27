package com.mmaltuna.mreader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mmaltuna.mreader.adapter.SubscriptionListAdapter;
import com.mmaltuna.mreader.model.Data;
import com.mmaltuna.mreader.model.Subscription;
import com.mmaltuna.mreader.utils.FeedlyUtils;

import java.util.ArrayList;
import java.util.Collections;

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
    private int progressBarMax = 10000;
    private int progressBarInc;
    private int progressBarStep;

    private boolean showUnreadOnly = true;

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

        drawerLayout = (DrawerLayout) findViewById(R.id.subscriptionListActivityLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            getWindow().setStatusBarColor(getResources().getColor(R.color.TransparentToolbar));
        }

        initProgressBar();

        loadData(false, showUnreadOnly);
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

    public void loadData(final boolean update, final boolean unreadOnly) {
        ListView subscriptionList = (ListView) findViewById(R.id.subscriptionList);

        final Data data = Data.getInstance();
        final FeedlyUtils feedly = FeedlyUtils.getInstance(this);
        final ArrayList<Subscription> subscriptions = new ArrayList<Subscription>();

        adapter = new SubscriptionListAdapter(this, subscriptions, unreadOnly);
        subscriptionList.setOnItemClickListener(subscriptionListClickListener);
        subscriptionList.setAdapter(adapter);

        initProgressBar();

        FeedlyUtils.Callback subscriptionsCallback = new FeedlyUtils.Callback() {
            @Override
            public void onComplete() {
                progressBarInc = progressBarMax / data.subscriptions.size();

                for (final Subscription s: data.subscriptions) {
                    if (update)
                        feedly.updateEntries(unreadOnly, s.getId(), new FeedlyUtils.Callback() {
                            @Override
                            public void onComplete() {
                                s.setUnreadEntries(data.unreadEntries.get(s.getId()).size());
                                if (!unreadOnly || (unreadOnly && s.getUnreadEntries() > 0))
                                    subscriptions.add(s);

                                Collections.sort(subscriptions, Subscription.comparatorMostUnread);
                                adapter.notifyDataSetChanged();

                                loadSubscription();
                            }
                        });
                    else
                        feedly.getEntries(s.getId(), new FeedlyUtils.Callback() {
                            @Override
                            public void onComplete() {
                                s.setUnreadEntries(data.unreadEntries.get(s.getId()).size());
                                if (!unreadOnly || (unreadOnly && s.getUnreadEntries() > 0))
                                    subscriptions.add(s);

                                Collections.sort(subscriptions, Subscription.comparatorMostUnread);
                                adapter.notifyDataSetChanged();

                                loadSubscription();
                            }
                        });
                }
            }
        };

        if (update)
            feedly.updateSubscriptions(subscriptionsCallback);
        else
            feedly.getSubscriptions(subscriptionsCallback);
    }

    public void updateAll(MenuItem menuItem) {
        loadData(true, showUnreadOnly);
    }

    private void initProgressBar() {
        progressBarStep = 0;
        if (progressBar == null)
            progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setProgress(0);
    }

    private void stepProgressBar() {
        int progress = progressBar.getProgress();
        progressBar.setProgress(progress + progressBarInc);
        progressBarStep++;
    }

    private void loadSubscription() {
        stepProgressBar();
        if (progressBarStep == Data.getInstance().subscriptions.size()) {
            adapter.notifyDataSetChanged();
            initProgressBar();
        }
    }
}
