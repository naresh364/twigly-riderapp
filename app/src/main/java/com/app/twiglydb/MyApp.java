package com.app.twiglydb;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by naresh on 13/01/16.
 */
public class MyApp extends android.app.Application {

    private static MyApp instance;

    public MyApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    // installing leakCanary
    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }

}
