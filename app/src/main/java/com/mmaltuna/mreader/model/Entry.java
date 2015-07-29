package com.mmaltuna.mreader.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by miguel on 23/7/15.
 */
public class Entry {
    public final static int CHAR_LIMIT_SUMMARY = 140;

    private String id;
    private String title;
    private String summary;
    private String content;
    private String author;
    private String url;
    private Date date;
    private boolean read;

    public Elements elements;

    public Entry() {}

    public static Comparator<Entry> comparatorNewest = new Comparator<Entry>() {
        @Override
        public int compare(Entry e1, Entry e2) {
            return e2.getDate().compareTo(e1.getDate());
        }
    };

    public static Comparator<Entry> comparatorOldest = new Comparator<Entry>() {
        @Override
        public int compare(Entry e1, Entry e2) {
            return e1.getDate().compareTo(e2.getDate());
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = plainSummary(summary, CHAR_LIMIT_SUMMARY);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        parseContent(content);
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    private void parseContent(String content) {
        Document doc = Jsoup.parse(content);
        elements = doc.body().children();
    }
}
