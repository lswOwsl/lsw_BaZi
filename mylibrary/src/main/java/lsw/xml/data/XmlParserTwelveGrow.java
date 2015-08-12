package lsw.xml.data;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.HashMap;

import lsw.model.EnumFiveElement;
import lsw.xml.XmlParser;
import lsw.xml.model.XmlModelTwelveGrow;

/**
 * Created by swli on 8/12/2015.
 */
public class XmlParserTwelveGrow extends XmlParser<XmlModelTwelveGrow> {

    public XmlParserTwelveGrow(InputStream inputStream) {
        super(inputStream);
    }

    private XmlModelTwelveGrow xmlModelTwelveGrow;

    @Override
    public void startDocument(XmlPullParser parser) {
        xmlModelTwelveGrow = new XmlModelTwelveGrow();
        xmlModelTwelveGrow.setFiveElementGrows(new HashMap<EnumFiveElement, Integer>());
        xmlModelTwelveGrow.setGrowsItem(new HashMap<Integer, String>());
    }

    @Override
    public void startTag(XmlPullParser parser) throws Exception {
        String nodeName = parser.getName();

        if (nodeName.equals("Grow")) {
            int id =Integer.parseInt(parser.getAttributeValue("", "id"));
            String value = parser.getAttributeValue("", "value");
            xmlModelTwelveGrow.getGrowsItem().put(id,value);
        }

        if(nodeName.equals("FiveElement"))
        {
            String name = parser.getAttributeValue("", "name");
            int index =Integer.parseInt(parser.getAttributeValue("", "terrestialIndex"));
            xmlModelTwelveGrow.getFiveElementGrows().put(EnumFiveElement.toEnum(name),index);
        }
    }

    @Override
    public void endTag(XmlPullParser parser) {
        String nodeName = parser.getName();
    }

    @Override
    public XmlModelTwelveGrow getT() {
        return xmlModelTwelveGrow;
    }
}
