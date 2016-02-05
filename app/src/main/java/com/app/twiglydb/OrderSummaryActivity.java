package com.app.twiglydb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.app.twiglydb.bus.EventCallback;
import com.app.twiglydb.bus.EventReceiver;
import com.app.twiglydb.bus.EventType;
import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.ServerCalls;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gcm.play.android.MyGcmListenerService;
import timber.log.Timber;

/**
 * Created by naresh on 10/01/16.
 */
public class OrderSummaryActivity extends AppCompatActivity{
    List<Order> orders;

    @InjectView(R.id.order_recycler_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.activity_main_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @InjectView(R.id.order_list_noorders)
    TextView noOrdersView;

    @InjectView(R.id.ViewSwitcher)
    ViewSwitcher viewSwitcher;

    OrderSummaryAdapter orderSummaryAdapter;

    private EventReceiver eventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orders = DeliveryBoy.getInstance().getAssignedOrders();
        setTitle("TwiglyDB: " + DeliveryBoy.getInstance().getName());

        orderSummaryAdapter = new OrderSummaryAdapter(this, orders);
        setContentView(R.layout.order_summary_list);
        ButterKnife.inject(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(orderSummaryAdapter);
        mRecyclerView.setHasFixedSize(true);

        updateNoOrderView();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DeliveryBoy.getInstance().updateOrders(new ServerCalls.ServerCallEndCallback() {
                    @Override
                    public void callback() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        orderSummaryAdapter.notifyDataSetChanged();
                        updateNoOrderView();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeliveryBoy.getInstance().updateOrders();
        orderSummaryAdapter.notifyDataSetChanged();
        updateNoOrderView();
        if (eventReceiver== null){
            eventReceiver = new EventReceiver(new EventCallback() {
                @Override
                public void callback(String data) {
                    newOrderReceived(data);
                }
            });
        }
        IntentFilter intentFilter = new IntentFilter(EventType.NEW_ORDER_EVENT);
        registerReceiver(eventReceiver, intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (eventReceiver != null) unregisterReceiver(eventReceiver);
    }

    private void updateNoOrderView(){
        if (!DeliveryBoy.getInstance().hasOrders()) {
            viewSwitcher.setDisplayedChild(1);
        } else {
            viewSwitcher.setDisplayedChild(0);
        }
    }

    private void newOrderReceived(String message) {
        Timber.e("mesage received");
        Gson gson = new Gson();
        try {
            Temp temp = gson.fromJson(message, Temp.class);
            if (temp != null && temp.order != null) {
                DeliveryBoy.getInstance().addNewOrder(temp.order);
                orderSummaryAdapter.notifyDataSetChanged();
                updateNoOrderView();
            }
        } catch (Exception ex) {
            Timber.e("Unable to convert the order");
        }
    }

    public class Temp {
        Order order;
    }
}
