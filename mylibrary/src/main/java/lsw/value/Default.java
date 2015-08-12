package lsw.value;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import lsw.model.EnumFiveElement;
import lsw.model.EnumLing;
import lsw.model.HexagramDefault;
import lsw.model.TrigramDefault;
import lsw.xml.XmlModelCache;
import lsw.xml.model.XmlModelExtProperty;

/**
 * Created by swli on 8/10/2015.
 */
public class Default {

    private static ArrayList<HexagramDefault> hexagrams;

    private static HexagramDefault createHexagram(int id, String name, String upperPart, String lowerPart, boolean side, String place, String c1, String c2, String c3, String c4, String c5, String c6, int self, int target) {
        HexagramDefault hexagramDefault = new HexagramDefault();
        hexagramDefault.setId(id);
        hexagramDefault.setName(name);
        hexagramDefault.setUpperPart(upperPart);
        hexagramDefault.setLowerPart(lowerPart);
        hexagramDefault.setSide(side);
        hexagramDefault.setPlace(place);
        hexagramDefault.setC1(c1);
        hexagramDefault.setC2(c2);
        hexagramDefault.setC3(c3);
        hexagramDefault.setC4(c4);
        hexagramDefault.setC5(c5);
        hexagramDefault.setC6(c6);
        hexagramDefault.setSelf(self);
        hexagramDefault.setTarget(target);

        return hexagramDefault;
    }

