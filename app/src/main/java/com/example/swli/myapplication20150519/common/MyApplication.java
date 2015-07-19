package com.example.swli.myapplication20150519.common;

import android.app.Application;

/**
 * Created by lsw_wsl on 7/18/15.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
    }
}
