package com.app.twiglydb;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.app.twiglydb.bus.EventCallback;
import com.app.twiglydb.bus.EventReceiver;
import com.app.twiglydb.bus.EventType;
import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.ServerCalls;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
public class OrderSummaryActivity extends AppCompatActivity {
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

    private DBLocationService serviceReference = null;
    private boolean isDBLocationBound = false;

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

        Intent intent = new Intent(this, DBLocationService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeliveryBoy.getInstance().updateOrders(new ServerCalls.ServerCallEndCallback() {
            @Override
            public void callback() {
                orderSummaryAdapter.notifyDataSetChanged();
                updateNoOrderView();
            }
        });
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

    public Location getCurrentLocation(){
        if (!serviceReference.isGPSEnabled()) {
            showSettingsAlert();
            return null;
        } else {
            return serviceReference.getLocation();
        }
    }

    public boolean checkLocationEnabled() {
        if (!serviceReference.isGPSEnabled()) {
            showSettingsAlert();
            return false;
        }
        return true;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceReference = ((DBLocationService.DBLocationBinder) service).getService();
            isDBLocationBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceReference = null;
            isDBLocationBound = false;
        }
    };

    /**
     * Function to show settings alert dialog
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
