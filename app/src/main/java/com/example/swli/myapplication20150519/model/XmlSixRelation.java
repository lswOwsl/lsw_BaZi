package com.example.swli.myapplication20150519.model;

import android.content.Context;

import com.example.swli.myapplication20150519.common.XmlParserSixRelation;

import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlSixRelation {

    private static XmlSixRelation xmlSixRelation;

    private XmlSixRelation() {}

    private HashMap<String,XmlExtTwoSide> enhance;
    private HashMap<String,XmlExtTwoSide> control;
    private HashMap<String,HashMap<Boolean,String>> relation;

    public HashMap<String, HashMap<Boolean, String>> getRelation() {
        return relation;
    }

    private void setRelation(HashMap<String, HashMap<Boolean, String>> relation) {
        this.relation = relation;
    }

    public HashMap<String, XmlExtTwoSide> getEnhance() {
        return enhance;
    }

    private void setEnhance(HashMap<String, XmlExtTwoSide> enhance) {
        this.enhance = enhance;
    }

    public HashMap<String, XmlExtTwoSide> getControl() {
        return control;
    }

    private void setControl(HashMap<String, XmlExtTwoSide> control) {
        this.control = control;
    }



    public static XmlSixRelation getInstance(Context context) {
        if (xmlSixRelation == null) {
            xmlSixRelation = new XmlSixRelation();
            XmlParserSixRelation xpwx = new XmlParserSixRelation(context);

            HashMap<String,XmlExtTwoSide> enhance = new HashMap<String, XmlExtTwoSide>();
            HashMap<String,XmlExtTwoSide> control = new HashMap<String, XmlExtTwoSide>();
            HashMap<String,HashMap<Boolean,String>> relation = new HashMap<String, HashMap<Boolean, String>>();

            xmlSixRelation.setControl(control);
            xmlSixRelation.setEnhance(enhance);
            xmlSixRelation.setRelation(relation);

            xpwx.parse(xmlSixRelation);
        }
        return xmlSixRelation;
    }
}
