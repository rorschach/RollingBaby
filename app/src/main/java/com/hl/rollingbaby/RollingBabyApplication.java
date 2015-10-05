package com.hl.rollingbaby;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by root on 15-10-5.
 */
public class RollingBabyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
