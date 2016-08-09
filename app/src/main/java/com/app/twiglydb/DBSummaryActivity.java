package com.app.twiglydb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class DBSummaryActivity extends AppCompatActivity {

    @BindView(R.id.rating) TextView dbRating;
    @BindView(R.id.table_amount_cash) TextView table_amount_cash;
    @BindView(R.id.table_count_cash) TextView table_count_cash;
    @BindView(R.id.table_amount_card) TextView table_amount_card;
    @BindView(R.id.table_count_card) TextView table_count_card;
    @BindView(R.id.table_amount_online) TextView table_amount_online;
    @BindView(R.id.table_count_online) TextView table_count_online;
    @BindView(R.id.table_amount_total) TextView table_amount_total;
    @BindView(R.id.table_count_total) TextView table_count_total;
    @BindView(R.id.table_amount_free) TextView table_amount_free;
    @BindView(R.id.table_count_free) TextView table_count_free;
    @BindView(R.id.table_amount_pending) TextView table_amount_pending;
    @BindView(R.id.table_count_pending) TextView table_count_pending;
    @BindView(R.id.table_amount_pendingCollected) TextView table_amount_pendingCollected;
    @BindView(R.id.table_count_pendingCollected) TextView table_count_pendingCollected;
    @BindView(R.id.table_amount_settlement) TextView table_amount_settlement;

    @BindView(R.id.ViewSwitcher) ViewSwitcher viewSwitcher;
    @BindView(R.id.my_toolbar) Toolbar myToolbar;
    @BindView(R.id.text_toolbar) TextView textToolbar;

    private String dateFrom, dateTo;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dbsummary);
        ButterKnife.bind(this);

        dateTo = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        setSupportActionBar(myToolbar);
        textToolbar.setText("DB: " + DeliveryBoy.getInstance().getName());

        viewSwitcher.showNext();
        dbRating.setText("Rating: "+DeliveryBoy.getInstance().getRating());

        TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();
        subscriptions.add(NetworkRequest.performAsyncRequest(
            api.getSummary(dateTo, dateTo),
            summary -> {
                table_amount_cash.setText("\u20B9 "+String.format("%.2f",summary.getAmountCash()));
                table_count_cash.setText(String.valueOf(summary.getCountCash()));
                table_amount_card.setText("\u20B9 "+String.format("%.2f",summary.getAmountCard()));
                table_count_card.setText(String.valueOf(summary.getCountCard()));
                table_amount_online.setText("\u20B9 "+String.format("%.2f",summary.getAmountOnline()));
                table_count_online.setText(String.valueOf(summary.getCountOnline()));
                table_amount_total.setText("\u20B9 "+String.format("%.2f",summary.getAmountTotal()));
                table_count_total.setText(String.valueOf(summary.getCountTotal()));
                table_amount_free.setText("\u20B9 "+String.format("%.2f",summary.getAmountFree()));
                table_count_free.setText(String.valueOf(summary.getCountFree()));
                table_amount_pending.setText("\u20B9 "+String.format("%.2f",summary.getAmountPending()));
                table_count_pending.setText(String.valueOf(summary.getCountPending()));
                table_amount_pendingCollected.setText("\u20B9 "+String.format("%.2f",summary.getAmountPendingCollected()));
                table_count_pendingCollected.setText(String.valueOf(summary.getCountPendingCollected()));
                table_amount_settlement.setText("\u20B9 "+String.format("%.2f",summary.getAmountSettlement()));
                viewSwitcher.showPrevious();
            },
            e -> Timber.e("network error in api.getSummary")
        ));

    }

    @Override
    public void onDestroy(){
        subscriptions.clear();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orderdetail_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_revert:
                subscriptions.clear();
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // Disable volume button
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return blockedKeys.contains(event.getKeyCode()) || super.dispatchKeyEvent(event);
    }
}