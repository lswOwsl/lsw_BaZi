package com.example.swli.myapplication20150519.activity.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;
import com.example.swli.myapplication20150519.common.MyApplication;

/**
 * Created by lsw_wsl on 7/25/15.
 */
public class DateSelectorDialog implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    interface ICallBack
    {
        void invoke(DateExt dateExt);
    }

    private ICallBack callBack;
    public void setCallBack(ICallBack callBack)
    {
        this.callBack = callBack;
    }

    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private DateExt initDateExt;

    private Activity activity;

    public DateSelectorDialog(DateExt initDateExt, Activity activity) {

        this.initDateExt = initDateExt;
        this.activity = activity;
    }

    private void init(DatePicker datePicker, TimePicker timePicker) {
        datePicker.init(initDateExt.getYear(),
                initDateExt.getMonth() - 1,
                initDateExt.getDay(), this);
        timePicker.setCurrentHour(initDateExt.getHour());
        timePicker.setCurrentMinute(initDateExt.getMinute());
    }

    public AlertDialog show() {

        View dateTimeLayout = LayoutInflater.from(activity).inflate(R.layout.common_datetime_picker,null);

        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);

        init(datePicker, timePicker);

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(this.activity)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(callBack != null)
                            callBack.invoke(initDateExt);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return ad;
    }

    @Override
    public void onDateChanged(DatePicker view, int i, int i2, int i3) {
        DateExt de = new DateExt(datePicker.getYear(), datePicker.getMonth()+1,
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(),0);

        initDateExt = de;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        onDateChanged(null, 0, 0, 0);
    }
}
