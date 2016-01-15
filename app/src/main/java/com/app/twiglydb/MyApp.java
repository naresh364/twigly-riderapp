package com.app.twiglydb;

import android.content.Context;

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

}
