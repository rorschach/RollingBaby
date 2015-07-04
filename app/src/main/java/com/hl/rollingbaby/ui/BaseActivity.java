package com.hl.rollingbaby.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public abstract class BaseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        initViews();
        getData();
        showContent();
    }

    public abstract void initViews();
    public abstract void getData();
    public abstract void showContent();
}