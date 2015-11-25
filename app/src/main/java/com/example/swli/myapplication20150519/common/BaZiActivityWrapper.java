package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.util.Pair;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import lsw.library.ColorHelper;
import lsw.library.DateExt;
import lsw.library.LunarCalendar;
import lsw.library.LunarCalendarWrapper;
import lsw.xml.XmlModelCache;
import lsw.xml.model.XmlModelCelestialStem;
import lsw.xml.model.XmlModelExtProperty;
import lsw.xml.model.XmlModelFiveElement;
import lsw.xml.model.XmlModelSixRelation;
import lsw.xml.model.XmlModelTerrestrial;

public class BaZiActivityWrapper {
    DateExt birthday;
    LunarCalendarWrapper calendarWrapper;
    int indexEraDay;
    int indexEraMonth;
    int indexEraYear;
    int indexEraHour;
    Context context;
    boolean isMale;
    Pair<Integer,Integer> beginYunAgeAndMonth;
    int beginYunAge;
    XmlModelCache xmlModelCache;
    ColorHelper colorHelper;

    public BaZiActivityWrapper(DateExt birthday, Context context, boolean isMale)
    {
        this.context = context;
        this.birthday = birthday;
        this.calendarWrapper = new LunarCalendarWrapper(birthday);
        this.indexEraDay = calendarWrapper.getChineseEraOfDay();
        this.indexEraMonth = calendarWrapper.getChineseEraOfMonth();
        this.indexEraHour = calendarWrapper.getChineseEraOfHour();
        this.indexEraYear = calendarWrapper.getChineseEraOfYear();
        this.isMale = isMale;
        this.beginYunAgeAndMonth = getBeginYunAgeMonth();
        xmlModelCache = XmlModelCache.getInstance(context);
        colorHelper = ColorHelper.getInstance(context);
    }
    public Pair<Integer,Integer> getBeginYunAge_Month()
    {
        return beginYunAgeAndMonth;
    }
    public int getBeginYunAge()
    {
        return beginYunAge;
    }

    public int getDayEraIndex()
    {
        return this.indexEraDay;
    }

    public int getMonthEraIndex()
    {
        return this.indexEraMonth;
    }

    public int getYearEraIndex()
    {
        return this.indexEraYear;
    }

    public int getHourEraIndex()
    {
        return this.indexEraHour;
    }

    public int getFlowYearEraIndex(int year, int month, int day)
    {
        LunarCalendar lunarCalendar = new LunarCalendar();
        return lunarCalendar.getChineseEraOfYear(new DateExt(year, month, day, 0, 0, 0));
    }

    public int getYearEraIndex(int numOfYearsFromBirth)
    {
        return this.indexEraYear + numOfYearsFromBirth;
    }

    public String getC(int eraIndex)
    {
        return calendarWrapper.toStringWithCelestialStem(eraIndex);
    }

    public String getT(int eraIndex)
    {
        return  calendarWrapper.toStringWithTerrestrialBranch(eraIndex);
    }

    public void setControl(TextView tvC, TextView tvT, TextView tvSixR, TextView tvH, int eraIndex)
    {
        String dayC = calendarWrapper.toStringWithCelestialStem(indexEraDay);

        tvC.setText(colorHelper.getColorCelestialStem(getC(eraIndex)));
        tvT.setText(colorHelper.getColorTerrestrial(getT(eraIndex)));
        setSixRelationByRiZhu(tvSixR, dayC, getC(eraIndex));
        setHiddenCelestialControl(tvH, dayC, getT(eraIndex));
    }

    public String getSixRelationByRiZhu(String riZhu,String celestialStem)
    {
        XmlModelCelestialStem xmlCelestialStem = xmlModelCache.getCelestialStem();
        XmlModelExtProperty riZhuEP = xmlCelestialStem.getCelestialStems().get(riZhu);
        XmlModelExtProperty celestialStemEP = xmlCelestialStem.getCelestialStems().get(celestialStem);

        XmlModelFiveElement xmlFiveElement = xmlModelCache.getFiveElement();
        String riZhuEhance = xmlFiveElement.getEnhance().get(riZhuEP.getWuXing());
        String riZhuConsume = xmlFiveElement.getEnhance().get(riZhuEhance);
        String riZhuControled = xmlFiveElement.getControl().get(riZhuEhance);

        XmlModelSixRelation xmlSixRelation = xmlModelCache.getSixRelation();

        boolean flag = riZhuEP.getYinYang().equals(celestialStemEP.getYinYang());

        if(riZhuEP.getWuXing().equals(celestialStemEP.getWuXing()))
            return xmlSixRelation.getRelation().get("equal").get(flag);
        else if(celestialStemEP.getWuXing().equals(riZhuEhance))
            return xmlSixRelation.getRelation().get("enhance").get(flag);
        else if(celestialStemEP.getWuXing().equals(riZhuConsume))
            return xmlSixRelation.getRelation().get("consume").get(flag);
        else if(celestialStemEP.getWuXing().equals(riZhuControled))
            return xmlSixRelation.getRelation().get("control").get(flag);
        else
            return xmlSixRelation.getRelation().get("support").get(flag);

    }

