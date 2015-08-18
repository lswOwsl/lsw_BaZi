package lsw.liuyao.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.InputStream;
import java.util.ArrayList;

import lsw.library.DatabaseManager;
import lsw.liuyao.R;
import lsw.liuyao.model.HexagramRow;

/**
 * Created by swli on 8/18/2015.
 */
public class Database extends DatabaseManager {

    public static final String DB_NAME = "liuYao.db";
    public static final String PACKAGE_NAME = "lsw.liuyao";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;


    private Context context;

    public Database(Context context) {
        this.context = context;
    }

    public void openDatabase() {
        super.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String databaseFile) {

        int resourceId = R.raw.database_structure;
        InputStream is = this.context.getResources().openRawResource(
                resourceId);
        return super.openDatabase(databaseFile,is);
    }


    public ArrayList<HexagramRow> getHexagramList(){
        ArrayList<HexagramRow> list=new ArrayList<HexagramRow>();

        openDatabase();
        SQLiteDatabase database = super.getDatabase();
        String sql = "SELECT * FROM Hexagram Order By ShakeDate DESC";
        Cursor cur = database.rawQuery(sql,null);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int idIndex = cur.getColumnIndex("Id");
            int id = cur.getInt(idIndex);

            String originalName = getColumnValue(cur, "OriginalName");
            String changedName = getColumnValue(cur,"ChangedName");

            String shakeDate = getColumnValue(cur, "ShakeDate");

            HexagramRow hexagramRow = new HexagramRow(id,originalName,changedName,shakeDate);

            list.add(hexagramRow);
        }

        cur.close();
        database.close();
        return list;
    }
}
