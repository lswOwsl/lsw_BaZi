package lsw.liuyao.common;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    public void setSearchCondition(String condition) {
        sharedPreferencesEditor.putString("SearchCondition", condition);
        sharedPreferencesEditor.commit();
    }

    public String getSearchCondition() {
        String s = sharedPreferences.getString("SearchCondition", null);
        if (s != null) {
           return s;
        } else {
            return "";
        }
    }
}
