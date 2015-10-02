package com.hl.rollingbaby.entity;


/**
 * Created by hl810 on 15-8-4.
 * 首页列表数据类型
 */
public class ItemData {
    public int icon;    //图标
    public String title;    //标题
    public String subTitle;     //子标题

    public ItemData(int icon, String title, String subTitle) {
        this.icon = icon;
        this.title = title;
        this.subTitle = subTitle;
    }
}
