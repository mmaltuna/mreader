package com.mmaltuna.mreader.model;

import org.jsoup.Jsoup;

import java.util.Date;

/**
 * Created by miguel on 23/7/15.
 */
public class Entry {
    public final static int CHAR_LIMIT_SUMMARY = 140;

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

    private String plainSummary(String summary, int limit) {
        summary = Jsoup.parse(summary).text();
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
        this.summary = plainSummary(summary, CHAR_LIMIT_SUMMARY);
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }
}
