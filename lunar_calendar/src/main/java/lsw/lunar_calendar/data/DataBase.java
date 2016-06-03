package lsw.lunar_calendar.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DatabaseManager;
import lsw.library.DateExt;
import lsw.library.DateLunar;
import lsw.library.LunarCalendarWrapper;
import lsw.lunar_calendar.R;
import lsw.lunar_calendar.common.MyApplication;
import lsw.lunar_calendar.model.EventRecord;
import lsw.lunar_calendar.model.HexagramDataRow;
import lsw.lunar_calendar.model.MemberDataRow;
import lsw.utility.FileHelper;

/**
 * Created by swli on 5/5/2016.
 */
public class DataBase extends DatabaseManager {

    SQLiteDatabase dbLiuYao;
    SQLiteDatabase dbBaZi;
    SQLiteDatabase dbCalendar;

    public void openDataBaseCalendar()
    {
        int resourceId = R.raw.calendar_recorder;
        InputStream is = MyApplication.getInstance().getResources().openRawResource(resourceId);
        FileHelper.createFolder(CrossAppKey.DB_PATH_CALENDAR);
        dbCalendar = super.openDatabase(CrossAppKey.DB_PATH_CALENDAR + "/" + CrossAppKey.DB_NAME_CALENDAR, is);
    }

    public void openDatabaseLiuYao()
    {
        dbLiuYao = SQLiteDatabase.openOrCreateDatabase(CrossAppKey.DB_PATH_LIUYAO + "/" + CrossAppKey.DB_NAME_LIUYAO, null);
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

    public EventRecord getEventRecordById(int id) {
        openDataBaseCalendar();
        String[] params = new String[]{id + ""};
        String sql = "SELECT * FROM " + EventRecord.TB_EventRecord + " where Id = ?";
        Cursor cur = dbCalendar.rawQuery(sql, params);
        EventRecord eventRecord = null;
        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            eventRecord = new EventRecord(cur);
            break;
        }
        dbCalendar.close();
        return eventRecord;
    }

    private static final String DateFormater = "yyyy-MM-dd";

    public ArrayList<EventRecord> getEventRecordBySql(String sql)
    {
        openDataBaseCalendar();
        ArrayList<EventRecord> list = new ArrayList<EventRecord>();
        String[] params = new String[]{};
        Cursor cur = dbCalendar.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            list.add(new EventRecord(cur));
        }

