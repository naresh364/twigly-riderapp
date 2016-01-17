package com.app.twiglydb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.OrderResponse;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.ServerCalls;

import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
        orders = DeliveryBoy.getInstance().getAssignedOrders();

//        Call<OrderResponse> orderResponseCall = ServerCalls.getInstanse().service.getRecentOrders("-1", 0, 10);
//        orderResponseCall.enqueue(new Callback<OrderResponse>() {
//            @Override
//            public void onResponse(Response<OrderResponse> response) {
//                final OrderResponse orderResponse = response.body();
//                if (orderResponse == null) {
//                    Timber.d("");
//                    Toast.makeText(OrderSummaryActivity.this, response.message(), Toast.LENGTH_LONG);
//                    return;
//                }
//                orders.addAll(orderResponse.getOrders());
//                orderSummaryAdapter.notifyDataSetChanged();
//                Timber.d("Success");
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Timber.d("Failure");
//            }
//        });

        orderSummaryAdapter = new OrderSummaryAdapter(this, orders);
        setContentView(R.layout.order_summary_list);
        ButterKnife.inject(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(orderSummaryAdapter);
        mRecyclerView.setHasFixedSize(true);
    }
}
