package lsw.lunar_calendar.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DatabaseManager;

/**
 * Created by swli on 5/5/2016.
 */
public class DataBase extends DatabaseManager {

    public List<String> hasHexagramDays(String beginDate, String endDate)
    {

        List<String> list = new ArrayList<String>();

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(CrossAppKey.DB_PATH_LIUYAO + "/" + CrossAppKey.DB_NAME,null);
        String[] params = new String[]{};

        String sql = "SELECT * FROM Hexagram where ShakeDate > '"+beginDate+"' and '"+endDate+"' > ShakeDate Order By ShakeDate DESC";
        Cursor cur = db.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            String shakeDate = getColumnValue(cur, "ShakeDate");
            list.add(shakeDate.substring(0,10));
        }

        cur.close();
        db.close();

        return list;
    }

}
