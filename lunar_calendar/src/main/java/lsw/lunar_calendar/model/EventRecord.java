package lsw.lunar_calendar.model;

import android.database.Cursor;

import lsw.library.StringHelper;
import lsw.lunar_calendar.common.RecordType;

/**
 * Created by swli on 6/2/2016.
 */
public class EventRecord {

    public static final String TB_EventRecord = "EventRecord";
    public static final String DF_Id = "Id";
    public static final String DF_BeginTime = "BeginTime";
    public static final String DF_EndTime = "EndTime";
    public static final String DF_ActualResult = "ActualResult";
    public static final String DF_AnalyzeResult = "AnalyzeResult";
    public static final String DF_RecordCycle = "RecordCycle";
    public static final String DF_LunarTime = "LunarTime";

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String beginTime;
    private String endTime;
    private String analyzeResult;
    private String actualResult;
    private String recordCycle;
    private String lunarTime;
    private RecordType recordType;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAnalyzeResult() {
        return analyzeResult;
    }

    public void setAnalyzeResult(String analyzeResult) {
        this.analyzeResult = analyzeResult;
    }

    public String getActualResult() {
        return actualResult;
    }

    public void setActualResult(String actualResult) {
        this.actualResult = actualResult;
    }

    public String getRecordCycle() {
        return recordCycle;
    }

    public void setRecordCycle(String recordCycle) {
        this.recordCycle = recordCycle;
    }

    public String getLunarTime() {
        return lunarTime;
    }

    public void setLunarTime(String lunarTime) {
        this.lunarTime = lunarTime;
    }

    public RecordType getRecordType()
    {
        if(analyzeResult.isEmpty() && !actualResult.isEmpty())
            return RecordType.Note;
        else
            return RecordType.All;
    }

    public EventRecord()
    {

    }

    public EventRecord(Cursor cursor)
    {
        setId(cursor.getInt(cursor.getColumnIndex(DF_Id)));
        setBeginTime(cursor.getString(cursor.getColumnIndex(DF_BeginTime)));
        setEndTime(cursor.getString(cursor.getColumnIndex(DF_EndTime)));
        setActualResult(cursor.getString(cursor.getColumnIndex(DF_ActualResult)));
        setAnalyzeResult(cursor.getString(cursor.getColumnIndex(DF_AnalyzeResult)));
        setRecordCycle(cursor.getString(cursor.getColumnIndex(DF_RecordCycle)));
        setLunarTime(cursor.getString(cursor.getColumnIndex(DF_LunarTime)));
    }
}
