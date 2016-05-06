package lsw.lunar_calendar.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DatabaseManager;
import lsw.library.DateExt;
import lsw.lunar_calendar.model.HexagramDataRow;
import lsw.lunar_calendar.model.MemberDataRow;

/**
 * Created by swli on 5/5/2016.
 */
public class DataBase extends DatabaseManager {

    SQLiteDatabase dbLiuYao;
    SQLiteDatabase dbBaZi;

    public void openDatabaseLiuYao()
    {
        dbLiuYao = SQLiteDatabase.openOrCreateDatabase(CrossAppKey.DB_PATH_LIUYAO + "/" + CrossAppKey.DB_NAME_LIUYAO,null);
    }

    public void openDatabaseBaZi()
    {
        dbBaZi = SQLiteDatabase.openOrCreateDatabase(CrossAppKey.DB_PATH_BAZI + "/" + CrossAppKey.DB_NAME_BAZI,null);
    }

    public List<String> hasHexagramDays(String beginDate, String endDate)
    {
        openDatabaseLiuYao();
        List<String> list = new ArrayList<String>();
        String[] params = new String[]{};

        String sql = "SELECT * FROM Hexagram where ShakeDate > '"+beginDate+"' and '"+endDate+"' > ShakeDate Order By ShakeDate DESC";
        Cursor cur = dbLiuYao.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            String shakeDate = getColumnValue(cur, "ShakeDate");
            list.add(shakeDate.substring(0,10));
        }

        cur.close();
        dbLiuYao.close();

        return list;
    }

    public List<HexagramDataRow> getHexagramDataRowByDate(String date)
    {
        openDatabaseLiuYao();

        List<HexagramDataRow> list = new ArrayList<HexagramDataRow>();
        String[] params = new String[]{};

        String sql = "SELECT * FROM Hexagram where strftime('%Y-%m-%d',shakedate) = '"+date+"'";
        Cursor cur = dbLiuYao.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            list.add(createHexagramRowByCursor(cur));
        }

        cur.close();
        dbLiuYao.close();

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

    public List<MemberDataRow> getMonthBirthday(String monthDay)
    {
        openDatabaseBaZi();

        List<MemberDataRow> list = new ArrayList<MemberDataRow>();
        String[] params = new String[]{};

        String sql = "SELECT * FROM Members where strftime('%m-%d',Birthday_Refactor) = '"+monthDay+"' Order By Birthday_Refactor ASC";
        Cursor cur = dbBaZi.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            list.add(createMemberDataRowByCursor(cur));
        }

        cur.close();
        dbBaZi.close();

        return list;
    }

    MemberDataRow createMemberDataRowByCursor(Cursor cur)
    {
        int idIndex = cur.getColumnIndex("Id");
        int nameIndex = cur.getColumnIndex("Name");
        int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
        int isMaleIndex = cur.getColumnIndex("IsMale");

        String name = cur.getString(nameIndex);
        String birthdayStr = cur.getString(birthdayIndex);
        String isMale = cur.getString(isMaleIndex);
        DateExt birthdayDE = new DateExt(birthdayStr, "yyyy-MM-dd HH:mm:ss");
        int isMaleI = Integer.parseInt(isMale);
        int id = cur.getInt(idIndex);

        MemberDataRow member = new MemberDataRow();
        member.setId(id);
        member.setName(name.trim());
        member.setIsMale(isMaleI == 1 ? true : false);
        member.setBirthday(birthdayDE);

        return member;
    }
}
