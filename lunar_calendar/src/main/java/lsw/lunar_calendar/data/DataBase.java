package lsw.lunar_calendar.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DatabaseManager;
import lsw.library.DateExt;
import lsw.library.DateLunar;
import lsw.library.LunarCalendarWrapper;
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

    public void setBaZiDatabase(SQLiteDatabase sqLiteDatabase)
    {
        dbBaZi = sqLiteDatabase;
    }

    public void openDatabaseBaZi()
    {
        try {

            dbBaZi = SQLiteDatabase.openOrCreateDatabase(CrossAppKey.DB_PATH_BAZI + "/" + CrossAppKey.DB_NAME_BAZI, null);
        }
        catch (Exception ex)
        {
            Log.d("database open", ex.getMessage());
        }
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

    public List<MemberDataRow> getBirthdayByDay(String day)
    {
        openDatabaseBaZi();

        List<MemberDataRow> list = new ArrayList<MemberDataRow>();
        String[] params = new String[]{};

        String sql = "SELECT * FROM Members where strftime('%m-%d',Birthday_Refactor) = '"+day+"' Order By Birthday_Refactor ASC";
        Cursor cur = dbBaZi.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            list.add(createMemberDataRowByCursor(cur));
        }

        cur.close();
        dbBaZi.close();

        return list;
    }

    public List<MemberDataRow> getLunarBithdayByDay(DateExt dateExt)
    {
        openDatabaseBaZi();

        List<MemberDataRow> list = new ArrayList<MemberDataRow>();
        String[] params = new String[]{};

        String beforeMonth = new DateExt(dateExt.getDate()).addMonths(-1).getFormatDateTime("MM");
        String currentMonth = dateExt.getFormatDateTime("MM");
        String afterMonth = new DateExt(dateExt.getDate()).addMonths(1).getFormatDateTime("MM");
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        DateLunar dateLunar = lunarCalendarWrapper.getDateLunar();

        String sql = "SELECT * FROM Members where strftime('%m',Birthday_Refactor) in ('"+beforeMonth+"','"+currentMonth+"','"+afterMonth+"') Order By Birthday_Refactor ASC";
        Cursor cur = dbBaZi.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
            String birthdayStr = cur.getString(birthdayIndex);
            DateExt tempDateExt = new DateExt(birthdayStr, "yyyy-MM-dd HH:mm:ss");
            DateLunar tempDateLunar = lunarCalendarWrapper.getDateLunar(tempDateExt);
            if(dateLunar.getLunarDay() == tempDateLunar.getLunarDay() &&
                    dateLunar.getLunarMonth() == tempDateLunar.getLunarMonth() &&
                    dateLunar.getIsLeapMonth() == tempDateLunar.getIsLeapMonth())
            {
                MemberDataRow memberDataRow = createMemberDataRowByCursor(cur);
                list.add(memberDataRow);
            }
        }

        cur.close();
        dbBaZi.close();

        return list;
    }


    public List<DateLunar> getLunarBithdayByMonth(DateExt dateExt)
    {
        openDatabaseBaZi();

        List<DateLunar> list = new ArrayList<DateLunar>();
        String[] params = new String[]{};

        String beforeMonth = new DateExt(dateExt.getDate()).addMonths(-1).getFormatDateTime("MM");
        String currentMonth = dateExt.getFormatDateTime("MM");
        String afterMonth = new DateExt(dateExt.getDate()).addMonths(1).getFormatDateTime("MM");

        String sql = "SELECT * FROM Members where strftime('%m',Birthday_Refactor) in ('"+beforeMonth+"','"+currentMonth+"','"+afterMonth+"') Order By Birthday_Refactor ASC";
        Cursor cur = dbBaZi.rawQuery(sql,params);

        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
            String birthdayStr = cur.getString(birthdayIndex);
            DateExt tempDateExt = new DateExt(birthdayStr, "yyyy-MM-dd HH:mm:ss");
            DateLunar tempDateLunar = lunarCalendarWrapper.getDateLunar(tempDateExt);

            list.add(tempDateLunar);
        }

        cur.close();
        dbBaZi.close();

        return list;
    }

    public List<String> getBirthdayByMonth(String month)
    {
        openDatabaseBaZi();

        List<String> list = new ArrayList<String>();
        String[] params = new String[]{};

        String sql = "SELECT * FROM Members where strftime('%m',Birthday_Refactor) = '"+month+"' Order By Birthday_Refactor ASC";
        Cursor cur = dbBaZi.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
            String birthdayStr = cur.getString(birthdayIndex);
            list.add(birthdayStr.substring(5,10));
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
