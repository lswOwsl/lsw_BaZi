package lsw.liuyao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lsw.liuyao.common.MyApplication;
import lsw.liuyao.data.Database;
import lsw.liuyao.data.xml.XmlParserHexagramRow;
import lsw.liuyao.model.HexagramRow;
import lsw.xml.XmlLoader;
import lsw.xml.XmlParser;

/**
 * Created by lsw_wsl on 4/10/16.
 */
public class HexagramImportActivity extends Activity {

    private String selectedFile;
    String[] source;
    Database database;

    String path = Environment.getExternalStorageDirectory() +"/"+
            MyApplication.getInstance().getResources().getString(R.string.externalSavingFolder)+"/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = new Database(this);

        setContentView(R.layout.hexagram_list_import_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        source = XmlLoader.loadFilesFromFolder(path).toArray(new String[]{});

        ListView lv = (ListView) findViewById(R.id.lvImportList);
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, source));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedFile = source[i];

                AlertDialog.Builder dialog = new AlertDialog.Builder(HexagramImportActivity.this);
                dialog.setMessage("导入当前文件？" + selectedFile).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<HexagramRow> rows = loadHexagramRowFromXml(path + selectedFile);
                        database.importHexagramsToDb(rows);
                        Toast.makeText(HexagramImportActivity.this, "导入记录成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        });
    }

    public List<HexagramRow> loadHexagramRowFromXml(String path)
    {
        try {
            InputStream is = new FileInputStream(path);

            XmlParser<List<HexagramRow>> parser = new XmlParserHexagramRow(is);
            List<HexagramRow> members = parser.getT();
            return members;
        }
        catch (Exception ex)
        {
            Log.d("import member xml", ex.getMessage());
        }
        return null;
    }
}
