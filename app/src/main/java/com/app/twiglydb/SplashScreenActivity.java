package com.app.twiglydb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.NetworkRequest;
//import com.app.twiglydb.network.ServerCalls;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.eze.api.EzeAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import gcm.play.android.RegistrationIntentService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
/**
 * Created by naresh on 10/01/16.
 */
public class SplashScreenActivity extends Activity{

    public static SharedPreferences sharedPreferences;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    private String mob = DeliveryBoy.getInstance().getMob();
    private String device_id = DeliveryBoy.getInstance().getDev_id();
    TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            startService(new Intent(this, RegistrationIntentService.class));
        }
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_screen);

        if (mob ==  null || device_id == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            DeliveryBoy.getInstance().initDeliveryBoy(mob, device_id);
            /*String[] listOfFiles = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).list();
            for(String file: listOfFiles){
                Toast.makeText(SplashScreenActivity.this, file, Toast.LENGTH_SHORT).show();
                Timber.i(file);
            }*/
            /*String extension = MimeTypeMap.getFileExtensionFromUrl(PATH);
            Toast.makeText(SplashScreenActivity.this, MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) +"", Toast.LENGTH_LONG).show();*/
        }

/*
        final Call<List<Order>> ordersCall =  ServerCalls.getInstance().service.getOrders();
        ordersCall.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Response<List<Order>> response) {
                if (response.code() == 401) {
                    //user not authorized, ask for signin
                    startActivity(loginIntent);
                    finish();
                    return;
                }
                List<Order> orders = response.body();
                if (orders == null) {
                    Toast.makeText(SplashScreenActivity.this, "Not able to retrieve the details", Toast.LENGTH_LONG).show();
                    return;
                }
                DeliveryBoy.getInstance().setAssignedOrders(orders);
                startActivity(deliverySummaryIntent);
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this)
                        .setTitle("Network error")
                        .setMessage("Check your internet connection or call your manager to update the states")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
                t.printStackTrace();
            }
        });*/
    }

    @Override
    protected void onDestroy(){
        if(subscriptions != null) subscriptions.clear();
        super.onDestroy();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("mabhi256@gmail.com");
        Crashlytics.setUserName("mabhi");
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                    .show();
            } else {
                Timber.e("This device is not supported.");
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*String PATH = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS) + "/app-release.apk";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(PATH)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        if(mob != null  || device_id != null){
            subscriptions.add(NetworkRequest.performAsyncRequest(
                api.getOrders(),
                (orders) -> {
                    DeliveryBoy.getInstance().setAssignedOrders(orders);
                    Intent i = new Intent(this, OrderSummaryActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    subscriptions.clear();
                    //finish();
                }, (error) -> {
                    // Handle all errors at one place
                    subscriptions = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this)
                        .setTitle("Network error "+ error.toString())
                        .setMessage("Check your internet connection or call your manager to update the states")
                        .setPositiveButton("Exit", (DialogInterface d, int which)-> finish());
                    builder.show();
                }
            ));
        }
    }
}
