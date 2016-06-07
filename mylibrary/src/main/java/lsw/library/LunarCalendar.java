package lsw.library;

import java.util.Calendar;

/**
 * Created by swli on 8/3/2015.
 */
public class LunarCalendar extends LunarSolarTerm{

    public static int minYear = 1616;
    public static int minMonth = 2;
    public static int minDay = 17;
    public static DateExt minDate = new DateExt(minYear,minMonth,minDay,0,0,0);
    /**
     0x表示16进制数
     04bd8 = 0000 0100 1011 1101 1000
     ----0000---
     前4为说明是润大月还是润小月，大月30天小月29天
     ----0100 1011 1101----
     1-小月
     2-大月
     3,4-小月
     5-大
     6-小
     7,8，9,10-大
     11，小
     12，小
     ----1000----
     最后四位8月闰月
     **/

    public static int[] lunarDateArray = new int[]{

            0x09b50,0x04b60,0x0aae4,0x0a4f0,0x05260,0x1d262,0x0d550,0x15a9a,0x056a0,0x096d0,
            0x149d6,0x049e0,0x0a4d0,0x0d4d4,0x0d250,0x0d53b,0x0b540,0x0b5a0,0x195a8,0x095b0,
            0x049b0,0x0a974,0x0a4b0,0x02a50,0x0ea51,0x06d40,0x0adbb,0x02b60,0x09370,0x04af6,
            0x04970,0x064b0,0x164a4,0x0da50,0x06b20,0x196c2,0x0ab60,0x192d6,0x092e0,0x0c960,
            0x1d155,0x0d4a0,0x0da50,0x15553,0x056a0,0x0a7a7,0x0a5d0,0x092d0,0x0aab6,0x0a950,
            0x0b4a0,0x0baa4,0x0ad50,0x055a0,0x18ba2,0x0a5b0,0x05377,0x052b0,0x06930,0x17155,
            0x06aa0,0x0ad50,0x05b53,0x04b60,0x0a5e8,0x0a4e0,0x0d260,0x0ea66,0x0d520,0x0daa0,
            0x166a4,0x056d0,0x04ae0,0x0a9d3,0x0a4d0,0x0d2b7,0x0b250,0x0d520,0x1d545,0x0b5a0,
            0x055d0,0x055b3,0x049b0,0x0a577,0x0a4b0,0x0aa50,0x0b656,0x06d20,0x0ada0,0x05b64,
            0x09370,0x04970,0x06973,0x064b0,0x06aa7,0x0da50,0x05aa0,0x0aec5,0x0aae0,0x092e0,
            0x0d2e3,0x0c960,0x1d458,0x0d4a0,0x0d550,0x15956,0x056a0,0x0a6d0,0x055d4,0x052d0,
            0x0a950,0x1c953,0x0b4a0,0x1b4a7,0x0ad50,0x055a0,0x1a3a5,0x0a5b0,0x052b0,0x1a174,
            0x06930,0x06ab9,0x06aa0,0x0ab50,0x04f56,0x04b60,0x0a570,0x052e4,0x0d160,0x0e930,
            0x07523,0x0daa0,0x15aa7,0x056d0,0x04ae0,0x1a1d5,0x0a2d0,0x0d150,0x0da54,0x0b520,
            0x0d6a9,0x0ada0,0x055d0,0x129b6,0x045b0,0x0a2b0,0x0b2b5,0x0a950,0x0b520,0x1ab22,
            0x0ad60,0x15567,0x05370,0x04570,0x06575,0x052b0,0x06950,0x07953,0x05aa0,0x0ab6a,
            0x0a6d0,0x04ae0,0x0a6e6,0x0a560,0x0d2a0,0x0eaa5,0x0d550,0x05aa0,0x0b6a3,0x0a6d0,
            0x04bd7,0x04ab0,0x0a8d0,0x0d555,0x0b2a0,0x0b550,0x05d54,0x04da0,0x095d0,0x05572,
            0x049b0,0x0a976,0x064b0,0x06a90,0x0baa4,0x06b50,0x02ba0,0x0ab62,0x09370,0x052e6,
            0x0d160,0x0e4b0,0x06d25,0x0da90,0x05b50,0x036d3,0x02ae0,0x0a2e0,0x0e2d2,0x0c950,
            0x0d556,0x0b520,0x0b690,0x05da4,0x055d0,0x025d0,0x0a5b3,0x0a2b0,0x1a8b7,0x0a950,
            0x0b4a0,0x1b2a5,0x0ad50,0x055b0,0x02b74,0x02570,0x052f9,0x052b0,0x06950,0x06d56,
            0x05aa0,0x0ab50,0x056d4,0x04ae0,0x0a570,0x14553,0x0d2a0,0x1e8a7,0x0d550,0x05aa0,
            0x0ada5,0x095d0,0x04ae0,0x0aab4,0x0a4d0,0x0d2b8,0x0b290,0x0b550,0x05757,0x02da0,
            0x095d0,0x04d75,0x049b0,0x0a4b0,0x1a4b3,0x06a90,0x0ad98,0x06b50,0x02b60,0x19365,
            0x09370,0x04970,0x06964,0x0e4a0,0x0ea6a,0x0da90,0x05ad0,0x12ad6,0x02ae0,0x092e0,
            0x0cad5,0x0c950,0x0d4a0,0x1d4a3,0x0b690,0x057a7,0x055b0,0x025d0,0x095b5,0x092b0,
            0x0a950,0x0d954,0x0b4a0,0x0b550,0x06b52,0x055b0,0x02776,0x02570,0x052b0,0x0aaa5,
            0x0e950,0x06aa0,0x0baa3,0x0ab50,

            0x04bd8,0x04ae0,0x0a570,0x054d5,0x0d260,0x0d950,0x16554,0x056a0,0x09ad0,0x055d2,
            0x04ae0,0x0a5b6,0x0a4d0,0x0d250,0x1d255,0x0b540,0x0d6a0,0x0ada2,0x095b0,0x14977,
            0x04970,0x0a4b0,0x0b4b5,0x06a50,0x06d40,0x1ab54,0x02b60,0x09570,0x052f2,0x04970,
            0x06566,0x0d4a0,0x0ea50,0x06e95,0x05ad0,0x02b60,0x186e3,0x092e0,0x1c8d7,0x0c950,
            0x0d4a0,0x1d8a6,0x0b550,0x056a0,0x1a5b4,0x025d0,0x092d0,0x0d2b2,0x0a950,0x0b557,

            0x06ca0,0x0b550,0x15355,0x04da0,0x0a5b0,0x14573,0x052b0,0x0a9a8,0x0e950,0x06aa0,
            0x0aea6,0x0ab50,0x04b60,0x0aae4,0x0a570,0x05260,0x0f263,0x0d950,0x05b57,0x056a0,
            0x096d0,0x04dd5,0x04ad0,0x0a4d0,0x0d4d4,0x0d250,0x0d558,0x0b540,0x0b6a0,0x195a6,
            0x095b0,0x049b0,0x0a974,0x0a4b0,0x0b27a,0x06a50,0x06d40,0x0af46,0x0ab60,0x09570,
            0x04af5,0x04970,0x064b0,0x074a3,0x0ea50,0x06b58,0x055c0,0x0ab60,0x096d5,0x092e0,

            0x0c960,0x0d954,0x0d4a0,0x0da50,0x07552,0x056a0,0x0abb7,0x025d0,0x092d0,0x0cab5,
            0x0a950,0x0b4a0,0x0baa4,0x0ad50,0x055d9,0x04ba0,0x0a5b0,0x15176,0x052b0,0x0a930,
            0x07954,0x06aa0,0x0ad50,0x05b52,0x04b60,0x0a6e6,0x0a4e0,0x0d260,0x0ea65,0x0d530,
            0x05aa0,0x076a3,0x096d0,0x04bd7,0x04ad0,0x0a4d0,0x1d0b6,0x0d250,0x0d520,0x0dd45,
            0x0b5a0,0x056d0,0x055b2,0x049b0,0x0a577,0x0a4b0,0x0aa50,0x1b255,0x06d20,0x0ada0,

            0x04b63,0x0937f,0x049f8,0x04970,0x064b0,0x068a6,0x0ea5f,0x06b20,0x0a6c4,0x0aaef,
            0x092e0,0x0d2e3,0x0c960,0x0d557,0x0d4a0,0x0da50,0x05d55,0x056a0,0x0a6d0,0x055d4,
            0x052d0,0x0a9b8,0x0a950,0x0b4a0,0x0b6a6,0x0ad50,0x055a0,0x0aba4,0x0a5b0,0x052b0,
            0x0b273,0x06930,0x07337,0x06aa0,0x0ad50,0x04b55,0x04b6f,0x0a570,0x054e4,0x0d260,
            0x0e968,0x0d520,0x0daa0,0x06aa6,0x056df,0x04ae0,0x0a9d4,0x0a4d0,0x0d150,0x0f252,
            0x0d520
    };

