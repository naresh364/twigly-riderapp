package com.app.twiglydb;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.twiglydb.models.Order;

import java.util.List;
import java.util.Timer;

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
        @InjectView(R.id.call_button)
        public Button callButton;
        public OrderViewHolder(View v) {
            super(v);
            ButterKnife.inject(this,v);
        }
    }

    List<Order> orders;
    Context context;
    public OrderSummaryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public OrderSummaryAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.order_summary_card, parent, false);
        final OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        orderViewHolder.callButton.setOnClickListener(this);
        return orderViewHolder;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.customer_name.setText(orders.get(position).getName());
        holder.cartPrice.setText(orders.get(position).getTotal()+"");
        holder.address.setText(orders.get(position).getAddress());
        holder.orderId.setText(orders.get(position).getOrderId()+"");

        holder.cartPrice.setTag(holder);
        holder.address.setTag(holder);
        holder.callButton.setTag(holder);
        holder.cardView.setTag(holder);
        holder.summaryLayout.setTag(holder);
        holder.customer_name.setTag(holder);
        holder.orderId.setTag(holder);
        holder.summaryLayout.setTag(holder);

        holder.summaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderViewHolder ovh = (OrderViewHolder) v.getTag();
                if (ovh == null) return;

                Timber.d("click on position at:"+ovh.getLayoutPosition());

            }
        });
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();

        OrderViewHolder ovh = (OrderViewHolder)v.getTag();
        if (ovh == null) return;

        if (viewId == R.id.call_button) {
            //call the customer
        }

    }
}
