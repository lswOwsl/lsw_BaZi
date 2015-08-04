package lsw.lunar_calendar.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import lsw.library.DateExt;
import lsw.library.LunarCalendar;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class LunarDateSelectorDialog {

    interface ICallBack
    {
        void invoke(DateExt dateExt);
    }

    private ICallBack callBack;
    public void setCallBack(ICallBack callBack)
    {
        this.callBack = callBack;
    }

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker dayPicker;

    private AlertDialog ad;
    private Activity activity;

    private int initYear, initMonth, initDay;

    private static List<String> yearlist = new ArrayList<String>();
    private static List<String> monthList = new ArrayList<String>();
    private static List<String> dayList = new ArrayList<String>();

    LunarCalendar lunarCalendar = new LunarCalendar();

    public LunarDateSelectorDialog(int year, int month, int day, Activity activity)
    {
        this.initYear = year;
        this.initMonth = month;
        this.initDay = day;

        this.activity = activity;
    }

    public List<String> getYearList()
    {
        if(yearlist.size() == 0 ) {
            for (int i = 0; i < LunarCalendar.lunarDateArray.length; i++) {
                yearlist.add(lunarCalendar.toStringWithChineseYear(LunarCalendar.minYear + i)+"年");
            }
        }
        return yearlist;
    }

    private List<String> getMonthList(int initYear) {
        monthList.clear();
        for (int i = 1; i < 13; i++) {
            monthList.add(lunarCalendar.toStringWithChineseMonth(i) + "月");
            int tempMonth = lunarCalendar.getChineseLeapMonth(initYear);
            if (tempMonth != 0)
                monthList.add("闰" + lunarCalendar.toStringWithChineseMonth(tempMonth) + "月");
        }
        return monthList;
    }

    private List<String> getDayList(int initYear, int initMonth)
    {
        int tempDays = lunarCalendar.getChineseMonthDays(initYear, initMonth);
        for(int i=1;i<=tempDays;i++) {
            dayList.add(lunarCalendar.toStringWithChineseDay(i));
        }
        return dayList;
    }
}

