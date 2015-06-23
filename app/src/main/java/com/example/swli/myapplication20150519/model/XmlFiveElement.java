package com.example.swli.myapplication20150519.model;

import android.content.Context;

import com.example.swli.myapplication20150519.common.XmlParserFiveElement;

import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlFiveElement {

    private static XmlFiveElement xmlFiveElement;

    private XmlFiveElement() {}

    private HashMap<String,String> enhance;
    private HashMap<String,String> control;

    public HashMap<String, String> getEnhance() {
        return enhance;
    }

    private void setEnhance(HashMap<String, String> enhance) {
        this.enhance = enhance;
    }

    public HashMap<String, String> getControl() {
        return control;
    }

    private void setControl(HashMap<String, String> control) {
        this.control = control;
    }

    public static XmlFiveElement getInstance(Context context) {
        if (xmlFiveElement == null) {
            xmlFiveElement = new XmlFiveElement();
            XmlParserFiveElement xpwx = new XmlParserFiveElement(context);

            HashMap<String,String> enhance = new HashMap<String, String>();
            HashMap<String,String> control = new HashMap<String, String>();

            xmlFiveElement.setControl(control);
            xmlFiveElement.setEnhance(enhance);

            xpwx.parse(xmlFiveElement);
        }
        return xmlFiveElement;
    }
}
