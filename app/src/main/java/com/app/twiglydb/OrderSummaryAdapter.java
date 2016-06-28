package com.app.twiglydb;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.models.OrderDetail;
import com.app.twiglydb.network.ServerCalls;
import com.app.twiglydb.network.ServerResponseCode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by naresh on 11/01/16.
 */
public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderViewHolder> implements View.OnClickListener{

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @BindView(R.id.order_summary_card) CardView cardView;
        @BindView(R.id.order_summary_layout) public LinearLayout summaryLayout;
        @BindView(R.id.customer_name) public TextView customer_name;
        @BindView(R.id.order_id) public TextView orderId;
        @BindView(R.id.cart_price) public TextView cartPrice;
        @BindView(R.id.address) public TextView address;
        @BindView(R.id.delivery_time) public TextView deliveryTime;
        @BindView(R.id.call_button) public Button callButton;
        @BindView(R.id.navigate_button) public Button navigateButton;
        @BindView(R.id.checkin_button) public Button checkinButton;
        @BindView(R.id.undo_checkin_button) public Button undoCheckinButton;
        @BindView(R.id.checkin_layout) public RelativeLayout checkinLayout;
        @BindView(R.id.done_layout) public RelativeLayout doneLayout;
        @BindView(R.id.call_progress) public ProgressBar callProgress;

        public OrderViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }

        public void showProgress(boolean show) {
            if (show) {
                callProgress.setVisibility(View.VISIBLE);
                checkinLayout.setVisibility(View.INVISIBLE);
                //doneLayout.setVisibility(View.INVISIBLE);
            } else {
                callProgress.setVisibility(View.INVISIBLE);
            }
        }

        public void checkInDone(boolean done){
            showProgress(false);
            if (done) {
                checkinLayout.setVisibility(View.GONE);
                doneLayout.setVisibility(View.VISIBLE);
            } else {
                checkinLayout.setVisibility(View.VISIBLE);
                doneLayout.setVisibility(View.GONE);
            }
        }

        public void checkInFailed() {
            showProgress(false);
            checkinLayout.setVisibility(View.VISIBLE);
        }

    }

    List<Order> orders;
    Context context;
    DBLocationService locationService;
    //private XYZinterface xyzListener;
    public OrderSummaryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        //this.xyzListener = xyzListener;
        //locationService = new DBLocationService(context);
    }

    @Override
    public OrderSummaryAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.order_summary_card, parent, false);
        final OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        orderViewHolder.callButton.setOnClickListener(this);
        orderViewHolder.navigateButton.setOnClickListener(this);
        orderViewHolder.checkinButton.setOnClickListener(this);
        orderViewHolder.undoCheckinButton.setOnClickListener(this);
        return orderViewHolder;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, final int position) {
        holder.customer_name.setText(orders.get(position).getName());
        holder.cartPrice.setText(String.format("%.2f",orders.get(position).getTotal()));
        holder.address.setText(orders.get(position).getAddress());
        holder.orderId.setText(orders.get(position).getOrderId() + "");
        holder.deliveryTime.setText(orders.get(position).getDeliveryTime());

        holder.navigateButton.setTag(holder);
        holder.callButton.setTag(holder);
        holder.summaryLayout.setTag(holder);
        holder.checkinButton.setTag(holder);
        holder.undoCheckinButton.setTag(holder);

        holder.summaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderViewHolder ovh = (OrderViewHolder) v.getTag();
                if (ovh == null) return;

                Timber.d("click on position at:"+ovh.getLayoutPosition());

            }
        });

        holder.customer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToDetails(orders.get(position));
            }
        });

        holder.orderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToDetails(orders.get(position));
            }
        });

        Order order = orders.get(holder.getAdapterPosition());

        holder.checkInDone(order.isCheckedIn);
        if (order.getLat() == 0 || order.getLng() == 0) {
            holder.navigateButton.setVisibility(View.INVISIBLE);
        } else {
            holder.navigateButton.setVisibility(View.VISIBLE);
        }

    }

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

        if (viewId == R.id.undo_checkin_button) {
            ovh.checkInDone(false);
        }

    }

    private final int REQUESTCODE_ORDERDONE = 0;
    private void GoToDetails(final Order order) {
        Intent i = OrderDetailActivity.newIntent(context, order);
        ((Activity)context).startActivityForResult(i, REQUESTCODE_ORDERDONE);

    }

    private boolean mOrderDone;
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUESTCODE_ORDERDONE) {
            if (data == null) {
                return;
            }
            mOrderDone = OrderDetailActivity.wasOrderDone(data);
            /*if(mOrderDone) {
                DeliveryBoy.getInstance().getAssignedOrders().remove(order);
                notifyDataSetChanged();
            }*/
        }
    }

    public Boolean getOrderStatus(){
        return mOrderDone;
    }

    private void checkIn(final OrderViewHolder ovh, final Order order) {

        ovh.showProgress(true);

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
        });
    }

}
