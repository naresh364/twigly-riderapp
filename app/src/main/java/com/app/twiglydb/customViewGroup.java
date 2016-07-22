package com.app.twiglydb;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;

import timber.log.Timber;

/**
 * Created by abhishek on 20-07-2016.
 */
public class customViewGroup extends ViewGroup {

    public customViewGroup(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Timber.v("customViewGroup", "**********Intercepted");
        return true;
    }
}
