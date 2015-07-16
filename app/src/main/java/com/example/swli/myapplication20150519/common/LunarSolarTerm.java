package com.example.swli.myapplication20150519.common;

import android.util.Pair;

/**
 * Created by swli on 6/5/2015.
 */
public class LunarSolarTerm {

    public static String[] lunarHoliDayName = {
            "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
            "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};

    public int getChineseEraOfYear(DateExt solarDateTime) {

        int g = (solarDateTime.getYear() - 4) % 60;
        if ((dayDifference(solarDateTime.getYear(), solarDateTime.getMonth(), solarDateTime.getDay()) + solarDateTime.getHour() / 24)
                < term(solarDateTime.getYear(), 3, true) - 1) {
            g -= 1;
        }
        return g + 1;
    }

    public int getChineseEraOfMonth(DateExt solarDateTime) {
        int v = (solarDateTime.getYear() * 12 + solarDateTime.getMonth() + 12) % 60;
        if (solarDateTime.getDay() < getSolarTerm(solarDateTime)[0].getSolarTermDate().getDay())
            v -= 1;
        return v + 1;
    }


    public int getChineseEraOfDay(DateExt solarDateTime) {
        double gzD =
                //(solarDateTime.getHour() < 23)
                //?
        equivalentStandardDay(solarDateTime.getYear(), solarDateTime.getMonth(), solarDateTime.getDay());
                //: equivalentStandardDay(solarDateTime.getYear(), solarDateTime.getMonth(), solarDateTime.getDay()) + 1;
        return (int) Math.round((double) rem((int) gzD + 15, 60));
    }


    public int getChineseEraOfHour(DateExt solarDateTime) {
        double v = 12 * gan(getChineseEraOfDay(solarDateTime)) + Math.floor((double) ((solarDateTime.getHour() + 1) / 2)) - 11;
        //早子夜
        //if (solarDateTime.getHour() == 23)
          //  v -= 12;
        return (int) Math.round(rem(v, 60));
    }

    public String toStringWithCelestialStem(int x) {
        return getCelestialStem(x);
    }


    public String toStringWithTerrestrialBranch(int x) {
        return getTerrestrialBranch(x);
    }

    public static String getCelestialStem(int x)
    {
        int index = x % 10;
        return "癸甲乙丙丁戊己庚辛壬".substring(index, index + 1);
    }

    public static String getTerrestrialBranch(int x)
    {
        int index = x % 12;
        return "亥子丑寅卯辰巳午未申酉戌".substring(index, index + 1);
    }


    public String toStringWithSexagenary(int x) {
        return toStringWithCelestialStem(x) + toStringWithTerrestrialBranch(x);
    }


    public SolarTerm[] getSolarTerm(int year, int month) {

        SolarTerm[] solarTerm = new SolarTerm[2];

        for (int n = month * 2 - 1; n <= month * 2; n++) {
            SolarTerm st = new SolarTerm();
            double dd = term(year, n, true);
            double sd1 = antiDayDifference(year, Math.floor(dd));
            //double sm1 = Math.Floor(sd1 / 100);
            int h = (int) Math.floor(tail(dd) * 24);
            int min = (int) Math.floor((tail(dd) * 24 - h) * 60);
            int mmonth = (int) Math.ceil((double) n / 2);
            int day = (int) sd1 % 100;
            st.setSolarTermDateTime(new DateExt(year, mmonth, day, h, min, 0));
            st.setName(lunarHoliDayName[n - 1]);
            solarTerm[n - month * 2 + 1] = st;
        }
        return solarTerm;
    }

    public SolarTerm[] getSolarTerm(DateExt solarDateTime) {
        return getSolarTerm(solarDateTime.getYear(), solarDateTime.getMonth());
    }

    public Pair<SolarTerm,SolarTerm> getPairSolarTerm(DateExt dateExt)
    {
        SolarTerm[] solarTerms = getSolarTerm(dateExt);
        return Pair.create(solarTerms[0],solarTerms[1]);
    }

    protected double equivalentStandardDay(int y, int m, int d) {
        double v = (y - 1) * 365 + Math.floor((double) ((y - 1) / 4)) + dayDifference(y, m, d) - 2;  //Julian的等效标准天数
        if (y > 1582)
            v += -Math.floor((double) ((y - 1) / 100)) + Math.floor((double) ((y - 1) / 400)) + 2;  //Gregorian的等效标准天数
        return v;
    }

    protected double antiDayDifference(int y, double x) {
        int m = 1;
        for (int j = 1; j <= 12; j++) {
            int mL = dayDifference(y, j + 1, 1) - dayDifference(y, j, 1);
            if (x <= mL || j == 12) {
                m = j;
                break;
            } else
                x -= mL;
        }
        return 100 * m + x;
    }

    protected double rem(double x, double w) {
        return tail((x / w)) * w;
    }


    protected int gan(int x) {
        return x % 10;
    }

    //原始：tail
    /// <summary>
    /// 返回x的小数尾数，若x为负值，则是1-小数尾数
    /// </summary>
    protected double tail(double x) {
        return x - Math.floor(x);
    }

    //原始：S
    /// <summary>
    ///返回y年第n个节气（如小寒为1）的日差天数值（pd取值真假，分别表示平气和定气）
    /// </summary>
    protected double term(int y, int n, Boolean pd) {
        double juD = y * (365.2423112 - 6.4e-14 * (y - 100) * (y - 100) - 3.047e-8 * (y - 100)) + 15.218427 * n + 1721050.71301;//儒略日
        double tht = 3e-4 * y - 0.372781384 - 0.2617913325 * n;//角度
        double yrD = (1.945 * Math.sin(tht) - 0.01206 * Math.sin(2 * tht)) * (1.048994 - 2.583e-5 * y);//年差实均数
        double shuoD = -18e-4 * Math.sin(2.313908653 * y - 0.439822951 - 3.0443 * n);//朔差实均数
        double vs = (pd) ? (juD + yrD + shuoD - equivalentStandardDay(y, 1, 0) - 1721425) : (juD - equivalentStandardDay(y, 1, 0) - 1721425);
        return vs;
    }


    protected int dayDifference(int y, int m, int d) {
        int ifG = ifGregorian(y, m, d, 1);
        int[] monL = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (ifG == 1)
            if ((y % 100 != 0 && y % 4 == 0) || (y % 400 == 0))
                monL[2] += 1;
            else if (y % 4 == 0)
                monL[2] += 1;
        int v = 0;
        for (int i = 0; i <= m - 1; i++) {
            v += monL[i];
        }
        v += d;
        if (y == 1582) {
            if (ifG == 1)
                v -= 10;
            if (ifG == -1)
                v = 0;  //infinity
        }
        return v;
    }

    protected int ifGregorian(int y, int m, int d, int opt) {
        if (opt == 1) {
            if (y > 1582 || (y == 1582 && m > 10) || (y == 1582 && m == 10 && d > 14))
                return (1);  //Gregorian
            else if (y == 1582 && m == 10 && d >= 5 && d <= 14)
                return (-1);  //空
            else
                return (0);  //Julian
        }

        if (opt == 2)
            return (1);  //Gregorian
        if (opt == 3)
            return (0);  //Julian
        return (-1);
    }
}
