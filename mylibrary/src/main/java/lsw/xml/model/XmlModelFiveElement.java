package lsw.xml.model;

import java.util.HashMap;

/**
 * Created by swli on 6/8/2015.
 */
public class XmlModelFiveElement {

    public XmlModelFiveElement() {}

    public HashMap<String,String> enhance;
    public HashMap<String,String> control;

    public HashMap<String, String> getEnhance() {
        return enhance;
    }

    public void setEnhance(HashMap<String, String> enhance) {
        this.enhance = enhance;
    }

    public HashMap<String, String> getControl() {
        return control;
    }

    public void setControl(HashMap<String, String> control) {
        this.control = control;
    }

}
