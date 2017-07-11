package com.app.twiglydb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naresh on 14/01/16.
 */
public class Utils {
    public static boolean mayRequestPermission(final Context mContext, final String permission) {
        String permissions[] = {permission};
        return mayRequestPermission(mContext, permissions);

    }

    public static boolean mayRequestPermission(final Context mContext, final String[] permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean allok = true;
        List<String> tobetaken = new ArrayList<>();
        for (String p : permission) {
            if (mContext.checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                allok = false;
                tobetaken.add(p);
            }
        }
        String[] finalpermissions = tobetaken.toArray(new String[tobetaken.size()]);

        if (allok == true) return true;
        if (!(mContext instanceof Activity)) {
            return false;
        }

        ActivityCompat.requestPermissions((Activity) mContext,
                finalpermissions,
                0);
        return false;
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
