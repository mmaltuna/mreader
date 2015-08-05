package com.mmaltuna.mreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmaltuna.mreader.R;
import com.mmaltuna.mreader.model.Entry;
import com.mmaltuna.mreader.utils.CacheUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
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
            convertView = layoutInflater.inflate(R.layout.entry_row, null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView summary = (TextView) convertView.findViewById(R.id.summary);
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);

        Entry entry = entries.get(position);
        title.setText(entry.getTitle());

        summary.setText(entry.getSummaryFragment());

        int w = entry.getSummaryFragment().equals("") ? dpToPx(120) : dpToPx(80);
        int h = entry.getSummaryFragment().equals("") ? dpToPx(80) : dpToPx(120);

        if ("".compareTo(entry.getThumbnailUrl()) != 0)
            Picasso.with(activity)
                    .load(new File(CacheUtils.getInstance(activity).getFilePath(entry.getThumbnailUrl())))
                    .resize(w, h)
                    .centerCrop()
                    .into(thumbnail);

        return convertView;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
