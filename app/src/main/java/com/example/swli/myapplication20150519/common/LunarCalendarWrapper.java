package com.example.swli.myapplication20150519.common;

import lsw.library.DateExt;
import lsw.library.LunarCalendar;
import lsw.library.SolarTerm;

/**
 * Created by swli on 5/27/2015.
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
        this.solarTerms = getSolarTerm(this.year,this.month);
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
}
