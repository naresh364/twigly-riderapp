package com.app.twiglydb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.twiglydb.models.Order;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by naresh on 10/01/16.
 */
public class OrderSummaryActivity extends AppCompatActivity{
    List<Order> orders;

    @InjectView(R.id.order_recycler_view)
    RecyclerView mRecyclerView;

    OrderSummaryAdapter orderSummaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orders = new LinkedList<>();
        Order o1 = new Order();
        o1.setAddress("test\ntest2\ntest3\n");
        o1.setName("Naresh");
        o1.setMobile_number("8800880088");
        o1.setOrderId(1010101);
        o1.setTotal(200f);
        orders.add(o1);
        Order o2 = new Order();
        o2.setAddress("teest\nteest2\nteest3\n");
        o2.setName("Naresh 123");
        o2.setMobile_number("0088008800");
        o2.setOrderId(1010110);
        o2.setTotal(500f);
        orders.add(o2);

        orderSummaryAdapter = new OrderSummaryAdapter(this, orders);
        setContentView(R.layout.order_summary_list);
        ButterKnife.inject(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(orderSummaryAdapter);
        mRecyclerView.setHasFixedSize(true);
    }
}
