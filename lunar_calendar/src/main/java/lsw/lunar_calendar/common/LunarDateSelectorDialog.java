package lsw.lunar_calendar.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import lsw.library.DateExt;
import lsw.library.DateLunar;
import lsw.library.LunarCalendar;
import lsw.library.LunarCalendarWrapper;
import lsw.lunar_calendar.R;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class LunarDateSelectorDialog {

    public interface ICallBack
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
    private boolean isLeapMonth;

    private static List<String> monthList = new ArrayList<String>();
    private static List<String> dayList = new ArrayList<String>();

    LunarCalendarWrapper lunarCalendar;

    private DateExt initDateExt;

    public LunarDateSelectorDialog(DateExt dateExt, Activity activity)
    {
        lunarCalendar = new LunarCalendarWrapper(dateExt);
        DateLunar dateLunar = lunarCalendar.getDateLunar();
        this.initYear = dateLunar.getLunarYear();
        this.initMonth = dateLunar.getLunarMonth();
        this.initDay = dateLunar.getLunarDay();
        this.isLeapMonth = dateLunar.getIsLeapMonth();
        this.initDateExt = dateExt;
        this.activity = activity;
    }

    static List<String> lunarYearTextList;
    static List<Integer> lunarYearValueList;

    public Pair<List<Integer>,List<String>> getYearList()
    {
        if(lunarYearTextList == null && lunarYearValueList == null) {
            lunarYearTextList = new ArrayList<String>();
            lunarYearValueList = new ArrayList<Integer>();

            for (int i = 0; i < LunarCalendar.lunarDateArray.length; i++) {
                Integer yearValue = LunarCalendar.minYear + i;
                String yearText = lunarCalendar.toStringWithChineseYear(yearValue) + "年";
                lunarYearValueList.add(yearValue);
                lunarYearTextList.add(yearText);
            }
        }
        return Pair.create(lunarYearValueList,lunarYearTextList);
    }

    private List<String> getMonthList(int initYear) {
        monthList.clear();
        int tempMonth = lunarCalendar.getChineseLeapMonth(initYear);
        for (int i = 1; i < 13; i++) {
            monthList.add(lunarCalendar.toStringWithChineseMonth(i) + "月");
            if (tempMonth == i) {
                monthList.add("闰" + lunarCalendar.toStringWithChineseMonth(tempMonth) + "月");
            }
        }
        return monthList;
    }

    private List<String> getDayList(int initYear, int initMonth)
    {
        dayList.clear();
        int tempDays = lunarCalendar.getChineseMonthDays(initYear, initMonth);
        for(int i=1;i<=tempDays;i++) {
            dayList.add(lunarCalendar.toStringWithChineseDay(i));
        }
        return dayList;
    }

    public AlertDialog show() {

        View dateTimeLayout = LayoutInflater.from(activity).inflate(R.layout.common_lunar_datetime_picker, null);

        yearPicker = (NumberPicker) dateTimeLayout.findViewById(R.id.npYear);
        monthPicker = (NumberPicker) dateTimeLayout.findViewById(R.id.npMonth);
        dayPicker = (NumberPicker)dateTimeLayout.findViewById(R.id.npDay);

        init();

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                initYear = i1;
                monthList = getMonthList(initYear);
                if(monthList.size() < initMonth) {
                    initMonth = 1;
                }
                initMonth();

                dayList = getDayList(initYear,initMonth);
                if(dayList.size() < initDay)
                {
                    initDay = 1;
                }
                initDay();
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                initMonth = i1;
                //当前选的是不是闰月
                if(monthList.get(i1-1).contains("闰"))
                {
                    isLeapMonth = true;
                }
                else
                {
                    isLeapMonth = false;
                }
                dayList = getDayList(initYear, initMonth);
                if (dayList.size() < initDay) {
                    initDay = 1;
                }
                initDay();
            }
        });

        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                initDay = i1;
            }
        });

        ad = new AlertDialog.Builder(this.activity)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(callBack != null) {
                            DateLunar dateLunar = new DateLunar();
                            dateLunar.setLunarYear(initYear);
                            int yearLeapMonth = lunarCalendar.getChineseLeapMonth(initYear);
                            if(isLeapMonth || ( yearLeapMonth != 0 && (initMonth > yearLeapMonth)))
                                initMonth = initMonth - 1;
                            dateLunar.setLunarMonth(initMonth);
                            dateLunar.setLunarDay(initDay);
                            dateLunar.setIsLeapMonth(isLeapMonth);

                            DateExt dateExt = lunarCalendar.getDate(dateLunar);
                            int hour = initDateExt.getHour();
                            int min = initDateExt.getMinute();
                            dateExt.addTime(0,0,hour,min, 0);
                            callBack.invoke(dateExt);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();

        return ad;
    }

    private void init() {

        initYear();
        monthList = getMonthList(initYear);
        initMonth();
        dayList = getDayList(initYear,initMonth);
        initDay();

    }

    private void initYear()
    {
        yearPicker.setDisplayedValues(getYearList().second.toArray(new String []{}));
        yearPicker.setMinValue(getYearList().first.get(0));
        yearPicker.setMaxValue(getYearList().first.get(getYearList().first.size() - 1));
        yearPicker.setValue(initYear);
    }

    private void initMonth()
    {
        monthPicker.setDisplayedValues(null);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(monthList.size());
        monthPicker.setDisplayedValues(monthList.toArray(new String[]{}));
        int yearLeapMonth = lunarCalendar.getChineseLeapMonth(initYear);
        if(isLeapMonth || (yearLeapMonth != 0 && (yearLeapMonth < initMonth)))
            initMonth = initMonth+1;
        monthPicker.setValue(initMonth);
    }

    private void initDay() {
        dayPicker.setDisplayedValues(null);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(dayList.size());
        dayPicker.setDisplayedValues(dayList.toArray(new String[]{}));
        dayPicker.setValue(initDay);
    }
}

