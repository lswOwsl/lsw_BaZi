package lsw.lunar_calendar.model;

import lsw.library.DateExt;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class DayModel {

    private String day;
    private String lunar_day;
    private String era_day;
    private String formatDate;
    private boolean isCurrentMonth;
    private boolean isToday;
    private boolean isSelected;
    private boolean isSolarTerm;
    private String solarTermText;

    public String getSolarTermText() {
        return solarTermText;
    }

    public void setSolarTermText(String solarTermText) {
        this.solarTermText = solarTermText;
    }

    private boolean isWeekend;

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    private boolean showNotifyPoint;

    public boolean isShowNotifyPointBottom() {
        return showNotifyPointBottom;
    }

    public void setShowNotifyPointBottom(boolean showNotifyPointBottom) {
        this.showNotifyPointBottom = showNotifyPointBottom;
    }

    private boolean showNotifyPointBottom;

    public boolean isShowNotifyPoint() {
        return showNotifyPoint;
    }

    public void setShowNotifyPoint(boolean showNotifyPoint) {
        this.showNotifyPoint = showNotifyPoint;
    }

    public boolean isSolarTerm() {
        return isSolarTerm;
    }

    public void setIsSolarTerm(boolean isSolarTerm) {
        this.isSolarTerm = isSolarTerm;
    }

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
