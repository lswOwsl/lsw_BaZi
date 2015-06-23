package com.example.swli.myapplication20150519.model;

import android.content.Context;
import android.util.Pair;

import com.example.swli.myapplication20150519.common.XmlParserCelestialStem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swli on 6/5/2015.
 */
public class XmlCelestialStem {

    private static XmlCelestialStem xmlCelestialStem;

    private XmlCelestialStem() {}

    private HashMap<String, XmlExtProperty> celestialStems;
    private HashMap<Pair<String, String>, String> pairSuits;
    private ArrayList<Pair<String, String>> pairInverses;

    private void setCelestialStems(HashMap<String, XmlExtProperty> celestialStems) {
        this.celestialStems = celestialStems;
    }

    private void setPairSuits(HashMap<Pair<String, String>, String> pairSuits) {
        this.pairSuits = pairSuits;
    }

    private void setPairInverses(ArrayList<Pair<String, String>> pairInverses) {
        this.pairInverses = pairInverses;
    }

    public HashMap<String, XmlExtProperty> getCelestialStems() {
        return celestialStems;
    }

    public HashMap<Pair<String, String>, String> getPairSuits() {
        return pairSuits;
    }

    public ArrayList<Pair<String, String>> getPairInverses() {
        return pairInverses;
    }

    public static XmlCelestialStem getInstance(Context context)
    {
        if(xmlCelestialStem == null) {
            xmlCelestialStem = new XmlCelestialStem();
            XmlParserCelestialStem xpcs = new XmlParserCelestialStem(context);

            HashMap<String, XmlExtProperty> celestialStems = new HashMap<String, XmlExtProperty>();
            HashMap<Pair<String, String>, String> pairSuits = new HashMap<Pair<String, String>, String>();
            ArrayList<Pair<String, String>> pairInverses = new ArrayList<Pair<String, String>>();

            xmlCelestialStem.setCelestialStems(celestialStems);
            xmlCelestialStem.setPairInverses(pairInverses);
            xmlCelestialStem.setPairSuits(pairSuits);

            xpcs.parse(xmlCelestialStem);
        }
        return xmlCelestialStem;
    }

    public String getFiveElementBy(String text)
    {
        return getCelestialStems().get(text).getWuXing();
    }

}