    public void setSixRelationByRiZhu(TextView textView, String riZhu,String celestialStem)
    {
        textView.setText(getSixRelationByRiZhu(riZhu, celestialStem));
    }

    public void setHiddenCelestialControl(TextView textView, String riZhu, String terrestrial)
    {
        textView.setText("");
        XmlModelTerrestrial xmlTerrestrial = xmlModelCache.getTerrestrial();
        ArrayList<Pair<String,String>> arrayList =  xmlTerrestrial.getHiddenCelestialStem(terrestrial);
        int size = arrayList.size();
        //判断size是为了让文字居中排列
        for (int i=0;i< size ;i++) {
            if (size == 1)
                textView.append("\n");

            Pair<String, String> pair = arrayList.get(i);
            textView.append(colorHelper.getColorCelestialStem( pair.first));
            textView.append(":");
            textView.append(getSixRelationByRiZhu(riZhu, pair.first));

            if (size == 1)
                textView.append("\n");
            else if (size == 2)
                textView.append("\n");
            else if (size == 3 && arrayList.lastIndexOf(pair) != 2)
                textView.append("\n");
        }
    }

    private String getJiaGongByList(HashMap<String,ArrayList<String>> list, Pair<String,String> str12) {
        for (String key : list.keySet()) {
            ArrayList<String> temp = new ArrayList<String>();

            temp.addAll(list.get(key));
            if (temp.contains(str12.first))
                temp.remove(str12.first);
            if (temp.contains(str12.second))
                temp.remove(str12.second);
            if (temp.size() == 1) {
                return temp.get(0);
            }
        }
        return null;
    }

    public String getJiaGong(String cs1, String cs2, String t1,String t2)
    {
        if(cs1.equals(cs2) && !t1.equals(t2)) {
            XmlModelTerrestrial xmlTerrestrial = xmlModelCache.getTerrestrial();

             String converge = getJiaGongByList(xmlTerrestrial.getThreeConverge(), Pair.create(t1, t2));
            if(converge != null)
                return converge;

            String suit = getJiaGongByList(xmlTerrestrial.getThreeSuits(), Pair.create(t1, t2));
            if(suit != null && xmlTerrestrial.getSpecial().get("四旺").contains(suit))
                return suit;

            HashMap<Integer,String> map = xmlTerrestrial.getTerrestrialMaps();
            int t1Index = xmlTerrestrial.getTerrestrials().get(t1).getId();
            int t2Index = xmlTerrestrial.getTerrestrials().get(t2).getId();
            if (t1Index + 2 == t2Index) {
                return map.get(t1Index + 1);
            }
            if (t1Index - 2 == t2Index) {
                return map.get(t1Index - 1);
            }
            if ((t1Index == 12 && t2Index == 2) || (t2Index == 12 && t1Index == 2)) {
                return map.get(1);

            }
            if ((t1Index == 11 && t2Index == 1) || (t2Index == 11 && t1Index == 1)) {
                 return map.get(12);

            }
        }
        return null;
    }

    public void setJiaGong(TextView textView, Pair<EnumPart,EnumPart> pair, Integer daYunEraIndex, Integer flowYearEraIndex)
    {
        HashMap<Pair<EnumPart,EnumPart>,String> list = getJiaGong(flowYearEraIndex,daYunEraIndex);
        if(list.containsKey(pair))
        {
            String str = list.get(pair);
            textView.setText(colorHelper.getColorTerrestrial(str));
        }
    }

    public void setJiaGong(TextView textView, Pair<EnumPart,EnumPart> pair, Integer daYunIndex)
    {
        setJiaGong(textView,pair,daYunIndex,null);
    }

    public void setJiaGong(TextView textView, Pair<EnumPart,EnumPart> pair)
    {
        setJiaGong(textView,pair,null);
    }

