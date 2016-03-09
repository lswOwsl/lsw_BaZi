package lsw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swli on 8/10/2015.
 */
public class Trigram implements Serializable {

    private String name;
    private EnumFiveElement fiveElement;

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

    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    private ArrayList<Line> lines;
}
