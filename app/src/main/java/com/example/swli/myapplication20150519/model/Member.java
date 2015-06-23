package com.example.swli.myapplication20150519.model;

import com.example.swli.myapplication20150519.activity.sidebar.SortModel;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.DateLunar;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;

/**
 * Created by swli on 5/27/2015.
 */
public class Member {
    private int id;
    private boolean isMale;
    private DateExt birthday;
    private String name;
    private String note;
    private SortModel sortModel;

    public SortModel getSortModel() {
        return sortModel;
    }

    public void setSortModel(SortModel sortModel) {
        this.sortModel = sortModel;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public void setIsMale(boolean isMale)
    {
        this.isMale = isMale;
    }

    public boolean getIsMale()
    {
        return this.isMale;
    }

    public void setBirthday(DateExt birthday)
    {
        this.birthday = birthday;
    }

    public DateExt getBirthday()
    {
        return  this.birthday;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return  this.name;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public String getNote()
    {
        return this.note;
    }

    public String getGender()
    {
        if(getIsMale())
            return "男";
        else
            return "女";
    }

    public String getLunarBirthday()
    {
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(getBirthday());
        DateLunar dateLunar = lunarCalendarWrapper.getDateLunar(this.getBirthday());
        if(dateLunar != null) {

            return lunarCalendarWrapper.toStringWithChineseYear(dateLunar.getLunarYear()) + "年" +
                   (dateLunar.getIsLeapMonth() ? "闰":"") +
                   lunarCalendarWrapper.toStringWithChineseMonth(dateLunar.getLunarMonth()) + "月" +
                   lunarCalendarWrapper.toStringWithChineseDay(dateLunar.getLunarDay());
        }
        else
        {
            return "";
        }
    }
}
