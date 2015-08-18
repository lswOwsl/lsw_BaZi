package lsw.liuyao.model;

/**
 * Created by swli on 8/18/2015.
 */
public class HexagramRow {
    private String originalName;
    private String changedName;
    private String date;
    private int id;

    public HexagramRow()
    {

    }

    public HexagramRow(int id, String originalName, String changedName, String date)
    {
        this.originalName = originalName;
        this.changedName = changedName;
        this.date = date;
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getChangedName() {
        return changedName;
    }

    public void setChangedName(String changedName) {
        this.changedName = changedName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
