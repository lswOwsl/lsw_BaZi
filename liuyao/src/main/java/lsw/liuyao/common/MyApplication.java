package lsw.liuyao.common;

import android.app.Application;

/**
 * Created by swli on 3/22/2016.
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
