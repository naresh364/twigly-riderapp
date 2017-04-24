package com.app.twiglydb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.ServerResponseCode;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Date;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity {

    /*
        In future, when we need to track DBs, ordersummary activity also needs to extend base activity
     */

    private Context mContext;
    protected static double lat, lng, acc, speed;
    protected static float bat;
    protected static long location_update_time;

    TwiglyRestAPI api;
    protected CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api =  TwiglyRestAPIBuilder.buildRetroService();
        subscriptions = new CompositeSubscription();
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("NewLocationRecevied"));
        if (!DialerReceiver.callDone) return;
        DialerReceiver.removeViewHandler.removeCallbacks(DialerReceiver.removeViewRunnable);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                if(DialerReceiver.ly1!=null)
                {
                    DialerReceiver.callDone = false;
                    wm.removeView(DialerReceiver.ly1);
                    DialerReceiver.ly1 = null;
                    String strUriCalls = "content://call_log/calls";
                    Uri UriCalls = Uri.parse(strUriCalls);
                    //MyApp.getContext().getContentResolver().
                    //        delete(UriCalls, CallLog.Calls.NUMBER +"=?", new String[]{DialerReceiver.savedNumber});
                }
            }
        }, 1000);
    }

    private final long update_delay = 5*1000;
    private static long last_update = (new Date()).getTime();
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            lat = intent.getDoubleExtra("lat", 0f);
            lng = intent.getDoubleExtra("lng", 0f);
            acc = intent.getDoubleExtra("acc", 0f);
            bat = intent.getFloatExtra("bat", 0f);
            speed = intent.getDoubleExtra("speed", 0f);
            location_update_time = intent.getLongExtra("time", 0);
            if (location_update_time - last_update < update_delay) return;
            last_update = location_update_time;
            api.updateDeviceInfo(lat, lng, acc,
                    speed, location_update_time, bat);
            subscriptions.add( NetworkRequest.performAsyncRequest(
                    api.updateDeviceInfo(lat, lng, acc,
                            speed, location_update_time, bat),
                    (data) -> {
                        if(ServerResponseCode.valueOf(data.code) == ServerResponseCode.OK) {
                            Timber.d("location status update sent");
                        }
                    }, (error) -> {
                        Timber.d("location status update failed");
                    }));

        }
    };
}
