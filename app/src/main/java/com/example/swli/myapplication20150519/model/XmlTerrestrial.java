package com.example.swli.myapplication20150519.model;

import android.content.Context;
import android.util.Pair;

import com.example.swli.myapplication20150519.common.XmlParserTerrestrial;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlTerrestrial {

    private static XmlTerrestrial xmlTerrestrial;

    private XmlTerrestrial() {}

    public HashMap<String, XmlExtProperty> getTerrestrials() {
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

    private HashMap<String, XmlExtProperty> terrestrials;
    private HashMap<String, ArrayList<Pair<String,String>>> terrestrialHiddens;
    private HashMap<Pair<String, String>, String> sixSuits;
    private ArrayList<Pair<String, String>> sixInverses;
    private HashMap<String,ArrayList<String>> threeSuits;
    private HashMap<String,ArrayList<String>> threeConverge;
    private ArrayList<Pair<String,String>> punishment;
    private ArrayList<ArrayList<String>> threePunishment;

    private void setPunishment(ArrayList<Pair<String, String>> punishment) {
        this.punishment = punishment;
    }

    private void setThreePunishment(ArrayList<ArrayList<String>> threePunishment) {
        this.threePunishment = threePunishment;
    }

    private void setTerrestrials(HashMap<String, XmlExtProperty> terrestrials) {
        this.terrestrials = terrestrials;
    }

    private void setTerrestrialHiddens(HashMap<String, ArrayList<Pair<String,String>>> terrestrialHiddens) {
        this.terrestrialHiddens = terrestrialHiddens;
    }

    private void setSixSuits(HashMap<Pair<String, String>, String> sixSuits) {
        this.sixSuits = sixSuits;
    }

    private void setSixInverses(ArrayList<Pair<String, String>> sixInverses) {
        this.sixInverses = sixInverses;
    }

    private void setThreeSuits(HashMap<String, ArrayList<String>> threeSuits) {
        this.threeSuits = threeSuits;
    }

    private void setThreeConverge(HashMap<String, ArrayList<String>> threeConverge) {
        this.threeConverge = threeConverge;
    }



    public static XmlTerrestrial getInstance(Context context)
    {
        if(xmlTerrestrial == null) {
            xmlTerrestrial = new XmlTerrestrial();
            XmlParserTerrestrial xpt = new XmlParserTerrestrial(context);

            HashMap<String, XmlExtProperty> terrestrials = new HashMap<String, XmlExtProperty>();
            HashMap<String, ArrayList<Pair<String,String>>> terrestrialHiddens = new HashMap<String, ArrayList<Pair<String,String>>>();
            HashMap<Pair<String, String>, String> sixSuits = new HashMap<Pair<String, String>, String>();
            ArrayList<Pair<String, String>> sixInverses = new ArrayList<Pair<String, String>>();
            HashMap<String,ArrayList<String>> threeSuits = new HashMap<String, ArrayList<String>>();
            HashMap<String,ArrayList<String>> threethreeConverge = new HashMap<String, ArrayList<String>>();
            ArrayList<Pair<String,String>> punishment = new ArrayList<Pair<String, String>>();
            ArrayList<ArrayList<String>> threePunishment = new ArrayList<ArrayList<String>>();

            xmlTerrestrial.setSixInverses(sixInverses);
            xmlTerrestrial.setSixSuits(sixSuits);
            xmlTerrestrial.setTerrestrialHiddens(terrestrialHiddens);
            xmlTerrestrial.setTerrestrials(terrestrials);
            xmlTerrestrial.setThreeSuits(threeSuits);
            xmlTerrestrial.setThreeConverge(threethreeConverge);
            xmlTerrestrial.setPunishment(punishment);
            xmlTerrestrial.setThreePunishment(threePunishment);

            xpt.parse(xmlTerrestrial);
        }
        return xmlTerrestrial;
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
