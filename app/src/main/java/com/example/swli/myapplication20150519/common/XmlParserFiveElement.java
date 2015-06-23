package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.model.XmlFiveElement;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlParserFiveElement extends XmlParser<XmlFiveElement> {

    public XmlParserFiveElement(Context context) {
        super(context);
    }

    public void parse(XmlFiveElement xmlFiveElement) {
        try {

            HashMap<String,String> enhance = null;
            HashMap<String,String> control = null;

            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(this.context.getResources().openRawResource(R.raw.wu_xing), "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        enhance = xmlFiveElement.getEnhance();
                        control = xmlFiveElement.getControl();
                        break;
                    }
                    case XmlPullParser.START_TAG:{
                        if(parser.getName().equals("Enhance")){

                            String from = parser.getAttributeValue("", "from");
                            String to = parser.getAttributeValue("", "to");

                            enhance.put(from, to);
                        }
                        else if(parser.getName().equals("Control")){

                            String from = parser.getAttributeValue("", "from");
                            String to = parser.getAttributeValue("", "to");

                            control.put(from,to);
                        }

                        break;
                    }
                    case XmlPullParser.END_TAG:

                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception ex) {
            Log.d("xml", ex.getMessage());
        }
    }
}
