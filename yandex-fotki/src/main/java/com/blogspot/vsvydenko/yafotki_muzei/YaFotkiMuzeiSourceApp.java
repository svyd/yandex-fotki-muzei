package com.blogspot.vsvydenko.yafotki_muzei;

import android.app.Application;

/**
 * Created by vsvydenko on 26.02.14.
 */
public class YaFotkiMuzeiSourceApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceHelper.init(this);
    }
}
