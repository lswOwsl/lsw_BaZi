package com.example.swli.myapplication20150519.activity.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.BaZiActivityWrapper;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.LunarCalendar;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;
import com.example.swli.myapplication20150519.phone.base.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swli on 7/24/2015.
 */
public class CalendarCustomization extends Activity {

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        gridView = (GridView)findViewById(R.id.gv_calendar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    public List<DayModel> getOnePageDays(int year, int month)
    {

        List<DayModel> listDays = new ArrayList<DayModel>();

        //得到每月的1月1日，看是星期几，因为第一行的第一个格一定是星期一，所以如果不是星期一要补全前面的日子
        DateExt beginDate = new DateExt(year,month,1,0,0,0);
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(beginDate);

        int eraDayIndex = lunarCalendarWrapper.getChineseEraOfDay();
        int beginIndex = beginDate.getIndexOfWeek();
        int offsetDay = 0;
        //因为1是周一，0是星期日
        if(beginIndex != 1 && beginIndex != 0)
        {
            offsetDay = 1 - beginIndex;
        }
        else if(beginIndex == 0)
        {
            offsetDay = 6;
        }

        //一共显示6行，一行7天
        for(int i=0;i<42;i++)
        {
            DateExt tempDate =  beginDate.addDays(offsetDay);
            DayModel dayModel = new DayModel();
            dayModel.setDay(Integer.toString(tempDate.getDay()));
            dayModel.setEra_day(lunarCalendarWrapper.toStringWithSexagenary(getEraDayIndex(eraDayIndex,offsetDay)));
            dayModel.setLunar_day(lunarCalendarWrapper.toStringWithChineseDay(lunarCalendarWrapper.getDateLunar(tempDate).getLunarDay()));
            offsetDay ++;
            listDays.add(dayModel);
        }

        return listDays;
    }

    private int getEraDayIndex(int eraIndex, int offset)
    {
        if(eraIndex - offset < 0)
        {
            return 59 + (eraIndex - offset);
        }
        else
        {
            return eraIndex - offset;
        }
    }
}
