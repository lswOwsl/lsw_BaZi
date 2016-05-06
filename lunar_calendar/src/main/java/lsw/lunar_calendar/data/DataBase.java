package lsw.lunar_calendar.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DatabaseManager;
import lsw.lunar_calendar.model.HexagramDataRow;

/**
 * Created by swli on 5/5/2016.
 */
public class DataBase extends DatabaseManager {

    SQLiteDatabase db;

    public void openDatabase()
    {
        db = SQLiteDatabase.openOrCreateDatabase(CrossAppKey.DB_PATH_LIUYAO + "/" + CrossAppKey.DB_NAME,null);
    }

    public List<String> hasHexagramDays(String beginDate, String endDate)
    {
        openDatabase();
        List<String> list = new ArrayList<String>();
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

    public List<HexagramDataRow> getHexagramDataRowByDate(String date)
    {
        openDatabase();

        List<HexagramDataRow> list = new ArrayList<HexagramDataRow>();
        String[] params = new String[]{};

        String sql = "SELECT * FROM Hexagram where strftime('%Y-%m-%d',shakedate) = '"+date+"'";
        Cursor cur = db.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            list.add(createHexagramRowByCursor(cur));
        }

        cur.close();
        db.close();

        return list;
    }

    HexagramDataRow createHexagramRowByCursor(Cursor cursor)
    {
        int idIndex = cursor.getColumnIndex("Id");
        int id = cursor.getInt(idIndex);

        String originalName = getColumnValue(cursor, "OriginalName");
        String changedName = getColumnValue(cursor,"ChangedName");
        String note = getColumnValue(cursor, "Note");

        //String shakeDate = getColumnValue(cursor, "ShakeDate");

        HexagramDataRow hexagramRow = new HexagramDataRow();
        hexagramRow.setOriginalName(originalName);
        hexagramRow.setChangedName(changedName);
        hexagramRow.setNote(note);
        hexagramRow.setId(id);

        return hexagramRow;
    }
}
