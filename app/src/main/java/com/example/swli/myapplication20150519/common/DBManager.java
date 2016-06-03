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

import lsw.library.CrossAppKey;
import lsw.library.DatabaseManager;
import lsw.utility.FileHelper;


public class DBManager extends DatabaseManager {


    private Context context;

    public DBManager(Context context) {
        this.context = context;
    }

    public void openDatabase() {
        System.out.println(CrossAppKey.DB_PATH_BAZI + "/" + CrossAppKey.DB_NAME_BAZI);
        super.database = this.openDatabase(CrossAppKey.DB_PATH_BAZI + "/" + CrossAppKey.DB_NAME_BAZI);
    }

    private SQLiteDatabase openDatabase(String databaseFile) {

        int resourceId = R.raw.ba_zi_debug;
        if (context.getResources().getBoolean(R.bool.isRelease))
            resourceId = R.raw.ba_zi_release;

        InputStream is = this.context.getResources().openRawResource(
                resourceId); //欲导入的数据库

        FileHelper.createFolder(CrossAppKey.DB_PATH_BAZI);
        return super.openDatabase(databaseFile,is);
    }

}
