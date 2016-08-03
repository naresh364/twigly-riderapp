package com.app.twiglydb;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.app.twiglydb.bus.EventReceiver;
import com.app.twiglydb.bus.RxBus;
import com.app.twiglydb.models.DeliveryBoy;
import com.app.twiglydb.models.Order;
import com.app.twiglydb.network.NetworkRequest;
import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by naresh on 10/01/16.
 */
public class OrderSummaryActivity extends BaseActivity {/*implements XYZinterface*/
    List<Order> orders;

    @BindView(R.id.order_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.activity_main_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.order_list_noorders) TextView noOrdersView;
    @BindView(R.id.ViewSwitcher) ViewSwitcher viewSwitcher;
    @BindView(R.id.toolbar) Toolbar myToolbar;
    @BindView(R.id.text_toolbar) TextView textToolbar;
    @BindView(R.id.fab_home) FloatingActionButton fab_home;

    OrderSummaryAdapter orderSummaryAdapter;

    private EventReceiver eventReceiver;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private int exitCode;
    public int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orders = DeliveryBoy.getInstance().getAssignedOrders();
        //setTitle("TwiglyDB: " + DeliveryBoy.getInstance().getName());

        orderSummaryAdapter = new OrderSummaryAdapter(this, orders);

        // disable lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.order_summary_list);
        ButterKnife.bind(this);

        setSupportActionBar(myToolbar);
        textToolbar.setText("DB: " + DeliveryBoy.getInstance().getName());

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(orderSummaryAdapter);
        mRecyclerView.setHasFixedSize(true);
        updateNoOrderView();

        mSwipeRefreshLayout.setOnRefreshListener(()->{
            DeliveryBoy.getInstance().updateOrders(new OrderRefreshCallback() {
                @Override
                public void orderRefreshed(boolean wasSuccess) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (!wasSuccess) {
                        Toast.makeText(MyApp.getContext(), "Unable to refresh orders", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            orderSummaryAdapter.notifyDataSetChanged();
            updateNoOrderView();
        });

        // Disable notification bar expansion
        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

        // this is to enable the notification to recieve touch events
        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

        // Draws over status bar
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup view = new customViewGroup(this);
        manager.addView(view, localLayoutParams);
        //-------------notification bar expansion disabler ends here------------------*/

        subscriptions.add(RxBus.INSTANCE.register(Bundle.class, bundle -> {
            String type = bundle.getString("title");
            if(type != null && type.equalsIgnoreCase("order")){
                newOrderReceived(bundle.getString("msg"));
            }
        }));

        fab_home.setOnClickListener(click -> {
            String uri = "tel:" + DeliveryBoy.getInstance().getManager();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            if (Utils.mayRequestPermission(this, Manifest.permission.CALL_PHONE)) {
                startActivity(intent);
            }
        });

        WritePhoneContact("Utpal Sir", "9560190710", this);
        WritePhoneContact("Naresh Sir", "9686444640", this);
        WritePhoneContact("Sonal Sir", "9910030423", this);
        WritePhoneContact("Rohan Sir", "9910013951", this);
        WritePhoneContact("Osama Sir", "8006260747", this);
        WritePhoneContact("Sonali Mam", "9971336107", this);
        WritePhoneContact("Anuj Sir", "9654335332", this);
        WritePhoneContact("Amit Sir (Sector 46)", "9205270022", this);
        WritePhoneContact("Amit Sir (Phase1)", "9821879503", this);
        WritePhoneContact("Aalam", "9560304801", this);
        WritePhoneContact("OP", "9560304367", this);
        WritePhoneContact("Ranjeet", "9599395053", this);
    }

    public void WritePhoneContact(String displayName, String number,Context cntx /*App or Activity Ctx*/)
    {
        Context contetx 	= cntx; //Application's context or Activity's context
        String strDisplayName 	=  displayName; // Name of the Person to add
        String strNumber 	=  number; //number of the person to add with the Contact

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strNumber) // Number to be added
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        try
        {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = contetx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
        }
        catch (RemoteException exp)
        {
            //logs;
        }
        catch (OperationApplicationException exp)
        {
            //logs
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ordersummary_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        orderSummaryAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(orderSummaryAdapter.getOrderStatus() >= 0){
            Timber.d("getOrderStatus true");
            //subscriptions.clear();
            //finish();
        }

        TwiglyRestAPI api = TwiglyRestAPIBuilder.buildRetroService();
        subscriptions.add(NetworkRequest.performAsyncRequest(
                api.getVersionInfo(),
                info -> {
                    exitCode = info.getPassword();
                    if(info.getVersion() > BuildConfig.VERSION_CODE){
                        final Uri dlUri = Uri.parse(info.getUrl());

                        String PATH = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS) + "/" + dlUri.getLastPathSegment();
                        final Uri uri = Uri.parse("file://" + PATH);

                        //Delete update file if exists
                        File file = new File(PATH);
                        if(file.exists()) file.delete();

                        //set download manager
                        DownloadManager.Request request = new DownloadManager.Request(dlUri);

                        //set destination
                        request.setDestinationUri(uri);

                        // get download service and enqueue file
                        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        final long downloadId = manager.enqueue(request);

                        //set BroadcastReceiver to install app when .apk is downloaded
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                Intent install = new Intent(Intent.ACTION_VIEW);
                                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                install.setDataAndType(uri, "application/vnd.android.package-archive");
                                //manager.getMimeTypeForDownloadedFile(downloadId));
                                startActivity(install);
                                unregisterReceiver(this);
                                subscriptions.clear();
                                finish();
                            }
                        };

                        //register receiver for when .apk download is compete
                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }

                }, e -> {
                }
        ));

        /*if (eventReceiver== null){
            eventReceiver = new EventReceiver(data -> newOrderReceived(data));
        }
        IntentFilter intentFilter = new IntentFilter(EventType.NEW_ORDER_EVENT);
        registerReceiver(eventReceiver, intentFilter);*/
    }

