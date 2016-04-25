package lsw.liuyao.data.xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lsw.liuyao.model.HexagramMenuData;
import lsw.liuyao.model.HexagramRow;
import lsw.xml.XmlParser;

/**
 * Created by swli on 4/25/2016.
 */
public class XmlParserListMenu extends XmlParser<List<HexagramMenuData>> {

    private List<HexagramMenuData> hexagramMenuDataList;
    private List<HexagramMenuData> hexagramMenuDataSubList;

    private String name;
    private String condition;

    private String subName;
    private String subCondition;

    public XmlParserListMenu(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public void startDocument(XmlPullParser parser) {
        hexagramMenuDataList = new ArrayList<HexagramMenuData>();
    }

    @Override
    public void startTag(XmlPullParser parser) throws Exception {
        if(parser.getName().equals("Item")){

            name = parser.getAttributeValue("","Name");
            condition = parser.getAttributeValue("","Condition");

            hexagramMenuDataSubList = new ArrayList<HexagramMenuData>();
        }

        if( parser.getName().equals("SubItem"))
        {
            subName = parser.getAttributeValue("","Name");
            subCondition = parser.getAttributeValue("","Condition");
        }

    }

    @Override
    public void endTag(XmlPullParser parser) {

        if(parser.getName().equals("Item")){
            HexagramMenuData menuData = new HexagramMenuData();
            menuData.setName(name);
            menuData.setCondition(condition);
            menuData.setSecondLevelData(hexagramMenuDataSubList);
            hexagramMenuDataList.add(menuData);

            hexagramMenuDataSubList = null;
            name = condition = "";
        }
        if(parser.getName().equals("SubItem"))
        {
            HexagramMenuData menuData = new HexagramMenuData();
            menuData.setName(subName);
            menuData.setCondition(subCondition);

            hexagramMenuDataSubList.add(menuData);
            subName = subCondition = "";
        }

    }

    @Override
    public List<HexagramMenuData> getT() {
        return hexagramMenuDataList;
    }
}
