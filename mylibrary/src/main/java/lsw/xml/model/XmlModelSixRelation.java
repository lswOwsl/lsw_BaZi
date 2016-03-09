package lsw.xml.model;

import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlModelSixRelation {

    public XmlModelSixRelation() {}

    private HashMap<String,XmlModelExtTwoSide> enhance;
    private HashMap<String,XmlModelExtTwoSide> control;
    private HashMap<String,HashMap<Boolean,String>> relation;

    public HashMap<String, HashMap<Boolean, String>> getRelation() {
        return relation;
    }

    public void setRelation(HashMap<String, HashMap<Boolean, String>> relation) {
        this.relation = relation;
    }

    public HashMap<String, XmlModelExtTwoSide> getEnhance() {
        return enhance;
    }

    public void setEnhance(HashMap<String, XmlModelExtTwoSide> enhance) {
        this.enhance = enhance;
    }

    public HashMap<String, XmlModelExtTwoSide> getControl() {
        return control;
    }

    public void setControl(HashMap<String, XmlModelExtTwoSide> control) {
        this.control = control;
    }

}
