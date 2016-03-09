package lsw.model;

import java.io.Serializable;

/**
 * Created by swli on 8/10/2015.
 */
public class EarthlyBranch implements Serializable{

    private int id;
    private String name;
    private EnumFiveElement fiveElement;

    public EarthlyBranch(){}
    public EarthlyBranch(int id, String name, EnumFiveElement fiveElement)
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
