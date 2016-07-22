package com.app.twiglydb.models;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.app.twiglydb.network.NetworkRequest;
//import com.app.twiglydb.network.ServerCalls;
import com.app.twiglydb.network.ServerCalls;
import com.app.twiglydb.network.ServerResponseCode;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by naresh on 13/01/16.
 */
@Generated("org.jsonschema2pojo")
public class DeliveryBoy {
    transient static DeliveryBoy boy = null;
    @SerializedName("deliveryBoyId")
    @Expose
    String deliveryBoyId = null;
    @SerializedName("deliveryBoyContact")
    @Expose
    String mob = null;
    @SerializedName("deliveryBoyName")
    @Expose
    String name;
    transient String dev_id = null;
    transient List<Order> assignedOrders = new ArrayList<>();

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

    public void updateOrders() {

        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.getOrders(),
                (orders) -> {
                    DeliveryBoy.getInstance().setAssignedOrders(orders);
                    //startActivity(new Intent(this, OrderSummaryActivity.class));
                    subscriptions.clear();
                    //finish();
                }, (error) -> {
                    // Handle all errors at one place
                    subscriptions = null;
                    Timber.e(error.toString());
                }
        ));
       /* updateOrders(new ServerCalls.ServerCallEndCallback() {
            @Override
            public void callback() {

            }
        });*/

    }

    TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();
    private CompositeSubscription subscriptions = new CompositeSubscription();
    /*
    public void updateOrders(final ServerCalls.ServerCallEndCallback serverCallEndCallback) {
        final Call<List<Order>> ordersCall =  ServerCalls.getInstance().service.getOrders();
        ordersCall.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Response<List<Order>> response) {
                if (response.code() == 401) {
                    serverCallEndCallback.callback();
                    return;
                }
                List<Order> orders = response.body();
                if (orders == null) {
                    serverCallEndCallback.callback();
                    return;
                }
                DeliveryBoy.getInstance().setAssignedOrders(orders);
                serverCallEndCallback.callback();
            }

            @Override
            public void onFailure(Throwable t) {
                serverCallEndCallback.callback();
            }
        });
    }*/

    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }

    public void addNewOrder(Order order){
        for (Order temp : assignedOrders) {
            if (temp.getOrderId().equals(order.getOrderId())) return;
        }
        assignedOrders.add(0, order);
    }

    //TODO: check for mvp or mvvp pattern
    public void setAssignedOrders(List<Order> assignedOrders) {
        this.assignedOrders.clear();
        this.assignedOrders.addAll(assignedOrders);
    }

    public String getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(String id) {
        this.deliveryBoyId = id;
    }

    public String getName() {
        if (name == null) {
            name = TwiglyDBSharedPreference.getPreference().getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
        TwiglyDBSharedPreference.getPreference().setName(name);
    }

    public boolean hasOrders() {
        return this.assignedOrders.size()>0;
    }

}
