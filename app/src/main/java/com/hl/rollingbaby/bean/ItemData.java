package com.hl.rollingbaby.bean;

import android.graphics.Color;

import java.util.Date;

/**
 * Created by hl810 on 15-8-4.
 */
public class ItemData {
    public int color;
    public int icon;
    public String title;

    public ItemData(int color, int icon, String title) {
        this.color = color;
        this.icon = icon;
        this.title = title;
    }

    public ItemData(int icon, String title) {
        this(Color.DKGRAY, icon, title);
    }
}
