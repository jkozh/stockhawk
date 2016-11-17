package com.julia.android.stockhawk;

import android.app.Application;

import timber.log.Timber;

public class StockHawkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            /** Remove all planted trees. */
            Timber.uprootAll();
            /** Add a new logging tree for debug builds. */
            Timber.plant(new Timber.DebugTree());
        }
    }
}
