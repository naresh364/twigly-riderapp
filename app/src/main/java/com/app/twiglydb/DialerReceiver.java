package com.app.twiglydb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.app.twiglydb.network.TwiglyRestAPI;
import com.app.twiglydb.network.TwiglyRestAPIBuilder;

import java.util.Date;

import timber.log.Timber;

/**
 * Created by naresh on 02/08/16.
 */
public class DialerReceiver extends BroadcastReceiver{


    private WindowManager wm;
    public static LinearLayout ly1;
    public static boolean callDone = true;
    private WindowManager.LayoutParams params1;

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing



    public static Handler removeViewHandler = new Handler();
    public static Runnable removeViewRunnable = new Runnable() {
        @Override
        public void run() {
            WindowManager wm = (WindowManager) MyApp.getContext().getSystemService(Context.WINDOW_SERVICE);
            if (ly1 != null) {
                callDone = false;
                wm.removeView(ly1);
                ly1 = null;
                String strUriCalls = "content://call_log/calls";
                Uri UriCalls = Uri.parse(strUriCalls);
                //MyApp.getContext().getContentResolver().delete(UriCalls, CallLog.Calls.NUMBER +"=?", new String[]{savedNumber});
            }
        }
    };

    TwiglyRestAPI api;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (api == null) api = TwiglyRestAPIBuilder.buildRetroService();

        boolean showOverlay = false;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Timber.d("Dialed number is :"+savedNumber);
            showOverlay=true;
        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
                showOverlay = true;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
                showOverlay = true;
            }

            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    showOverlay = false;
                }
            }

            onCallStateChanged(context, state, number);
        }

        // Adds a view on top of the dialer app when it launches.
        if(showOverlay  && false){//disabled temporary
            if (ly1 != null) return;
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            params1 = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            params1.gravity = Gravity.TOP;

            params1.height = Utils.dpToPx(300);
            params1.width = Utils.dpToPx(512);
            params1.x = 0;
            params1.y = 0;
            params1.format = PixelFormat.TRANSLUCENT;

            ly1 = new LinearLayout(context);
            ly1.setBackgroundColor(Color.DKGRAY);
            ly1.setOrientation(LinearLayout.VERTICAL);
            ly1.setBaselineAligned(true);

            wm.addView(ly1, params1);
        } else {
            callDone = true;
            removeViewHandler.postDelayed(removeViewRunnable, 7500);
        }
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                api.dbCalledStarted(callStartTime.getTime(), savedNumber, "ring");
                Log.d("call", savedNumber+", start ring");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    api.dbCalledStarted(callStartTime.getTime(), savedNumber, "out");
                    Log.d("call", savedNumber+", start out");
                }
                else
                {
                    isIncoming = true;
                    callStartTime = new Date();
                    api.dbCalledStarted(callStartTime.getTime(), savedNumber, "in");
                    Log.d("call", savedNumber+", start in");
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    api.dbCalledStarted(callStartTime.getTime(), savedNumber, "missed");
                    Log.d("call", savedNumber+", start missed");
                }
                else if(isIncoming){
                    api.dbCalledEnded(callStartTime.getTime(), savedNumber, "in");
                    Log.d("call", savedNumber+", end in");
                }
                else{
                    api.dbCalledEnded(callStartTime.getTime(), savedNumber, "out");
                    Log.d("call", savedNumber+", end out");
                }
                break;
        }
        lastState = state;
    }

}
