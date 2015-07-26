package com.example.swli.myapplication20150519.activity.calendar;

import com.example.swli.myapplication20150519.common.DateExt;

/**
 * Created by swli on 7/24/2015.
 */
public class DayModel {

    private String day;
    private String lunar_day;
    private String era_day;
    private DateExt dateExt;

    public DateExt getDateExt() {
        return dateExt;
    }

    public void setDateExt(DateExt dateExt) {
        this.dateExt = dateExt;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLunar_day() {
        return lunar_day;
    }

    public void setLunar_day(String lunar_day) {
        this.lunar_day = lunar_day;
    }

    public String getEra_day() {
        return era_day;
    }

    public void setEra_day(String era_day) {
        this.era_day = era_day;
    }
}
