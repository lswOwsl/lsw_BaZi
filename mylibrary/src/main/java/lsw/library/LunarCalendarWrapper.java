package lsw.library;

import android.util.Pair;

import java.util.Calendar;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class LunarCalendarWrapper extends LunarCalendar {

    private DateExt dateExt;
    private int year;
    private int month;
    private int day;
    private int hour;
    private SolarTerm[] solarTerms;

    public LunarCalendarWrapper(DateExt dateExt)
    {
        super();
        this.year = dateExt.getYear();
        this.month = dateExt.getMonth();
        this.day = dateExt.getDay();
        this.hour = dateExt.getHour();
        this.solarTerms = getSolarTerm(this.year, this.month);
        this.dateExt = dateExt;
    }

    public DateExt getDateExt()
    {
        return this.dateExt;
    }

    public int getChineseEraOfYear()
    {

        int g = (year - 4) % 60;
        if ((dayDifference(year, month, day) + hour / 24)
                <term(year, 3, true) - 1)
        {
            g -= 1;
        }
        return g + 1;
    }


    public int getChineseEraOfMonth()
    {
        int v = (year * 12 + month + 12) % 60;
        if (day < solarTerms[0].getSolarTermDate().getDay())
            v -= 1;
        return v + 1;
    }

    public int getChineseEraOfMonth(boolean checkWithHourMins) {

        int v = (year * 12 + month + 12) % 60;
        if (checkWithHourMins && dateExt.compareTo(solarTerms[0].getSolarTermDate()) == DateExt.EnumDateCompareResult.Earlier) {
                v -= 1;
            return v + 1;
        } else {
            return getChineseEraOfMonth();
        }
    }


    public int getChineseEraOfDay()
    {
        double gzD =
                //(hour < 23)
                //?
                equivalentStandardDay(year, month, day);
        //: equivalentStandardDay(year, month, day) + 1;
        return (int)Math.round(rem((int) gzD + 15, 60));
    }


    public int getChineseEraOfHour()
    {
        double v = 12 * gan(getChineseEraOfDay()) + Math.floor((double) ((hour + 1) / 2)) - 11;
        //if (hour == 23)
        //  v -= 12;
        return (int)Math.round(rem(v, 60));
    }

    public Pair<SolarTerm,SolarTerm> getPairSolarTerm()
    {
        return super.getPairSolarTerm(dateExt);
    }

    public DateLunar getDateLunar()
    {
        return super.getDateLunar(dateExt);
    }
}