    public static ArrayList<HexagramDefault> getHexagrams() {
        if (hexagrams == null) {
            hexagrams = new ArrayList<HexagramDefault>();
            hexagrams.add(createHexagram(1, "乾", "天", "天", true, "乾", "|", "|", "|", "|", "|", "|", 6, 3));
            hexagrams.add(createHexagram(2, "姤", "天", "风", true, "乾", "||", "|", "|", "|", "|", "|", 1, 4));
            hexagrams.add(createHexagram(3, "遁", "天", "山", true, "乾", "||", "||", "|", "|", "|", "|", 2, 5));
            hexagrams.add(createHexagram(4, "否", "天", "地", true, "乾", "||", "||", "||", "|", "|", "|", 3, 6));
            hexagrams.add(createHexagram(5, "观", "风", "地", true, "乾", "||", "||", "||", "||", "|", "|", 4, 1));
            hexagrams.add(createHexagram(6, "剥", "山", "地", true, "乾", "||", "||", "||", "||", "||", "|", 5, 2));
            hexagrams.add(createHexagram(7, "晋", "火", "地", true, "乾", "||", "||", "||", "|", "||", "|", 4, 1));
            hexagrams.add(createHexagram(8, "大有", "火", "天", true, "乾", "|", "|", "|", "|", "||", "|", 3, 6));
            hexagrams.add(createHexagram(9, "坎", "水", "水", true, "坎", "||", "|", "||", "||", "|", "||", 6, 3));
            hexagrams.add(createHexagram(10, "节", "水", "泽", true, "坎", "|", "|", "||", "||", "|", "||", 1, 4));
            hexagrams.add(createHexagram(11, "屯", "水", "雷", true, "坎", "|", "||", "||", "||", "|", "||", 2, 5));
            hexagrams.add(createHexagram(12, "既济", "水", "火", true, "坎", "|", "||", "|", "||", "|", "||", 3, 6));
            hexagrams.add(createHexagram(13, "革", "泽", "火", true, "坎", "|", "||", "|", "|", "|", "||", 4, 1));
            hexagrams.add(createHexagram(14, "丰", "雷", "火", true, "坎", "|", "||", "|", "|", "||", "||", 5, 2));
            hexagrams.add(createHexagram(15, "明夷", "地", "火", true, "坎", "|", "||", "|", "||", "||", "||", 4, 1));
            hexagrams.add(createHexagram(16, "师", "地", "水", true, "坎", "||", "|", "||", "||", "||", "||", 3, 6));
            hexagrams.add(createHexagram(17, "艮", "山", "山", true, "艮", "||", "||", "|", "||", "||", "|", 6, 3));
            hexagrams.add(createHexagram(18, "贲", "山", "火", true, "艮", "|", "||", "|", "||", "||", "|", 1, 4));
            hexagrams.add(createHexagram(19, "大畜", "山", "天", true, "艮", "|", "|", "|", "||", "||", "|", 2, 5));
            hexagrams.add(createHexagram(20, "损", "山", "泽", true, "艮", "|", "|", "||", "||", "||", "|", 3, 6));
            hexagrams.add(createHexagram(21, "睽", "火", "泽", true, "艮", "|", "|", "||", "|", "||", "|", 4, 1));
            hexagrams.add(createHexagram(22, "履", "天", "泽", true, "艮", "|", "|", "||", "|", "|", "|", 5, 2));
            hexagrams.add(createHexagram(23, "中孚", "风", "泽", true, "艮", "|", "|", "||", "||", "|", "|", 4, 1));
            hexagrams.add(createHexagram(24, "渐", "风", "山", true, "艮", "||", "||", "|", "||", "|", "|", 3, 6));
            hexagrams.add(createHexagram(25, "震", "雷", "雷", true, "震", "|", "||", "||", "|", "||", "||", 6, 3));
            hexagrams.add(createHexagram(26, "豫", "雷", "地", true, "震", "||", "||", "||", "|", "||", "||", 1, 4));
            hexagrams.add(createHexagram(27, "解", "雷", "水", true, "震", "||", "|", "||", "|", "||", "||", 2, 5));
            hexagrams.add(createHexagram(28, "恒", "雷", "风", true, "震", "||", "|", "|", "|", "||", "||", 3, 6));
            hexagrams.add(createHexagram(29, "升", "地", "风", true, "震", "||", "|", "|", "||", "||", "||", 4, 1));
            hexagrams.add(createHexagram(30, "井", "水", "风", true, "震", "||", "|", "|", "||", "|", "||", 5, 2));
            hexagrams.add(createHexagram(31, "大过", "泽", "风", true, "震", "||", "|", "|", "|", "|", "||", 4, 1));
            hexagrams.add(createHexagram(32, "随", "泽", "雷", true, "震", "|", "||", "||", "|", "|", "||", 3, 6));
            hexagrams.add(createHexagram(33, "巽", "风", "风", false, "巽", "||", "|", "|", "||", "|", "|", 6, 3));
            hexagrams.add(createHexagram(34, "小畜", "风", "天", false, "巽", "|", "|", "|", "||", "|", "|", 1, 4));
            hexagrams.add(createHexagram(35, "家人", "风", "火", false, "巽", "|", "||", "|", "||", "|", "|", 2, 5));
            hexagrams.add(createHexagram(36, "益", "风", "雷", false, "巽", "|", "||", "||", "||", "|", "|", 3, 6));
            hexagrams.add(createHexagram(37, "无妄", "天", "雷", false, "巽", "|", "||", "||", "|", "|", "|", 4, 1));
            hexagrams.add(createHexagram(38, "噬嗑", "火", "雷", false, "巽", "|", "||", "||", "|", "||", "|", 5, 2));
            hexagrams.add(createHexagram(39, "颐", "山", "雷", false, "巽", "|", "||", "||", "||", "||", "|", 4, 1));
            hexagrams.add(createHexagram(40, "蛊", "山", "风", false, "巽", "||", "|", "|", "||", "||", "|", 3, 6));
            hexagrams.add(createHexagram(41, "离", "火", "火", false, "离", "|", "||", "|", "|", "||", "|", 6, 3));
            hexagrams.add(createHexagram(42, "旅", "火", "山", false, "离", "||", "||", "|", "|", "||", "|", 1, 4));
            hexagrams.add(createHexagram(43, "鼎", "火", "风", false, "离", "||", "|", "|", "|", "||", "|", 2, 5));
            hexagrams.add(createHexagram(44, "未济", "火", "水", false, "离", "||", "|", "||", "|", "||", "|", 3, 6));
            hexagrams.add(createHexagram(45, "蒙", "山", "水", false, "离", "||", "|", "||", "||", "||", "|", 4, 1));
            hexagrams.add(createHexagram(46, "涣", "风", "水", false, "离", "||", "|", "||", "||", "|", "|", 5, 2));
            hexagrams.add(createHexagram(47, "讼", "天", "水", false, "离", "||", "|", "||", "|", "|", "|", 4, 1));
            hexagrams.add(createHexagram(48, "同人", "天", "火", false, "离", "|", "||", "|", "|", "|", "|", 3, 6));
            hexagrams.add(createHexagram(49, "坤", "地", "地", false, "坤", "||", "||", "||", "||", "||", "||", 6, 3));
            hexagrams.add(createHexagram(50, "复", "地", "雷", false, "坤", "|", "||", "||", "||", "||", "||", 1, 4));
            hexagrams.add(createHexagram(51, "临", "地", "泽", false, "坤", "|", "|", "||", "||", "||", "||", 2, 5));
            hexagrams.add(createHexagram(52, "泰", "地", "天", false, "坤", "|", "|", "|", "||", "||", "||", 3, 6));
            hexagrams.add(createHexagram(53, "大壮", "雷", "天", false, "坤", "|", "|", "|", "|", "||", "||", 4, 1));
            hexagrams.add(createHexagram(54, "夬", "泽", "天", false, "坤", "|", "|", "|", "|", "|", "||", 5, 2));
            hexagrams.add(createHexagram(55, "需", "水", "天", false, "坤", "|", "|", "|", "||", "|", "||", 4, 1));
            hexagrams.add(createHexagram(56, "比", "水", "地", false, "坤", "||", "||", "||", "||", "|", "||", 3, 6));
            hexagrams.add(createHexagram(57, "兑", "泽", "泽", false, "兑", "|", "|", "||", "|", "|", "||", 6, 3));
            hexagrams.add(createHexagram(58, "困", "泽", "水", false, "兑", "||", "|", "||", "|", "|", "||", 1, 4));
            hexagrams.add(createHexagram(59, "翠", "泽", "地", false, "兑", "||", "||", "||", "|", "|", "||", 2, 5));
            hexagrams.add(createHexagram(60, "咸", "泽", "山", false, "兑", "||", "||", "|", "|", "|", "||", 3, 6));
            hexagrams.add(createHexagram(61, "蹇", "水", "山", false, "兑", "||", "||", "|", "||", "|", "||", 4, 1));
            hexagrams.add(createHexagram(62, "谦", "地", "山", false, "兑", "||", "||", "|", "||", "||", "||", 5, 2));
            hexagrams.add(createHexagram(63, "小过", "雷", "山", false, "兑", "||", "||", "|", "|", "||", "||", 4, 1));
            hexagrams.add(createHexagram(64, "归妹", "雷", "泽", false, "兑", "|", "|", "||", "|", "||", "||", 3, 6));
        }

        return hexagrams;
    }

