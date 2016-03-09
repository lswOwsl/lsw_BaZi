package com.example.swli.myapplication20150519.common;

/**
 * Created by swli on 5/20/2015.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.swli.myapplication20150519.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lsw.library.DatabaseManager;


public class DBManager extends DatabaseManager {

    public static final String DB_NAME = "myapplication20150519.db"; //保存的数据库文件名
    public static final String PACKAGE_NAME = "com.example.swli.myapplication20150519";
    //private final String DB_PATH = Environment
      //      .getExternalStorageDirectory().getAbsolutePath() +"/"+ PACKAGE_NAME;
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;  //在手机里存放数据库的位置(/data/data/com.cssystem.activity/cssystem.db)


    private Context context;

    public DBManager(Context context) {
        this.context = context;
    }

    public void openDatabase() {
        System.out.println(DB_PATH + "/" + DB_NAME);
        super.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String databaseFile) {

        int resourceId = R.raw.ba_zi_debug;
        if (context.getResources().getBoolean(R.bool.isRelease))
            resourceId = R.raw.ba_zi_release;

        InputStream is = this.context.getResources().openRawResource(
                resourceId); //欲导入的数据库

        return super.openDatabase(databaseFile,is);
    }

}
