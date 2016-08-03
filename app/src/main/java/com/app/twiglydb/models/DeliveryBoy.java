package com.app.twiglydb.models;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.app.twiglydb.OrderSummaryActivity;
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
    @SerializedName("deliveryManagerContact")
    @Expose
    String manager = null;
    transient String dev_id = null;
    transient List<Order> assignedOrders = new ArrayList<>();
    transient List<Order> dailyOrders = new ArrayList<>();

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

    public String getManager() {
        if (manager == null) manager = "";//TwiglyDBSharedPreference.getPreference().getManagerNum();
        return manager;
    }

    public void setManager(String mob) {
        this.manager = manager;
        TwiglyDBSharedPreference.getPreference().setManagerNum(mob);
    }

    public void updateOrders(OrderSummaryActivity.OrderRefreshCallback callback) {

        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.getOrders(),
                (orders) -> {
                    DeliveryBoy.getInstance().setAssignedOrders(orders);
                    //startActivity(new Intent(this, OrderSummaryActivity.class));
                    //finish();
                    callback.orderRefreshed(true);
                }, (error) -> {
                    // Handle all errors at one place
                    Timber.e(error.toString());
                    callback.orderRefreshed(false);
                }
        ));

        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.getDailyOrders(),
                (orders) -> {
                    DeliveryBoy.getInstance().setDailyOrders(orders);
                    //startActivity(new Intent(this, OrderSummaryActivity.class));
                    //finish();
                }, (error) -> {
                    // Handle all errors at one place
                    Timber.e(error.toString());
                }
        ));
    }

    TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();
    private CompositeSubscription subscriptions = new CompositeSubscription();
    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }
    public List<Order> getDailyOrders() {
        return dailyOrders;
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
    public void setDailyOrders(List<Order> dailyOrders) {
        this.dailyOrders.clear();
        this.dailyOrders.addAll(dailyOrders);
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
