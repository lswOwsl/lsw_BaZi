package lsw.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swli on 8/10/2015.
 */

public class Hexagram implements Serializable, Cloneable {
    private int id;
    private String place;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Trigram getUpper() {
        return upper;
    }

    public void setUpper(Trigram upper) {
        this.upper = upper;
    }

    public Trigram getLower() {
        return lower;
    }

    public void setLower(Trigram lower) {
        this.lower = lower;
    }

    public EnumFiveElement getFiveElement() {
        return fiveElement;
    }

    public void setFiveElement(EnumFiveElement fiveElement) {
        this.fiveElement = fiveElement;
    }

    public int getSelf() {
        return self;
    }

    public void setSelf(int self) {
        this.self = self;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    private String name;
    private Trigram upper;
    private Trigram lower;
    private EnumFiveElement fiveElement;
    private int self;
    private int target;

    public List<Line> getLines()
    {
        ArrayList<Line> lines = new ArrayList<Line>();
        for(Line line : getUpper().getLines())
        {
            lines.add(line);
        }
        for(Line line: getLower().getLines())
        {
            lines.add(line);
        }
        return lines;
    }

    public Hexagram deepClone() throws IOException, ClassNotFoundException{
        //将对象写到流里
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        //从流里读回来
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (Hexagram)ois.readObject();
    }
}