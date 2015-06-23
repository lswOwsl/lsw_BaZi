package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.model.XmlCelestialStem;
import com.example.swli.myapplication20150519.model.XmlExtProperty;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swli on 6/5/2015.
 */
public class XmlParserCelestialStem extends XmlParser<XmlCelestialStem> {

    public XmlParserCelestialStem(Context context) {
        super(context);
    }

    public void parse(XmlCelestialStem xmlCelestialStem) {
        try {
            HashMap<String, XmlExtProperty> celestialStems = null;
            HashMap<Pair<String, String>, String> pairSuits = null;
            ArrayList<Pair<String, String>> pairInverses = null;

            String pairWuXing = null;
            String name1 = null;
            String name2 = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(this.context.getResources().openRawResource(R.raw.celestial_stem), "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        celestialStems = xmlCelestialStem.getCelestialStems();
                        pairSuits = xmlCelestialStem.getPairSuits();
                        pairInverses= xmlCelestialStem.getPairInverses();
                        break;
                    }
                    case XmlPullParser.START_TAG:{
                        if(parser.getName().equals("CelestrialStem")){
                            XmlExtProperty xet = new XmlExtProperty();
                            xet.setYinYang(parser.getAttributeValue("", "yinYang"));
                            xet.setWuXing(parser.getAttributeValue("", "wuXing"));
                            celestialStems.put(parser.getAttributeValue("", "name"),xet);
                        }
                        else if(parser.getName().equals("PairSuit")){
                            pairWuXing = parser.getAttributeValue("","wuXing");
                        }
                        else if(parser.getName().equals("Name"))
                        {
                            if(name1 == null)
                                name1 = parser.nextText();
                            else
                                name2 = parser.nextText();
                        }
                        //else if(parser.getName().equals("PairInverse")){
                          //  if(name1 == null)
                            //    name1 = parser.getText();
                           // else
                             //   name2 = parser.getText();
                            //parser.next();
                        //}
                        break;
                    }
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("PairSuit"))
                        {
                           pairSuits.put(Pair.create(name1, name2),pairWuXing);
                           name1 = name2 = null;
                            pairWuXing = null;
                        }
                        else if(parser.getName().equals("PairInverse"))
                        {
                            pairInverses.add(Pair.create(name1,name2));
                            name1 = name2 = null;
                        }
                        else if(parser.getName().equals("CelestrialStem"))
                        {
                            //celestialStems.put()
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception ex) {
            Log.d("xml", ex.getMessage());
        }


    }

}
