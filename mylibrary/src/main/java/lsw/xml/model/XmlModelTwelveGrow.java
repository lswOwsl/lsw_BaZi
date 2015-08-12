package lsw.xml.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import lsw.model.EnumFiveElement;

/**
 * Created by swli on 8/12/2015.
 */
public class XmlModelTwelveGrow {

    public XmlModelTwelveGrow(){}

    private HashMap<Integer,String> growsItem;

    public HashMap<Integer, String> getGrowsItem() {
        return growsItem;
    }

    public void setGrowsItem(HashMap<Integer, String> growsItem) {
        this.growsItem = growsItem;
    }

    public HashMap<EnumFiveElement, Integer> getFiveElementGrows() {
        return fiveElementGrows;
    }

    public void setFiveElementGrows(HashMap<EnumFiveElement, Integer> fiveElementGrows) {
        this.fiveElementGrows = fiveElementGrows;
    }

    private HashMap<EnumFiveElement,Integer> fiveElementGrows;

    private static HashMap<EnumFiveElement,ArrayList<Pair<Integer,String>>> fiveElementGrowsMapping;

    public HashMap<EnumFiveElement,ArrayList<Pair<Integer,String>>> getFiveElementGrowsMapping()
    {
        if(fiveElementGrowsMapping == null && (growsItem != null && fiveElementGrows != null))
        {
            fiveElementGrowsMapping = new HashMap<EnumFiveElement, ArrayList<Pair<Integer, String>>>();

            for (EnumFiveElement enumFiveElement : fiveElementGrows.keySet()) {
                ArrayList<Pair<Integer, String>> list = new ArrayList<Pair<Integer, String>>();

                int tempId = fiveElementGrows.get(enumFiveElement);
                //把12长生按xml里的id排序
                List<Integer> integers = Arrays.asList(getGrowsItem().keySet().toArray(new Integer[]{}));
                Collections.sort(integers);

                for (Integer integer : integers) {
                    if (tempId > 12)
                        tempId = 1;
                    list.add(Pair.create(tempId, getGrowsItem().get(integer)));
                    tempId++;
                }

                fiveElementGrowsMapping.put(enumFiveElement, list);
            }
        }
        return fiveElementGrowsMapping;
    }

}