    private void updateNoOrderView(){
        if (DeliveryBoy.getInstance().hasOrders()) {
            viewSwitcher.setDisplayedChild(0);
        } else {
            viewSwitcher.setDisplayedChild(1);
        }
    }

    private void newOrderReceived(String message) {
        Timber.e("mesage received");
        Gson gson = new Gson();
        try {
            OrderWrapper wrapper = gson.fromJson(message, OrderWrapper.class);
            if (wrapper != null && wrapper.order != null) {
                DeliveryBoy.getInstance().addNewOrder(wrapper.order);
                orderSummaryAdapter.notifyDataSetChanged();
                updateNoOrderView();
            }
        } catch (Exception ex) {
            Timber.e("Unable to convert the order");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

    }

    public class OrderWrapper {
        Order order;
    }

    @Override
    protected void onStop(){
        super.onStop();
        orderSummaryAdapter.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventReceiver != null) unregisterReceiver(eventReceiver);
    }

    @Override
    public void onBackPressed() {
        // these are not the droids you are looking for
    }

    // Disable volume button
    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return blockedKeys.contains(event.getKeyCode()) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                exitCodeAlert(OrderSummaryActivity.this);
                return true;

            case R.id.action_dailyorders:
                startActivity(new Intent(this, DailyOrderActivity.class));
                return true;

            case R.id.action_summary:
                startActivity(new Intent(this, DBSummaryActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void exitCodeAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Use an EditText view to get user input.
        final EditText input = new EditText(context);
        input.setGravity(Gravity.CENTER);
        input.setTextSize(40);
        input.setTextColor(getResources().getColor(R.color.black_semi_transparent_60));
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        // dynamically set input size
        input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });

        TextView title = new TextView(this);
        title.setText("Exit-Code");
        title.setTextSize(25);
        title.setGravity(Gravity.CENTER);

        builder
            .setCustomTitle(title)
            .setView(input)
            .setPositiveButton("Ok", (dialog, yes) -> {
                String code = input.getText().toString();
                Timber.d("exitCodeAlert Password: " + code);
                if(code.equalsIgnoreCase(String.valueOf(exitCode))){
                    subscriptions.clear();
                    OrderSummaryActivity.this.finishAffinity();
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(i, "Choose Launcher"));
                } else {
                    Toast.makeText(OrderSummaryActivity.this, "Incorrect Code, try again!", Toast.LENGTH_SHORT).show();
                    exitCodeAlert(context);
                }
            })
            .setNegativeButton("Cancel", (dialog, no) -> {
            });

        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alert.show();

    }

    public interface OrderRefreshCallback {
        public void orderRefreshed(boolean wasSuccess);
    }
}
