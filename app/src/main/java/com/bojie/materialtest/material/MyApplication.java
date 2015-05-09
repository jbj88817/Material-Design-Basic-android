package com.bojie.materialtest.material;

import android.app.Application;
import android.content.Context;

/**
 * Created by bojiejiang on 5/2/15.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
