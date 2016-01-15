package com.app.twiglydb;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.ServerCalls;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Created by naresh on 11/01/16.
 */
public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderViewHolder> implements View.OnClickListener{

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @InjectView(R.id.order_summary_card)
        public CardView cardView;
        @InjectView(R.id.order_summary_layout)
        public RelativeLayout summaryLayout;
        @InjectView(R.id.customer_name)
        public TextView customer_name;
        @InjectView(R.id.order_id)
        public TextView orderId;
        @InjectView(R.id.cart_price)
        public TextView cartPrice;
        @InjectView(R.id.address)
        public TextView address;
        @InjectView(R.id.delivery_time)
        public TextView deliveryTime;
        @InjectView(R.id.call_button)
        public Button callButton;
        @InjectView(R.id.navigate_button)
        public Button navigateButton;
        @InjectView(R.id.card_payment_button)
        public Button cardPaymentButton;
        @InjectView(R.id.cash_payment_button)
        public Button cashPaymentButton;

        public OrderViewHolder(View v) {
            super(v);
            ButterKnife.inject(this,v);
        }
    }

    List<Order> orders;
    Context context;
    DBLocationService locationService;
    public OrderSummaryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        locationService = new DBLocationService(context);
    }

    @Override
    public OrderSummaryAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.order_summary_card, parent, false);
        final OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        orderViewHolder.callButton.setOnClickListener(this);
        orderViewHolder.navigateButton.setOnClickListener(this);
        orderViewHolder.cashPaymentButton.setOnClickListener(this);
        orderViewHolder.cardPaymentButton.setOnClickListener(this);
        return orderViewHolder;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.customer_name.setText(orders.get(position).getName());
        holder.cartPrice.setText(String.format("%.2f",orders.get(position).getTotal()));
        holder.address.setText(orders.get(position).getAddress());
        holder.orderId.setText(orders.get(position).getOrderId() + "");
        holder.deliveryTime.setText(orders.get(position).getDeliveryTime());

        holder.cardPaymentButton.setTag(holder);
        holder.navigateButton.setTag(holder);
        holder.cashPaymentButton.setTag(holder);
        holder.callButton.setTag(holder);
        holder.summaryLayout.setTag(holder);

        holder.summaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderViewHolder ovh = (OrderViewHolder) v.getTag();
                if (ovh == null) return;

                Timber.d("click on position at:"+ovh.getLayoutPosition());

            }
        });

        Order order = orders.get(holder.getAdapterPosition());
        if (order.getLat() == 0 || order.getLng() == 0) {
            holder.navigateButton.setVisibility(View.INVISIBLE);
            return;
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
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", order.getLat(), order.getLng());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(intent);
            return;
        }

        String mode = "";
        if (viewId == R.id.cash_payment_button) {
           mode = "cash";
        }

        if (viewId == R.id.card_payment_button) {
            mode = "card";
        }

        if (!mode.equalsIgnoreCase("")) {
            if (!locationService.isGPSEnabled()) {
                locationService.showSettingsAlert();
                return;
            }
            double lat = locationService.getLatitude();
            double lng = locationService.getLongitude();

            ServerCalls.getInstanse().service.markDone(mode, order.getOrderId(), lat, lng);
        }

    }
}
