package lsw.model;

import java.util.ArrayList;

/**
 * Created by swli on 8/10/2015.
 */
public enum EnumSixRelation {
    XiongDi("兄弟"),
    FuMu("父母"),
    QiCai("妻财"),
    ZiSun("子孙"),
    GuanGui("官鬼");

    String text;
    EnumSixRelation(String text)
    {
        this.text = text;
    }

    public String toString() {
        return String.format(text);
    }

    private static ArrayList<EnumSixRelation> allRelation;

    public static ArrayList<EnumSixRelation> getAll()
    {
        if(allRelation == null)
        {
            allRelation = new ArrayList<EnumSixRelation>();
            allRelation.add(EnumSixRelation.FuMu);
            allRelation.add(EnumSixRelation.GuanGui);
            allRelation.add(EnumSixRelation.QiCai);
            allRelation.add(EnumSixRelation.XiongDi);
            allRelation.add(EnumSixRelation.ZiSun);
        }
        return allRelation;
    }
}