    private static ArrayList<TrigramDefault> trigrams;

    private static TrigramDefault createTrigram(int id, String place, String name, EnumFiveElement fiveElement, String c1, String c2, String c3, String c4, String c5, String c6) {
        TrigramDefault trigram = new TrigramDefault();
        trigram.setId(id);
        trigram.setName(name);
        trigram.setPlace(place);
        trigram.setFiveElement(fiveElement);
        trigram.setC1(c1);
        trigram.setC2(c2);
        trigram.setC3(c3);
        trigram.setC4(c4);
        trigram.setC5(c5);
        trigram.setC6(c6);

        return trigram;
    }

    public static ArrayList<TrigramDefault> getTrigrams() {
        if (trigrams == null) {
            trigrams = new ArrayList<TrigramDefault>();
            trigrams.add(createTrigram(1, "乾", "天", EnumFiveElement.Metal, "子", "寅", "辰", "午", "申", "戌"));
            trigrams.add(createTrigram(2, "坎", "水", EnumFiveElement.Water, "寅", "辰", "午", "申", "戌", "子"));
            trigrams.add(createTrigram(3, "艮", "山", EnumFiveElement.Earth, "辰", "午", "申", "戌", "子", "寅"));
            trigrams.add(createTrigram(4, "震", "雷", EnumFiveElement.Wood, "子", "寅", "辰", "午", "申", "戌"));
            trigrams.add(createTrigram(5, "巽", "风", EnumFiveElement.Wood, "丑", "亥", "酉", "未", "巳", "卯"));
            trigrams.add(createTrigram(6, "离", "火", EnumFiveElement.Fire, "卯", "丑", "亥", "酉", "未", "巳"));
            trigrams.add(createTrigram(7, "坤", "地", EnumFiveElement.Earth, "未", "巳", "卯", "丑", "亥", "酉"));
            trigrams.add(createTrigram(8, "兑", "泽", EnumFiveElement.Metal, "巳", "卯", "丑", "亥", "酉", "未"));
        }
        return trigrams;
    }

