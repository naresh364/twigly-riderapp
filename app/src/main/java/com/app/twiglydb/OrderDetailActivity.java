package com.app.twiglydb;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.ServerResponseCode;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class OrderDetailActivity extends BaseActivity {
    private Order order;
    private String ezTxnId = null;
    private final int REQUESTCODE_INIT = 10001;
    private final int REQUESTCODE_SALE = 10006;
    private static final String INTENTEXTRA_ORDERDETAILS = "com.app.twiglydb.extra.order_details";
    private static final String INTENTEXTRA_PARCEL_ORDERDETAILS = "com.app.twiglydb.extra.parcel.order_details";
    private static final String INTENTEXTRA_ORDERDONE = "com.app.twiglydb.order_done";
    private static final String INTENTEXTRA_ORDER_CHECKEDIN = "com.app.twiglydb.order_checkedin";

    private EzTapServices ez;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();

    @BindView(R.id.order_detail_layout) LinearLayout orderDetailLayout;
    @BindView(R.id.detail_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.cardcash_layout) LinearLayout cardCashLayout;
    @BindView(R.id.card_payment_button) Button cardButton;
    @BindView(R.id.cash_payment_button) Button cashButton;
    @BindView(R.id.paid_button) Button paidButton;
    @BindView(R.id.checkin_view) RecyclerView checkinView;
    @BindView(R.id.checkin_progress) ProgressBar checkProgress;

    //TODO: redundant data, avoid repetition
    @BindView(R.id.order_summary_layout) RelativeLayout orderSummaryLayout;
    @BindView(R.id.customer_name) TextView mCustomerName;
    @BindView(R.id.order_id) public TextView orderId;
    @BindView(R.id.cart_price) public TextView cartPrice;
    @BindView(R.id.address) public TextView address;
    @BindView(R.id.delivery_time) public TextView deliveryTime;
    @BindView(R.id.call_button) public ImageButton callButton;
    @BindView(R.id.navigate_button) public Button navigateButton;
/*
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
*/

    ItemDetailAdapter itemDetailAdapter;
    CheckinAdapter checkinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        order = getIntent().getBundleExtra(INTENTEXTRA_ORDERDETAILS).getParcelable(INTENTEXTRA_PARCEL_ORDERDETAILS);
        //subscriptions = new CompositeSubscription();
        //Toast.makeText(OrderDetailActivity.this, order.getOrderStatus(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(OrderDetailActivity.this, ""+order.isCheckedIn, Toast.LENGTH_SHORT).show();
        //subscriptions.add( RxBus.getInstance().register(Order.class, o1 -> {
        //    order = o1;
        setTitle("TwiglyDB: " + DeliveryBoy.getInstance().getName());

        setContentView(R.layout.order_detail);
        ButterKnife.bind(this);

        itemDetailAdapter = new ItemDetailAdapter(this, order.getOrderDetails());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true); // sets height for the recycler view
        mRecyclerView.setAdapter(itemDetailAdapter);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setFocusable(false);
        checkinView.bringToFront();

        //TODO: repeated stuff-----------------------------------------------------------------------
        mCustomerName.setText(order.getName());
        mCustomerName.setTextColor(Color.parseColor("#009688"));

        orderId.setText("#"+order.getOrderId());
        address.setText(order.getAddress());
        cartPrice.setText("\u20B9 " + String.format("%.2f",order.getTotal()));
        deliveryTime.setText(order.getDeliveryTime());
        callButton.setOnClickListener(view -> {
            String uri = "tel:" + order.getMobileNumber().trim() ;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            if (Utils.mayRequestPermission(OrderDetailActivity.this, android.Manifest.permission.CALL_PHONE)) {
                startActivity(intent);
            }
        });
        navigateButton.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + order.getLat() + ","+order.getLng());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            return;
        });
        if (order.getLat() == 0 || order.getLng() == 0) {
            navigateButton.setVisibility(View.INVISIBLE);
        } else {
            navigateButton.setVisibility(View.VISIBLE);
        }
        //----------------------------------------------------------------------------------

        //check if it is already paid
        String mode = order.getPaymentOption();
        if (!mode.equalsIgnoreCase("CardOD") && !mode.equalsIgnoreCase("COD")) {
            //mode = "OnLine";
            paidButton.setVisibility(View.VISIBLE);
            cardButton.setVisibility(View.GONE);
            cashButton.setVisibility(View.GONE);
        }

        if(order.isCheckedIn){
            checkinView.setVisibility(View.GONE);
            cardCashLayout.setVisibility(View.VISIBLE);
            setCardCashListener();
        } else {
            checkinAdapter = new CheckinAdapter(this);
            LinearLayoutManager llm2 = new LinearLayoutManager(this);
            llm2.setAutoMeasureEnabled(true); // sets height for the recycler view
            checkinView.setAdapter(checkinAdapter);
            checkinView.setLayoutManager(llm2);
            checkinView.setFocusable(false);
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //Remove swiped item from list and notify the RecyclerView
                        checkProgress.setVisibility(View.VISIBLE);
                        checkinAdapter.notifyDataSetChanged();
                        checkIn(order);
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView rv, RecyclerView.ViewHolder rvh,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                            final float alpha = 1.0f-Math.abs(dX) / (float) rvh.itemView.getWidth();
                            rvh.itemView.setAlpha(alpha);
                            rvh.itemView.setTranslationX(dX);

                            super.onChildDraw(c, rv, rvh, dX, dY, actionState, isCurrentlyActive);
                        }
                    }
                };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(checkinView);
        }

        //}));
        /*
        Eventbus -> register here in onCreate; rest of the code (from setContentView onwards) in @subscribe
        RxBus & Intent -> subscribe here in onCreate
         */
    }

    private void setCardCashListener(){
        cashButton.setOnClickListener(click -> {
            AlertDialog.Builder b = new AlertDialog.Builder(OrderDetailActivity.this);
            b.setTitle("Cash Payment")
                    .setMessage("Are you sure you want to continue?")
                    .setNegativeButton("Cancel", (DialogInterface dialog, int id) -> {})
                    .setPositiveButton("Accept", (DialogInterface dialog, int id) -> MarkOrderDone(order, "Cash"));

            AlertDialog d = b.create();
            d.show();
        });

        cardButton.setOnClickListener(click -> {
            ez = new EzTapServices(this, order);
            ez.initialize();
        });
    }

   /* @Override
    protected void onResume(){
        super.onResume();
    }*/
