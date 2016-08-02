package com.app.twiglydb;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.models.OrderDetail;
import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.ServerResponseCode;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;

//import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by naresh on 11/01/16.
 */
public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderViewHolder>{

    List<Order> orders;
    Context context;
    private Subscription getPostSubscription;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private int pos;

    //private XYZinterface xyzListener;
    public OrderSummaryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;

        //this.xyzListener = xyzListener;
        //locationService = new DBLocationService(context);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.order_summary_card) CardView cardView;
        @BindView(R.id.order_summary_layout) public RelativeLayout summaryLayout;
        @BindView(R.id.customer_name) public TextView customer_name;
        @BindView(R.id.order_id) public TextView orderId;
        @BindView(R.id.cart_price) public TextView cartPrice;
        @BindView(R.id.address) public TextView address;
        @BindView(R.id.delivery_time) public TextView deliveryTime;
        @BindView(R.id.call_button) public ImageButton callButton;
        @BindView(R.id.navigate_button) public Button navigateButton;
        @BindView(R.id.divider2) public View divider2;
        @BindView(R.id.call_progress) public ProgressBar callProgress;

        public OrderViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }

        public void showProgress(boolean show) {
            if (show) {
                callProgress.setVisibility(View.VISIBLE);
            } else {
                callProgress.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public OrderSummaryAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.order_summary_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, final int position) {
        // RecyclerView will not call this method again if the position of the item changes in the data set
        // So, only use the position parameter while acquiring the related data item inside this method and should not keep
        // a copy of it.If you need the position of an item later on (e.g. in a click listener), use getAdapterPosition()
        // which will have the updated adapter position

        Order order = orders.get(position);
        String isPending = "";
        Double pending = order.getUserPendingBalance();
        if(pending != 0){
            isPending = "*";
        }
        pos = position;

        holder.customer_name.setText(order.getName());
        holder.customer_name.setTextColor(Color.parseColor("#009688"));

        holder.orderId.setText("#"+order.getOrderId());
        holder.address.setText(order.getAddress().trim());
        holder.cartPrice.setText("\u20B9 " + String.format("%.2f",order.getTotal()+pending) + isPending);
        holder.deliveryTime.setText(order.getDeliveryTime());

        //holder.checkinButton.setTag(holder);

        holder.callButton.setOnClickListener(view -> {
            String uri = "tel:" + order.getMobileNumber().trim() ;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            if (Utils.mayRequestPermission(context, Manifest.permission.CALL_PHONE)) {
                context.startActivity(intent);
            }
        });
        holder.navigateButton.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + order.getLat() + ","+order.getLng());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
            return;
        });

        holder.customer_name.setOnClickListener(view -> GoToDetails(order, holder));
        holder.orderId.setOnClickListener(view -> GoToDetails(order, holder));
        holder.address.setOnClickListener(view -> GoToDetails(order, holder));

        if (order.getLat() == 0 || order.getLng() == 0) {
            holder.navigateButton.setVisibility(View.GONE);
            holder.divider2.setVisibility(View.GONE);
        } else {
            holder.navigateButton.setVisibility(View.VISIBLE);
        }
    }
/*
    @Override
    public void onClick(View v) {
        final int viewId = v.getId();

        OrderViewHolder ovh = (OrderViewHolder)v.getTag();
        if (ovh == null) return;

        Order order = orders.get(ovh.getAdapterPosition());

        if (viewId == R.id.call_button) {
            //call the customer
            String uri = "tel:" + orders.get(ovh.getLayoutPosition()).getMobileNumber().trim() ;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            if (Utils.mayRequestPermission(context, Manifest.permission.CALL_PHONE)) {
                context.startActivity(intent);
            }
            return;
        }

        if (viewId == R.id.navigate_button) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + order.getLat() + ","+order.getLng());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
            return;
        }

        if (viewId == R.id.checkin_button) {
            checkIn(ovh, order);
        }

    }*/

    private final int REQUESTCODE_ORDERDONE = 0;
    /*private void GoToDetails(OrderViewHolder ovh) {
        //get the updated details of orders
        ovh.showProgress(true);
        TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();
        // iff async-call (done to twigly server)successful, use lambda to call GoToDetails
        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.getOrders(),
                (orders) -> {
                    ovh.showProgress(false);
                    Intent i = OrderDetailActivity.newIntent(context, orders.get(ovh.getAdapterPosition()));
                    ((Activity)context).startActivityForResult(i, REQUESTCODE_ORDERDONE);
                }, (error) -> {
                    // Handle all errors at one place
                    getPostSubscription = null;

                }));

        //RxBus.getInstance().post(order);
        //EventBus.getDefault().postSticky(order);
        //((Activity)context).startActivityForResult(new Intent(context, OrderDetailActivity.class), REQUESTCODE_ORDERDONE);

    }*/
    private void GoToDetails(Order order, OrderViewHolder ovh){
        Timber.i("DB selected an order detail");
        if(!order.isCheckedIn) order.isCheckedIn = mOrderCheckedIn;
        Intent i = OrderDetailActivity.newIntent(context, order, ovh.getAdapterPosition());
        ((Activity)context).startActivityForResult(i, REQUESTCODE_ORDERDONE);
    }

    private int mOrderDone;
    private boolean mOrderCheckedIn = false;
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUESTCODE_ORDERDONE) {
            if (data == null) {
                return;
            }
            mOrderCheckedIn = OrderDetailActivity.wasOrderCheckedIn(data);
            mOrderDone = OrderDetailActivity.wasOrderDone(data);
            if(mOrderDone >= 0){
                orders.remove(mOrderDone);
                notifyDataSetChanged();
            }
        }
    }

    public void onStop(){
        if(getPostSubscription != null) getPostSubscription.unsubscribe();
    }

    public int getOrderStatus(){
        return mOrderDone;
    }


/*
        Call<ServerCalls.ServerResponse> response = ServerCalls.getInstance().service.reachedDestination(order.getOrderId());
        response.enqueue(new Callback<ServerCalls.ServerResponse>() {
            @Override
            public void onResponse(Response<ServerCalls.ServerResponse> response) {
                if (response == null) {
                    Toast.makeText(context, "Unable to complete the request", Toast.LENGTH_LONG).show();
                    ovh.checkInFailed();
                    return;
                }
                ServerCalls.ServerResponse serverResponse = response.body();
                if (serverResponse == null) {
                    Toast.makeText(context, "Server response null", Toast.LENGTH_LONG).show();
                    ovh.checkInFailed();
                    return;
                }
                ServerResponseCode code = ServerResponseCode.valueOf(serverResponse.code);
                if (code == ServerResponseCode.OK){
                    ovh.checkInDone(true);
                    order.isCheckedIn = true;
                    GoToDetails(order);
                    return;
                }
                ovh.checkInFailed();
                Toast.makeText(context, serverResponse.message, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Unable to complete the request", Toast.LENGTH_LONG).show();
                ovh.checkInFailed();
            }
        });*/
    //}

}
