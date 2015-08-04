package lsw.xml.data;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.HashMap;

import lsw.xml.XmlParser;
import lsw.xml.model.XmlModelCelestialStem;
import lsw.xml.model.XmlModelExtTwoSide;
import lsw.xml.model.XmlModelSixRelation;

/**
 * Created by swli on 8/4/2015.
 */
public class XmlParserSixRelation  extends XmlParser<XmlModelSixRelation> {

    public XmlParserSixRelation(InputStream inputStream) {
        super(inputStream);
    }

    private XmlModelSixRelation xmlModelSixRelation = new XmlModelSixRelation();

    String from = null;
    String sameTo = null;
    String differentTo = null;
    String action = null;
    HashMap<Boolean,String> relationYinYang = null;

    @Override
    public void startDocument(XmlPullParser parser) {
        xmlModelSixRelation.setEnhance(new HashMap<String,XmlModelExtTwoSide>());
        xmlModelSixRelation.setControl(new HashMap<String,XmlModelExtTwoSide>());
        xmlModelSixRelation.setRelation(new HashMap<String, HashMap<Boolean, String>>());
    }

    @Override
    public void startTag(XmlPullParser parser) throws Exception {
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

    }

    @Override
    public void endTag(XmlPullParser parser) {
        if(parser.getName().equals("Enhance")){
            XmlModelExtTwoSide twoSide = new XmlModelExtTwoSide();
            twoSide.setDifferent(differentTo);
            twoSide.setSame(sameTo);
            xmlModelSixRelation.getEnhance().put(from, twoSide);
            from = sameTo = differentTo = null;
        }
        else if(parser.getName().equals("Control")){
            XmlModelExtTwoSide twoSide = new XmlModelExtTwoSide();
            twoSide.setDifferent(differentTo);
            twoSide.setSame(sameTo);
            xmlModelSixRelation.getControl().put(from, twoSide);
            from = sameTo = differentTo = null;
        }
        else if(parser.getName().equals("Relation"))
        {
            xmlModelSixRelation.getRelation().put(action,relationYinYang);
            relationYinYang = null;
            action = null;
        }
    }

    @Override
    public XmlModelSixRelation getT() {
        return this.xmlModelSixRelation;
    }
}