/*
Eventbus specific---------------------------------------------------------
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Order o) {
        // UI updates must run on MainThread
        setContentView(R.layout.order_detail);
        ButterKnife.bind(this);
        order = o;
        itemDetailAdapter = new ItemDetailAdapter(this, order.getOrderDetails());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true); // sets height for the recycler view
        mRecyclerView.setAdapter(itemDetailAdapter);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setFocusable(false);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null && intent.hasExtra("response")) {
            //Toast.makeText(this, requestCode+" "+resultCode,Toast.LENGTH_SHORT).show();
            Timber.i("SampleAppLogs",intent.getStringExtra("response"));
        }
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
                        MarkOrderDone(order, "Card");
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

        //ovh.showProgress(true);

        double lat =0 , lng=0, acc=0;
        if (!checkLocationEnabled()) return;
        Location location = getCurrentLocation();
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            acc = location.getAccuracy();
        }

        // iff async-call (done to twigly server)successful, use lambda to call setOrderDone
        subscriptions.add( NetworkRequest.performAsyncRequest(
                api.markDone(mode, order.getOrderId(), lat, lng, acc),
                (data) -> {
                    if(ServerResponseCode.valueOf(data.code) == ServerResponseCode.OK) {
                        setOrderDone(true);
                    }
                }, (error) -> {
                    // Handle error
                    //markDoneFailed(); --> keep showing progress bar
                    //getPostSubscription = null;
                    //TODO: alert dialog and  option to call
                }));
    }

    // OrderSummaryAdapter calls this function to start OrderDetailActivity
    public static Intent newIntent(Context packageContext, Order o) {
        Intent i = new Intent(packageContext, OrderDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(INTENTEXTRA_PARCEL_ORDERDETAILS, o);
        i.putExtra(INTENTEXTRA_ORDERDETAILS, b);
        // can also put serializeable
        return i;
    }

    // OrderSummaryAdapter calls this function to check if the order was done
    public static boolean wasOrderDone(Intent result) {
        return result.getBooleanExtra(INTENTEXTRA_ORDERDONE, false);
    }
    public static boolean wasOrderCheckedIn(Intent result){
        return result.getBooleanExtra(INTENTEXTRA_ORDER_CHECKEDIN, false);
    }

    // wasOrderDone reads the value set by this function to check if order was done
    private void setOrderDone(boolean isOrderDone){
        setResult(RESULT_OK, new Intent().putExtra(INTENTEXTRA_ORDERDONE, isOrderDone));
        subscriptions.clear();
        finish(); // go back to previous activity after setting result
    }
    private void setOrderCheckedin(boolean isOrderCheckedin){
        setResult(RESULT_OK, new Intent().putExtra(INTENTEXTRA_ORDER_CHECKEDIN, isOrderCheckedin));
    }

    private void checkIn(final Order order) {

        // iff async-call (done to twigly server)successful, use lambda to call GoToDetails
        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.reachedDestination(order.getOrderId()),
                (data) -> {
                    setOrderCheckedin(true);
                    checkProgress.setVisibility(View.GONE);
                    checkinView.setVisibility(View.GONE);
                    cardCashLayout.setVisibility(View.VISIBLE);
                    setCardCashListener();
                }, (error) -> {
                    checkProgress.setVisibility(View.GONE);
                    Toast.makeText(OrderDetailActivity.this, "chekin fail", Toast.LENGTH_LONG).show();
                    checkinAdapter.notifyItemChanged(0);
                    // Handle error
                    //TODO: alert dialog with option to call like in the main app
                }));
    }

}
