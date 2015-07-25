package com.mmaltuna.mreader;

import android.content.DialogInterface;
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
    private ArrayList<Subscription> subscriptions;
    private SubscriptionListAdapter adapter;

    public final static String SELECTED_FEED_ID = "selectedFeedId";

    private AdapterView.OnItemClickListener subscriptionListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), EntryList.class);
            String selectedFeedId = ((Subscription) adapter.getItem(position)).getId();
            intent.putExtra(SELECTED_FEED_ID, selectedFeedId);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

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

        ListView subscriptionList = (ListView) findViewById(R.id.subscriptionList);
        subscriptionList.setOnItemClickListener(subscriptionListClickListener);
        subscriptions = new ArrayList<Subscription>();
        adapter = new SubscriptionListAdapter(this, subscriptions);
        subscriptionList.setAdapter(adapter);

        FeedlyUtils feedly = FeedlyUtils.getInstance(this);
        feedly.getSubscriptions(new FeedlyUtils.Callback() {
            @Override
            public void onComplete() {
                updateSubscriptions();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void updateSubscriptions() {
        for (Subscription s: Data.getInstance().subscriptions)
            subscriptions.add(s);

        adapter.notifyDataSetChanged();
    }

}
