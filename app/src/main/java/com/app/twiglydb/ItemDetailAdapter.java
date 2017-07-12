package com.app.twiglydb;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.twiglydb.models.OrderDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 22-06-2016.
 */
public class ItemDetailAdapter extends RecyclerView.Adapter<ItemDetailAdapter.ItemViewHolder> {

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_detail) public LinearLayout itemDetailLayout;
        @BindView(R.id.item_quantity) public TextView quantity;
        @BindView(R.id.item_name) public TextView name;
        @BindView(R.id.verified) public CheckBox verified;

        public ItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            verified.setEnabled(false);
        }
    }

    private List<OrderDetail> mItems;
    private List<ItemViewHolder> itemViewHolders = new ArrayList<>();
    Context context;
    public ItemDetailAdapter(Context context, List<OrderDetail> items) {
        this.context = context;
        this.mItems = items;
    }

    @Override
    public ItemDetailAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        final View itemtView = inflater.inflate(R.layout.item_detail, parent, false);

        // Return a new holder instance
        ItemViewHolder ivh = new ItemViewHolder(itemtView);
        return ivh;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Get the data model based on position
        if((position%2) == 1) holder.itemDetailLayout.setBackgroundColor(Color.parseColor("#ECEFF1"));

        // Set item views based on the data model
        holder.quantity.setText(mItems.get(position).getQuantity() + "");
        holder.name.setText(mItems.get(position).getMenuItemName());
        holder.verified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mItems.get(position).setVerified(isChecked);
                for (OrderDetail od : mItems) {
                    if (!od.isVerified()) {
                        ((OrderDetailActivity) context).allItemsChecked(false);
                        return;
                    }
                }
                ((OrderDetailActivity) context).allItemsChecked(true);
            }
        });
        holder.verified.setChecked(mItems.get(position).isVerified());

        if (!itemViewHolders.contains(holder)) itemViewHolders.add(holder);
        ((OrderDetailActivity) context).itemsInitialized();
    }

    public void checkboxEnable(boolean enable) {
        for (ItemViewHolder ivh : itemViewHolders) {
            ivh.verified.setEnabled(enable);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public interface AllItemChecked {
        public void allItemsChecked(boolean itemchecked);
        public void itemsInitialized();
    }

}