        cur.close();
        dbCalendar.close();
        return list;
    }

    public void deleteEventRecordById(int id)
    {
        openDataBaseCalendar();
        dbCalendar.delete(EventRecord.TB_EventRecord, "Id ='" + id + "'", null);
        dbCalendar.close();
    }

    public ArrayList<EventRecord> getEventRecords() {

        String sql = "select * from "+EventRecord.TB_EventRecord+" order by ID desc";
        return getEventRecordBySql(sql);
    }

    public ArrayList<EventRecord> getEventRecordByMonth(DateExt date) {
        String condition = date.getFormatDateTime("yyyy-MM");
        String sql = "SELECT * FROM " + EventRecord.TB_EventRecord + " where " +
                "strftime('%Y-%m'," + EventRecord.DF_BeginTime + ") = '" + condition + "' and " +
                "strftime('%Y-%m'," + EventRecord.DF_EndTime + ") = '" + condition + "'";

        return getEventRecordBySql(sql);
    }

    public ArrayList<EventRecord> getEventRecordByWeek(DateExt date)
    {
        DateExt begin = new DateExt(date.getDate()).getThisWeekMonday();
        DateExt end = new DateExt(begin.getDate()).addDays(7);
        String sql = "SELECT * FROM "+EventRecord.TB_EventRecord+" where " +
                "strftime('%Y-%m-%d',"+EventRecord.DF_BeginTime+") >= '"+begin.getFormatDateTime(DateFormater)+"' and " +
                "strftime('%Y-%m-%d',"+EventRecord.DF_EndTime+") <= '"+end.getFormatDateTime(DateFormater)+"'";

        return getEventRecordBySql(sql);
    }


    public ArrayList<EventRecord> getEventRecordByDay(DateExt date)
    {
        String sql = "SELECT * FROM "+EventRecord.TB_EventRecord+" where " +
                "strftime('%Y-%m-%d',"+EventRecord.DF_BeginTime+") = '"+date.getFormatDateTime(DateFormater)+"' and " +
                "strftime('%Y-%m-%d',"+EventRecord.DF_EndTime+") = '"+date.getFormatDateTime(DateFormater)+"'";

        return getEventRecordBySql(sql);
    }



    public EventRecord saveEventRecord(EventRecord model)
    {
        openDataBaseCalendar();

        if(model.getId() == 0)
        {
            ContentValues cv = new ContentValues();
            cv.put(EventRecord.DF_BeginTime, model.getBeginTime());
            cv.put(EventRecord.DF_EndTime, model.getEndTime());
            cv.put(EventRecord.DF_AnalyzeResult, model.getAnalyzeResult());
            cv.put(EventRecord.DF_ActualResult, model.getActualResult());
            cv.put(EventRecord.DF_RecordCycle, model.getRecordCycle());
            cv.put(EventRecord.DF_LunarTime, model.getLunarTime());
            dbCalendar.insert(EventRecord.TB_EventRecord, null, cv);

            String sql = "select * from "+EventRecord.TB_EventRecord+" order by ID desc limit 1";
            return getEventRecordBySql(sql).get(0);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(EventRecord.DF_ActualResult, model.getActualResult());
            values.put(EventRecord.DF_AnalyzeResult, model.getAnalyzeResult());
            String whereClause = EventRecord.DF_Id + "=?";
            String[] whereArgs = {String.valueOf(model.getId())};
            dbCalendar.update(EventRecord.TB_EventRecord, values, whereClause, whereArgs);

        }
        dbCalendar.close();
        return model;
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
                memberDataRow.setIsLunarBirthday(true);
                list.add(memberDataRow);
            }
        }

        cur.close();
        dbBaZi.close();

        return list;
    }

    public List<MemberDataRow> getLunarBithdayDataRowsByMonth(DateExt dateExt)
    {
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        DateExt begin = new DateExt(dateExt.getYear(),dateExt.getMonth(),1,0,0,0);
        DateExt end = new DateExt(begin.getDate()).addMonths(1).addDays(-1);

        ArrayList<DateLunar> listLunar = new ArrayList<DateLunar>();
        for(int i= begin.getDay(); i<=end.getDay(); i++)
        {
            DateLunar dateLunar = lunarCalendarWrapper.getDateLunar(new DateExt(begin.getDate()).addDays(i));
            listLunar.add(dateLunar);
        }

        openDatabaseBaZi();

        List<MemberDataRow> list = new ArrayList<MemberDataRow>();
        String[] params = new String[]{};

        String beforeMonth = new DateExt(dateExt.getDate()).addMonths(-1).getFormatDateTime("MM");
        String currentMonth = dateExt.getFormatDateTime("MM");
        String afterMonth = new DateExt(dateExt.getDate()).addMonths(1).getFormatDateTime("MM");

        String sql = "SELECT * FROM Members where strftime('%m',Birthday_Refactor) in ('"+beforeMonth+"','"+currentMonth+"','"+afterMonth+"') Order By Birthday_Refactor ASC";
        Cursor cur = dbBaZi.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
            String birthdayStr = cur.getString(birthdayIndex);
            DateExt tempDateExt = new DateExt(birthdayStr, "yyyy-MM-dd HH:mm:ss");
            DateLunar tempDateLunar = lunarCalendarWrapper.getDateLunar(tempDateExt);

            for(DateLunar dateLunar : listLunar) {
                if (dateLunar.getLunarDay() == tempDateLunar.getLunarDay() &&
                        dateLunar.getLunarMonth() == tempDateLunar.getLunarMonth() &&
                        dateLunar.getIsLeapMonth() == tempDateLunar.getIsLeapMonth()) {
                    MemberDataRow memberDataRow = createMemberDataRowByCursor(cur);
                    memberDataRow.setIsLunarBirthday(true);
                    list.add(memberDataRow);
                }
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
            list.add(birthdayStr.substring(5, 10));
        }

        cur.close();
        dbBaZi.close();

        return list;
    }

    public List<MemberDataRow> getBirthdayDataRowsByMonth(String month)
    {
        openDatabaseBaZi();

        List<MemberDataRow> list = new ArrayList<MemberDataRow>();
        String[] params = new String[]{};

        String sql = "SELECT * FROM Members where strftime('%m',Birthday_Refactor) = '"+month+"' Order By Birthday_Refactor ASC";
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
