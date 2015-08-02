package com.example.swli.myapplication20150519.activity.calendar;

import com.example.swli.myapplication20150519.common.DateExt;

/**
 * Created by swli on 7/24/2015.
 */
public class DayModel {

    private String day;
    private String lunar_day;
    private String era_day;
    private String formatDate;
    private boolean isCurrentMonth;
    private boolean isToday;
    private boolean isSelected;

    public boolean isToday() {
        return isToday;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public void setIsCurrentMonth(boolean isCurrentMonth) {
        this.isCurrentMonth = isCurrentMonth;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(String formatDate) {
        this.formatDate = formatDate;
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

    public DateExt getDateExt() {
        return new DateExt(formatDate);
    }
}
