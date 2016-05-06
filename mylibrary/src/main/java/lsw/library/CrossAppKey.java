package lsw.library;

import android.os.Environment;

/**
 * Created by swli on 8/25/2015.
 */
public class CrossAppKey {

    public final static String DateTime = "lunar_calendar_dateTime";
    public final static String HexagramId = "hexagram_id";

    public static final String DB_NAME_HEXAGRAM_NOTE = "liuYaoHexagramNote.db";
    public static final String DB_NAME = "liuYao.db";
    public static final String PACKAGE_NAME_LIUYAO = "lsw.liuyao";
//    public static final String DB_PATH = "/data"
//            + Environment.getDataDirectory().getAbsolutePath() + "/"
//            + PACKAGE_NAME;

    public static final String DB_PATH_LIUYAO = Environment.getExternalStorageDirectory() + "/" + PACKAGE_NAME_LIUYAO;
}
