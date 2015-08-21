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
        cv.put("Note", model.getNote());
        getDatabase().insert("Hexagram", null, cv);
        closeDatabase();
    }

    HexagramRow createHexagramRowByCursor(Cursor cursor)
    {
        int idIndex = cursor.getColumnIndex("Id");
        int id = cursor.getInt(idIndex);

        String originalName = getColumnValue(cursor, "OriginalName");
        String changedName = getColumnValue(cursor,"ChangedName");
        String note = getColumnValue(cursor,"Note");

        String shakeDate = getColumnValue(cursor, "ShakeDate");

        HexagramRow hexagramRow = new HexagramRow(id,originalName,changedName,shakeDate, note);

        return hexagramRow;
    }

    public HexagramRow getHexagramById(int paramId)
    {
        openDatabase();
        String[] params = new String[]{ paramId+"" };
        String sql = "SELECT * FROM Hexagram where Id = ?";
        Cursor cur = database.rawQuery(sql,params);
        HexagramRow hexagram = null;
        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            hexagram = createHexagramRowByCursor(cur);
            break;
        }
        closeDatabase();
        return hexagram;
    }

    public void updateHexagram(HexagramRow hexagramRow) {

        openDatabase();
        ContentValues values = new ContentValues();
        values.put("Note", hexagramRow.getNote());
        String whereClause = "id=?";
        String[] whereArgs={String.valueOf(hexagramRow.getId())};
        database.update("Hexagram", values, whereClause, whereArgs);
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

            HexagramRow hexagramRow = createHexagramRowByCursor(cur);

            list.add(hexagramRow);
        }

        cur.close();
        database.close();
        return list;
    }
}
