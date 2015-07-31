package com.example.swli.myapplication20150519.activity.calendar;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.ColorHelper;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;
import com.example.swli.myapplication20150519.common.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsw_wsl on 7/24/15.
 */
public class CalendarAdapter extends BaseAdapter {

    private DateExt dateSelected;
    LayoutInflater layoutInflater;
    List<DayModel> dayModels;

    public CalendarAdapter(LayoutInflater layoutInflater, DateExt selectedDate)
    {
        this.layoutInflater = layoutInflater;
        this.dateSelected = selectedDate;
        this.dayModels = getOneMonthDays(dateSelected);
    }

    @Override
    public int getCount() {
        return dayModels.size();
    }

    @Override
    public Object getItem(int i) {
        return dayModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ItemHolder controls;
        if (view == null) {
            controls = new ItemHolder();
            view = layoutInflater.inflate(R.layout.calendar_day, null);
            controls.tvDay = (TextView) view.findViewById(R.id.tvDay);
            controls.tvLunaryDay = (TextView) view.findViewById(R.id.tvLunarDay);
            controls.tvEraDay = (TextView)view.findViewById(R.id.tvEraDay);
            view.setTag(controls);
        } else {
            controls = (ItemHolder) view.getTag();
        }

        controls.tvDay.setText(dayModels.get(i).getDay());

        if(i == 0) {
            view.setBackgroundResource(R.drawable.gv_border_item);
        }
        else if(i>0 && i <7)
        {
            view.setBackgroundResource(R.drawable.gv_border_item_trb);
        }
        else if(i%7==0)
        {
            view.setBackgroundResource(R.drawable.gv_border_item_lbr);
        }
        else {
            view.setBackgroundResource(R.drawable.gv_border_item_rb);
        }

        return view;
    }

    class ItemHolder {
        public TextView tvEraDay, tvLunaryDay, tvDay;
    }

    public static List<DayModel> getOneMonthDays(DateExt dateExt)
    {

        List<DayModel> listDays = new ArrayList<DayModel>();

        DateExt beginDate = new DateExt(dateExt.getYear(),dateExt.getMonth(),1,dateExt.getHour(),dateExt.getMinute(),0);
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(beginDate);

        int eraDayIndex = lunarCalendarWrapper.getChineseEraOfDay();
        int beginIndex = beginDate.getIndexOfWeek();
        int offsetDay = 0;
        //???1???????0????????
        if(beginIndex != 1 && beginIndex != 0)
        {
            offsetDay = 1 - beginIndex;
        }//index 0是星期日
        else if(beginIndex == 0)
        {
            offsetDay = -6;
        }

        long startTime = System.nanoTime();
        //long startTimeM = System.currentTimeMillis();
        //one line for one week, so it will cost six columns


        for(int i=0;i<42;i++)
        {
            DateExt tempDate = new DateExt(beginDate.getFormatDateTime());
            Log.d("temp date", tempDate.getFormatDateTime());
            tempDate.addDays(offsetDay);
            DayModel dayModel = new DayModel();
            dayModel.setDay(Integer.toString(tempDate.getDay()));
            dayModel.setEra_day(lunarCalendarWrapper.toStringWithSexagenary(getEraDayIndex(eraDayIndex, offsetDay)));
            dayModel.setLunar_day("");
            //this will set again in the AsyncReloadGridView class
            //dayModel.setLunar_day(lunarCalendarWrapper.toStringWithChineseDay(lunarCalendarWrapper.getDateLunar(tempDate).getLunarDay()));
            dayModel.setDateExt(tempDate);
            offsetDay ++;
            listDays.add(dayModel);
        }

        long consumingTime = System.nanoTime() - startTime;
        //long consumingTimeM = System.currentTimeMillis() - startTimeM;
        Log.d("lsw",consumingTime/1000/1000 + " calendar model create date time.");
        //Log.d("lsw",consumingTimeM + "ms calendar model create date time.");
        return listDays;
    }

    private static int getEraDayIndex(int eraIndex, int offset)
    {
        if(eraIndex + offset < 0)
        {
            int value = eraIndex + offset;
            while (value < 0) {
                value = value + 59;
                break;
            }
            return value;
        }
        else
        {
            return eraIndex + offset;
        }
    }
}
