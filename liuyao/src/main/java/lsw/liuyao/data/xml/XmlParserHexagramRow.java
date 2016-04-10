package lsw.liuyao.data.xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lsw.library.DateExt;
import lsw.liuyao.model.HexagramRow;
import lsw.xml.XmlParser;

/**
 * Created by lsw_wsl on 4/9/16.
 */
public class XmlParserHexagramRow extends XmlParser<List<HexagramRow>> {

    private String originalName;
    private String changedName;
    private String shakeDate;
    private String note;

    private List<HexagramRow> hexagramRowList;

    public XmlParserHexagramRow(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public void startDocument(XmlPullParser parser) {

        hexagramRowList = new ArrayList<HexagramRow>();
    }

    @Override
    public void startTag(XmlPullParser parser) throws Exception {

        if(parser.getName().equals("Hexagram")){
            originalName = parser.getAttributeValue("", "OriginalName");
            changedName = parser.getAttributeValue("", "ChangedName");
            shakeDate = parser.getAttributeValue("", "ShakeDate");
            note = parser.getAttributeValue("","Note");
        }

    }

    @Override
    public void endTag(XmlPullParser parser) {

        if(parser.getName().equals("Hexagram")){
            HexagramRow row = new HexagramRow();
            row.setDate(shakeDate);
            row.setOriginalName(originalName);
            row.setChangedName(changedName);
            row.setNote(note);
            hexagramRowList.add(row);
            shakeDate = originalName = changedName = note = "";
        }

    }

    @Override
    public List<HexagramRow> getT() {
        return hexagramRowList;
    }
}
