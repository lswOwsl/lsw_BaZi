package lsw.hexagram;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.util.Property;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lsw.library.StringHelper;
import lsw.model.EarthlyBranch;
import lsw.model.EnumAttachedType;
import lsw.model.EnumFiveElement;
import lsw.model.EnumLineSymble;
import lsw.model.EnumSixRelation;
import lsw.model.Hexagram;
import lsw.model.HexagramDefault;
import lsw.model.Line;
import lsw.model.Trigram;
import lsw.model.TrigramDefault;
import lsw.value.Default;
import lsw.xml.XmlModelCache;
import lsw.xml.model.XmlModelExtProperty;

/**
 * Created by swli on 8/10/2015.
 */
public class Builder
{

    private Context context;
    XmlModelCache xmlModelCache;

    static ArrayList<Hexagram> hexagramDefaultSixtyFour;

    public Builder(Context context)
    {
        if (hexagramDefaultSixtyFour == null)
        {
            this.context = context;
            xmlModelCache = XmlModelCache.getInstance(context);
            hexagramDefaultSixtyFour = constructHexagramSixtyFour();
        }
    }

    private static HashMap<Integer,EarthlyBranch> earthlyBranch_Dic;

    public HashMap<Integer, EarthlyBranch> getEarthlyBranch_Dic() {

        if (earthlyBranch_Dic == null) {

            earthlyBranch_Dic = new HashMap<Integer, EarthlyBranch>();

            for(String key: xmlModelCache.getTerrestrial().getTerrestrials().keySet()) {
                XmlModelExtProperty xmlModelExtProperty = xmlModelCache.getTerrestrial().getTerrestrials().get(key);
                EarthlyBranch earthlyBranch = new EarthlyBranch();
                earthlyBranch.setId(xmlModelExtProperty.getId());
                earthlyBranch.setName(key);
                earthlyBranch.setFiveElement(EnumFiveElement.toEnum(xmlModelExtProperty.getWuXing()));
                earthlyBranch_Dic.put(xmlModelExtProperty.getId(),earthlyBranch);
            }

        }
        return earthlyBranch_Dic;
    }

    public ArrayList<Pair<Hexagram,Hexagram>> getAllHexagrams()
    {
        ArrayList<Pair<Hexagram,Hexagram>> list = new ArrayList<Pair<Hexagram,Hexagram>>();

        ArrayList<Hexagram> sixtyFourOriginal = hexagramDefaultSixtyFour;
        ArrayList<Hexagram> sixtyFourTarget = hexagramDefaultSixtyFour;

        for (Hexagram o : sixtyFourOriginal)
        {
            for (Hexagram t : sixtyFourTarget)
            {
                list.add(getHexagramByNames(o.getName(), t.getName()));
            }
        }

        return list;
    }

    public String getHexagramNameByTrigram(String upperTrigramName, String lowerTrigramName)
    {
        for(Hexagram hexagram: hexagramDefaultSixtyFour) {
            if(hexagram.getUpper().getName().equals(upperTrigramName) && hexagram.getLower().getName().equals(lowerTrigramName))
                return hexagram.getName();
        }

        return "";
    }

    public Hexagram getCloneHexagram(String name) {

        try {
            for (Hexagram hexagram : hexagramDefaultSixtyFour) {
                if (hexagram.getName().equals(name))
                    return hexagram.deepClone();
            }
        }
        catch (Exception ex) {
            Log.d("clone faild",ex.getMessage());
        }
        return null;
    }

    public Hexagram getHexagramByName(String originalName)
    {
        return getHexagramByNames(originalName, "").first;
    }

