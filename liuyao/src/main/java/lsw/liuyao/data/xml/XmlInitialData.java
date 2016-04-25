package lsw.liuyao.data.xml;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import lsw.liuyao.R;
import lsw.liuyao.common.MyApplication;
import lsw.liuyao.data.Database;
import lsw.liuyao.model.HexagramMenuData;
import lsw.liuyao.model.HexagramRow;
import lsw.xml.XmlParser;

/**
 * Created by swli on 4/25/2016.
 */
public class XmlInitialData {

    private static int BUFFER_SIZE = 400000;
    public static String Menu_List_Path =  Environment.getExternalStorageDirectory() + "/" + Database.PACKAGE_NAME + "/list_menu_data.xml;";

    public static  XmlInitialData getInstance()
    {
        return new XmlInitialData();
    }

    private XmlInitialData()
    {
        initListMenu();
    }

    public void initListMenu()
    {
        try {

            int resourceId = R.raw.list_menu_data;
            InputStream inputStream = MyApplication.getInstance().getResources().openRawResource(
                    resourceId);

            String xmlFilePath = Menu_List_Path;

            if (!(new File(xmlFilePath).exists())) {

                FileOutputStream fos = new FileOutputStream(xmlFilePath);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                inputStream.close();
            }

        } catch (FileNotFoundException e) {
            Log.e("xml file", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("xml", "IO exception");
            e.printStackTrace();
        }
    }

    public List<HexagramMenuData> getMenuData() {

        try {
            InputStream is = new FileInputStream(Menu_List_Path);
            XmlParser<List<HexagramMenuData>> parser = new XmlParserListMenu(is);
            return parser.getT();

        } catch (Exception ex) {
            Log.d("load menu xml", ex.getMessage());
        }
        return null;
    }

}
