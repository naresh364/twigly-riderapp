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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import com.app.twiglydb.DBLocationService;
import com.app.twiglydb.bus.EventType;
import com.app.twiglydb.bus.RxBus;
import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.google.android.gms.gcm.GcmListenerService;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MyGcmListenerService extends GcmListenerService {

    public static final String TAG = "MyGcmListenerService";
    private DBLocationService serviceReference = null;
    private boolean serviceConnected = false;
    private Location mLocation;
    private boolean batteryLevelUpdated = false;
    private double batteryLevel = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, DBLocationService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        getBatteryLevel();
    }

    @Override
    public void onDestroy() {
        unbindService(serviceConnection);
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
            while (!serviceConnected || !batteryLevelUpdated){
                try {
                    Thread.sleep(2000l);
                } catch (Exception ex) {

                }
            }//wait till the service connection is made
            if (mLocation != null) {
                Timber.e("current location :" + mLocation.getLatitude() + ", " + mLocation.getLatitude());
                updateDeviceInfo(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getAccuracy());
            } else {
                //tell server that we are not able to get the location
                updateDeviceInfo(0f, 0f, 0f);
                //sendNotification("Not able to update the location");
            }
        } else {
            //sendNotification(message);
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
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

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceReference = ((DBLocationService.DBLocationBinder) service).getService();
            mLocation = getCurrentLocation();
            serviceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceReference = null;
            serviceConnected = false;
        }
    };

    public Location getCurrentLocation(){
        if (serviceReference == null) return null;
        if (!serviceReference.isGPSEnabled()) {
            return null;
        } else {
            return serviceReference.getLocation();
        }
    }

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
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }

    private void updateDeviceInfo(double lat, double lng, double acc) {

        CompositeSubscription subscriptions = new CompositeSubscription();
        TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();
        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.updateDeviceInfo(lat, lng, acc, batteryLevel),
                response -> {
                    Timber.d("server response");
                }, e -> {
                    Timber.e("server failure");
                }
        ));
        /*Call<ServerCalls.ServerResponse> responseCall =
                ServerCalls.getInstance().service.updateDeviceInfo(lat, lng, acc, batteryLevel);

        responseCall.enqueue(new Callback<ServerCalls.ServerResponse>() {
            @Override
            public void onResponse(Response<ServerCalls.ServerResponse> response) {
                Timber.e("server response");
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e("server failure");
            }
        });*/
    }
}
