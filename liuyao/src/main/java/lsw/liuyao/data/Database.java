package lsw.liuyao.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lsw.library.DatabaseManager;
import lsw.liuyao.R;
import lsw.liuyao.common.MyApplication;
import lsw.liuyao.model.HexagramLineNote;
import lsw.liuyao.model.HexagramRow;

/**
 * Created by swli on 8/18/2015.
 */
public class Database extends DatabaseManager {

    public static final String DB_NAME_HEXAGRAM_NOTE = "liuYaoHexagramNote.db";
    public static final String DB_NAME = "liuYao.db";
    public static final String PACKAGE_NAME = "lsw.liuyao";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME;


    private Context context;

    private SQLiteDatabase databaseHexagramNote;

    public Database(Context context) {
        this.context = context;
    }

    public void openDatabase() {
        super.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }

    private SQLiteDatabase openDatabase(String databaseFile) {

        int resourceId = R.raw.database_structure_empty;
        InputStream is = this.context.getResources().openRawResource(
                resourceId);

        //加载64卦数据库
        int resourceIdHexagramNote = R.raw.sixty_four_hexagrams_note;
        InputStream isNote = this.context.getResources().openRawResource(resourceIdHexagramNote);
        databaseHexagramNote = super.openDatabase(DB_PATH + "/" + DB_NAME_HEXAGRAM_NOTE, isNote);

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
        String note = getColumnValue(cursor, "Note");

        String shakeDate = getColumnValue(cursor, "ShakeDate");

        HexagramRow hexagramRow = new HexagramRow(id,originalName,changedName,shakeDate, note);

        return hexagramRow;
    }

    public HexagramRow getHexagramById(int paramId)
    {
        openDatabase();
        String[] params = new String[]{ paramId+"" };
        String sql = "SELECT * FROM Hexagram where Id = ?";
        Cursor cur = database.rawQuery(sql, params);
        HexagramRow hexagram = null;
        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            hexagram = createHexagramRowByCursor(cur);
            break;
        }
        closeDatabase();
        return hexagram;
    }

    public List<HexagramLineNote> getHexagramByNameAndLinePosition(String name)
    {
        openDatabase();
        String[] params = new String[]{ name };
        String sql = "SELECT * FROM HexagramsNote where Name = ?";
        Cursor cursor = databaseHexagramNote.rawQuery(sql, params);
        List<HexagramLineNote> hexagramLineNotes = new ArrayList<HexagramLineNote>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            HexagramLineNote hexagramLineNote = new HexagramLineNote();

            String hexagramName = getColumnValue(cursor, "Name");
            String linePosition = getColumnValue(cursor, "LinePosition");
            String noteType = getColumnValue(cursor, "NoteType");

            String originalNote = getColumnValue(cursor, "OriginalNote");
            String decoratedNote = getColumnValue(cursor, "DecoratedNote");

            hexagramLineNote.setName(hexagramName.trim());
            if(TextUtils.isEmpty(linePosition.trim()))
                hexagramLineNote.setPosition(8);
            else
                hexagramLineNote.setPosition(Integer.valueOf(linePosition));

            hexagramLineNote.setNoteType(noteType.trim());
            hexagramLineNote.setOriginalNote(originalNote.trim());
            hexagramLineNote.setDecoratedNote(decoratedNote.trim());

            hexagramLineNotes.add(hexagramLineNote);
        }
        closeDatabase();
        databaseHexagramNote.close();
        return hexagramLineNotes;
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

    public void saveHexagramsToXML(List<HexagramRow> rows,  String localDir)
    {
        File file = new File(localDir);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                Log.d("XML saving folder", e.getMessage());
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
                    XmlSerializer serializer = Xml.newSerializer();
                    serializer.setOutput(fileos, "UTF-8");
                    serializer.startDocument("UTF-8", null);

                    // start a tag called
                    serializer.startTag(null, "Hexagrams");
                    for (HexagramRow row : rows) {
                        serializer.startTag(null, "Hexagram");
                        serializer.attribute(null, "OriginalName", row.getOriginalName());
                        serializer.attribute(null, "ChangedName", row.getChangedName());
                        serializer.attribute(null, "ShakeDate", row.getDate());
                        serializer.attribute(null, "Note", row.getNote());
                        serializer.endTag(null, "Hexagram");
                    }
                    serializer.endTag(null, "Hexagrams");
                    serializer.endDocument();
                    serializer.flush();
                    fileos.close();
                    MediaScannerConnection.scanFile(MyApplication.getInstance(), new String[]{newXmlFile.getAbsolutePath()}, null, null);
                }
            }
        } catch (Exception e) {
            Log.d("member saving",e.getMessage());
        }
        //return bFlag;
    }

    public void importHexagramsToDb(List<HexagramRow> hexagramRows) {
        openDatabase();

        database.execSQL("delete from Hexagram");

        for (HexagramRow row: hexagramRows) {
            ContentValues cv = new ContentValues();
            cv.put("OriginalName", row.getOriginalName());
            cv.put("ChangedName",row.getChangedName());
            cv.put("ShakeDate", row.getDate());
            cv.put("Note", row.getNote());
            database.insert("Hexagram", null, cv);
        }
        database.close();
    }
}
