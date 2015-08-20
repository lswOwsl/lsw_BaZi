package lsw.liuyao.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;

import lsw.library.DatabaseManager;
import lsw.library.DateExt;
import lsw.liuyao.R;
import lsw.liuyao.model.HexagramRow;
import lsw.model.Hexagram;

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

    public void deleteHexagram(int id)
    {
        openDatabase();
        String[] args = {String.valueOf(id)};
        getDatabase().delete("Hexagram", "Id=?", args);
        closeDatabase();
    }

    public void insertHexagram(HexagramRow model)
    {
        openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("OriginalName", model.getOriginalName());
        cv.put("ChangedName", model.getChangedName());
        cv.put("ShakeDate", model.getDate());
        cv.put("Note",model.getNote());
        getDatabase().insert("Hexagram", null, cv);
        closeDatabase();
    }

    public ArrayList<HexagramRow> getHexagramList(String str){
        ArrayList<HexagramRow> list=new ArrayList<HexagramRow>();

        openDatabase();
        SQLiteDatabase database = super.getDatabase();

        String sqlCondition = " ";
        String[] params = new String[]{};
        if(!TextUtils.isEmpty(str)) {
            sqlCondition = "where OriginalName = ? Or ShakeDate like ? Or Note like ?";
            params = new String[]{ ""+str+"","%"+str+"%","%"+str+"%" };
        }

        String sql = "SELECT * FROM Hexagram " + sqlCondition +" Order By ShakeDate DESC";
        Cursor cur = database.rawQuery(sql,params);

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int idIndex = cur.getColumnIndex("Id");
            int id = cur.getInt(idIndex);

            String originalName = getColumnValue(cur, "OriginalName");
            String changedName = getColumnValue(cur,"ChangedName");
            String note = getColumnValue(cur,"Note");

            String shakeDate = getColumnValue(cur, "ShakeDate");

            HexagramRow hexagramRow = new HexagramRow(id,originalName,changedName,shakeDate, note);

            list.add(hexagramRow);
        }

        cur.close();
        database.close();
        return list;
    }
}
