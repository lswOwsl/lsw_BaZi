package lsw.xml.model;

import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlModelFiveElement {

    private static XmlModelFiveElement xmlFiveElement;

    private XmlModelFiveElement() {}

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

}
