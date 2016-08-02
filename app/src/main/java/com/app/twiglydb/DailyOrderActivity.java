package com.app.twiglydb;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyOrderActivity extends AppCompatActivity {

    List<Order> orders;

    //showed nothing until I had finished marking my 1st order of the day as done

    @BindView(R.id.order_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.activity_main_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.order_list_noorders) TextView noOrdersView;
    @BindView(R.id.ViewSwitcher) ViewSwitcher viewSwitcher;
    @BindView(R.id.toolbar) Toolbar myToolbar;
    @BindView(R.id.text_toolbar) TextView textToolbar;

    DailyOrderAdapter dailyOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orders = DeliveryBoy.getInstance().getDailyOrders();
        dailyOrderAdapter = new DailyOrderAdapter(this, orders);

        // disable lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.order_summary_list);
        ButterKnife.bind(this);

        setSupportActionBar(myToolbar);
        textToolbar.setText("DB: " + DeliveryBoy.getInstance().getName());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(dailyOrderAdapter);
        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout.setOnRefreshListener(()->{
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orderdetail_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_revert:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // Disable volume button
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return blockedKeys.contains(event.getKeyCode()) || super.dispatchKeyEvent(event);
    }
}
