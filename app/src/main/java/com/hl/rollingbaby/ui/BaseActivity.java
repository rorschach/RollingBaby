package com.hl.rollingbaby.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
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