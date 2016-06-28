package com.app.twiglydb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.twiglydb.models.OrderDetail;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 22-06-2016.
 */
public class ItemDetailAdapter extends RecyclerView.Adapter<ItemDetailAdapter.ItemViewHolder> {

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_quantity) public TextView quantity;
        @BindView(R.id.item_name) public TextView name;

        public ItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    private List<OrderDetail> mItems;
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
        ItemViewHolder itemViewHolder = new ItemViewHolder(itemtView);
        return itemViewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Get the data model based on position
        OrderDetail contact = mItems.get(position);

        // Set item views based on the data model
        holder.quantity.setText(mItems.get(position).getQuantity() + "");
        holder.name.setText(mItems.get(position).getMenuItemName());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
