package com.app.twiglydb;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.app.twiglydb.models.Order;
import com.app.twiglydb.models.OrderDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class OrderDetailActivity extends AppCompatActivity {
    List<OrderDetail> itemDetails;

    @InjectView(R.id.detail_recycler_view)
    RecyclerView mRecyclerView;

    ItemDetailAdapter itemDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //itemDetails = Order.getInstance().getOrderDetails();
        itemDetails = getIntent().getExtras().getParcelableArrayList("item_details");
        itemDetailAdapter = new ItemDetailAdapter(this, itemDetails);
        setContentView(R.layout.order_detail);
        ButterKnife.inject(this);
        mRecyclerView.setAdapter(itemDetailAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
