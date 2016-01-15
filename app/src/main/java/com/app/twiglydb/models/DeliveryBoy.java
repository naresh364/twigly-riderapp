package com.app.twiglydb.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naresh on 13/01/16.
 */
public class DeliveryBoy {
    static DeliveryBoy boy = null;
    String mob = null;
    String dev_id = null;
    String name;
    List<Order> assignedOrders = new ArrayList<>();

    private DeliveryBoy(){
    }

    public static DeliveryBoy getInstance(){
        if (boy == null) boy = new DeliveryBoy();
        return boy;
    }

    public void initDeliveryBoy(String mob, String dev_id) {
        this.mob = mob;
        this.dev_id = dev_id;
    }

    public String getMob() {
        if (mob == null) mob = TwiglyDBSharedPreference.getPreference().getMobNum();
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
        TwiglyDBSharedPreference.getPreference().setMobNum(mob);
    }

    public String getDev_id() {
        if (dev_id == null) dev_id = TwiglyDBSharedPreference.getPreference().getDevid();
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
        TwiglyDBSharedPreference.getPreference().setDevid(dev_id);
    }

    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<Order> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}
