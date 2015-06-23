package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.model.XmlExtTwoSide;
import com.example.swli.myapplication20150519.model.XmlSixRelation;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlParserSixRelation extends XmlParser<XmlSixRelation> {

    public XmlParserSixRelation(Context context) {
        super(context);
    }

    public void parse(XmlSixRelation xmlSixRelation) {

        try {

            HashMap<String,XmlExtTwoSide> enhance = null;
            HashMap<String,XmlExtTwoSide> control = null;
            HashMap<String,HashMap<Boolean,String>> relation = null;

            String from = null;
            String sameTo = null;
            String differentTo = null;
            String action = null;
            HashMap<Boolean,String> relationYinYang = null;

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(this.context.getResources().openRawResource(R.raw.liu_qin), "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        enhance = xmlSixRelation.getEnhance();
                        control = xmlSixRelation.getControl();
                        relation = xmlSixRelation.getRelation();
                        break;
                    }
                    case XmlPullParser.START_TAG:{
                        if(parser.getName().equals("Enhance")){
                            from = parser.getAttributeValue("", "from");
                        }
                        else if(parser.getName().equals("Control")){
                            from = parser.getAttributeValue("", "from");
                        }
                        else if(parser.getName().equals("Same"))
                        {
                            sameTo = parser.getAttributeValue("", "to");
                        }
                        else if(parser.getName().equals("Different"))
                        {
                            differentTo = parser.getAttributeValue("", "to");
                        }
                        else if(parser.getName().equals("Relation"))
                        {
                            action =parser.getAttributeValue("", "action");
                            relationYinYang = new HashMap<Boolean, String>();
                        }
                        else if(parser.getName().equals("YinYang"))
                        {
                            String boolStr = parser.getAttributeValue("", "isSame");
                            Boolean bool = new Boolean(boolStr);
                            String name = parser.nextText();
                            relationYinYang.put(bool,name);
                        }

                        break;
                    }
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("Enhance")){
                            XmlExtTwoSide twoSide = new XmlExtTwoSide();
                            twoSide.setDifferent(differentTo);
                            twoSide.setSame(sameTo);
                            enhance.put(from, twoSide);
                            from = sameTo = differentTo = null;
                        }
                        else if(parser.getName().equals("Control")){
                            XmlExtTwoSide twoSide = new XmlExtTwoSide();
                            twoSide.setDifferent(differentTo);
                            twoSide.setSame(sameTo);
                            control.put(from, twoSide);
                            from = sameTo = differentTo = null;
                        }
                        else if(parser.getName().equals("Relation"))
                        {
                            relation.put(action,relationYinYang);
                            relationYinYang = null;
                            action = null;
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

