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
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import timber.log.Timber;

/**
 * Created by naresh on 02/08/16.
 */
public class DialerReceiver extends BroadcastReceiver{


    private WindowManager wm;
    public static LinearLayout ly1;
    public static boolean callDone = false;
    private WindowManager.LayoutParams params1;
    public static String savedNumber;
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
                MyApp.getContext().getContentResolver().delete(UriCalls, CallLog.Calls.NUMBER +"=?", new String[]{savedNumber});
            }
        }
    };


    @Override
    public void onReceive(Context context, Intent intent) {

        boolean showOverlay = false;
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            Timber.d("Dialed number is :"+savedNumber);
            showOverlay=true;
        } else {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) ||
                    state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                showOverlay = true;

            }
            if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    showOverlay = false;
                }
            }
        }

        // Adds a view on top of the dialer app when it launches.
        if(showOverlay ){
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

}
