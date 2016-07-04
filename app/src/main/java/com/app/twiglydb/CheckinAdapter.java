package com.app.twiglydb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhishek on 01-07-2016.
 */
public class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.ItemViewHolder> {
    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.button_checkin) public Button checkinText;

        public ItemViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    Context context;
    public CheckinAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CheckinAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        final View itemtView = inflater.inflate(R.layout.checkin, parent, false);

        // Return a new holder instance
        return new ItemViewHolder(itemtView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Get the data model based on position

        // Set item views based on the data model
        holder.checkinText.setText(">> Swipe to Checkin >>");
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
