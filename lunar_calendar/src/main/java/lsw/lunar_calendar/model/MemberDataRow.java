package lsw.lunar_calendar.model;

import lsw.library.DateExt;

/**
 * Created by swli on 5/6/2016.
 */
public class MemberDataRow {

    private int id;
    private String name;
    private DateExt birthday;
    private boolean isMale;

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
}