    public Pair<Hexagram, Hexagram> getHexagramByNames(String originalName, String resultName)
    {
        Hexagram originalHexagram = getCloneHexagram(originalName);

        if (originalHexagram == null)
            return null;
            //throw new Exception(originalName);

        setFuShenOnHexagram(originalHexagram,null);

        if (!StringHelper.isNullOrEmpty(resultName))
        {
            if (resultName != originalName)
            {
                Hexagram resultHexagram = getCloneHexagram(resultName);

                List<Line> originalLines = originalHexagram.getLines();
                HashMap<Integer,EnumLineSymble> resultYaosDic = new HashMap<Integer,EnumLineSymble>();
                for (Line line : resultHexagram.getLines()) {
                    resultYaosDic.put(line.getPosition(),line.getSymble());
                }

                for(Line line : originalLines)
                {
                    if (!line.getSymble().equals(resultYaosDic.get(line.getPosition())))
                    {
                        line.setSymble(lineToVariant(line.getSymble()));
                    }
                }

                List<Line> resultLines = resultHexagram.getLines();
                for (Line line : resultLines)
                {
                    line.setSixRelation(parseSixRelationByFiveElement(parseFiveElementByPalace(originalHexagram.getPlace()), line.getFiveElement()));
                }

                return Pair.create(originalHexagram, resultHexagram);
            }
            else
                return Pair.create(originalHexagram, originalHexagram);
        }
        return Pair.create(originalHexagram, null);
    }

    public Hexagram getHexagramByLines(EnumLineSymble[] lines) throws Exception
    {
        if (lines != null && lines.length == 6)
        {
            ArrayList<Line> listYao = new ArrayList<Line>();

            for (int i = 0; i < lines.length; i++)
            {
                Line line = new Line();
                line.setPosition(i+1);
                line.setSymble(lines[i]);

                listYao.add(line);
            }

            return getHexagramByLines(listYao, true);
        }
        else
        {
            Log.d("getHexagramByLines", "get hexagram failed.");
            //throw new NotSupportedException("method:GetGuaByYao");
        }

        return null;
    }

    public Hexagram getHexagramByLines(ArrayList<Line> lines, boolean includeFuShen) throws Exception
    {
        HashMap<ArrayList<Line>,Hexagram> sixtyFourDicForLines = new HashMap<ArrayList<Line>, Hexagram>();

        for(Hexagram hexagram : hexagramDefaultSixtyFour)
        {
            sixtyFourDicForLines.put(hexagram.getLines(),hexagram.deepClone());
        }

        //to avoid original yao's value be modified
        ArrayList<Line> noDynamicYaos = new ArrayList<Line>();
        for(Line line : lines)
        {
            Line temp = new Line();
            temp.setPosition(line.getPosition());
            temp.setSymble(lineToInvariant(line.getSymble()));
            noDynamicYaos.add(temp);
        }
        Hexagram result = null;
        for(ArrayList<Line> key: sixtyFourDicForLines.keySet())
        {
            int count = 0;
            for(Line noDynamicLine: noDynamicYaos)
            {
                for(Line line: key)
                {
                    if(line.getPosition() == noDynamicLine.getPosition() && noDynamicLine.getSymble().equals(line.getSymble()))
                    {
                        count ++;
                    }
                }
            }
            if(count == 6) {
                result = sixtyFourDicForLines.get(key);
                break;
            }
        }

        for(Line l : lines)
        {
            if (l.getSymble() == EnumLineSymble.LaoYin || l.getSymble() == EnumLineSymble.LaoYang)
            {
                if (l.getPosition() <= 3)
                {
                    Line ly = getLineByPosition(result.getLower().getLines(), l.getPosition());
                    ly.setSymble(l.getSymble());
                }
                else
                {
                    Line ly = getLineByPosition(result.getUpper().getLines(), l.getPosition());
                    ly.setSymble(l.getSymble());
                }
            }
        }

        if (includeFuShen)
            setFuShenOnHexagram(result);

        return result;
    }

    public Line getLineByPosition(ArrayList<Line> lines, int position)
    {
        for(Line line:lines)
        {
            if(line.getPosition() == position)
                return line;
        }
        return null;
    }

