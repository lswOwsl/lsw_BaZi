package lsw.xml.data;

import android.util.Pair;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import lsw.xml.XmlParser;
import lsw.xml.model.XmlModelExtProperty;
import lsw.xml.model.XmlModelTerrestrial;

/**
 * Created by swli on 8/4/2015.
 */
public class XmlParserTerrestrial extends XmlParser<XmlModelTerrestrial> {

    public XmlParserTerrestrial(InputStream inputStream) {
        super(inputStream);
    }

    private XmlModelTerrestrial xmlModelTerrestrial;

    String terrestrialName = null;
    String wuXing = null;
    String name1 = null;
    String name2 = null;
    String name3 = null;
    ArrayList<Pair<String,String>> hiddenList = null;

    @Override
    public void startDocument(XmlPullParser parser) {
        xmlModelTerrestrial = new XmlModelTerrestrial();
        xmlModelTerrestrial.setTerrestrials(new HashMap<String, XmlModelExtProperty>());
        xmlModelTerrestrial.setTerrestrialHiddens(new HashMap<String, ArrayList<Pair<String,String>>>());
        xmlModelTerrestrial.setSixSuits(new HashMap<Pair<String, String>, String>());
        xmlModelTerrestrial.setSixInverses(new ArrayList<Pair<String, String>>());
        xmlModelTerrestrial.setThreeSuits(new HashMap<String,ArrayList<String>>());
        xmlModelTerrestrial.setThreeConverge(new HashMap<String,ArrayList<String>>());
        xmlModelTerrestrial.setPunishment(new ArrayList<Pair<String,String>>());
        xmlModelTerrestrial.setThreePunishment(new ArrayList<ArrayList<String>>());

    }

    @Override
    public void startTag(XmlPullParser parser) throws Exception{
        String nodeName = parser.getName();

        if (nodeName.equals("Terrestrial")) {

            terrestrialName = parser.getAttributeValue("", "name");

            XmlModelExtProperty xet = new XmlModelExtProperty();
            xet.setYinYang(parser.getAttributeValue("", "yinYang"));
            xet.setWuXing(parser.getAttributeValue("", "wuXing"));
            xet.setId(Integer.parseInt(parser.getAttributeValue("", "id")));
            xmlModelTerrestrial.getTerrestrials().put(terrestrialName, xet);

            hiddenList = new ArrayList<Pair<String, String>>();

        }else if(nodeName.equals("CelestrialStem"))
        {
            String celestialStemName = parser.getAttributeValue("", "name");
            String priority = parser.getAttributeValue("", "priority");
            hiddenList.add(Pair.create(celestialStemName, priority));
        }
        else if (nodeName.equals("SixSuit") ||
                nodeName.equals("ThreeSuit")||
                nodeName.equals("ThreeConverge"))
        {
            wuXing = parser.getAttributeValue("", "wuXing");
        }
        else if (nodeName.equals("Name")) {
            if (name1 == null)
                name1 = parser.nextText();
            else if(name2 == null)
                name2 = parser.nextText();
            else if(name3 == null)
                name3 = parser.nextText();
        }
    }

    @Override
    public void endTag(XmlPullParser parser) {
        String nodeName = parser.getName();

        if (nodeName.equals("SixSuit"))
        {
            xmlModelTerrestrial.getSixSuits().put(Pair.create(name1, name2), wuXing);
            name1 = name2 = null;
            wuXing = null;
        }
        else if (nodeName.equals("SixInverse"))
        {
            xmlModelTerrestrial.getSixInverses().add(Pair.create(name1, name2));
            name1 = name2 = null;
        }
        else if(nodeName.equals("ThreeSuit"))
        {
            ArrayList<String> strings = new ArrayList<String>();
            strings.add(name1);
            strings.add(name2);
            strings.add(name3);
            xmlModelTerrestrial.getThreeSuits().put(wuXing, strings);
            name1 = name2 = name3 = wuXing = null;
        }
        else if(nodeName.equals("ThreeConverge"))
        {
            ArrayList<String> strings = new ArrayList<String>();
            strings.add(name1);
            strings.add(name2);
            strings.add(name3);
            xmlModelTerrestrial.getThreeConverge().put(wuXing, strings);
            name1 = name2 = name3 = wuXing = null;
        }
        else if (nodeName.equals("Terrestrial")) {
            xmlModelTerrestrial.getTerrestrialHiddens().put(terrestrialName, hiddenList);
            hiddenList = null;
            terrestrialName = null;
        }
        else if(nodeName.equals("Punishment"))
        {
            xmlModelTerrestrial.getPunishment().add(Pair.create(name1, name2));
            name1 = name2 = null;
        }
        else if(nodeName.equals("ThreePunishment"))
        {
            ArrayList<String> strings = new ArrayList<String>();
            strings.add(name1);
            strings.add(name2);
            strings.add(name3);
            xmlModelTerrestrial.getThreePunishment().add(strings);
            name1 = name2 = name3 = null;
        }
    }

    @Override
    public XmlModelTerrestrial getT() {
        return  this.xmlModelTerrestrial;
    }
}
