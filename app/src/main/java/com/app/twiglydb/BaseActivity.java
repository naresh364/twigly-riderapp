package com.app.twiglydb;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.ServerResponseCode;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Date;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    /*
        In future, when we need to track DBs, ordersummary activity also needs to extend base activity
     */

    private Context mContext;
    protected static double lat, lng, acc, speed;
    protected static float bat;
    protected static long location_update_time;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected GoogleApiClient mGoogleApiClient;
    protected static long onJobRepeat = 1*60*1000;
    protected static long offJobRepeat = 10*60*1000;


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
        //make sure we have all the permissions
        String[] permisions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS};
        Utils.mayRequestPermission(mContext, permisions);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("NewLocationRecevied"));

        if (!isLocationEnabled()) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                this.settingsrequest();
            } else {
                buildGoogleApiClient();
            }
        }

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
            lat = intent.getDoubleExtra("lat", 0d);
            lng = intent.getDoubleExtra("lng", 0d);
            acc = intent.getDoubleExtra("acc", 0d);
            bat = intent.getFloatExtra("bat", 0f);
            speed = intent.getDoubleExtra("speed", 0d);
            location_update_time = intent.getLongExtra("time", 0);
            if (location_update_time - last_update < update_delay) return;
            Timber.d("Location update sent : "+lat+", "+lng+", "+acc+", "+speed +", "+bat);
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

    public boolean isLocationEnabled() {
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if (!gps_enabled && !network_enabled) return false;
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) mGoogleApiClient.connect();
    }

    public void settingsrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        sendLocationUpdate();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity)mContext, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.disconnect();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        settingsrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    protected  void sendLocationUpdate() {
        long delay = getLocationUpdateDelay();
        //long timeSpent = (new Date()).getTime() - lastUpdate.getTime();
        //if (timeSpent < delay) return;

        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("updateInterval", delay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private long getLocationUpdateDelay() {
        int numJobs = DeliveryBoy.getInstance().getAssignedOrders().size();
        long delay = offJobRepeat;
        if (numJobs > 0) delay = onJobRepeat;
        return delay;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        this.settingsrequest();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
