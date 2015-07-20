package com.example.swli.myapplication20150519.data.handler;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.ListView;

import com.example.swli.myapplication20150519.activity.sidebar.SortModel;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.MyApplication;
import com.example.swli.myapplication20150519.common.XmlParser;
import com.example.swli.myapplication20150519.model.Member;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsw_wsl on 7/18/15.
 */
public class MemberDataHandler {

    DBManager dbManager;

    public List<Member> loadMembersFromDb() {
        dbManager = new DBManager(MyApplication.getInstance());

        List<Member> list = new ArrayList<Member>();

        dbManager.openDatabase();
        SQLiteDatabase database = dbManager.getDatabase();
        String[] params = new String[]{};
        String sql = "SELECT * FROM Members Order By Birthday_Refactor ASC";
        Cursor cur = database.rawQuery(sql, params);
        String result = "";

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int idIndex = cur.getColumnIndex("Id");
            int nameIndex = cur.getColumnIndex("Name");
            int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
            int isMaleIndex = cur.getColumnIndex("IsMale");

            String name = cur.getString(nameIndex);
            String birthdayStr = cur.getString(birthdayIndex);
            String isMale = cur.getString(isMaleIndex);
            DateExt birthdayDE = new DateExt(birthdayStr, "yyyy-MM-dd HH:mm:ss");
            int isMaleI = Integer.parseInt(isMale);
            int id = cur.getInt(idIndex);

            Member member = new Member();
            member.setId(id);
            member.setName(name.trim());
            member.setIsMale(isMaleI == 1 ? true : false);
            member.setBirthday(birthdayDE);
            list.add(member);
        }

        return list;
    }

    public void saveMembersToXML(List<Member> members,  String localDir)
    {
        File file = new File(localDir);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                Log.d("member saving folder",e.getMessage());
            }
        }

        boolean bFlag;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String strTmpName = sDateFormat.format(new java.util.Date()) + ".xml";
        FileOutputStream fileos = null;

        File newXmlFile = new File(localDir + strTmpName);
        try {
            if (newXmlFile.exists()) {
                bFlag = newXmlFile.delete();
            } else {
                bFlag = true;
            }

            if (bFlag) {

                if (newXmlFile.createNewFile()) {
                    fileos = new FileOutputStream(newXmlFile);

                    // we create a XmlSerializer in order to write xml data
                    XmlSerializer serializer = Xml.newSerializer();

                    // we set the FileOutputStream as output for the serializer,
                    // using UTF-8 encoding
                    serializer.setOutput(fileos, "UTF-8");

                    // <?xml version=”1.0″ encoding=”UTF-8″>
                    // Write <?xml declaration with encoding (if encoding not
                    // null) and standalone flag (if stan dalone not null)
                    // This method can only be called just after setOutput.
                    serializer.startDocument("UTF-8", null);

                    // start a tag called
                    serializer.startTag(null, "Members");
                    for (Member member : members) {
                        serializer.startTag(null, "Member");
                        serializer.attribute(null, "Name", member.getName());
                        serializer.attribute(null, "Gender", member.getGender());
                        serializer.attribute(null, "Birthday", member.getBirthday().getFormatDateTime());
                        serializer.endTag(null, "Member");
                    }
                    serializer.endTag(null, "Members");
                    serializer.endDocument();

                    // write xml data into the FileOutputStream
                    serializer.flush();
                    // finally we close the file stream
                    fileos.close();
                }
            }
        } catch (Exception e) {
            Log.d("member saving",e.getMessage());
        }
        //return bFlag;
    }

    public List<Member> loadMembersFromXml(String path)
    {
        try {
            InputStream is = new FileInputStream(path);

            XmlParserData<List<Member>> parser = new XmlParserMember(is);
            List<Member> members = parser.getT();
            return members;
        }
        catch (Exception ex)
        {
            Log.d("import member xml", ex.getMessage());
        }
        return null;
    }

    public void importMembersToDb(List<Member> members) {
        dbManager.openDatabase();

        dbManager.execute("delete from Members",new String[]{});

        for (Member member: members) {
            ContentValues cv = new ContentValues();
            cv.put("IsMale", member.getIsMale());
            cv.put("Birthday_Refactor", member.getBirthday().getDefaultFormatForSqllite());
            cv.put("Name", member.getName());
            dbManager.getDatabase().insert("Members", null, cv);
        }

        dbManager.closeDatabase();
    }


}