    private static HashMap<String, String> trigramReverse;

    public static String getTrigramReverseByName(String name) {
        if (trigramReverse == null) {
            trigramReverse = new HashMap<String, String>();
            trigramReverse.put("天", "地");
            trigramReverse.put("水", "火");
            trigramReverse.put("雷", "风");
            trigramReverse.put("山", "泽");
            trigramReverse.put("泽", "山");
            trigramReverse.put("风", "雷");
            trigramReverse.put("火", "水");
            trigramReverse.put("地", "天");
        }
        return trigramReverse.get(name);
    }

    private static HashMap<Integer, String> sixAnimals;

    public static HashMap<Integer, String> getSixAnimals() {
        if (sixAnimals == null) {
            sixAnimals = new HashMap<Integer, String>();
            sixAnimals.put(1, "青龙");
            sixAnimals.put(2, "朱雀");
            sixAnimals.put(3, "勾陈");
            sixAnimals.put(4, "螣蛇");
            sixAnimals.put(5, "白虎");
            sixAnimals.put(6, "玄武");
        }
        return sixAnimals;
    }

    private static HashMap<String, Integer> celestialStemSixAnimalsMapping;

    public static HashMap<String, Integer> getCelestialStemSixAnimalsMapping() {
        if (celestialStemSixAnimalsMapping == null) {
            celestialStemSixAnimalsMapping = new HashMap<String, Integer>();
            celestialStemSixAnimalsMapping.put("甲乙", 1);
            celestialStemSixAnimalsMapping.put("丙丁", 2);
            celestialStemSixAnimalsMapping.put("戊", 3);
            celestialStemSixAnimalsMapping.put("己", 4);
            celestialStemSixAnimalsMapping.put("庚辛", 5);
            celestialStemSixAnimalsMapping.put("壬癸", 6);
        }
        return celestialStemSixAnimalsMapping;
    }

    private static HashMap<EnumLing, int[]> lingEarthlyBranchMapping;

    public static HashMap<EnumLing, int[]> getLingEarthlyBranchMapping() {
        if (lingEarthlyBranchMapping == null) {
            lingEarthlyBranchMapping.put(EnumLing.Chun, new int[]{3, 4});
            lingEarthlyBranchMapping.put(EnumLing.Xia, new int[]{6, 7});
            lingEarthlyBranchMapping.put(EnumLing.Qiu, new int[]{9, 10});
            lingEarthlyBranchMapping.put(EnumLing.Dong, new int[]{1, 12});
            lingEarthlyBranchMapping.put(EnumLing.Tu, new int[]{2, 5, 8, 11});
        }
        return lingEarthlyBranchMapping;
    }

    ;


    private static HashMap<Integer, EnumLing> lingYuQiMapping;

