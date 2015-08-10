package lsw.model;

/**
 * Created by swli on 8/10/2015.
 */
public class EarthlyBranch {

    public int id;
    public String name;
    public EnumFiveElement fiveElement;

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
