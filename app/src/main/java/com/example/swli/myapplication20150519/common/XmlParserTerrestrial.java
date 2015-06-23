package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.util.Xml;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.model.XmlExtProperty;
import com.example.swli.myapplication20150519.model.XmlTerrestrial;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlParserTerrestrial extends XmlParser<XmlTerrestrial> {

    public XmlParserTerrestrial(Context context) {
        super(context);
    }

    public void parse(XmlTerrestrial xmlTerrestrial) {
        String terrestrialName = null;
        String wuXing = null;
        String name1 = null;
        String name2 = null;
        String name3 = null;
        ArrayList<Pair<String,String>> hiddens= null;

         HashMap<String, XmlExtProperty> terrestrials = null;
         HashMap<String, ArrayList<Pair<String,String>>> terrestrialHiddens = null;
         HashMap<Pair<String, String>, String> sixSuits = null;
         ArrayList<Pair<String, String>> sixInverses = null;
         HashMap<String,ArrayList<String>> threeSuits = null;
         HashMap<String,ArrayList<String>> threeConverge = null;
         ArrayList<Pair<String,String>> punishment = null;
         ArrayList<ArrayList<String>> threePunishment = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(this.context.getResources().openRawResource(R.raw.terrestrial), "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {

                        terrestrials = xmlTerrestrial.getTerrestrials();
                        terrestrialHiddens = xmlTerrestrial.getTerrestrialHiddens();
                        sixSuits = xmlTerrestrial.getSixSuits();
                        sixInverses = xmlTerrestrial.getSixInverses();
                        threeSuits = xmlTerrestrial.getThreeSuits();
                        threeConverge = xmlTerrestrial.getThreeConverge();
                        punishment = xmlTerrestrial.getPunishment();
                        threePunishment = xmlTerrestrial.getThreePunishment();

                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        String nodeName = parser.getName();

                        if (nodeName.equals("Terrestrial")) {

                            terrestrialName = parser.getAttributeValue("", "name");

                            XmlExtProperty xet = new XmlExtProperty();
                            xet.setYinYang(parser.getAttributeValue("", "yinYang"));
                            xet.setWuXing(parser.getAttributeValue("", "wuXing"));
                            xet.setId(Integer.parseInt(parser.getAttributeValue("", "id")));
                            terrestrials.put(terrestrialName, xet);

                            hiddens = new ArrayList<Pair<String, String>>();

                        }else if(nodeName.equals("CelestrialStem"))
                        {
                            String celestrialStemName = parser.getAttributeValue("", "name");
                            String priority = parser.getAttributeValue("", "priority");
                            hiddens.add(Pair.create(celestrialStemName, priority));
                        }
                        else if (nodeName.equals("SixSuit") ||
                                nodeName.equals("ThreeSuit")||
                                nodeName.equals("ThreeConverge"))
                        {
                            wuXing = parser.getAttributeValue("", "wuXing");
                        }
                        else if (nodeName.equals("Name")) {
                            if (name1 == null)
                                name1 = parser.nextText();
                            else if(name2 == null)
                                name2 = parser.nextText();
                            else if(name3 == null)
                                name3 = parser.nextText();
                        }

                        break;
                    }
                    case XmlPullParser.END_TAG:
                        String nodeName = parser.getName();

                        if (nodeName.equals("SixSuit"))
                        {
                            sixSuits.put(Pair.create(name1, name2), wuXing);
                            name1 = name2 = null;
                            wuXing = null;
                        }
                        else if (nodeName.equals("SixInverse"))
                        {
                            sixInverses.add(Pair.create(name1, name2));
                            name1 = name2 = null;
                        }
                        else if(nodeName.equals("ThreeSuit"))
                        {
                            ArrayList<String> strings = new ArrayList<String>();
                            strings.add(name1);
                            strings.add(name2);
                            strings.add(name3);
                            threeSuits.put(wuXing,strings);
                            name1 = name2 = name3 = wuXing = null;
                        }
                        else if(nodeName.equals("ThreeConverge"))
                        {
                            ArrayList<String> strings = new ArrayList<String>();
                            strings.add(name1);
                            strings.add(name2);
                            strings.add(name3);
                            threeConverge.put(wuXing, strings);
                            name1 = name2 = name3 = wuXing = null;
                        }
                        else if (nodeName.equals("Terrestrial")) {
                            terrestrialHiddens.put(terrestrialName,hiddens);
                            hiddens = null;
                            terrestrialName = null;
                        }
                        else if(nodeName.equals("Punishment"))
                        {
                            punishment.add(Pair.create(name1,name2));
                            name1 = name2 = null;
                        }
                        else if(nodeName.equals("ThreePunishment"))
                        {
                            ArrayList<String> strings = new ArrayList<String>();
                            strings.add(name1);
                            strings.add(name2);
                            strings.add(name3);
                            threePunishment.add(strings);
                            name1 = name2 = name3 = null;
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
