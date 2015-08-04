package lsw.lunar_calendar.data_source;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lsw.library.DateExt;
import lsw.library.LunarCalendarWrapper;
import lsw.lunar_calendar.R;
import lsw.lunar_calendar.model.DayModel;
import lsw.lunar_calendar.view.DayTextView;
import lsw.lunar_calendar.view.EraDayTextView;

/**
 * Created by lsw_wsl on 8/4/15.
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

    public void setDayModels(List<DayModel> list){this.dayModels = list;}

    public List<DayModel> getDayModels()
    {
        return this.dayModels;
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
            view = layoutInflater.inflate(R.layout.activity_day, null);
            controls.tvDay = (DayTextView) view.findViewById(R.id.tvDay);
            controls.tvLunaryDay = (TextView) view.findViewById(R.id.tvLunarDay);
            controls.tvEraDay = (EraDayTextView)view.findViewById(R.id.tvEraDay);
            view.setTag(controls);
        } else {
            controls = (ItemHolder) view.getTag();
        }
        DayModel dayModel = dayModels.get(i);

        controls.tvDay.setText(dayModel.isCurrentMonth(),dayModel.isSelected(),dayModel.isToday(),dayModel.getDay());
        String eraDay = dayModel.getEra_day();
        String c = eraDay.substring(0, 1);
        String t = eraDay.substring(1);
        controls.tvEraDay.setColorText(c,t,dayModel.isCurrentMonth());
        controls.tvLunaryDay.setText(dayModel.getLunar_day());


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
        public TextView tvLunaryDay;
        public EraDayTextView tvEraDay;
        public DayTextView tvDay;
    }

    public static List<DayModel> getOneMonthDays(DateExt dateExt)
    {
        long startTime = System.nanoTime();

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

        String beginMonthForCompare = beginDate.getFormatDateTime("yyyy-MM");
        String selectedDayForCompare = dateExt.getFormatDateTime("yyyy-MM-dd");
        String todayForCompare = new DateExt().getFormatDateTime("yyyy-MM-dd");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar =  beginDate.getCalendar();

        for(int i=0;i<42;i++)
        {
            calendar.add(Calendar.DAY_OF_YEAR, offsetDay);
            String formatDate = sdf.format(calendar.getTime());
            DayModel dayModel = new DayModel();
            if(beginMonthForCompare.equals(formatDate.substring(0,7))) {
                dayModel.setIsCurrentMonth(true);
            }
            String dayShortFormat = formatDate.substring(0,10);
            if(selectedDayForCompare.equals(dayShortFormat)){
                dayModel.setIsSelected(true);
            }
            if(todayForCompare.equals(dayShortFormat))
            {
                dayModel.setIsToday(true);
            }
            dayModel.setDay(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
            dayModel.setFormatDate(formatDate);
            dayModel.setEra_day(lunarCalendarWrapper.toStringWithSexagenary(getEraDayIndex(eraDayIndex, offsetDay)));
            dayModel.setLunar_day(lunarCalendarWrapper.toStringWithChineseDay(lunarCalendarWrapper.getDateLunar(calendar).getLunarDay()));
            calendar.add(Calendar.DAY_OF_YEAR, -offsetDay);
            offsetDay ++;
            listDays.add(dayModel);
            //Log.d("lsw month date time", formatDate);
        }

        long consumingTime = System.nanoTime() - startTime;
        Log.d("lsw", consumingTime / 1000 / 1000 + " create 42 models of calendar cost time.");
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