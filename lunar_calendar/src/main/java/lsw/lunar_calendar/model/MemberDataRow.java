package lsw.lunar_calendar.model;

import lsw.library.DateExt;
import lsw.library.DateLunar;
import lsw.library.LunarCalendarWrapper;

/**
 * Created by swli on 5/6/2016.
 */
public class MemberDataRow {

    private int id;
    private String name;
    private DateExt birthday;
    private boolean isMale;
    private boolean isLunarBirthday;

    public boolean isLunarBirthday() {
        return isLunarBirthday;
    }

    public void setIsLunarBirthday(boolean isLunarBirthday) {
        this.isLunarBirthday = isLunarBirthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateExt getBirthday() {
        return birthday;
    }

    public void setBirthday(DateExt birthday) {
        this.birthday = birthday;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setIsMale(boolean isMale) {
        this.isMale = isMale;
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
