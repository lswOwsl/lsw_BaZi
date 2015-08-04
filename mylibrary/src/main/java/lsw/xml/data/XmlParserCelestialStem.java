package lsw.xml.data;

import android.util.Log;
import android.util.Pair;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import lsw.xml.XmlParser;
import lsw.xml.model.XmlModelCelestialStem;
import lsw.xml.model.XmlModelExtProperty;

/**
 * Created by lsw_wsl on 8/3/15.
 */
public class XmlParserCelestialStem extends XmlParser<XmlModelCelestialStem> {

    public XmlParserCelestialStem(InputStream inputStream) {
        super(inputStream);
    }

    private XmlModelCelestialStem xmlModelCelestialStem = new XmlModelCelestialStem();

    String pairWuXing = null;
    String name1 = null;
    String name2 = null;

    @Override
    public void startDocument(XmlPullParser parser) {
        xmlModelCelestialStem.setCelestialStems(new HashMap<String,XmlModelExtProperty>());
        xmlModelCelestialStem.setPairSuits(new HashMap<Pair<String,String>,String>());
        xmlModelCelestialStem.setPairInverses(new ArrayList<Pair<String, String>>());
    }

    @Override
    public void startTag(XmlPullParser parser) throws Exception {

        if (parser.getName().equals("CelestrialStem")) {
            XmlModelExtProperty xet = new XmlModelExtProperty();
            xet.setYinYang(parser.getAttributeValue("", "yinYang"));
            xet.setWuXing(parser.getAttributeValue("", "wuXing"));
            xmlModelCelestialStem.getCelestialStems().put(parser.getAttributeValue("", "name"), xet);
        } else if (parser.getName().equals("PairSuit")) {
            pairWuXing = parser.getAttributeValue("", "wuXing");
        } else if (parser.getName().equals("Name")) {
            if (name1 == null)
                name1 = parser.nextText();
            else
                name2 = parser.nextText();
        }
    }

    @Override
    public void endTag(XmlPullParser parser) {
        if (parser.getName().equals("PairSuit"))
        {
            xmlModelCelestialStem.getPairSuits().put(Pair.create(name1, name2),pairWuXing);
            name1 = name2 = null;
            pairWuXing = null;
        }
        else if(parser.getName().equals("PairInverse"))
        {
            xmlModelCelestialStem.getPairInverses().add(Pair.create(name1,name2));
            name1 = name2 = null;
        }
        else if(parser.getName().equals("CelestrialStem"))
        {
            //celestialStems.put()
        }
    }

    @Override
    public XmlModelCelestialStem getT() {

        return this.xmlModelCelestialStem;
    }
}
