package lsw.xml.model;

import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swli on 6/5/2015.
 */
public class XmlModelCelestialStem {

    public XmlModelCelestialStem() {}

    private HashMap<String, XmlModelExtProperty> celestialStems;
    private HashMap<Pair<String, String>, String> pairSuits;
    private ArrayList<Pair<String, String>> pairInverses;

    public void setCelestialStems(HashMap<String, XmlModelExtProperty> celestialStems) {
        this.celestialStems = celestialStems;
    }

    public void setPairSuits(HashMap<Pair<String, String>, String> pairSuits) {
        this.pairSuits = pairSuits;
    }

    public void setPairInverses(ArrayList<Pair<String, String>> pairInverses) {
        this.pairInverses = pairInverses;
    }

    public HashMap<String, XmlModelExtProperty> getCelestialStems() {
        return celestialStems;
    }

    public HashMap<Pair<String, String>, String> getPairSuits() {
        return pairSuits;
    }

    public ArrayList<Pair<String, String>> getPairInverses() {
        return pairInverses;
    }

    public String getFiveElementBy(String text)
    {
        return getCelestialStems().get(text).getWuXing();
    }

}