    public boolean checkDateLimit(DateExt dateExt)
    {
        DateExt maxDate = new DateExt(minYear+lunarDateArray.length,minMonth,minDay,0,0,0);

        if(dateExt.compareTo(minDate) == DateExt.EnumDateCompareResult.Earlier ||
                dateExt.compareTo(maxDate) == DateExt.EnumDateCompareResult.Later)
            return false;
        else
            return true;

    }

    public boolean checkDateLimit(Calendar calendar)
    {
        DateExt dateExt = new DateExt(calendar);
        return checkDateLimit(dateExt);
    }

    public String toStringLunarDate(DateExt dateExt)
    {
        DateLunar dateLunar = this.getDateLunar(dateExt);
        if(dateLunar != null) {

            return this.toStringWithChineseYear(dateLunar.getLunarYear()) + "年" +
                    (dateLunar.getIsLeapMonth() ? "闰":"") +
                    this.toStringWithChineseMonth(dateLunar.getLunarMonth()) + "月" +
                    this.toStringWithChineseDay(dateLunar.getLunarDay());

        }

        return "";
    }

    public DateLunar getDateLunar(Calendar calendar)
    {
        DateExt dateExt = new DateExt(calendar);

        return getDateLunar(dateExt);
    }

    public DateLunar getDateLunar(DateExt dateExt)
    {
        //修正时间如果是0点0分会计算少一天问题
        if(dateExt.getHour() ==0 && dateExt.getMinute() == 0)
        {
            dateExt.addSeconds(1);
        }

        DateLunar dl = new DateLunar();
        int i;
        int leap;
        int temp;
        int offset;

        if(!checkDateLimit(dateExt))
            return null;

        //农历日期计算部分
        leap = 0;
        temp = 0;

        //计算两天的基本差距[即1900到当天的天差]
        offset = dateExt.getDaysOffset(minDate);

        int maxYear = minYear+lunarDateArray.length;

        for (i = minYear; i <= maxYear; i++)
        {
            temp = getChineseYearDays(i);  //求当年农历年天数
            if (offset - temp < 1)
                break;
            else
            {
                offset = offset - temp;
            }
        }
        dl.setLunarYear(i);

        leap = getChineseLeapMonth(dl.getLunarYear());//计算该年闰哪个月

        boolean isLeapMonth = false;
        for (i = 1; i <= 12; i++)
        {
            //闰月
            if ((leap > 0) && (i == leap + 1) && (isLeapMonth == false))
            {
                isLeapMonth = true;
                i = i - 1;
                temp = getChineseLeapMonthDays(dl.getLunarYear()); //计算闰月天数
            }
            else
            {
                isLeapMonth = false;
                temp = getChineseMonthDays(dl.getLunarYear(), i);//计算非闰月天数
            }

            offset = offset - temp;
            if (offset <= 0) break;
        }

        offset = offset + temp;
        dl.setIsLeapMonth(isLeapMonth);
        dl.setLunarMonth(i);
        dl.setLunarDay(offset);

        return dl;
    }