    public HashMap<Pair<EnumPart,EnumPart>,String> getJiaGong(Integer flowYearEraIndex, Integer daYunEraIndex)
    {
        String str;
        HashMap<Pair<EnumPart,EnumPart>,String> list = new HashMap<Pair<EnumPart, EnumPart>, String>();

        if(flowYearEraIndex != null)
        {
            str = getJiaGong(getC(flowYearEraIndex),getC(daYunEraIndex),getT(flowYearEraIndex),getT(daYunEraIndex));
            addToList(list, str, Pair.create(EnumPart.FlowYear, EnumPart.DaYun));

            str = getJiaGong(getC(flowYearEraIndex),getC(indexEraYear),getT(flowYearEraIndex),getT(indexEraYear));
            addToList(list, str, Pair.create(EnumPart.FlowYear, EnumPart.Year));

            str = getJiaGong(getC(flowYearEraIndex),getC(indexEraMonth),getT(flowYearEraIndex),getT(indexEraMonth));
            addToList(list, str, Pair.create(EnumPart.FlowYear, EnumPart.Month));

            str = getJiaGong(getC(flowYearEraIndex),getC(indexEraDay),getT(flowYearEraIndex),getT(indexEraDay));
            addToList(list, str, Pair.create(EnumPart.FlowYear, EnumPart.Day));

            str = getJiaGong(getC(flowYearEraIndex),getC(indexEraHour),getT(flowYearEraIndex),getT(indexEraHour));
            addToList(list, str, Pair.create(EnumPart.FlowYear, EnumPart.Hour));
        }

        if(daYunEraIndex != null)
        {
            str = getJiaGong(getC(daYunEraIndex),getC(indexEraYear),getT(daYunEraIndex),getT(indexEraYear));
            addToList(list, str, Pair.create(EnumPart.DaYun, EnumPart.Year));

            str = getJiaGong(getC(daYunEraIndex),getC(indexEraMonth),getT(daYunEraIndex),getT(indexEraMonth));
            addToList(list, str, Pair.create(EnumPart.DaYun, EnumPart.Month));

            str = getJiaGong(getC(daYunEraIndex),getC(indexEraDay),getT(daYunEraIndex),getT(indexEraDay));
            addToList(list, str, Pair.create(EnumPart.DaYun, EnumPart.Day));

            str = getJiaGong(getC(daYunEraIndex),getC(indexEraHour),getT(daYunEraIndex),getT(indexEraHour));
            addToList(list, str, Pair.create(EnumPart.DaYun, EnumPart.Hour));
        }

        str = getJiaGong(getC(indexEraYear),getC(indexEraMonth),getT(indexEraYear),getT(indexEraMonth));
        addToList(list, str, Pair.create(EnumPart.Year, EnumPart.Month));

        str =getJiaGong(getC(indexEraMonth), getC(indexEraDay), getT(indexEraMonth), getT(indexEraDay));
        addToList(list, str, Pair.create(EnumPart.Month, EnumPart.Day));

        str =getJiaGong(getC(indexEraDay), getC(indexEraHour), getT(indexEraDay), getT(indexEraHour));
        addToList(list, str, Pair.create(EnumPart.Day, EnumPart.Hour));

        return list;
    }

    private String addToList(HashMap<Pair<EnumPart,EnumPart>,String> list, String str, Pair<EnumPart,EnumPart> key)
    {
        if(str != null)
            list.put(key,str);
        return null;
    }

    public ArrayList<Integer> getDaYuns(int count)
    {
        return BaZiHelperExtender.getDaYunsByNum(getYearEraIndex(), getMonthEraIndex(), count, isMale);
    }

    private Pair<Integer,Integer> getBeginYunAgeMonth() {
        //起运年龄
        int hoursQiYun = Math.abs(BaZiHelperExtender.getBeginYunHours(birthday, isMale, getYearEraIndex()));
        //三天一岁，1天4个月，1个时辰10天
        int ageQiYun = (int) ((double) hoursQiYun / 24) / 3;
        int tempAge = ageQiYun;
        int monthQiYun = (int) ((double) hoursQiYun / 24 % 3 * 4);
        //起运的天数余多少小时
        int hourQiYun = hoursQiYun % 24;
        //两个小时一个时辰，所以先除2，乘以10是因为一个时辰10天，如果天数超过一个月，那年龄就应该加1岁，是为了显示用
        int dayQiYun = (int) ((double) hourQiYun / 2 * 10 / 30);
        if (dayQiYun != 0) {
            ageQiYun += 1;
        }
        this.beginYunAge = ageQiYun;
        return Pair.create(tempAge,monthQiYun);
    }

    public int getDaYunByFlowYear(int flowYear, int beginYunAge)
    {
        int currentAge = flowYear - birthday.getYear();
        ArrayList<Integer> daYuns = getDaYuns(currentAge/10+1);
        //因为有的人是0岁几个月起运，所以不能按0岁起运看，应该按1岁起运看
        return BaZiHelperExtender.getDaYunByFlowYear(currentAge, beginYunAge, daYuns);
    }
}

