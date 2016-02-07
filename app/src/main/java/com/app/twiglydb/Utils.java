package com.app.twiglydb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * Created by naresh on 14/01/16.
 */
public class Utils {
    public static boolean mayRequestPermission(final Context mContext, final String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (!(mContext instanceof Activity)) {
            return false;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permission)) {
                Snackbar.make(((Activity) mContext).getCurrentFocus(),
                        R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions((Activity) mContext,
                                        new String[]{permission},
                                        0);
                            }
                        });
        } else {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{permission},
                    0);
        }
        return false;
    }
}
