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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.app.NotificationCompat;

import com.app.twiglydb.OrderSummaryActivity;
import com.app.twiglydb.R;
import com.app.twiglydb.models.TwiglyDBSharedPreference;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MyGcmListenerService extends GcmListenerService {

    public static final String TAG = "MyGcmListenerService";

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, DBLocationService.class);
        startService(intent);
    }



    @Override
    public void onDestroy() {
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

            RxBus.INSTANCE.post(data);
        } else if (type.equals("update_interval")){
            //timeout
            try {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
                long interval = jsonObject.get("update_interval").getAsLong();
                sendMessageToActivity(interval);
            } catch (Exception ex) {

            }
        } else if (type.equals("location")) {
            sendMessageToActivity(0);
        }
        else {
            sendNotification(message);
        }
        Timber.d("app exited");

    }

    private void sendMessageToActivity(long interval) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("updateInterval", interval);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     */
    private void sendNotification(String message) {
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
    }

}