    /// <summary>
    /// 取农历年一年的天数
    /// </summary>
    /// <param name="year"></param>
    /// <returns></returns>
    private int getChineseYearDays(int year)
    {
        int i, f, sumDay, info;

        sumDay = 348; //29天 X 12个月
        i = 0x8000;
        info = lunarDateArray[year - minYear] & 0x0FFFF;
        //0x04BD8  & 0x0FFFF 中间12位，即4BD，每位代表一个月，为1则为大月，为0则为小月
        //计算12个月中有多少天为30天
        for (int m = 0; m < 12; m++)
        {
            f = info & i; // 0x04BD8  & 0x0FFFF  & 0x8000[1000 0000 0000 0000]
            if (f != 0)
            {
                sumDay++;
            }
            i = i >> 1;
        }
        return sumDay + getChineseLeapMonthDays(year);
    }

    //返回回农历 y年闰月的天数
    public int getChineseLeapMonthDays(int year)
    {
        if (getChineseLeapMonth(year) != 0)
        {
            //前4位，即0在这一年是润年时才有意义，它代表这年润月的大小月。
            if ((lunarDateArray[year - minYear] & 0x10000) != 0)
            {   //为1则润大月
                return 30;
            }
            else
            {   //为0则润小月
                return 29;
            }
        }
        else
        {
            return 0;
        }
    }

