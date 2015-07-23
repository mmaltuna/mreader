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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by miguel on 23/7/15.
 */
public class EntryListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private ArrayList<Entry> entries;

    public EntryListAdapter(Activity activity, ArrayList<Entry> entries) {
        this.activity = activity;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int index) {
        return entries.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null)
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.entry, null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);

        Entry entry = entries.get(position);
        title.setText(entry.getTitle());
        summary.setText(entry.getSummary());

        return convertView;
    }
}