    //易林补遗
    public void SetFuShenByYiLinBuYi(Hexagram hexagram)
    {
        TrigramDefault upperTrigram = getTrigramDefaultByName(hexagram.getUpper().getName());
        TrigramDefault lowerTrigram = getTrigramDefaultByName(hexagram.getLower().getName());

        createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), upperTrigram, 4);
        createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), lowerTrigram, 1);
    }

    //易冒
    public void SetFuShenByYiMao(Hexagram hexagram)
    {
        if (hexagram.getId() % 8 == 7)
        {
            HexagramDefault preHexagram = getHexagramDefaultById(hexagram.getId() - 1);
            TrigramDefault upperTrigram = getTrigramDefaultByName(preHexagram.getUpperPart());
            TrigramDefault lowerTrigram = getTrigramDefaultByName(Default.getTrigramReverseByName(hexagram.getLower().getName()));

            createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), upperTrigram, 4);
            createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), lowerTrigram, 1);
        }
        else
        {
            SetFuShenByYiLinBuYi(hexagram);
        }
    }

    //易隐
    public void SetFuShenByYiYin(Hexagram hexagram)
    {
        int remainder = hexagram.getId() % 8;
        int[] array = new int[]{2,3,4};
        if (Arrays.asList(array).contains(remainder))
        {
            String lowerPart = getHexagramDefaultByName(hexagram.getPlace()).getLowerPart();
            TrigramDefault upperTrigram = getTrigramDefaultByName(Default.getTrigramReverseByName(hexagram.getUpper().getName()));
            TrigramDefault lowerTrigram = getTrigramDefaultByName(lowerPart);

            createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), upperTrigram, 4);
            createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), lowerTrigram, 1);

        }
        else
        {
            SetFuShenByYiLinBuYi(hexagram);
        }
    }

    public void setFuShenOnHexagram(Hexagram hexagram, EnumAttachedType type)
    {
        //归魂卦
        if (hexagram.getId() % 8 == 0)
        {
            HexagramDefault attachedGua =getHexagramDefaultById(hexagram.getId() - 4);
            TrigramDefault upperTrigram = getTrigramDefaultByName(attachedGua.getUpperPart());
            TrigramDefault lowerTrigram = getTrigramDefaultByName(attachedGua.getLowerPart());

            createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), upperTrigram, 4);
            createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), lowerTrigram, 1);
        }
        //纯卦
        else if (hexagram.getId() % 8 == 1)
        {
            TrigramDefault upperTrigram = getTrigramDefaultByName(hexagram.getUpper().getName());
            TrigramDefault lowerTrigram = getTrigramDefaultByName(hexagram.getLower().getName());

            createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), upperTrigram, 4);
            createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), lowerTrigram, 1);
        }
        else
        {
            if (type == EnumAttachedType.YiLinBuYi)
            {
                SetFuShenByYiLinBuYi(hexagram);
            }
            else if (type == EnumAttachedType.YiMao)
            {
                SetFuShenByYiMao(hexagram);
            }
            else if (type == EnumAttachedType.YiYin)
            {
                SetFuShenByYiYin(hexagram);
            }
        }
    }

    public HexagramDefault getHexagramDefaultByName(String name)
    {
        for(HexagramDefault hexagramDefault: Default.getHexagrams())
        {
            if(hexagramDefault.getName().equals(name))
                return hexagramDefault;
        }
        return null;
    }


    public HexagramDefault getHexagramDefaultById(int id)
    {
        for(HexagramDefault hexagramDefault: Default.getHexagrams())
        {
            if(hexagramDefault.getId() == id)
                return hexagramDefault;
        }
        return null;
    }

    public TrigramDefault getTrigramDefaultByName(String name)
    {
        for(TrigramDefault trigramDefault: Default.getTrigrams())
        {
            if(trigramDefault.getName().equals(name))
                return trigramDefault;
        }
        return null;
    }

    public void setFuShenOnHexagram(Hexagram gua) throws Exception {
        try
        {

                ArrayList<EnumSixRelation> sixRelations = new ArrayList<EnumSixRelation>();
                for (Line line : gua.getLines()) {
                    if (!sixRelations.contains(line.getSixRelation())) {
                        sixRelations.add(line.getSixRelation());
                    }
                }

                ArrayList<EnumSixRelation> sixeRelationAll = new ArrayList<EnumSixRelation>();
                sixeRelationAll.add(EnumSixRelation.FuMu);
                sixeRelationAll.add(EnumSixRelation.GuanGui);
                sixeRelationAll.add(EnumSixRelation.QiCai);
                sixeRelationAll.add(EnumSixRelation.XiongDi);
                sixeRelationAll.add(EnumSixRelation.ZiSun);

                ArrayList<EnumSixRelation> sixRelationNotExisted = new ArrayList<EnumSixRelation>();

                for (EnumSixRelation esrAll : sixeRelationAll) {
                    if (!sixRelations.contains(esrAll))
                        sixRelationNotExisted.add(esrAll);
                }

                TrigramDefault trigramDefault = null;
                for (TrigramDefault td : Default.getTrigrams()) {
                    if (td.getPlace().equals(gua.getPlace())) {
                        trigramDefault = td;
                        break;
                    }
                }

                for (Line line : gua.getLines()) {
                    String earthlyBranch = "";
                    //switch (line.Position)
                    //{
                    //    case 1:
                    //        earthlyBranch = prop.C1;
                    //        break;
                    //    case 2:
                    //        earthlyBranch = prop.C2;
                    //        break;
                    //    case 3:
                    //        earthlyBranch = prop.C3;
                    //        break;
                    //    case 4:
                    //        earthlyBranch = prop.C4;
                    //        break;
                    //    case 5:
                    //        earthlyBranch = prop.C5;
                    //        break;
                    //    case 6:
                    //        earthlyBranch = prop.C6;
                    //        break;
                    //}
                    Method method = TrigramDefault.class.getMethod("getC"+line.getPosition());
                    earthlyBranch = method.invoke(trigramDefault,null).toString();

                    createLineFuShen(gua.getPlace(), line, earthlyBranch, sixRelationNotExisted);
                }

        } catch (NoSuchMethodException e) {
            System.out.println(e.toString());
        }
    }

    public Hexagram changeToHexagram(Hexagram original, boolean includeFuShen) throws Exception
    {
        List<Line> symbles = original.getLines();

        for(Line line: symbles) {

            if (line.getSymble() == EnumLineSymble.LaoYang || line.getSymble() == EnumLineSymble.LaoYin)
            {
                ArrayList<Line> targetSymbles = new ArrayList<Line>();
                for(Line s : symbles)
                {
                    EnumLineSymble symble = lineChange(s.getSymble());

                    Line tempLine = new Line();
                    line.setSymble(symble);
                    line.setPosition(s.getPosition());

                    targetSymbles.add(tempLine);
                }

                Hexagram result = getHexagramByLines(targetSymbles, includeFuShen);

                for(Line y : result.getUpper().getLines())
                {
                    y.setSixRelation(parseSixRelationByFiveElement(original.getFiveElement(), y.getFiveElement()));
                }

                for(Line y : result.getLower().getLines())
                {
                    y.setSixRelation(parseSixRelationByFiveElement(original.getFiveElement(), y.getFiveElement()));
                }
                return result;
            }
        }
        return null;
    }

    public EnumLineSymble lineToInvariant(EnumLineSymble symble)
    {
        if (symble == EnumLineSymble.LaoYang)
            return EnumLineSymble.Yang;
        else if (symble == EnumLineSymble.LaoYin)
            return EnumLineSymble.Yin;
        else
            return symble;
    }

    public EnumLineSymble lineToVariant(EnumLineSymble original)
    {
        if (original == EnumLineSymble.Yang)
            return EnumLineSymble.LaoYang;
        if (original == EnumLineSymble.Yin)
            return EnumLineSymble.LaoYin;
        return original;
    }

    public EnumLineSymble lineChange(EnumLineSymble original)
    {
        if (original == EnumLineSymble.LaoYang)
            return EnumLineSymble.Yin;
        else if (original == EnumLineSymble.LaoYin)
            return EnumLineSymble.Yang;
        else
            return original;
    }

    public ArrayList<Hexagram> constructHexagramSixtyFour()
    {
        ArrayList<Hexagram> list = new ArrayList<Hexagram>();

        for (HexagramDefault g : Default.getHexagrams())
        {
            Hexagram gua = new Hexagram();
            gua.setId(g.getId());
            gua.setName(g.getName());

            gua.setPlace(g.getPlace());
            gua.setSelf(g.getSelf());
            gua.setTarget(g.getTarget());

            gua.setFiveElement(parseFiveElementByPalace(gua.getPlace()));

            gua.setUpper(createTrigram(g, false));
            gua.setLower(createTrigram(g, true));

            list.add(gua);
        }

        return list;
    }

    public Trigram createTrigram(HexagramDefault g, boolean isLowerPart)
    {
        Trigram gh = new Trigram();



        gh.setLines(new ArrayList<Line>());

        gh.setFiveElement(parseFiveElementByPalace(g.getPlace()));

        if (isLowerPart)
        {
            TrigramDefault tempTrigram = null;
            for(TrigramDefault trigramDefault: Default.getTrigrams())
            {
                if(trigramDefault.getName().equals(g.getLowerPart()))
                {
                    tempTrigram = trigramDefault;
                    break;
                }
            }

            gh.setName(g.getLowerPart());
            gh.getLines().add(createLine(g.getPlace(), 1, tempTrigram.getC1(), g.getC1()));
            gh.getLines().add(createLine(g.getPlace(), 2, tempTrigram.getC2(), g.getC2()));
            gh.getLines().add(createLine(g.getPlace(), 3, tempTrigram.getC3(), g.getC3()));
        }
        else
        {
            TrigramDefault tempTrigram = null;
            for(TrigramDefault trigramDefault: Default.getTrigrams())
            {
                if(trigramDefault.getName().equals(g.getUpperPart()))
                {
                    tempTrigram = trigramDefault;
                    break;
                }
            }

            gh.setName(g.getUpperPart());
            gh.getLines().add(createLine(g.getPlace(), 4, tempTrigram.getC4(), g.getC4()));
            gh.getLines().add(createLine(g.getPlace(), 5, tempTrigram.getC5(), g.getC5()));
            gh.getLines().add(createLine(g.getPlace(), 6, tempTrigram.getC6(), g.getC6()));
        }

        return gh;
    }

    public EnumLineSymble getSybmleByString(String sybmle) {
        if (sybmle.equals("|"))
            return EnumLineSymble.Yang;
        else if (sybmle.equals("||"))
            return EnumLineSymble.Yin;
        else if (sybmle.equals("x"))
            return EnumLineSymble.LaoYin;
        else
            return EnumLineSymble.LaoYang;
    }

    public Line createLine(String place, int position, String earthlyBranch, String symble)
    {
        Line line = new Line();
        EarthlyBranch tempEarthlyBranch = new EarthlyBranch();
        tempEarthlyBranch.setName(earthlyBranch);
        XmlModelExtProperty xmlModelExtProperty = xmlModelCache.getTerrestrial().getTerrestrials().get(earthlyBranch);
        tempEarthlyBranch.setId(xmlModelExtProperty.getId());
        EnumFiveElement enumFiveElement = EnumFiveElement.toEnum(xmlModelExtProperty.getWuXing());
        tempEarthlyBranch.setFiveElement(enumFiveElement);

        line.setEarthlyBranch(tempEarthlyBranch);
        line.setPosition(position);
        line.setSymble(getSybmleByString(symble));
        line.setFiveElement(parseFiveElementByEarthlyBranch(earthlyBranch));
        line.setSixRelation(parseSixRelationByFiveElement(parseFiveElementByPalace(place), line.getFiveElement()));
        return line;
    }

    public void createLineFuShen(String place, ArrayList<Line> lines, TrigramDefault trigram, int beginLineIndex)
    {
        try {
            for (int i = 0; i < lines.size(); i++) {
                String earthlyBranch = trigram.getClass().getMethod("getC" + (i + beginLineIndex), null).toString();
                EnumFiveElement fiveElement = parseFiveElementByEarthlyBranch(earthlyBranch);
                EnumSixRelation sixRelation = parseSixRelationByFiveElement(parseFiveElementByPalace(place), fiveElement);

                XmlModelExtProperty xmlModelExtProperty = xmlModelCache.getTerrestrial().getTerrestrials().get(earthlyBranch);

                EarthlyBranch tempEarthlyBranch = new EarthlyBranch();
                tempEarthlyBranch.setName(earthlyBranch);
                tempEarthlyBranch.setId(xmlModelExtProperty.getId());
                EnumFiveElement enumFiveElement = EnumFiveElement.toEnum(xmlModelExtProperty.getWuXing());
                tempEarthlyBranch.setFiveElement(enumFiveElement);

                lines.get(i).setEarthlyBranchAttached(tempEarthlyBranch);
                lines.get(i).setSixRelationAttached(sixRelation);
                lines.get(i).setFiveElement(fiveElement);
            }
        }
        catch(NoSuchMethodException e) {
            System.out.println(e.toString());
        }
    }

    public void createLineFuShen(String place, Line line,String earthlyBranch, ArrayList<EnumSixRelation> existedSixRelation)
    {
        EnumFiveElement fiveElement = parseFiveElementByEarthlyBranch(earthlyBranch);
        EnumSixRelation sixRelation = parseSixRelationByFiveElement(parseFiveElementByPalace(place), fiveElement);

        if (existedSixRelation.contains(sixRelation))
        {
            EarthlyBranch earthlyBranchAttached = new EarthlyBranch();

            earthlyBranchAttached.setName(earthlyBranch);
            XmlModelExtProperty xmlModelExtProperty = xmlModelCache.getTerrestrial().getTerrestrials().get(earthlyBranch);
            earthlyBranchAttached.setId(xmlModelExtProperty.getId());
            EnumFiveElement enumFiveElement = EnumFiveElement.toEnum(xmlModelExtProperty.getWuXing());
            earthlyBranchAttached.setFiveElement(enumFiveElement);

            line.setEarthlyBranchAttached(earthlyBranchAttached);
            line.setSixRelationAttached(sixRelation);
            line.setFiveElementAttached(fiveElement);
        }
    }

    public static EnumSixRelation parseSixRelationByFiveElement(EnumFiveElement hexagramProperty, EnumFiveElement lineProperty)
    {
        if (hexagramProperty == lineProperty)
            return EnumSixRelation.XiongDi;
        else if (FiveElementRestrain(hexagramProperty) == lineProperty)
            return EnumSixRelation.GuanGui;
        else if (FiveElementSupport(hexagramProperty) == lineProperty)
            return EnumSixRelation.FuMu;
        else if (hexagramProperty == FiveElementRestrain(lineProperty))
            return EnumSixRelation.QiCai;
            //if (guaProperty == WuXingSupport(yaoProperty))
        else
            return EnumSixRelation.ZiSun;
    }

    public EnumFiveElement parseFiveElementByEarthlyBranch(String obj)
    {
        for(String key : xmlModelCache.getTerrestrial().getTerrestrials().keySet()) {
            if(key.equals(obj)) {
                return EnumFiveElement.toEnum(xmlModelCache.getTerrestrial().getTerrestrials().get(key).getWuXing());
            }
        }
        return null;
    }

    public EnumFiveElement parseFiveElementByPalace(String place)
    {
        for(TrigramDefault trigramDefault: Default.getTrigrams()) {
            if(trigramDefault.getPlace().equals(place))
                return trigramDefault.getFiveElement();

        }
        return null;
    }

    public static EnumFiveElement FiveElementRestrain(EnumFiveElement obj)
    {
        switch (obj)
        {
            case Metal:
                return EnumFiveElement.Fire;
            case Wood:
                return EnumFiveElement.Metal;
            case Water:
                return EnumFiveElement.Earth;
            case Fire:
                return EnumFiveElement.Water;
            default:
                return EnumFiveElement.Wood;
        }
    }

    public static EnumFiveElement FiveElementSupport(EnumFiveElement obj)
    {
        switch (obj)
        {
            case Metal:
                return EnumFiveElement.Earth;
            case Wood:
                return EnumFiveElement.Water;
            case Water:
                return EnumFiveElement.Metal;
            case Fire:
                return EnumFiveElement.Wood;
            default:
                return EnumFiveElement.Fire;
        }
    }
}







