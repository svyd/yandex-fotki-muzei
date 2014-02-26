package com.blogspot.vsvydenko.yafotki_muzei;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by vsvydenko on 26.02.14.
 */
public class PreferenceHelper {

    public static String SHARED_PREF_INTERVAL   = "SHARED_PREF_INTERVAL";
    public static String SHARED_PREF_SOURCE_URL = "SHARED_PREF_SOURCE_URL";

    public static String UPDATE_ON_WIFI_ONLY = "UPDATE_ON_WIFI_ONLY";

    static Context mContext = null;
    static SharedPreferences prefs = null;


    private PreferenceHelper() {}

    public static void init(Context applicationContext) {
        if (mContext != null) {
            return;
        }

        mContext = applicationContext;
        prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }

    private static void updatePref(String key, Boolean value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(key, value);

        if (Utils.hasGingerbread()) {
            edit.apply();
        } else {
            if (!edit.commit()) {
                Log.w(Utils.LOG_TAG, "preference save failed!");
            }
        }
    }

    private static void updatePref(String key, int value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(key, value);

        if (Utils.hasGingerbread()) {
            edit.apply();
        } else {
            if (!edit.commit()) {
                Log.w(Utils.LOG_TAG, "preference save failed!");
            }
        }
    }

    private static void updatePref(String key, String value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);

        if (Utils.hasGingerbread()) {
            edit.apply();
        } else {
            if (!edit.commit()) {
                Log.w(Utils.LOG_TAG, "preference save failed!");
            }
        }
    }

    public static void setInterval(int interval) {
        updatePref(SHARED_PREF_INTERVAL, interval);
    }

    public static int getInterval() {
        return prefs.getInt(SHARED_PREF_INTERVAL, SettingsActivity.THREE_HOURS);
    }

    public static void setWifiChecked(Boolean value) {
        updatePref(UPDATE_ON_WIFI_ONLY, value);
    }

    public static boolean isWiFiChecked() {
        return prefs.getBoolean(UPDATE_ON_WIFI_ONLY, false);
    }

    public static void setSourceUrl(String sourceUrl) {
        updatePref(SHARED_PREF_SOURCE_URL, sourceUrl);
    }

    public static String getSourceUrl() {
        return prefs.getString(SHARED_PREF_SOURCE_URL, YandexFotkiArtSource.POD);
    }
}
