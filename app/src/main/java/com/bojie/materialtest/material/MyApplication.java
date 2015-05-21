package com.bojie.materialtest.material;

import android.app.Application;
import android.content.Context;

import com.bojie.materialtest.database.MoviesDatabase;

/**
 * Created by bojiejiang on 5/2/15.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;
    public static final String API_KEY_ROTTEN_TOMATOES = "e4hvtyb5vhpd7wq24kj4dn35";
    private static MoviesDatabase sMoviesDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public synchronized static MoviesDatabase getWritableDatabase() {
        if (sMoviesDatabase == null) {
            sMoviesDatabase = new MoviesDatabase(getAppContext());
        }

        return sMoviesDatabase;
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
}
