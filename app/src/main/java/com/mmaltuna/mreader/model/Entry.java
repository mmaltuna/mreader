package com.mmaltuna.mreader.model;

import org.jsoup.Jsoup;

import java.util.Date;

/**
 * Created by miguel on 23/7/15.
 */
public class Entry {
    private String title;
    private String summary;
    private Date date;

    public Entry() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public String getSummary(int limit) {
        String summary = Jsoup.parse(this.summary).text();
        int length = 0;
        if (summary != null) {
            length = summary.length();
            summary = summary.substring(0, Math.min(length, limit));

            if (length > limit)
                summary += "...";
        }

        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }
}
