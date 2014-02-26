package com.blogspot.vsvydenko.yafotki_muzei;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vsvydenko on 26.02.14.
 */
public class Utils {

    public static String LOG_TAG = "----";

    public static boolean isWiFiOn (Context mContext) {
        if (mContext != null) {
            ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
    }
}