    public static HashMap<Integer, EnumLing> getLingYuQiMapping() {
        if (lingYuQiMapping == null) {
            lingYuQiMapping = new HashMap<Integer, EnumLing>();
            lingYuQiMapping.put(5, EnumLing.Chun);
            lingYuQiMapping.put(8, EnumLing.Xia);
            lingYuQiMapping.put(11, EnumLing.Qiu);
            lingYuQiMapping.put(2, EnumLing.Dong);
        }
        return lingYuQiMapping;
    }

    private static HashMap<Integer, Integer> earthlyBranchForward;

    public static HashMap<Integer, Integer> getEarthlyBranchForward() {
        if (earthlyBranchForward == null) {
            earthlyBranchForward.put(3, 4);
            earthlyBranchForward.put(9, 10);
            earthlyBranchForward.put(2, 5);
            earthlyBranchForward.put(8, 11);
        }
        return earthlyBranchForward;
    }

    private static HashMap<Integer, Integer> earthlyBranchBackward;

    public static HashMap<Integer, Integer> getEarthlyBranchBackward() {
        if (earthlyBranchBackward == null) {
            earthlyBranchBackward.put(4, 3);
            earthlyBranchBackward.put(10, 9);
            earthlyBranchBackward.put(5, 2);
            earthlyBranchBackward.put(11, 8);
        }
        return earthlyBranchBackward;
    }

    private static HashMap<Integer,Integer> earthlyBranchSixSuit;

    public static HashMap<Integer,Integer> getEarthlyBranchSixSuit(Context context)
    {
        if(earthlyBranchSixSuit == null) {
            XmlModelCache xmlModelCache = XmlModelCache.getInstance(context);
            earthlyBranchSixSuit = new HashMap<Integer, Integer>();
            for(Pair<String,String> pair: xmlModelCache.getTerrestrial().getSixSuits().keySet())
            {
                int firstId = xmlModelCache.getTerrestrial().getTerrestrials().get(pair.first).getId();
                int secondId = xmlModelCache.getTerrestrial().getTerrestrials().get(pair.second).getId();
                earthlyBranchSixSuit.put(firstId,secondId);
                earthlyBranchSixSuit.put(secondId,firstId);
            }
        }
        return earthlyBranchSixSuit;
    }

    private static HashMap<Integer,Integer> earthlyBranchSixInverse;

    public static HashMap<Integer,Integer> getEarthlyBranchSixInverse(Context context)
    {
        if(earthlyBranchSixInverse == null)
        {
            XmlModelCache xmlModelCache = XmlModelCache.getInstance(context);
            earthlyBranchSixInverse = new HashMap<Integer, Integer>();
            for(Pair<String,String> pair: xmlModelCache.getTerrestrial().getSixInverses())
            {
                int firstId = xmlModelCache.getTerrestrial().getTerrestrials().get(pair.first).getId();
                int secondId = xmlModelCache.getTerrestrial().getTerrestrials().get(pair.second).getId();
                earthlyBranchSixInverse.put(firstId,secondId);
                earthlyBranchSixInverse.put(secondId,firstId);
            }
        }

        return earthlyBranchSixInverse;
    }

    private static HashMap<Integer,Integer> bathMapping;

    public static HashMap<Integer,Integer> getBathMapping(Context context)
    {
        if(bathMapping == null) {

            bathMapping = new HashMap<Integer, Integer>();

            XmlModelCache xmlModelCache = XmlModelCache.getInstance(context);

            for (String t : xmlModelCache.getInstance(context).getTerrestrial().getTerrestrials().keySet()) {

                XmlModelExtProperty property = xmlModelCache.getTerrestrial().getTerrestrials().get(t);

                ArrayList<Pair<Integer,String>> list = xmlModelCache.getXmlModelTwelveGrow().getFiveElementGrowsMapping().get(EnumFiveElement.toEnum(property.getWuXing()));

                for(Pair<Integer,String> pair: list)
                {
                    if(pair.second.equals("沐浴"))
                        bathMapping.put(property.getId(),pair.first);
                }
            }
        }
        return bathMapping;
    }

}
