package com.app.twiglydb;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.ServerCalls;
import com.app.twiglydb.network.ServerResponseCode;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends BaseActivity {
    private Order order;
    private String ezTxnId = null;
    private final int REQUESTCODE_INIT = 10001;
    private final int REQUESTCODE_SALE = 10006;
    private static final String INTENTEXTRA_ORDERDETAILS = "com.app.twiglydb.extra.order_details";
    private static final String INTENTEXTRA_PARCEL_ORDERDETAILS = "com.app.twiglydb.extra.parcel.order_details";
    private static final String INTENTEXTRA_ORDERDONE = "com.app.twiglydb.order_done";
    private EzTapServices ez;
    private Context mContext = this;

    @BindView(R.id.order_detail_layout) LinearLayout orderDetailLayout;
    @BindView(R.id.detail_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.cardcash_layout) LinearLayout cardCashLayout;
    @BindView(R.id.card_payment_button) Button cardButton;
    @BindView(R.id.cash_payment_button) Button cashButton;
    @BindView(R.id.paid_button) Button paidButton;
    @BindView(R.id.customer_name) TextView mCustomerName;

    @OnClick(R.id.card_payment_button)
    public void cardPayment(){
        eztapper();
        if(ez.strTxnId != null){
            MarkOrderDone(order, "Card");
        }
    }

    @OnClick(R.id.cash_payment_button)
    public void cashPayment(){
        MarkOrderDone(order, "Cash");
    }

    ItemDetailAdapter itemDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //order = getIntent().getExtras().getParcelable("order");
        order = getIntent().getBundleExtra(INTENTEXTRA_ORDERDETAILS).getParcelable(INTENTEXTRA_PARCEL_ORDERDETAILS);
        itemDetailAdapter = new ItemDetailAdapter(this, order.getOrderDetails());
        setContentView(R.layout.order_detail);
        ButterKnife.bind(this);
        mRecyclerView.setAdapter(itemDetailAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true); // sets height for the recycler view
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setFocusable(false);
    }

    public void eztapper(){
        ez = new EzTapServices(this, order);
        ez.initialize();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REQUESTCODE_INIT:
                // if ez device is successfully initialized and prepared, start the card payment process
                if (resultCode == RESULT_OK) {
                    try {
                        ez.pay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case REQUESTCODE_SALE:
                // if card transaction is successful, save the ez transaction id
                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        response = response.getJSONObject("txn");
                        ezTxnId = response.getString("txnId");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }

    }

    // saving location on markdone, not checkin
    private void MarkOrderDone(final Order order, String mode) {
        if (!order.getPaymentOption().equalsIgnoreCase("CardOD") && !order.getPaymentOption().equalsIgnoreCase("COD")) {
            //check if it is already paid
            mode = "OnLine";
            paidButton.setVisibility(View.VISIBLE);
            cardButton.setVisibility(View.GONE);
            cashButton.setVisibility(View.GONE);
        }
        //ovh.showProgress(true);

        double lat =0 , lng=0, acc=0;
        if (!checkLocationEnabled()) return;
        Location location = getCurrentLocation();
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            acc = location.getAccuracy();
        }

        Call<ServerCalls.ServerResponse> response = ServerCalls.getInstance().service.markDone(
                mode, order.getOrderId(), lat, lng, acc);
        response.enqueue(new Callback<ServerCalls.ServerResponse>() {
            @Override
            public void onResponse(Response<ServerCalls.ServerResponse> response) {
                if (response == null) {
                    Toast.makeText(mContext, "Unable to complete the request", Toast.LENGTH_LONG).show();
                    //ovh.markDoneFailed();
                    return;
                }
                ServerCalls.ServerResponse serverResponse = response.body();
                ServerResponseCode code = ServerResponseCode.valueOf(serverResponse.code);
                if (code == ServerResponseCode.OK){
                    setOrderDone(true);
                    return;
                }
                Toast.makeText(mContext, serverResponse.message, Toast.LENGTH_LONG).show();
                //ovh.markDoneFailed();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(mContext, "Unable to complete the request", Toast.LENGTH_LONG).show();
                //ovh.markDoneFailed();
            }
        });
    }

    // OrderSummaryAdapter calls this function to start OrderDetailActivity
    public static Intent newIntent(Context packageContext, Order order_det) {
        Intent i = new Intent(packageContext, OrderDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(INTENTEXTRA_PARCEL_ORDERDETAILS, order_det);
        i.putExtra(INTENTEXTRA_ORDERDETAILS, b);
        return i;
    }

    // OrderSummaryAdapter calls this function to check if the order was done
    public static boolean wasOrderDone(Intent result) {
        return result.getBooleanExtra(INTENTEXTRA_ORDERDONE, false);
    }

    // wasOrderDone reads the value set by this function to check if order was done
    private void setOrderDone(boolean isOrderDone){
        Intent data = new Intent();
        data.putExtra(INTENTEXTRA_ORDERDONE, isOrderDone);
        setResult(RESULT_OK, data);
        finish(); // go back to previous activity after setting result
    }
}
