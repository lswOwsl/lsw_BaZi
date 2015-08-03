package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import com.example.swli.myapplication20150519.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import lsw.library.BaZiHelper;
import lsw.library.DateExt;
import lsw.library.LunarCalendar;
import lsw.library.LunarSolarTerm;
import lsw.library.SolarTerm;

public class BaZiHelperExtender extends BaZiHelper {

    public static String getGrowTrick(Context context, String celestrialStem, String terrestrial)
    {
        String[] strings = new String[]{"id","日主","长生", "沐浴", "冠带", "临官", "帝旺", "衰", "病", "死", "墓", "绝", "胎", "养"};

        DBManager dbManager = new DBManager(context);
        dbManager.openDatabase();

        String sql = "SELECT * FROM GrowLiveTrick where CelestialStem = ?";
        Cursor cur = dbManager.execute(sql, new String[]
                {celestrialStem});
        String result = "";
        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            for(int i=2; i<strings.length; i++) {
                String temp = cur.getString(i);
                if(terrestrial.equals(temp)) {
                    return strings[i];
                }
            }
        }
        cur.close();
        dbManager.closeDatabase();

        return result;
    }

}
