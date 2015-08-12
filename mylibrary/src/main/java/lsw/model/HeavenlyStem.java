package lsw.model;

/**
 * Created by swli on 8/10/2015.
 */
public class HeavenlyStem {

    public int id;
    public String name;
    public EnumFiveElement fiveElement;

    public HeavenlyStem(){}

    public HeavenlyStem(int id, String name, EnumFiveElement fiveElement)
    {
        this.id = id;
        this.name = name;
        this.fiveElement = fiveElement;
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

    public EnumFiveElement getFiveElement() {
        return fiveElement;
    }

    public void setFiveElement(EnumFiveElement fiveElement) {
        this.fiveElement = fiveElement;
    }
}
