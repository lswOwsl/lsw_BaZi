package com.example.swli.myapplication20150519.activity.calendar;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.DateExt;

import java.util.List;

/**
 * Created by swli on 7/30/2015.
 */
public class AsyncReloadGridView extends AsyncTask<DateExt, Void, DateExt>
{
    private GridView gridView;
    private List<DayModel> dayModelList;

    public AsyncReloadGridView(GridView gridView, List<DayModel> dayModelList)
    {
        this.gridView = gridView;
        this.dayModelList = dayModelList;
    }

    @Override
    protected DateExt doInBackground(DateExt... dateExts) {

        Log.d("datetime",dateExts[0].getFormatDateTime());


        return dateExts[0];
    }

    @Override
    protected void onPostExecute(DateExt dateExt)
    {
        DateExt selectedDate = dateExt;

        for(int i=0; i<gridView.getChildCount(); i++){

            View childView = gridView.getChildAt(i);
            TextView tvDay = (TextView) childView.findViewById(R.id.tvDay);
            //TextView tvLunaryDay = (TextView) childView.findViewById(R.id.tvLunarDay);
            TextView tvEraDay = (TextView)childView.findViewById(R.id.tvEraDay);

            DayModel dayModel = dayModelList.get(i);
            if(dayModel.getDateExt().getMonth() == selectedDate.getMonth() && dayModel.getDateExt().getDay() == selectedDate.getDay())
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
        }
    }
}