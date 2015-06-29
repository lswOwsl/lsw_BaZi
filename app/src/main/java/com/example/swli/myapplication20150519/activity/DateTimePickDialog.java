package com.example.swli.myapplication20150519.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.example.swli.myapplication20150519.MemberMaintain;
import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.DateExt;

/**
 * Created by swli on 5/26/2015.
 */
public class DateTimePickDialog implements OnDateChangedListener, OnTimeChangedListener {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    //private DateExt dateExt;
    private DateExt initDateExt;
    private Activity activity;
    private EditText etDateTimeOfBirth;

    public DateTimePickDialog(Activity activity) {
        this.activity = activity;
        etDateTimeOfBirth = (EditText) activity.findViewById(R.id.etDateTimeOfBirth);
    }

    public void init(DatePicker datePicker, TimePicker timePicker) {

        initDateExt = new DateExt(etDateTimeOfBirth.getText().toString());

        //-1 是因为java时间月份从0开始，但是DateExt已经默认加1了
        datePicker.init(initDateExt.getYear(),
                initDateExt.getMonth() - 1,
                initDateExt.getDay(), this);
        timePicker.setCurrentHour(initDateExt.getHour());
        timePicker.setCurrentMinute(initDateExt.getMinute());
    }

    public AlertDialog dateTimePicKDialog() {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime_picker, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        init(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateExt.getFormatDateTime())
                .setView(dateTimeLayout)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        etDateTimeOfBirth.setText(initDateExt.getFormatDateTime());
                        if(activity instanceof MemberMaintain)
                        {
                            ((MemberMaintain)activity).loadLunarBirthday(initDateExt);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //inputDate.setText("");
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

        //dateExt = de;
        initDateExt = de;
        ad.setTitle(de.getFormatDateTime());
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        onDateChanged(null, 0, 0, 0);
    }
}
