package lsw.lunar_calendar.common;

/**
 * Created by swli on 6/2/2016.
 */
public enum RecordType {

    Forecast(0),
    Note(1),
    All(2);

    private int value = 0;

    private RecordType(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static RecordType valueOf(int value) {
        switch (value) {
            case 0:
                return Forecast;
            case 1:
                return Note;
            case 2:
                return All;
            default:
                return null;
        }
    }

    public int value() {
        return this.value;
    }
}
