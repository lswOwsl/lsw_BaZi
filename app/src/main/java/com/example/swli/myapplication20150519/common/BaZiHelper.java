package com.example.swli.myapplication20150519.common;

import android.util.Pair;

import java.util.ArrayList;

public class BaZiHelper {

    static LunarSolarTerm lunarSolarTerm = new LunarSolarTerm();

    //三天一岁，1天4个月，1个时辰10天
    public static int getBeginYunHours(DateExt dateExt, boolean isMale, int yearIndex) {
        Pair<SolarTerm, SolarTerm> pair = getPairJie(dateExt);
        if (isForward(yearIndex, isMale))
            return pair.second.getSolarTermDate().getHoursOffset(dateExt);
        else
            return dateExt.getHoursOffset(pair.first.getSolarTermDate());
    }

    public static int getDaYunByFlowYear(int currentAge, int beginYunAge, ArrayList<Integer> daYuns)
    {
        //求出当前年龄和起运年龄的差，例如4岁起运，那么按10岁1运的话就是4/10上0
        int indexYun = (currentAge - beginYunAge)/10;
        return daYuns.get(indexYun);
    }

    //根据起运岁得到流年天干地支index
    public static int getBeginYunFlowYearIndex(int age, int indexYear)
    {
        return indexYear + age;
    }

    //得到大运天干地支的index
    public static ArrayList<Integer> getDaYunsByNum(int yearIndex, int monthIndex, int numOfDaYun, boolean isMale)
    {
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i=1; i<= numOfDaYun; i++)
        {
            int tempIndex;
            //年干阳男性和年干阴女性，大运顺行
            if(isForward(yearIndex,isMale))
            {
                tempIndex = fixIndex(monthIndex + i);
            }
            else
            {
                tempIndex = fixIndex(monthIndex - i);
            }

            result.add(tempIndex);
        }

        return result;
    }

    public static int fixIndex(int i)
    {
        int years = 59;
        if(i<0)
        {
            return years + i;
        }
        else
        {
            return i;
        }
    }

    public static boolean isForward(int yearIndex, boolean isMale) {
        //"癸甲乙丙丁戊己庚辛壬",单数为阳
        return (yearIndex % 2 == 1 && isMale) || (yearIndex % 2 == 0 && !isMale);
    }

    public static Pair<SolarTerm,SolarTerm> getPairJie(DateExt dateExt) {

        Pair<SolarTerm, SolarTerm> pairSolarTerm = lunarSolarTerm.getPairSolarTerm(dateExt);
        SolarTerm begin, end;
        begin = end = null;

        //阳历每个月都是以节开始，所以不用判断first是不是节了

        //是节的话，比较出生日期时间是不是在这个节之前
        if (dateExt.isEarlierThan(pairSolarTerm.first.getSolarTermDate())) {
            //如果是之前，那么第一个就是要得到节的第一个
            end = pairSolarTerm.first;
        } else {
            begin = pairSolarTerm.first;
        }

        DateExt dateExtTemp = new DateExt(dateExt.getDate());
        //如果已经有开始的节了，说明出生日期在6号左右以后，因为24节气是跟阳历来的，
        //那么直接拿下个月的第一个就是end的节
        if (end == null) {
            //判断第二个是不是节，如果不是节就要查下一个月,下个月的第一个肯定是节
            pairSolarTerm = lunarSolarTerm.getPairSolarTerm(dateExtTemp.addMonths(1));
            end = pairSolarTerm.first;

        } else {
            //如果有结束的节了，说明出生日期在6号左右以前,前一个月的first财是节
            pairSolarTerm = lunarSolarTerm.getPairSolarTerm(dateExtTemp.addMonths(-1));
            begin = pairSolarTerm.first;
        }

        return Pair.create(begin,end);
    }

    public static ArrayList<SolarTerm> getSolarTermsInLunarYear(int year)
    {
        ArrayList<SolarTerm> list = new ArrayList<SolarTerm>();
        //从2月开始，因为立春都是在2月4,5日
        //小雨等于14是为了多算一个月的，好显示到转年的立春
        for(int month = 2; month<=14;month++) {
            SolarTerm st;
            if(month <13) {
                //因为每年立春都是2月4号5号左右所以某个月，第一个就是节
                st  = lunarSolarTerm.getSolarTerm(year, month)[0];
            }
            else
            {
                int tempMonth = month -12;
                int tempYear = year + 1;
                st = lunarSolarTerm.getSolarTerm(tempYear,tempMonth)[0];
            }
            list.add(st);
        }

        return list;
    }

    public static boolean onDemandSolarTerm(String solarTermName, boolean isMainSolarTerm)
    {
        String[] allSolarTerms = LunarCalendar.lunarHoliDayName;

        //节是小寒开始，气是大寒开始，所以索引0开始还是1开始
        int i= isMainSolarTerm ? 0  :1;
        while (i<allSolarTerms.length)
        {
            if(allSolarTerms[i].equals(solarTermName))
            {
                return true;
            }

            i+=2;
        }

        return false;
    }


}
