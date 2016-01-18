package com.app.twiglydb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.ServerCalls;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by naresh on 10/01/16.
 */
public class SplashScreenActivity extends Activity{

    public static SharedPreferences sharedPreferences;
    Intent loginIntent;
    Intent deliverySummaryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        loginIntent = new Intent(this, LoginActivity.class);
        deliverySummaryIntent = new Intent(this, OrderSummaryActivity.class);

        String mob = DeliveryBoy.getInstance().getMob();
        String dev_id = DeliveryBoy.getInstance().getDev_id();
        if (mob ==  null || dev_id == null) {
            startActivity(loginIntent);
            finish();
            return;
        }

        DeliveryBoy.getInstance().initDeliveryBoy(mob, dev_id);

        final Call<List<Order>> ordersCall =  ServerCalls.getInstanse().service.getOrders();
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
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
