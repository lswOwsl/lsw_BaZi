package lsw.xml.data;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.HashMap;

import lsw.xml.XmlParser;
import lsw.xml.model.XmlModelCelestialStem;
import lsw.xml.model.XmlModelFiveElement;

/**
 * Created by swli on 8/4/2015.
 */
public class XmlParserFiveElement  extends XmlParser<XmlModelFiveElement> {

    public XmlParserFiveElement(InputStream inputStream) {
        super(inputStream);
    }

    private XmlModelFiveElement xmlModelFiveElement;

    @Override
    public void startDocument(XmlPullParser parser) {
        xmlModelFiveElement = new XmlModelFiveElement();
        xmlModelFiveElement.setEnhance(new HashMap<String,String>());
        xmlModelFiveElement.setControl(new HashMap<String,String>());
    }

    @Override
    public void startTag(XmlPullParser parser) throws Exception {
        if(parser.getName().equals("Enhance")){

            String from = parser.getAttributeValue("", "from");
            String to = parser.getAttributeValue("", "to");

            xmlModelFiveElement.getEnhance().put(from, to);
        }
        else if(parser.getName().equals("Control")){

            String from = parser.getAttributeValue("", "from");
            String to = parser.getAttributeValue("", "to");

            xmlModelFiveElement.getControl().put(from,to);
        }
    }

    @Override
    public void endTag(XmlPullParser parser) {

    }

    @Override
    public XmlModelFiveElement getT() {
        return xmlModelFiveElement;
    }


}
