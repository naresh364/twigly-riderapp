package com.app.twiglydb.models;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.app.twiglydb.MyApp;

/**
 * Created by naresh on 13/01/16.
 */
public class TwiglyDBSharedPreference {
    public static final String MANAGER_NUM = "MANAGER";
    public static final String MOB_NUM = "MOB";
    public static final String NAME = "NAME";
    public static final String DEVID= "DEV_ID";
    public static final String PREF = "TWIGLY_DB_PREF";
    public static final String NUMBER_ADDED = "TWIGLY_NUMBERS_ADDED";
    public static final String LOC_UPDATE_ON_JOB = "LOC_UPDATE_ON_JOB";
    public static final String LOC_UPDATE_OFF_JOB = "LOC_UPDATE_OFF_JOB";
    public static final String DBID = "DBID";

    public SharedPreferences sharedPreferences;

    static TwiglyDBSharedPreference preference = null;
    private TwiglyDBSharedPreference() {
        // private constructor ensures class is not directly instantiated using 'new'
        this.sharedPreferences = MyApp.getContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static TwiglyDBSharedPreference getPreference() {
        // ensures only one object is created at a time
        if (preference == null) preference = new TwiglyDBSharedPreference();
        return preference;
    }

    public String getDBId() {
        return sharedPreferences.getString(DBID, null);
    }

    public String getMobNum() {
        return sharedPreferences.getString(MOB_NUM, null);
    }

    public String getDevid() {
        return sharedPreferences.getString(DEVID, null);
    }

    public String getName() {
        return sharedPreferences.getString(NAME, null);
    }

    public String getManagerNum() {
        return sharedPreferences.getString(MANAGER_NUM, null);
    }

    public void setManagerNum(String mob){
        //SharedPref.Editor editor = sharedPreferences.edit();
        //editor.putString(MANAGER_NUM, mob);
        //editor.apply();
    }

    public void setDBId(String id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DBID, id);
        editor.apply();
    }

    public void setMobNum(String mob){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOB_NUM, mob);
        editor.apply();
    }

    public void setName(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public void setDevid(String devid){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVID, devid);
        editor.apply();
    }

    public void adminNumbersSaved() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NUMBER_ADDED, true);
        editor.apply();
    }

    public boolean numbersSaved(){
        return sharedPreferences.getBoolean(NUMBER_ADDED, false);
    }

    public int getLocUpdateOnJob() {
        return sharedPreferences.getInt(LOC_UPDATE_ON_JOB, 60*60);
    }

    public int getLocUpdateOffJob() {
        return sharedPreferences.getInt(LOC_UPDATE_ON_JOB, 60*60);
    }

    public void setLocUpdateOnJob(int time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LOC_UPDATE_ON_JOB, time);
        editor.apply();
    }

    public void setLocUpdateOffJob(int time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LOC_UPDATE_OFF_JOB, time);
        editor.apply();
    }


}
