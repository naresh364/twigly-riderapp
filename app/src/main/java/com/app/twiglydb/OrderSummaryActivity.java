package com.app.twiglydb;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by naresh on 10/01/16.
 */
public class OrderSummaryActivity extends BaseActivity {/*implements XYZinterface*/
    List<Order> orders;

    @BindView(R.id.order_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.activity_main_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.order_list_noorders) TextView noOrdersView;
    @BindView(R.id.ViewSwitcher) ViewSwitcher viewSwitcher;

    OrderSummaryAdapter orderSummaryAdapter;

    private EventReceiver eventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orders = DeliveryBoy.getInstance().getAssignedOrders();
        setTitle("TwiglyDB: " + DeliveryBoy.getInstance().getName());

        orderSummaryAdapter = new OrderSummaryAdapter(this, orders);
        setContentView(R.layout.order_summary_list);
        ButterKnife.bind(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(orderSummaryAdapter);
        mRecyclerView.setHasFixedSize(true);
        updateNoOrderView();

        // SwipeRefresh is enabled iff view is at top of first item of recycler view
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                boolean enable = false;
                if(recyclerView != null && (recyclerView.getChildCount() > 0)){
                    boolean isFirstView = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0;
                    boolean isTopOfFirstView = recyclerView.getChildAt(0).getTop() == 0;
                    enable = isFirstView && isTopOfFirstView;
                }
                mSwipeRefreshLayout.setEnabled(enable);

            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        orderSummaryAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(orderSummaryAdapter.getOrderStatus()){
            DeliveryBoy.getInstance().updateOrders(new ServerCalls.ServerCallEndCallback() {
                @Override
                public void callback() {
                    orderSummaryAdapter.notifyDataSetChanged();
                    updateNoOrderView();
                }
            });
        }

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
        if (DeliveryBoy.getInstance().hasOrders()) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
        }
    }

    private void newOrderReceived(String message) {
        Timber.e("mesage received");
        Gson gson = new Gson();
        try {
            OrderWrapper wrapper = gson.fromJson(message, OrderWrapper.class);
            if (wrapper != null && wrapper.order != null) {
                DeliveryBoy.getInstance().addNewOrder(wrapper.order);
                orderSummaryAdapter.notifyDataSetChanged();
                updateNoOrderView();
            }
        } catch (Exception ex) {
            Timber.e("Unable to convert the order");
        }
    }

    public class OrderWrapper {
        Order order;
    }

}
