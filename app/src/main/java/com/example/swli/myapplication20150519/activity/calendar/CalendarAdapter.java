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

    private ICallBack callBack;
    interface ICallBack
    {
        void invoke(DateExt dateExt);
    }

    public void setICallBack(ICallBack callBack)
    {
        this.callBack = callBack;
    }

    public CalendarAdapter(LayoutInflater layoutInflater, DateExt selectedDate)
    {
        this.layoutInflater = layoutInflater;
        this.dateSelected = selectedDate;
        this.dayModels = getOneMonthDays();
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

    private TextView previsouselectedtextview;
    private TextView todayTextView;
    private DateExt previousSelectedDate;

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
        final DayModel dayModel = dayModels.get(i);
        String eraDay = dayModel.getEra_day();
        String c = eraDay.substring(0,1);
        String t = eraDay.substring(1);

        controls.tvDay.setText(dayModel.getDay());
        controls.tvLunaryDay.setText(dayModel.getLunar_day());

        if(dayModel.getDateExt().getMonth() == dateSelected.getMonth() && dayModel.getDateExt().getDay() == dateSelected.getDay())
        {
            previsouselectedtextview = controls.tvDay;
            previousSelectedDate = dayModel.getDateExt();
            controls.tvDay.setBackgroundResource(R.drawable.tv_circle_highlight_temp);
            controls.tvDay.setTextColor(Color.WHITE);
        }
        DateExt today = new DateExt();
        if(today.getYear() == dayModel.getDateExt().getYear()
               && today.getMonth() == dayModel.getDateExt().getMonth()
                && dayModel.getDateExt().getDay() == today.getDay())
        {
            todayTextView = controls.tvDay;
            controls.tvDay.setBackgroundResource(R.drawable.tv_circle_highlight);
            controls.tvDay.setTextColor(Color.WHITE);
        }
        //????????????е???????????е????????
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

        //isn't current month, set bacagroud to gray
        if(dayModel.getDateExt().getMonth() != dateSelected.getMonth())
        {
            controls.tvDay.setTextColor(Color.LTGRAY);
            controls.tvEraDay.setText("");
            controls.tvEraDay.append(ColorHelper.getTextByColor(c, Color.LTGRAY));
            controls.tvEraDay.append(ColorHelper.getTextByColor(t, Color.LTGRAY));
        }
        else
        {
            controls.tvEraDay.setText("");
            controls.tvEraDay.append(ColorHelper.getColorCelestialStem(MyApplication.getInstance(), c));
            controls.tvEraDay.append(ColorHelper.getColorTerrestrial(MyApplication.getInstance(), t));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(previsouselectedtextview != null) {
                    if(!previsouselectedtextview.equals(todayTextView)) {
                        previsouselectedtextview.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
                        if(previousSelectedDate != null && (previousSelectedDate.getMonth() == dateSelected.getMonth()))
                        {
                            previsouselectedtextview.setTextColor(Color.BLACK);
                        }
                        else
                        {
                            previsouselectedtextview.setTextColor(Color.LTGRAY);
                        }
                    }
                    else
                    {
                        previsouselectedtextview.setBackgroundResource(R.drawable.tv_circle_highlight);
                        previsouselectedtextview.setTextColor(Color.WHITE);
                    }
                }
                if(callBack != null) {
                    callBack.invoke(dayModel.getDateExt());
                    previsouselectedtextview = controls.tvDay;
                    previousSelectedDate = dayModel.getDateExt();
                    controls.tvDay.setBackgroundResource(R.drawable.tv_circle_highlight_temp);
                    controls.tvDay.setTextColor(Color.WHITE);
                }
            }
        });

        return view;
    }

    class ItemHolder {
        public TextView tvEraDay, tvLunaryDay, tvDay;
    }

    public List<DayModel> getOneMonthDays()
    {

        List<DayModel> listDays = new ArrayList<DayModel>();

        //???????1??1?????????????????????е?????????????????????????????????????????????
        DateExt beginDate = new DateExt(dateSelected.getYear(),dateSelected.getMonth(),1,dateSelected.getHour(),dateSelected.getMinute(),0);
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
        long startTimeM = System.currentTimeMillis();
        //??????6?У????7??
        for(int i=0;i<42;i++)
        {
            DateExt tempDate = new DateExt(beginDate.getFormatDateTime());
            tempDate.addDays(offsetDay);
            DayModel dayModel = new DayModel();
            dayModel.setDay(Integer.toString(tempDate.getDay()));
            dayModel.setEra_day(lunarCalendarWrapper.toStringWithSexagenary(getEraDayIndex(eraDayIndex, offsetDay)));
            dayModel.setLunar_day(lunarCalendarWrapper.toStringWithChineseDay(lunarCalendarWrapper.getDateLunar(tempDate).getLunarDay()));
            dayModel.setDateExt(tempDate);
            offsetDay ++;
            listDays.add(dayModel);
        }

        long consumingTime = System.nanoTime() - startTime;
        long consumingTimeM = System.currentTimeMillis() - startTimeM;
        Log.d("lsw",consumingTime/1000/1000 + " calendar model create date time.");
        Log.d("lsw",consumingTimeM + "ms calendar model create date time.");
        return listDays;
    }

    private int getEraDayIndex(int eraIndex, int offset)
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
