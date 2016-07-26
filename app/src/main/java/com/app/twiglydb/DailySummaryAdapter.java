package com.app.twiglydb;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.twiglydb.models.Order;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 26-07-2016.
 */
public class DailySummaryAdapter extends RecyclerView.Adapter<DailySummaryAdapter.OrderViewHolder> {

    List<Order> orders;
    Context context;
    public DailySummaryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
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
    public DailySummaryAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.order_summary_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DailySummaryAdapter.OrderViewHolder holder, int position) {

        Order order = orders.get(position);

        holder.customer_name.setText(order.getName());
        holder.customer_name.setTextColor(Color.parseColor("#009688"));
        holder.orderId.setText("#"+order.getOrderId());
        holder.address.setText(order.getAddress().trim());
        holder.cartPrice.setText("\u20B9 " + String.format("%.2f",order.getTotal()));
        holder.deliveryTime.setText(order.getDeliveryTime());

        holder.callButton.setVisibility(View.GONE);
        holder.navigateButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
