/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm.play.android;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import com.app.twiglydb.DBLocationService;
import com.app.twiglydb.bus.RxBus;
import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GcmListenerService;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MyGcmListenerService extends GcmListenerService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    public static final String TAG = "MyGcmListenerService";
    private Location mLocation;
    private boolean batteryLevelUpdated = false;
    boolean locationUpdated = false;
    private double batteryLevel = 0;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    boolean mRequestingLocationUpdates = false;
    boolean exit = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, DBLocationService.class);
        startService(intent);
        batteryLevelUpdated = false;
        locationUpdated = false;
        getBatteryLevel();
        buildGoogleApiClient();
    }



    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("msg");
        String type = data.getString("title");
        if (type == null) type = "";

        if (type.equals("order")) {

            // play ringtone
            MediaPlayer player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
            player.start();

            //sendNotification("New order received");
            //Intent intent = new Intent(EventType.NEW_ORDER_EVENT);
            //intent.putExtra("data", message);
            //sendBroadcast(intent);
            RxBus.INSTANCE.post(data);
        } else if (type.equals("location")){
            //timeout
            try {
                int count = 0;
                while ((!locationUpdated || !batteryLevelUpdated) && !exit) {
                    Thread.sleep(1000);
                    count++;
                    if (count > 20) return;
                }
            } catch (Exception ex) {

            }
        } else {
            //sendNotification(message);
        }
        Timber.d("app exited");

    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     */
    /*private void sendNotification(String message) {
        Intent intent = new Intent(this, OrderSummaryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setContentTitle("New Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
                //.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());
    }*/

    private void getBatteryLevel(){
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                batteryLevel = -1;
                if (rawlevel >= 0 && scale > 0) {
                    batteryLevel = (rawlevel * 100) / scale;
                }
                batteryLevelUpdated = true;
                if (mLocation != null) {
                    updateDeviceInfo(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getAccuracy(), batteryLevel);
                }
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    private void updateDeviceInfo(double lat, double lng, double acc, double batteryLevel) {

        if (!batteryLevelUpdated || !locationUpdated) return;
        CompositeSubscription subscriptions = new CompositeSubscription();
        TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();
        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.updateDeviceInfo(lat, lng, acc, batteryLevel),
                response -> {
                    Timber.d("server response");
                    exit =true;
                }, e -> {
                    Timber.e("server failure");
                    exit =true;
                }
        ));
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())return;
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(status -> {
                mRequestingLocationUpdates = true;
            });
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())return;
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(status -> {
            mRequestingLocationUpdates = false;
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED || permissionCheckCoarse == PackageManager.PERMISSION_GRANTED){
            startLocationUpdates();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        Timber.i("Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5 * 60 * 1000)
                .setFastestInterval(60 * 1000);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locationUpdated = true;
        mLocation = location;
        if (location != null) {
            updateDeviceInfo(location.getLatitude(), location.getLongitude(), location.getAccuracy(), batteryLevel);
        } else {
            updateDeviceInfo(0, 0, 0, batteryLevel);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        exit = true;
        locationUpdated = true;//we cant get location update here
    }
}
