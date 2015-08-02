package com.mmaltuna.mreader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.mmaltuna.mreader.model.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by miguel on 2/8/15.
 */
public class EntryFragment extends Fragment {

    private String feedId;
    private int selectedView;
    private int position;
    private com.mmaltuna.mreader.model.Entry entry;

    private Data data;

    public EntryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.entry_fragment, container, false);

        Bundle bundle = getArguments();
        feedId = bundle.getString("feedId");
        selectedView = bundle.getInt("selectedView");
        position = bundle.getInt("position");

        data = Data.getInstance();

        switch (selectedView) {
            case SubscriptionList.VIEW_UNREAD:
                entry = data.unreadEntries.get(feedId).get(position);
                break;
            case SubscriptionList.VIEW_READ:
                entry = data.readEntries.get(feedId).get(position);
                break;
            case SubscriptionList.VIEW_SAVED:
                break;
        }

        String header = getHeader(entry);

        String content = entry.getContent();
        content = "".compareTo(content) == 0 ? entry.getSummary() : content;
        content = header + content;

        String style = "<style>" + getStyle() + "</style>";

        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(style + content, "text/html;charset=utf-8", null);

        return view;
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
