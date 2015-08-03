package com.example.swli.myapplication20150519.activity.calendar;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.ColorHelper;
import com.example.swli.myapplication20150519.common.MyApplication;

import java.util.List;

import lsw.library.DateExt;
import lsw.library.LunarCalendar;

/**
 * Created by swli on 7/30/2015.
 */
public class AsyncReloadGridView extends AsyncTask<DateExt, Void, List<DayModel>>
{
    private GridView gridView;
    private DateExt selectedDate;

    public AsyncReloadGridView(GridView gridView)
    {
        this.gridView = gridView;
    }

    private ICallBack callBack;
    interface ICallBack
    {
        void invoke(DateExt dateExt);
    }

    public void setICallBack(ICallBack callBack)
    {
        this.callBack = callBack;
    }

    @Override
    protected List<DayModel> doInBackground(DateExt... dateExts) {

        Log.d("datetime doInBackground", dateExts[0].getFormatDateTime());
        this.selectedDate = dateExts[0];

        List<DayModel> dayModelList = CalendarAdapter.getOneMonthDays(this.selectedDate);
        LunarCalendar lunarCalendar = new LunarCalendar();
        for(DayModel dayModel: dayModelList)
        {
            dayModel.setLunar_day(lunarCalendar.toStringWithChineseDay(lunarCalendar.getDateLunar(dayModel.getDateExt()).getLunarDay()));
        }
        return dayModelList;
    }

    @Override
    protected void onPostExecute(final List<DayModel> dayModelList)
    {
        TextView tvDay = null;
        TextView tvLunaryDay = null;
        TextView tvEraDay = null;

        for(int i=0; i<gridView.getChildCount(); i++){

            View childView = gridView.getChildAt(i);
            tvDay = (TextView) childView.findViewById(R.id.tvDay);
            tvLunaryDay = (TextView) childView.findViewById(R.id.tvLunarDay);
            tvEraDay = (TextView)childView.findViewById(R.id.tvEraDay);

            DayModel dayModel = dayModelList.get(i);

            tvLunaryDay.setText(dayModel.getLunar_day());
            tvDay.setText(dayModel.getDateExt().getDay());

            if(dayModel.getDateExt().getMonth() == selectedDate.getMonth()
                    && dayModel.getDateExt().getDay() == selectedDate.getDay())
            {
                tvDay.setBackgroundResource(R.drawable.tv_circle_highlight_temp);
                tvDay.setTextColor(Color.WHITE);
            }

            DateExt today = new DateExt();
            if(today.getYear() == dayModel.getDateExt().getYear()
                    && today.getMonth() == dayModel.getDateExt().getMonth()
                    && dayModel.getDateExt().getDay() == today.getDay())
            {
                tvDay.setBackgroundResource(R.drawable.tv_circle_highlight);
                tvDay.setTextColor(Color.WHITE);
            }

            String eraDay = dayModel.getEra_day();
            String c = eraDay.substring(0, 1);
            String t = eraDay.substring(1);
//
            if(dayModel.getDateExt().getMonth() != selectedDate.getMonth())
            {
                tvDay.setTextColor(Color.LTGRAY);
                tvEraDay.setText("");
                tvEraDay.append(ColorHelper.getTextByColor(c, Color.LTGRAY));
                tvEraDay.append(ColorHelper.getTextByColor(t, Color.LTGRAY));
            }
            else
            {
                tvEraDay.setText("");
                tvEraDay.append(ColorHelper.getColorCelestialStem(MyApplication.getInstance(), c));
                tvEraDay.append(ColorHelper.getColorTerrestrial(MyApplication.getInstance(), t));
            }
        }

        final TextView innerTvDay = tvDay;


    }
}