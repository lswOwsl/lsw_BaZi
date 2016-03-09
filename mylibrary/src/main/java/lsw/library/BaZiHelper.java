package lsw.library;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by swli on 8/3/2015.
 */
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
        int temp = currentAge - beginYunAge;

        if(temp < 0)
            temp = 0;

        int indexYun = (temp)/10;

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

    static HashMap<String,String> hashMapNaYinFiveElements;

    public static String getNaYinFiveElement(String key)
    {
        if(hashMapNaYinFiveElements == null) {
            hashMapNaYinFiveElements = new HashMap<String, String>();
            hashMapNaYinFiveElements.put("甲子", "海中金");
            hashMapNaYinFiveElements.put("乙丑", "海中金");
            hashMapNaYinFiveElements.put("丙寅", "炉中火");
            hashMapNaYinFiveElements.put("丁卯", "炉中火");
            hashMapNaYinFiveElements.put("戊辰", "大林木");
            hashMapNaYinFiveElements.put("己巳", "大林木");
            hashMapNaYinFiveElements.put("庚午", "路旁土");
            hashMapNaYinFiveElements.put("辛未", "路旁土");
            hashMapNaYinFiveElements.put("壬申", "剑锋金");
            hashMapNaYinFiveElements.put("癸酉", "剑锋金");
            hashMapNaYinFiveElements.put("甲戌", "山头火");
            hashMapNaYinFiveElements.put("乙亥", "山头火");
            hashMapNaYinFiveElements.put("丙子", "涧下水");
            hashMapNaYinFiveElements.put("丁丑", "涧下水");
            hashMapNaYinFiveElements.put("戊寅", "城头土");
            hashMapNaYinFiveElements.put("己卯", "城头土");
            hashMapNaYinFiveElements.put("庚辰", "白蜡金");
            hashMapNaYinFiveElements.put("辛巳", "白蜡金");
            hashMapNaYinFiveElements.put("壬午", "杨柳木");
            hashMapNaYinFiveElements.put("癸未", "杨柳木");
            hashMapNaYinFiveElements.put("甲申", "泉中水");
            hashMapNaYinFiveElements.put("乙酉", "泉中水");
            hashMapNaYinFiveElements.put("丙戌", "屋上土");
            hashMapNaYinFiveElements.put("丁亥", "屋上土");
            hashMapNaYinFiveElements.put("戊子", "霹雳火");
            hashMapNaYinFiveElements.put("己丑", "霹雳火");
            hashMapNaYinFiveElements.put("庚寅", "松柏木");
            hashMapNaYinFiveElements.put("辛卯", "松柏木");
            hashMapNaYinFiveElements.put("壬辰", "长流水");
            hashMapNaYinFiveElements.put("癸巳", "长流水");
            hashMapNaYinFiveElements.put("甲午", "砂石金");
            hashMapNaYinFiveElements.put("乙未", "砂石金");
            hashMapNaYinFiveElements.put("丙申", "山下火");
            hashMapNaYinFiveElements.put("丁酉", "山下火");
            hashMapNaYinFiveElements.put("戊戌", "平地木");
            hashMapNaYinFiveElements.put("己亥", "平地木");
            hashMapNaYinFiveElements.put("庚子", "壁上土");
            hashMapNaYinFiveElements.put("辛丑", "壁上土");
            hashMapNaYinFiveElements.put("壬寅", "金箔金");
            hashMapNaYinFiveElements.put("癸卯", "金箔金");
            hashMapNaYinFiveElements.put("甲辰", "灯头火");
            hashMapNaYinFiveElements.put("乙巳", "灯头火");
            hashMapNaYinFiveElements.put("丙午", "天河水");
            hashMapNaYinFiveElements.put("丁未", "天河水");
            hashMapNaYinFiveElements.put("戊申", "大驿土");
            hashMapNaYinFiveElements.put("己酉", "大驿土");
            hashMapNaYinFiveElements.put("庚戌", "钗钏金");
            hashMapNaYinFiveElements.put("辛亥", "钗钏金");
            hashMapNaYinFiveElements.put("壬子", "桑柘木");
            hashMapNaYinFiveElements.put("癸丑", "桑柘木");
            hashMapNaYinFiveElements.put("甲寅", "大溪水");
            hashMapNaYinFiveElements.put("乙卯", "大溪水");
            hashMapNaYinFiveElements.put("丙辰", "沙中土");
            hashMapNaYinFiveElements.put("丁巳", "沙中土");
            hashMapNaYinFiveElements.put("戊午", "天上火");
            hashMapNaYinFiveElements.put("己未", "天上火");
            hashMapNaYinFiveElements.put("庚申", "石榴木");
            hashMapNaYinFiveElements.put("辛酉", "石榴木");
            hashMapNaYinFiveElements.put("壬戌", "大海水");
            hashMapNaYinFiveElements.put("癸亥", "大海水");
        }
        if(hashMapNaYinFiveElements.containsKey(key))
        {
             return hashMapNaYinFiveElements.get(key);
        }
        return null;
    }
}