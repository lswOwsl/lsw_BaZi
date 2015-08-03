package lsw.library;

/**
 * Created by swli on 8/3/2015.
 */
public class DateLunar {

    private int lunarYear;
    private int lunarMonth;
    private int lunarDay;
    private boolean isLeapMonth;

    public boolean getIsLeapMonth() {
        return isLeapMonth;
    }

    public void setIsLeapMonth(boolean isLeapMonth) {
        this.isLeapMonth = isLeapMonth;
    }

    public void setLunarYear(int lunarYear) {
        this.lunarYear = lunarYear;
    }

    public void setLunarMonth(int lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    public void setLunarDay(int lunarDay) {
        this.lunarDay = lunarDay;
    }

    public int getLunarYear() {

        return lunarYear;
    }

    public int getLunarMonth() {
        return lunarMonth;
    }

    public int getLunarDay() {
        return lunarDay;
    }
}
