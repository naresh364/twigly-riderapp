package com.app.twiglydb;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.ServerResponseCode;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.util.Date;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by naresh on 14/01/16.
 */
public class DBLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30*60*1000l;
    public static final long SERVER_CALL_INTERVAL = 30*1000l;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    private Context mContext;
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */

    /**
     * Time when the location was updated represented as a String.
     */
    protected Date mLastUpdateTime;

    TwiglyRestAPI api;
    private CompositeSubscription subscriptions;

    @Override
    public void onCreate() {
        super.onCreate();

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        api =  TwiglyRestAPIBuilder.buildRetroService();
        subscriptions = new CompositeSubscription();
        mLastUpdateTime = new Date();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest(long interval) {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
        }

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(interval);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(interval/2);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        } catch (SecurityException ex) {
            Log.d("Locaiton", "Location permission issue");
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        createLocationRequest(UPDATE_INTERVAL_IN_MILLISECONDS);
        sendMessageToServer(mCurrentLocation);
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //TODO: notify our server
        sendMessageToServer(location);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    private float getBatteryLevel(){
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    private void sendMessageToServer(Location l) {
        if (l == null) return;
        long time = (new Date()).getTime() - mLastUpdateTime.getTime();
        if (time < SERVER_CALL_INTERVAL) return;

        mLastUpdateTime = new Date();
        //TODO: update my location
        api.updateDeviceInfo(l.getLatitude(), l.getLongitude(), l.getAccuracy(), getBatteryLevel());
        subscriptions.add( NetworkRequest.performAsyncRequest(
                api.updateDeviceInfo(l.getLatitude(), l.getLongitude(), l.getAccuracy(), getBatteryLevel()),
                (data) -> {
                    if(ServerResponseCode.valueOf(data.code) == ServerResponseCode.OK) {
                        Timber.d("location status update sent");
                    }
                }, (error) -> {
                    Timber.d("location status update failed");
                }));
        stopLocationUpdates();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            long updateInterval = intent.getLongExtra("updateInterval", UPDATE_INTERVAL_IN_MILLISECONDS);
            updateInterval = (updateInterval <= 30) ? UPDATE_INTERVAL_IN_MILLISECONDS : updateInterval*1000;
            createLocationRequest(updateInterval);
        }
    };

    //listen for update events

}
