package lsw.library;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by swli on 8/3/2015.
 */
public class DateExt {
    Calendar calendar;
    public static String DefaultFormat = "yyyy-MM-dd HH:mm";

    public DateExt(Calendar calendar)
    {
        this.calendar = calendar;
    }

    public DateExt(int year,int month,int day, int hour, int minute, int second)
    {
        this();
        calendar.set(year, month - 1, day, hour, minute, second);
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

    public  int getSecond(){return  calendar.get(Calendar.SECOND);}

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

    public DateExt getLastWeekMonday()
    {
        int i = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
        calendar.add(Calendar.DATE, -i - 6);
        return new DateExt(calendar.getTime());
    }

    public DateExt getThisWeekMonday()
    {
        int i = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
        calendar.add(Calendar.DATE, -i + 1);
        return new DateExt(calendar.getTime());
    }

    public DateExt getFirstMondayInMonth()
    {
        int i = 1;

        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
            calendar.set(Calendar.DAY_OF_MONTH, i++);//设置这个月的星期1 为几号
        }

        return new DateExt(calendar.getTime());
    }

    public DateExt addWeeks(int weeks)
    {
        calendar.add(Calendar.WEEK_OF_YEAR,weeks);
        return new DateExt(calendar.getTime());
    }

    public DateExt addDays(int days)
    {
        calendar.add(Calendar.DAY_OF_YEAR,days);
        return new DateExt(calendar.getTime());
    }

    public DateExt addHours(int hours)
    {
        calendar.add(Calendar.HOUR_OF_DAY,hours);
        return new DateExt(calendar.getTime());
    }

    public DateExt addMins(int min)
    {
        calendar.add(Calendar.MINUTE,min);
        return new DateExt(calendar.getTime());
    }

    public DateExt addMonths(int months)
    {
        calendar.add(Calendar.MONTH,months);
        return new DateExt(calendar.getTime());
    }

    public DateExt addSeconds(int seconds)
    {
        calendar.add(Calendar.SECOND,seconds);
        return new DateExt(calendar.getTime());
    }

    public DateExt addTime(int months, int days, int hours, int min, int seconds)
    {
        calendar.add(Calendar.MONTH,months);
        calendar.add(Calendar.DAY_OF_YEAR,days);
        calendar.add(Calendar.HOUR_OF_DAY,hours);
        calendar.add(Calendar.MINUTE,min);
        calendar.add(Calendar.SECOND,seconds);
        return new DateExt(calendar.getTime());
    }

    public int getIndexOfWeek()
    {
        //默认 index = 1 是星期日
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

    public String getChineseDayOfWeek()
    {
        int indexOfWeek = this.getIndexOfWeek();
        return indexOfWeek == 0 ? "日" : LunarCalendar.toChineseDayInWeek(indexOfWeek);
    }
}