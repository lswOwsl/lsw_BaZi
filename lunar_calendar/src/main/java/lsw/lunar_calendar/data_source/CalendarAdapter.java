package lsw.lunar_calendar.data_source;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.library.DateLunar;
import lsw.library.LunarCalendarWrapper;
import lsw.library.SolarTerm;
import lsw.lunar_calendar.R;
import lsw.lunar_calendar.common.MyApplication;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.model.DayModel;
import lsw.lunar_calendar.model.MemberDataRow;
import lsw.lunar_calendar.view.DayNotifyPointView;
import lsw.lunar_calendar.view.DayTextView;
import lsw.lunar_calendar.view.EraDayTextView;
import lsw.lunar_calendar.view.LunarDayTextView;

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
            controls.tvLunaryDay = (LunarDayTextView) view.findViewById(R.id.tvLunarDay);
            controls.tvEraDay = (EraDayTextView)view.findViewById(R.id.tvEraDay);
            controls.vDayNotifyPoint = (DayNotifyPointView)view.findViewById(R.id.viewDayNotifyPoint);
            controls.vDayNotifyPointBottom = (DayNotifyPointView)view.findViewById(R.id.viewDayNotifyPointBottom);
            view.setTag(controls);
        } else {
            controls = (ItemHolder) view.getTag();
        }
        DayModel dayModel = dayModels.get(i);

        controls.tvDay.setText(dayModel.isCurrentMonth(),dayModel.isSelected(),dayModel.isToday(),dayModel.isWeekend(),dayModel.isSolarTerm(), dayModel.getDay());
        String eraDay = dayModel.getEra_day();
        String c = eraDay.substring(0, 1);
        String t = eraDay.substring(1);
        controls.tvEraDay.setColorText(c, t, dayModel.isCurrentMonth() && !dayModel.isSolarTerm(), dayModel.isSolarTerm());
        controls.tvLunaryDay.setText(dayModel.isSolarTerm() ? dayModel.getSolarTermText() : dayModel.getLunar_day());
        controls.tvLunaryDay.setBackground(dayModel.isSolarTerm());
        controls.vDayNotifyPoint.setVisibility(dayModel.isShowNotifyPoint());
        controls.vDayNotifyPointBottom.setVisibility(dayModel.isShowNotifyPointBottom());


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
        public LunarDayTextView tvLunaryDay;
        public EraDayTextView tvEraDay;
        public DayTextView tvDay;
        public DayNotifyPointView vDayNotifyPoint;
        public DayNotifyPointView vDayNotifyPointBottom;
    }

    public static List<DayModel> getOneMonthDays(DateExt dateExt)
    {
        DataBase db = new DataBase();

        long startTime = System.nanoTime();

        List<DayModel> listDays = new ArrayList<DayModel>();

        DateExt beginDate = new DateExt(dateExt.getYear(),dateExt.getMonth(),1,dateExt.getHour(),dateExt.getMinute(),0);
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(beginDate);

        int eraDayIndex = lunarCalendarWrapper.getChineseEraOfDay();
        int beginIndex = beginDate.getIndexOfWeek();
        int offsetDay = 0;

        if(!MyApplication.getInstance().isSaturdayForMonthFirstDay()) {
            //以周一为日历第一天
            if (beginIndex != 1 && beginIndex != 0) {
                offsetDay = 1 - beginIndex;
            }//index 0是星期日
            else if (beginIndex == 0) {
                offsetDay = -6;
            }
        }
        else {
            //以周日为日历第一天
            //0,1,2,3,4,5,6 日一二三四五六
            if (beginIndex == 0) {
                offsetDay = 0;
            } else {
                offsetDay = 0 - beginIndex;
            }
        }

        Pair<SolarTerm,SolarTerm> currentMonthSolarTerm = lunarCalendarWrapper.getPairSolarTerm(dateExt);
        Pair<String,String> st1ForCompare = Pair.create(currentMonthSolarTerm.first.getSolarTermDate().getFormatDateTime("yyyy-MM-dd"), currentMonthSolarTerm.first.getName());
        Pair<String,String> st2ForCompare = Pair.create(currentMonthSolarTerm.second.getSolarTermDate().getFormatDateTime("yyyy-MM-dd"),currentMonthSolarTerm.second.getName());

        String beginMonthForCompare = beginDate.getFormatDateTime("yyyy-MM");
        String selectedDayForCompare = dateExt.getFormatDateTime("yyyy-MM-dd");
        String todayForCompare = new DateExt().getFormatDateTime("yyyy-MM-dd");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //Locale.setDefault(Locale.CHINESE);
        Calendar calendar =  beginDate.getCalendar();

        //查询阳历生日，阴历生日
        //查询当天卦例

        String dataPath = CrossAppKey.DB_PATH_LIUYAO + "/" + CrossAppKey.DB_NAME_LIUYAO;

        List<String> hasHexagrams = null;
        if(new File(dataPath).exists())
        {
            hasHexagrams = db.hasHexagramDays(beginDate.getFormatDateTime("yyyy-MM-dd"), new DateExt(beginDate.getDate()).addDays(42).getFormatDateTime("yyyy-MM-dd"));
        }

        String dataPathBaZi = CrossAppKey.DB_PATH_BAZI + "/" + CrossAppKey.DB_NAME_BAZI;
        List<String> hasMemebers = null;
        List<DateLunar> hasLunarMembers = null;
        if(new File(dataPathBaZi).exists())
        {
            hasMemebers = db.getBirthdayByMonth(beginDate.getFormatDateTime("MM"));
            hasLunarMembers = db.getLunarBithdayByMonth(beginDate);
        }



        //一周第一天是否为星期天
        boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);

        for(int i=0;i<42;i++) {
            calendar.add(Calendar.DAY_OF_YEAR, offsetDay);
            String formatDate = sdf.format(calendar.getTime());
            DayModel dayModel = new DayModel();
            if (beginMonthForCompare.equals(formatDate.substring(0, 7))) {
                dayModel.setIsCurrentMonth(true);
            }
            String dayShortFormat = formatDate.substring(0, 10);
            //是不是当天
            if (todayForCompare.equals(dayShortFormat)) {
                dayModel.setIsToday(true);
            }
            //当天是不是节气
            if (st1ForCompare.first.equals(dayShortFormat)) {
                dayModel.setIsSolarTerm(true);
                dayModel.setSolarTermText(st1ForCompare.second);
            }
            if(st2ForCompare.first.equals(dayShortFormat))
            {
                dayModel.setIsSolarTerm(true);
                dayModel.setSolarTermText(st2ForCompare.second);
            }

            //获取周几
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            //若一周第一天为星期天，则-1
            if(isFirstSunday){
                weekDay = weekDay - 1;
                if(weekDay == 0 || weekDay == 6)
                    dayModel.setIsWeekend(true);
            }
            else
            {
                if(weekDay == 7 || weekDay == 6)
                    dayModel.setIsWeekend(true);
            }

            dayModel.setDay(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
            dayModel.setFormatDate(formatDate);
            dayModel.setEra_day(lunarCalendarWrapper.toStringWithSexagenary(getEraDayIndex(eraDayIndex, offsetDay)));
            DateLunar tempDateLunar = lunarCalendarWrapper.getDateLunar(calendar);
            dayModel.setLunar_day(lunarCalendarWrapper.toStringWithChineseDay(tempDateLunar.getLunarDay()));
            calendar.add(Calendar.DAY_OF_YEAR, -offsetDay);

            //是不是默认选中的天
            if (selectedDayForCompare.equals(dayShortFormat)) {
                dayModel.setIsSelected(true);
            }

            String dateTemp = dayModel.getDateExt().getFormatDateTime("yyyy-MM-dd");
            //查询当天是不是存在卦例
            if (hasHexagrams != null && hasHexagrams.size() > 0) {
                if (hasHexagrams.contains(dateTemp))
                    dayModel.setShowNotifyPoint(true);
            }

            if(hasMemebers != null && hasMemebers.size() > 0)
            {
                if(hasMemebers.contains(dateTemp.substring(5,10)))
                    dayModel.setShowNotifyPointBottom(true);
            }

            if(hasLunarMembers != null && hasLunarMembers.size()>0)
            {
                for(DateLunar dateLunar: hasLunarMembers)
                {
                    if(dateLunar.getIsLeapMonth() == tempDateLunar.getIsLeapMonth() &&
                            dateLunar.getLunarMonth() == tempDateLunar.getLunarMonth() &&
                            dateLunar.getLunarDay() == tempDateLunar.getLunarDay())
                    {
                        dayModel.setShowNotifyPointBottom(true);
                    }
                }
            }

            offsetDay++;
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