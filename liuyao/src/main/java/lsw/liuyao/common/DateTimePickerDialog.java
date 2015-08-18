package lsw.liuyao.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import lsw.hexagram.Builder;
import lsw.library.DateExt;
import lsw.library.LunarCalendarWrapper;
import lsw.liuyao.R;

/**
 * Created by swli on 8/11/2015.
 */
public class DateTimePickerDialog implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    public interface ICallBack
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

    private boolean showMinute;

    private TextView tvTitle;

    public DateTimePickerDialog(DateExt initDateExt, Activity activity, boolean showMinute)
    {
        this.initDateExt = initDateExt;
        this.activity = activity;
        this.showMinute = showMinute;
    }

    public DateTimePickerDialog(DateExt initDateExt, Activity activity) {

        this(initDateExt, activity, true);
    }

    private void init(DatePicker datePicker, TimePicker timePicker) {
        datePicker.init(initDateExt.getYear(),
                initDateExt.getMonth() - 1,
                initDateExt.getDay(), this);
        timePicker.setCurrentHour(initDateExt.getHour());
        timePicker.setCurrentMinute(initDateExt.getMinute());
    }

    public AlertDialog show() {

        View dateTimeLayout = LayoutInflater.from(activity).inflate(R.layout.common_datetime_picker, null);

        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        tvTitle = (TextView) dateTimeLayout.findViewById(R.id.tvTitle);

        init(datePicker, timePicker);

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        if (!showMinute) {
            tvTitle.setText(getTitleByDate(initDateExt));
        }
        ad = builder.setView(dateTimeLayout).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (callBack != null)
                            callBack.invoke(initDateExt);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();


        onDateChanged(null, 0, 0, 0);

        if (!showMinute) {
            timePicker.setVisibility(View.INVISIBLE);
            resizeNumberPicker(timePicker);
        }
        else
        {
            tvTitle.setVisibility(View.INVISIBLE);
        }

        return ad;
    }

    private void resizeNumberPicker(TimePicker np){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    @Override
    public void onDateChanged(DatePicker view, int i, int i2, int i3) {
        DateExt de = new DateExt(datePicker.getYear(), datePicker.getMonth()+1,
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute(),0);
        if(!showMinute) {
            tvTitle.setText(getTitleByDate(de));
        }
        initDateExt = de;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        onDateChanged(null, 0, 0, 0);
    }

    private String getTitleByDate(DateExt dateExt)
    {
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        String monthEar = lunarCalendarWrapper.toStringWithSexagenary(lunarCalendarWrapper.getChineseEraOfMonth());
        String dayEra = lunarCalendarWrapper.toStringWithSexagenary(lunarCalendarWrapper.getChineseEraOfDay());

        return monthEar+"月"+"   "+dayEra+"日";
    }
}
