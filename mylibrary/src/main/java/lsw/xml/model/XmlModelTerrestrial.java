package lsw.xml.model;

import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlModelTerrestrial {

    public XmlModelTerrestrial() {}

    public HashMap<String, XmlModelExtProperty> getTerrestrials() {
        return terrestrials;
    }

    public HashMap<String, ArrayList<Pair<String,String>>> getTerrestrialHiddens() {
        return terrestrialHiddens;
    }

    public HashMap<Pair<String, String>, String> getSixSuits() {
        return sixSuits;
    }

    public ArrayList<Pair<String, String>> getSixInverses() {
        return sixInverses;
    }

    public HashMap<String, ArrayList<String>> getThreeSuits() {
        return threeSuits;
    }

    public HashMap<String, ArrayList<String>> getThreeConverge() {
        return threeConverge;
    }

    public ArrayList<Pair<String, String>> getPunishment() {
        return punishment;
    }

    public ArrayList<ArrayList<String>> getThreePunishment() {
        return threePunishment;
    }

    private HashMap<String, XmlModelExtProperty> terrestrials;
    private HashMap<String, ArrayList<Pair<String,String>>> terrestrialHiddens;
    private HashMap<Pair<String, String>, String> sixSuits;
    private ArrayList<Pair<String, String>> sixInverses;
    private HashMap<String,ArrayList<String>> threeSuits;
    private HashMap<String,ArrayList<String>> threeConverge;
    private ArrayList<Pair<String,String>> punishment;
    private ArrayList<ArrayList<String>> threePunishment;

    public void setPunishment(ArrayList<Pair<String, String>> punishment) {
        this.punishment = punishment;
    }

    public void setThreePunishment(ArrayList<ArrayList<String>> threePunishment) {
        this.threePunishment = threePunishment;
    }

    public void setTerrestrials(HashMap<String, XmlModelExtProperty> terrestrials) {
        this.terrestrials = terrestrials;
    }

    public void setTerrestrialHiddens(HashMap<String, ArrayList<Pair<String,String>>> terrestrialHiddens) {
        this.terrestrialHiddens = terrestrialHiddens;
    }

    public void setSixSuits(HashMap<Pair<String, String>, String> sixSuits) {
        this.sixSuits = sixSuits;
    }

    public void setSixInverses(ArrayList<Pair<String, String>> sixInverses) {
        this.sixInverses = sixInverses;
    }

    public void setThreeSuits(HashMap<String, ArrayList<String>> threeSuits) {
        this.threeSuits = threeSuits;
    }

    public void setThreeConverge(HashMap<String, ArrayList<String>> threeConverge) {
        this.threeConverge = threeConverge;
    }

    public String getFiveElementBy(String text)
    {
        return getTerrestrials().get(text).getWuXing();
    }

    public ArrayList<Pair<String,String>> getHiddenCelestialStem(String terrestrial)
    {
        return getTerrestrialHiddens().get(terrestrial);
    }

    public HashMap<Integer,String> getTerrestrialMaps() {
        HashMap<Integer,String> list = new HashMap<Integer, String>();
        for(String key: terrestrials.keySet())
        {
            int id = terrestrials.get(key).getId();
            String name = key;
            list.put(id,key);
        }
        return list;
    }
}