    //返回农历 y年闰哪个月 1-12 , 没闰传回 0
    public int getChineseLeapMonth(int year)
    {
        //最后4位，即8，代表这一年的润月月份，为0则不润。首4位要与末4位搭配使用
        return lunarDateArray[year - minYear] & 0xF;
    }

    //返回农历 y年m月的总天数
    public int getChineseMonthDays(int year, int month)
    {
        //0X0FFFF[0000 {1111 1111 1111} 1111]
        if (bitTest32((lunarDateArray[year - minYear] & 0x0000FFFF), (16 - month)))
        {
            return 30;
        }
        else
        {
            return 29;
        }
    }

    /// <summary>
    /// 测试某位是否为真
    /// </summary>
    /// <param name="num"></param>
    /// <param name="bitpostion"></param>
    /// <returns></returns>
    private boolean bitTest32(int num, int bitpostion)
    {

        if ((bitpostion > 31) || (bitpostion < 0))
            new Exception("Error Param: bitpostion[0-31]:" + bitpostion);

        int bit = 1 << bitpostion;

        if ((num & bit) == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public DateExt getDate(DateLunar dateLunar)
    {
        int i, leap, Temp, offset;

        int cy = dateLunar.getLunarYear();
        int cm = dateLunar.getLunarMonth();
        int cd = dateLunar.getLunarDay();
        boolean leapMonthFlag = dateLunar.getIsLeapMonth();

        checkChineseDateLimit(cy, cm, cd, leapMonthFlag);

        offset = 0;

        for (i = minYear; i < cy; i++)
        {
            Temp = getChineseYearDays(i); //求当年农历年天数
            offset = offset + Temp;
        }

        leap = getChineseLeapMonth(cy);// 计算该年应该闰哪个月
        boolean cIsLeapYear;
        if (leap != 0)
        {
            cIsLeapYear = true;
        }
        else
        {
            cIsLeapYear = false;
        }
        boolean cIsLeapMonth;
        if (cm != leap)
        {
            cIsLeapMonth = false;  //当前日期并非闰月
        }
        else
        {
            cIsLeapMonth = leapMonthFlag;  //使用用户输入的是否闰月月份
        }


        if ((cIsLeapYear == false) || //当年没有闰月
                (cm < leap)) //计算月份小于闰月
        {

            for (i = 1; i < cm; i++)
            {
                Temp = getChineseMonthDays(cy, i);//计算非闰月天数
                offset = offset + Temp;
            }

            //检查日期是否大于最大天
            if (cd > getChineseMonthDays(cy, cm))
            {
                new Exception("不合法的农历日期");
            }
            offset = offset + cd; //加上当月的天数

        }
        else   //是闰年，且计算月份大于或等于闰月
        {

            for (i = 1; i < cm; i++)
            {
                Temp = getChineseMonthDays(cy, i); //计算非闰月天数
                offset = offset + Temp;
            }

            if (cm > leap) //计算月大于闰月
            {
                Temp = getChineseLeapMonthDays(cy);   //计算闰月天数
                offset = offset + Temp;               //加上闰月天数

                if (cd > getChineseMonthDays(cy, cm))
                {
                    new Exception("不合法的农历日期");
                }
                offset = offset + cd;
            }
            else  //计算月等于闰月
            {
                //如果需要计算的是闰月，则应首先加上与闰月对应的普通月的天数
                if (cIsLeapMonth == true) //计算月为闰月
                {
                    Temp = getChineseMonthDays(cy, cm); //计算非闰月天数
                    offset = offset + Temp;
                }

                if (cd > getChineseLeapMonthDays(cy))
                {
                    new Exception("不合法的农历日期");
                }
                offset = offset + cd;
            }

        }


        DateExt date = new DateExt(minDate.getDate()).addDays(offset);

        return date;
    }

    /// <summary>
    /// 检查农历日期是否合理
    /// </summary>
    /// <param name="year"></param>
    /// <param name="month"></param>
    /// <param name="day"></param>
    /// <param name="leapMonth"></param>
    private void checkChineseDateLimit(int year, int month, int day, boolean leapMonth)
    {
        if ((year < minYear) || (year > minYear+lunarDateArray.length))
        {
            new Exception("非法农历日期");
        }
        if ((month < 1) || (month > 12))
        {
            new Exception("非法农历日期");
        }
        if ((day < 1) || (day > 30)) //中国的月最多30天
        {
            new Exception("非法农历日期");
        }

        int leap = getChineseLeapMonth(year);// 计算该年应该闰哪个月
        if ((leapMonth) && (month != leap))
        {
            new Exception("非法农历日期");
        }


    }



    public String toStringWithChineseMonth(int x) {
        String[] array = new String[]{
                "正", "二", "三", "四", "五", "六", "七", "八", "九","十", "十一", "腊"
        };

        return array[x-1];
    }

    static final String nStr1[] = new String[]{"一","二","三","四","五","六","七","八","九"};
    static final String nStr2[] = new String[]{"初","十","廿","卅"};

    public static String toChineseDayInWeek(int x) {
        int value = x % 10;
        if(value == 1)
            return "日";
        else
            return nStr1[value-2];
    }

    public String toStringWithChineseDay(int x)
    {
        switch (x)
        {
            case 0:
                return "";
            case 10:
                return "初十";
            case 20:
                return "二十";
            case 30:
                return "三十";
            default:
                return nStr2[(x / 10)] + nStr1[x % 10-1];

        }
    }

    public String toStringWithChineseYear(int x)
    {
        String tempStr = "";
        char[] num = Integer.toString(x).toCharArray();
        for (int i = 0; i < 4; i++)
        {
            tempStr += convertNumToChineseNum(num[i]);
        }
        return tempStr;
    }

    public String convertNumToChineseNum(char n)
    {
        String[] numArray = new String[] {"零","一","二","三","四","五","六","七","八","九"};

        if ((n < '0') || (n > '9')) return "";
        switch (n)
        {
            case '0':
                return numArray[0];
            case '1':
                return numArray[1];
            case '2':
                return numArray[2];
            case '3':
                return numArray[3];
            case '4':
                return numArray[4];
            case '5':
                return numArray[5];
            case '6':
                return numArray[6];
            case '7':
                return numArray[7];
            case '8':
                return numArray[8];
            case '9':
                return numArray[9];
            default:
                return "";
        }
    }

    public String getNaYinWuXing(int x) {
        String[] s = new String[]
                {"海中金", "炉中火", "大林木", "路旁土", "剑锋金", "山头火", "洞下水", "城墙土", "白腊金", "杨柳木", "泉中水",
                        "屋上土", "霹雷火", "松柏木", "常流水", "沙中金", "山下火", "平地木", "壁上土", "金箔金", "佛灯火", "天河水",
                        "大驿土", "钗钏金", "桑松木", "大溪水", "沙中土", "天上火", "石榴木", "大海水"};
        return s[(int) Math.floor((double) ((x - 1) / 2))];
    }




    public String getConstellation(DateExt dateExt) {
        int constellation = -1;
        int Y = dateExt.getMonth() * 100 + dateExt.getDay();
        if (((Y >= 321) && (Y <= 419))) {
            constellation = 0;
        } else if ((Y >= 420) && (Y <= 520)) {
            constellation = 1;
        } else if ((Y >= 521) && (Y <= 620)) {
            constellation = 2;
        } else if ((Y >= 621) && (Y <= 722)) {
            constellation = 3;
        } else if ((Y >= 723) && (Y <= 822)) {
            constellation = 4;
        } else if ((Y >= 823) && (Y <= 922)) {
            constellation = 5;
        } else if ((Y >= 923) && (Y <= 1022)) {
            constellation = 6;
        } else if ((Y >= 1023) && (Y <= 1121)) {
            constellation = 7;
        } else if ((Y >= 1122) && (Y <= 1221)) {
            constellation = 8;
        } else if ((Y >= 1222) || (Y <= 119)) {
            constellation = 9;
        } else if ((Y >= 120) && (Y <= 218)) {
            constellation = 10;
        } else if ((Y >= 219) && (Y <= 320)) {
            constellation = 11;
        }

        String con = "白羊金牛双子巨蟹狮子处女天秤天蝎射手摩羯水瓶双鱼";
        int index = 2 * constellation;
        return con.substring(index, index + 2) + "座";
    }

}
