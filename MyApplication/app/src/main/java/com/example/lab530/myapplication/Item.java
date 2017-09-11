package com.example.lab530.myapplication;

import java.util.Date;
import java.util.Locale;

public class Item implements java.io.Serializable {

    //日期時間、標題、
    public long datetime;
    public String title;
    Boolean check;

    public Item() {
        title = "";

    }

    public Item(long datetime, String title) {
        this.datetime = datetime;
        this.title = title;
        check=false;
    }

    public long getDatetime() {
        return datetime;
    }

    // 裝置區域的日期時間
    public String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date(datetime));
    }

    // 裝置區域的日期
    public String getLocaleDate() {
        return String.format(Locale.getDefault(), "%tF", new Date(datetime));
    }

    // 裝置區域的時間
    public String getLocaleTime() {
        return String.format(Locale.getDefault(), "%tR", new Date(datetime));
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}