package lsw.library;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import lsw.utility.FileHelper;

/**
 * Created by swli on 8/3/2015.
 */
public class DatabaseManager {

    protected final int BUFFER_SIZE = 400000;

    protected SQLiteDatabase database;

    public DatabaseManager() {

    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    protected SQLiteDatabase openDatabase(String databaseFile, InputStream resource) {

        try {
            if (!(new File(databaseFile).exists())) {
                //判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = resource; //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(databaseFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFile,null);
            return db;

        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return null;
    }

    public void closeDatabase() {
        this.database.close();
    }

    public Cursor execute(String sql,String[] args)
    {
        Cursor cur = database.rawQuery(sql, args);
        return cur;
    }

    public String getColumnValue(Cursor cursor,String columnName)
    {
        int commentIndex = cursor.getColumnIndex(columnName);
        return StringHelper.getString(cursor.getString(commentIndex));
    }

    public int getColumnIntValue(Cursor cursor,String columnName)
    {
        int commentIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(commentIndex);
    }

    public double getColumnDoubleValue(Cursor cursor,String columnName)
    {
        int commentIndex = cursor.getColumnIndex(columnName);
        return cursor.getDouble(commentIndex);
    }
}