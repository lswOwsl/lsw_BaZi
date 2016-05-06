package lsw.library;

import android.os.Environment;

/**
 * Created by swli on 8/25/2015.
 */
public class CrossAppKey {

    public final static String DateTime = "lunar_calendar_dateTime";
    public final static String HexagramId = "hexagram_id";

    public static final String DB_NAME_HEXAGRAM_NOTE = "liuYaoHexagramNote.db";
    public static final String DB_NAME_LIUYAO = "liuYao.db";
    public static final String PACKAGE_NAME_LIUYAO = "lsw.liuyao";
//    public static final String DB_PATH = "/data"
//            + Environment.getDataDirectory().getAbsolutePath() + "/"
//            + PACKAGE_NAME;

    public static final String DB_PATH_LIUYAO = Environment.getExternalStorageDirectory() + "/" + PACKAGE_NAME_LIUYAO;


    public static final String DB_NAME_BAZI = "myapplication20150519.db"; //保存的数据库文件名
    public static final String PACKAGE_NAME_BAZI = "com.example.swli.myapplication20150519";
    //private final String DB_PATH = Environment
    //      .getExternalStorageDirectory().getAbsolutePath() +"/"+ PACKAGE_NAME;
    public static final String DB_PATH_BAZI = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME_BAZI;  //在手机里存放数据库的位置(/data/data/com.cssystem.activity/cssystem.db)



}
