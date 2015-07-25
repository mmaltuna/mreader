package com.mmaltuna.mreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mmaltuna.mreader.R;
import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.model.Subscription;

import java.util.ArrayList;

/**
 * Created by miguel on 25/7/15.
 */
public class SubscriptionListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater layoutInflater;
    private ArrayList<Subscription> subscriptions;

    public SubscriptionListAdapter(Activity activity, ArrayList<Subscription> subscriptions) {
        this.activity = activity;
        this.subscriptions = subscriptions;
    }

    @Override
    public int getCount() {
        return subscriptions.size();
    }

    @Override
    public Object getItem(int position) {
        return subscriptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.subscription_row, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);

        Subscription subscription = subscriptions.get(position);
        name.setText(subscription.getTitle());

        return convertView;
    }
}
