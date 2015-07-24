package com.example.swli.myapplication20150519.common;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by swli on 5/26/2015.
 */
public class DateExt {
    Calendar calendar;
    public static String DefaultFormat = "yyyy-MM-dd HH:mm";

    public DateExt(int year,int month,int day, int hour, int minute, int second)
    {
        this();
        calendar.set(year, month - 1,day,hour,minute,second);
    }

    public DateExt(Date date)
    {
        this();
        calendar.setTime(date);
    }

    public DateExt(String strDateTime)
    {
        this(strDateTime, DefaultFormat);
    }

    public DateExt(String strDateTime, String format)
    {
        this();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date date = formatter.parse(strDateTime, pos);
        calendar.setTime(date);
    }

    public DateExt()
    {
        calendar = Calendar.getInstance();
        //int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        //int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        //calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
    }

    public static DateExt getCurrentTime()
    {
        return new DateExt();
    }

    public int getYear()
    {
        return calendar.get(Calendar.YEAR);
    }

    public  int getMonth()
    {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public  int getDay()
    {
        return  calendar.get(Calendar.DAY_OF_MONTH);
    }

    public  int getHour()
    {
        return  calendar.get(Calendar.HOUR_OF_DAY);
    }

    public  int getMinute(){return  calendar.get(Calendar.MINUTE);}

    public Calendar getCalendar()
    {
        return this.calendar;
    }
    public Date getDate(){ return this.calendar.getTime(); }

    public String getFormatDateTime(String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(calendar.getTime());
    }

    public String getDefaultFormatForSqllite()
    {
        return this.getFormatDateTime(DefaultFormat + ":ss");
    }

    public String getFormatDateTime()
    {
       return this.getFormatDateTime(DefaultFormat);
    }

    public int getDaysOffset(DateExt dateExt)
    {
        return (int) (getMillisOffset(dateExt) / (24 * 60 * 60 * 1000));
    }

    public int getHoursOffset(DateExt dateExt)
    {
        return (int) (getMillisOffset(dateExt)/ (60*60*1000));
    }

    public long getMillisOffset(DateExt dateExt)
    {
        long beginTime = calendar.getTimeInMillis();
        long endTime = dateExt.calendar.getTimeInMillis();
        return beginTime-endTime;
    }

    public DateExt addDays(int days)
    {
        calendar.add(Calendar.DAY_OF_YEAR,days);
        return new DateExt(calendar.getTime());
    }

    public DateExt addMonths(int months)
    {
        calendar.add(Calendar.MONTH,months);
        return new DateExt(calendar.getTime());
    }

    public int getIndexOfWeek()
    {
        //?????????? index = 1
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        return dayIndex - 1;
    }

    public EnumDateCompareResult compareTo(DateExt dateExt)
    {
        int result = calendar.compareTo(dateExt.calendar);
        //calendar earlier than dateExt.calendar
        return EnumDateCompareResult.valueOf(result);
    }

    public boolean isEarlierThan(DateExt dateExt)
    {
        return compareTo(dateExt) == EnumDateCompareResult.Earlier;
    }

    public enum EnumDateCompareResult
    {
        Earlier(-1),
        Later(1),
        Equal(0);

        private int value = 0;

        private EnumDateCompareResult(int value)
        {
            this.value = value;
        }

        public static EnumDateCompareResult valueOf(int value)
        {
            switch (value)
            {
                case -1:
                    return Earlier;
                case 1:
                    return Later;
                case 0:
                    return Equal;
                default:
                    return null;
            }
        }

        public int value()
        {
            return this.value;
        }
    }
}
