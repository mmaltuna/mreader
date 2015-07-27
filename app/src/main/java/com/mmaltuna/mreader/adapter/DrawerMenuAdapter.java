package com.mmaltuna.mreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmaltuna.mreader.R;

/**
 * Created by miguel on 26/7/15.
 */
public class DrawerMenuAdapter extends RecyclerView.Adapter<DrawerMenuAdapter.ViewHolder> {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_OPTION = 1;

    private String name;
    private String email;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private int viewType;

        private TextView title;
        private TextView name;
        private TextView email;
        private ImageView icon;
        private ImageView profilePicture;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == VIEW_TYPE_HEADER) {
                name = (TextView) itemView.findViewById(R.id.userName);
                email = (TextView) itemView.findViewById(R.id.userEmail);
            } else {
                icon = (ImageView) itemView.findViewById(R.id.icon);
                title = (TextView) itemView.findViewById(R.id.title);
            }

            this.viewType = viewType;
        }
    }

    public DrawerMenuAdapter() {
        name = "Miguel Márquez";
        email = "mmaltuna@gmail.com";
    }

    @Override
    public DrawerMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_menu_header, parent, false);
            return new ViewHolder(v, viewType);
        } else if (viewType == VIEW_TYPE_OPTION) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_menu_row, parent, false);
            return new ViewHolder(v, viewType);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(DrawerMenuAdapter.ViewHolder holder, int position) {
        if (holder.viewType == VIEW_TYPE_HEADER) {
            holder.name.setText(name);
            holder.email.setText(email);
        } else {
            holder.title.setText("Option " + position);
            holder.icon.setImageResource(R.drawable.ic_refresh_white_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_HEADER;
        return VIEW_TYPE_OPTION;
    }
}
