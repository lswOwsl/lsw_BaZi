package lsw.xml;

import android.content.Context;
import java.io.InputStream;
import lsw.library.R;
import lsw.xml.data.XmlParserCelestialStem;
import lsw.xml.data.XmlParserFiveElement;
import lsw.xml.data.XmlParserSixRelation;
import lsw.xml.data.XmlParserTerrestrial;
import lsw.xml.data.XmlParserTwelveGrow;
import lsw.xml.model.XmlModelCelestialStem;
import lsw.xml.model.XmlModelFiveElement;
import lsw.xml.model.XmlModelSixRelation;
import lsw.xml.model.XmlModelTerrestrial;
import lsw.xml.model.XmlModelTwelveGrow;

/**
 * Created by swli on 8/4/2015.
 */
public class XmlModelCache {

    private Context context;
   
    private XmlModelCache(Context context) {
        this.context = context;
    }

    private static XmlModelCache xmlModelCache;

    public static XmlModelCache getInstance(Context context)
    {
        if(xmlModelCache == null)
        {
            xmlModelCache = new XmlModelCache(context);
        }
        return xmlModelCache;
    }

    private InputStream getInputStream(int resourceId)
    {
        return this.context.getResources().openRawResource(resourceId);
    }

    private static XmlModelCelestialStem xmlModelCelestialStem;
    public XmlModelCelestialStem getCelestialStem()
    {
        if(xmlModelCelestialStem == null)
        {
            xmlModelCelestialStem = new XmlParserCelestialStem(getInputStream(R.raw.celestial_stem)).getT();
        }
        return xmlModelCelestialStem;
    }

    private static XmlModelTerrestrial xmlModelTerrestrial;
    public XmlModelTerrestrial getTerrestrial()
    {
        if(xmlModelTerrestrial == null)
        {
            xmlModelTerrestrial = new XmlParserTerrestrial(getInputStream(R.raw.terrestrial)).getT();
        }
        return xmlModelTerrestrial;
    }

    private static XmlModelSixRelation xmlModelSixRelation;
    public XmlModelSixRelation getSixRelation()
    {
        if(xmlModelSixRelation == null)
        {
            xmlModelSixRelation = new XmlParserSixRelation(getInputStream(R.raw.six_relation)).getT();
        }
        return xmlModelSixRelation;
    }

    private static XmlModelFiveElement xmlModelFiveElement;
    public XmlModelFiveElement getFiveElement()
    {
        if(xmlModelFiveElement == null)
        {
            xmlModelFiveElement = new XmlParserFiveElement(getInputStream(R.raw.five_element)).getT();
        }
        return xmlModelFiveElement;
    }

    private static XmlModelTwelveGrow xmlModelTwelveGrow;
    public XmlModelTwelveGrow getXmlModelTwelveGrow()
    {
        if(xmlModelTwelveGrow == null)
        {
            xmlModelTwelveGrow = new XmlParserTwelveGrow(getInputStream(R.raw.twelve_grow)).getT();
        }
        return xmlModelTwelveGrow;
    }
}
