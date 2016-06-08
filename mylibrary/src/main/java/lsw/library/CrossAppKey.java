package lsw.library;

import android.os.Environment;

/**
 * Created by swli on 8/25/2015.
 */
public class CrossAppKey {

    public final static String RequestInfo = "request_info";

    public final static String MemberId = "member_id";

    public final static String DateTime = "lunar_calendar_dateTime";
    public final static String HexagramId = "hexagram_id";

    public static final String DB_NAME_HEXAGRAM_NOTE = "liuYaoHexagramNote.db";
    public static final String DB_NAME_LIUYAO = "liuYao.db";
    public static final String PACKAGE_NAME_LIUYAO = "lsw.liuyao";
    public static final String DB_PATH_LIUYAO = Environment.getExternalStorageDirectory() + "/" + PACKAGE_NAME_LIUYAO;

    public static final String DB_NAME_BAZI = "bazi.db"; //保存的数据库文件名
    public static final String PACKAGE_NAME_BAZI = "lsw.bazi";
    public static final String DB_PATH_BAZI = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ PACKAGE_NAME_BAZI;
//    public static final String DB_PATH_BAZI = "/data"
//            + Environment.getDataDirectory().getAbsolutePath() + "/"
//            + PACKAGE_NAME_BAZI;  //在手机里存放数据库的位置(/data/data/com.cssystem.activity/cssystem.db)

    public static final String DB_NAME_CALENDAR = "calendar.db";
    public static final String PACKAGE_NAME_CALENDAR = "lsw.calendar";
    public static final String DB_PATH_CALENDAR = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + PACKAGE_NAME_CALENDAR;

}
