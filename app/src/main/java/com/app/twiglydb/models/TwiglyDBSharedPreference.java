package com.app.twiglydb.models;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.twiglydb.MyApp;

/**
 * Created by naresh on 13/01/16.
 */
public class TwiglyDBSharedPreference {
    public static final String MOB_NUM = "MOB";
    public static final String DEVID= "DEV_ID";
    public static final String PREF = "TWIGLY_DB_PREF";

    public SharedPreferences sharedPreferences;

    static TwiglyDBSharedPreference preference = null;
    private TwiglyDBSharedPreference() {
        this.sharedPreferences = MyApp.getContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static TwiglyDBSharedPreference getPreference() {
        if (preference == null) preference = new TwiglyDBSharedPreference();
        return preference;
    }

    public String getMobNum() {
        return sharedPreferences.getString(MOB_NUM, null);
    }

    public String getDevid() {
        return sharedPreferences.getString(DEVID, null);
    }

    public void setMobNum(String mob){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOB_NUM, mob);
        editor.apply();
    }

    public void setDevid(String devid){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVID, devid);
        editor.apply();
    }
}
