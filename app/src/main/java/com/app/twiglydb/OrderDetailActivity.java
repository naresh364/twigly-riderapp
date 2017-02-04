package com.app.twiglydb;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private static final String INTENTEXTRA_POSITION = "com.app.twiglydb.order_position";


    private EzTapServices ez;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();

    private int position;

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
    @BindView(R.id.checkbox_pending) CheckBox checkbox_pending;
    @BindView(R.id.qr_button) ImageButton qr_button;

    @BindView(R.id.my_toolbar) Toolbar myToolbar;
    @BindView(R.id.text_toolbar) TextView textToolbar;
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

    private String isPending = "";
    private Double pending = 2.1;
    private int pos;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        //EventBus.getDefault().register(this);
        order = getIntent().getBundleExtra(INTENTEXTRA_ORDERDETAILS).getParcelable(INTENTEXTRA_PARCEL_ORDERDETAILS);
        pos = getIntent().getBundleExtra(INTENTEXTRA_ORDERDETAILS).getInt(INTENTEXTRA_POSITION);
        //subscriptions = new CompositeSubscription();
        //Toast.makeText(OrderDetailActivity.this, order.getOrderStatus(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(OrderDetailActivity.this, ""+order.isCheckedIn, Toast.LENGTH_SHORT).show();
        //subscriptions.add( RxBus.getInstance().register(Order.class, o1 -> {
        //    order = o1;
        //setTitle("TwiglyDB: " + DeliveryBoy.getInstance().getName());

        // disable lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.order_detail);
        ButterKnife.bind(this);

        setSupportActionBar(myToolbar);
        textToolbar.setText(DeliveryBoy.getInstance().getName());
        ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);


        itemDetailAdapter = new ItemDetailAdapter(this, order.getOrderDetails());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true); // sets height for the recycler view
        mRecyclerView.setAdapter(itemDetailAdapter);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setFocusable(false);
        checkinView.bringToFront();

        //TODO: repeated stuff-----------------------------------------------------------------------
        pending = order.getUserPendingBalance();
        if(pending != 0.0){
            checkbox_pending.setVisibility(View.VISIBLE);
            checkbox_pending.setText("Pending  \u20B9"+pending);
            isPending = "*";
            order.setCollectPending(true);
        }

        if (order.getQrCode() != null) {
            qr_button.setVisibility(View.VISIBLE);
            qr_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qrCode;
                    if (order.shouldCollectPending()) {
                        qrCode = order.getQrCode();
                    } else {
                        qrCode = order.getQrCodeWithPending();
                    }
                    String htmlqr = "<html> <p style=\"text-align:center;\"><img style='width:70%;'" +
                            " src=\"data:image/png;base64," + qrCode +"\" id=\"qrCode\"></p></html>";
                    Intent intent = new Intent(mContext, WebviewActivity.class);
                    intent.putExtra("title", "QR code for #"+order.getOrderId());
                    intent.putExtra("data", htmlqr);
                    startActivity(intent);
                }
            });
        }

        mCustomerName.setText(order.getName());
        mCustomerName.setTextColor(Color.parseColor("#009688"));

        orderId.setText("#"+order.getOrderId());
        address.setText(order.getAddress().trim());
        cartPrice.setText(getOrderTotal(true));
        deliveryTime.setText(order.getDeliveryTime());
        callButton.setOnClickListener(view -> {
            String uri = "tel:" + order.getMobileNumber().trim() ;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            if (Utils.mayRequestPermission(OrderDetailActivity.this, Manifest.permission.CALL_PHONE)) {
                startActivity(intent);
            }
            api.dbCalled(order.getMobileNumber());
        });
        navigateButton.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + order.getLat() + ","+order.getLng());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            //return;
        });
        if (order.getLat() == 0 || order.getLng() == 0) {
            navigateButton.setVisibility(View.INVISIBLE);
        } else {
            navigateButton.setVisibility(View.VISIBLE);
        }
        //----------------------------------------------------------------------------------

        //check if it is already paid
        if (order.isPaid() && pending.intValue() <= 0) {
            //mode = "OnLine";
            showPaidButton(true);
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
                        //checkinAdapter.notifyDataSetChanged();
                        /*if(mGoogleApiClient.isConnected()){
                            checkLocationSettings();
                        }
                        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){*/
                            checkIn(order);
                        /*} else {
                            checkProgress.setVisibility(View.GONE);
                            checkinAdapter.notifyItemChanged(0);
                        }*/

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

    //TODO: setTag on buttons for common functions
    private void setCardCashListener(){
        Timber.i("Setting up listeners for card/cash button");
        cashButton.setOnClickListener(click -> {
            /*if(mGoogleApiClient.isConnected()){
                checkLocationSettings();
            }
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){*/
                AlertDialog.Builder b = new AlertDialog.Builder(OrderDetailActivity.this);
                b.setTitle("Cash Payment")
                        .setMessage("Are you sure you want to continue?")
                        .setNegativeButton("Cancel", (DialogInterface dialog, int id) -> {})
                        .setPositiveButton("Accept", (DialogInterface dialog, int id) -> {
                            MarkOrderDone(order, "COD");
                        });

                AlertDialog d = b.create();
                d.show();
            //}
        });

        cardButton.setOnClickListener(click -> {
            /*if(mGoogleApiClient.isConnected()){
                checkLocationSettings();
            }
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){*/
            if(ezTxnId == null){
                cardCashLayout.setVisibility(View.GONE);
                checkProgress.setVisibility(View.VISIBLE);
                ez = new EzTapServices(this, order);
                ez.initialize();
            } else {
                MarkOrderDone(order, "CardOD");
            }

            //}
        });

        paidButton.setOnClickListener(click -> {
            /*if(mGoogleApiClient.isConnected()){
                checkLocationSettings();
            }
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
            if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){*/
                MarkOrderDone(order, "Online");
            //}
        });
    }
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
    protected void onResume(){
        super.onResume();

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        if(checkProgress.getVisibility() == View.VISIBLE){
            checkProgress.setVisibility(View.GONE);
            if(order.isCheckedIn){
                cardCashLayout.setVisibility(View.VISIBLE);
            } else {
                checkinView.setVisibility(View.VISIBLE);
            }
        }

        // if ez-transaction is complete but server call to twigly failed, simply call markorderdone on clicking card
        if(ezTxnId != null){
            cardButton.setOnClickListener((click) -> MarkOrderDone(order, "CardOD"));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null && intent.hasExtra("response")) {
            //Toast.makeText(this, requestCode+" "+resultCode,Toast.LENGTH_SHORT).show();
            Timber.i("EZtap log: ",intent.getStringExtra("response"));
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
                        MarkOrderDone(order, "CardOD");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    checkProgress.setVisibility(View.GONE);
                    cardCashLayout.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    // saving location on markdone, not checkin
    private void MarkOrderDone(final Order order, String mode) {
        Timber.i("DB delivered");
        cardCashLayout.setVisibility(View.GONE);
        checkProgress.setVisibility(View.VISIBLE);

        double lat=0, lng=0, acc=0;
        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (Exception ex) {
            Timber.d("unable to load location");
        }
        if (mCurrentLocation!= null) {
            lat = mCurrentLocation.getLatitude();
            lng = mCurrentLocation.getLongitude();
            acc = mCurrentLocation.getAccuracy();
        }

        // iff async-call (done to twigly server)successful, use lambda to call setOrderDone
        subscriptions.add( NetworkRequest.performAsyncRequest(
                api.markDone(mode, order.getOrderId(), lat, lng, acc, order.shouldCollectPending()),
                (data) -> {
                    if(ServerResponseCode.valueOf(data.code) == ServerResponseCode.OK) {
                        checkProgress.setVisibility(View.GONE);
                        //setPosition(pos);
                        setOrderDone(pos);
                    }
                }, (error) -> {
                    // Handle error
                    //getPostSubscription = null;
                    //TODO: alert dialog and  option to call
                    Toast.makeText(OrderDetailActivity.this, "Order Status Update Failed", Toast.LENGTH_LONG).show();
                    checkProgress.setVisibility(View.GONE);
                    cardCashLayout.setVisibility(View.VISIBLE);
                }));
    }

    // OrderSummaryAdapter calls this function to start OrderDetailActivity
    public static Intent newIntent(Context packageContext, Order o, int position) {
        Intent i = new Intent(packageContext, OrderDetailActivity.class);
        Bundle b = new Bundle();
        b.putParcelable(INTENTEXTRA_PARCEL_ORDERDETAILS, o);
        b.putInt(INTENTEXTRA_POSITION, position);
        i.putExtra(INTENTEXTRA_ORDERDETAILS, b);
        return i;
    }

    // OrderSummaryAdapter calls this function to check if the order was done
    public static int wasOrderDone(Intent result) {
        return result.getIntExtra(INTENTEXTRA_ORDERDONE, -1);
    }
    public static boolean wasOrderCheckedIn(Intent result){
        return result.getBooleanExtra(INTENTEXTRA_ORDER_CHECKEDIN, false);
    }
    /*public static int getPosition(Intent result){
        for (String key : result.getExtras().keySet()) {
            Object value = result.getExtras().get(key);
            Timber.d(String.format("%s %s (%s)", key,value.toString(), value.getClass().getName()));
        }
        return result.getIntExtra(INTENTEXTRA_POSITION, 0);
    }*/

    // wasOrderDone reads the value set by this function to check if order was done
    private void setOrderDone(int pos){
        setResult(RESULT_OK, new Intent().putExtra(INTENTEXTRA_ORDERDONE, pos));
        subscriptions.clear();
        finish(); // go back to previous activity after setting result
    }
    private void setOrderCheckedin(boolean isOrderCheckedin){
        setResult(RESULT_OK, new Intent().putExtra(INTENTEXTRA_ORDER_CHECKEDIN, isOrderCheckedin));
    }

    private void checkIn(final Order order) {
        Timber.i("DB checking in");
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

    @Override
    public void onConnected(Bundle bundle){
        checkLocationSettings();
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED && mCurrentLocation == null){
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    // Invoked when SettingsApi#checkLocationSettings is called
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        getStatus(locationSettingsResult);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Disable volume button
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //return blockedKeys.contains(event.getKeyCode()) || super.dispatchKeyEvent(event);
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orderdetail_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_revert:
                subscriptions.clear();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onPendingCheck(View v){
        boolean checked = ((CheckBox) v).isChecked();

        if(checked){
            cartPrice.setText(getOrderTotal(true));
            order.setCollectPending(true);
            if (order.isPaid() && pending.intValue() > 0) {
                //we have pending amount should show card/cash
                showPaidButton(false);
            }
        } else {
            cartPrice.setText(getOrderTotal(false));
            order.setCollectPending(false);
            if (order.isPaid() && pending.intValue() > 0) {
                //we have pending amount should show card/cash
                showPaidButton(true);
            }
        }

    }

    private void showPaidButton(boolean show) {
         if (show) {
             paidButton.setVisibility(View.VISIBLE);
             cardButton.setVisibility(View.GONE);
             cashButton.setVisibility(View.GONE);
         } else {
             paidButton.setVisibility(View.GONE);
             cardButton.setVisibility(View.VISIBLE);
             cashButton.setVisibility(View.VISIBLE);

         }

    }

    private String getOrderTotal(boolean includePending) {
        double pendingAmount = (includePending) ? pending : 0;
        String orderValue = "\u20B9 " + String.format("%.2f",order.getTotal()+pendingAmount) + isPending;
        if (order.isPaid()) {
            orderValue = "PAID"+isPending;
        }
        return orderValue;
    }

}
