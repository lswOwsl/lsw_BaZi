package lsw.hexagram;

import android.content.Context;
import android.util.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lsw.library.StringHelper;
import lsw.model.EarthlyBranch;
import lsw.model.EnumFuShenType;
import lsw.model.EnumFiveElement;
import lsw.model.EnumLineSymbol;
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

    private Builder(Context context)
    {
        if (hexagramDefaultSixtyFour == null)
        {
            this.context = context;
            xmlModelCache = XmlModelCache.getInstance(context);
            hexagramDefaultSixtyFour = constructHexagramSixtyFour();
        }
    }

    private static Builder builder;

    public static Builder getInstance(Context context)
    {
        if(builder == null)
            builder = new Builder(context);
        return builder;
    }

    public ArrayList<Pair<Hexagram,Hexagram>> getAllHexagrams() throws Exception
    {
        return getAllHexagrams(null);
    }

    public ArrayList<Pair<Hexagram,Hexagram>> getAllHexagrams(EnumFuShenType enumFuShenType) throws Exception
    {
        ArrayList<Pair<Hexagram,Hexagram>> list = new ArrayList<Pair<Hexagram,Hexagram>>();

        ArrayList<Hexagram> sixtyFourOriginal = hexagramDefaultSixtyFour;
        ArrayList<Hexagram> sixtyFourTarget = hexagramDefaultSixtyFour;

        for (Hexagram o : sixtyFourOriginal)
        {
            for (Hexagram t : sixtyFourTarget)
            {
                list.add(getHexagramByNames(o.getName(), t.getName(), enumFuShenType));
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

    public Hexagram getCloneHexagram(String name) throws Exception{

        for (Hexagram hexagram : hexagramDefaultSixtyFour) {
            if (hexagram.getName().equals(name))
                return hexagram.deepClone();
        }
        return null;
    }

    public Hexagram getHexagramByName(String originalName) throws Exception
    {
        return getHexagramByNames(originalName, "", null).first;
    }

    public Hexagram getHexagramByName(String originalName, EnumFuShenType enumFuShenType) throws Exception
    {
        return getHexagramByNames(originalName, "", enumFuShenType).first;
    }

    public Pair<Hexagram, Hexagram> getHexagramByNames(String originalName, String resultName) throws Exception
    {
        return getHexagramByNames(originalName,resultName,null);
    }

    public Pair<Hexagram, Hexagram> getHexagramByNames(String originalName, String resultName, EnumFuShenType enumFuShenType) throws Exception
    {
        Hexagram originalHexagram = getCloneHexagram(originalName);

        if (originalHexagram == null)
            throw new Exception("Hexagram not found!");

        if(enumFuShenType == null)
        {
            setFuShenOnHexagram(originalHexagram);
        }
        else
        {
            setFuShenOnHexagram(originalHexagram, enumFuShenType);
        }


        if (!StringHelper.isNullOrEmpty(resultName))
        {
            if (resultName != originalName)
            {
                Hexagram resultHexagram = getCloneHexagram(resultName);

                List<Line> originalLines = originalHexagram.getLines();
                HashMap<Integer,EnumLineSymbol> resultYaosDic = new HashMap<Integer,EnumLineSymbol>();
                for (Line line : resultHexagram.getLines()) {
                    resultYaosDic.put(line.getPosition(),line.getLineSymbol());
                }

                for(Line line : originalLines)
                {
                    if (!line.getLineSymbol().equals(resultYaosDic.get(line.getPosition())))
                    {
                        line.setLineSymbol(lineToVariant(line.getLineSymbol()));
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

    public Hexagram getHexagramByLines(EnumLineSymbol... lines) throws Exception
    {
        if (lines != null && lines.length == 6)
        {
            ArrayList<Line> listYao = new ArrayList<Line>();

            for (int i = 0; i < lines.length; i++)
            {
                Line line = new Line();
                line.setPosition(i+1);
                line.setLineSymbol(lines[i]);

                listYao.add(line);
            }

            return getHexagramByLines(listYao, true);
        }
        else
        {
            throw new Exception("Need six lines to search the Hexagram.");
        }
    }

    public Hexagram getHexagramByLines(ArrayList<Line> lines, boolean includeFuShen) throws Exception
    {
        HashMap<ArrayList<Line>,String> sixtyFourDicForLines = new HashMap<ArrayList<Line>, String>();

        for(Hexagram hexagram : hexagramDefaultSixtyFour)
        {
            sixtyFourDicForLines.put(hexagram.getLines(),hexagram.getName());
        }

        //to avoid original yao's value be modified
        ArrayList<Line> noDynamicYaos = new ArrayList<Line>();
        for(Line line : lines)
        {
            Line temp = new Line();
            temp.setPosition(line.getPosition());
            temp.setLineSymbol(lineToInvariant(line.getLineSymbol()));
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
                    if(line.getPosition() == noDynamicLine.getPosition() && noDynamicLine.getLineSymbol().equals(line.getLineSymbol()))
                    {
                        count ++;
                    }
                }
            }
            if(count == 6) {
                result = getHexagramByName(sixtyFourDicForLines.get(key));
                break;
            }
        }

        for(Line l : lines)
        {
            if (l.getLineSymbol() == EnumLineSymbol.LaoYin || l.getLineSymbol() == EnumLineSymbol.LaoYang)
            {
                if (l.getPosition() <= 3)
                {
                    Line ly = getLineByPosition(result.getLower().getLines(), l.getPosition());
                    ly.setLineSymbol(l.getLineSymbol());
                }
                else
                {
                    Line ly = getLineByPosition(result.getUpper().getLines(), l.getPosition());
                    ly.setLineSymbol(l.getLineSymbol());
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
        //伏神斗本宫的按纯卦的起，所以直接用getPlace来查纯卦是哪一个卦
        TrigramDefault trigramDefault = getTrigramDefaultByName(hexagram.getPlace());

        createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), trigramDefault, 4);
        createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), trigramDefault, 1);
    }

    //易冒
    public void SetFuShenByYiMao(Hexagram hexagram)
    {
        //游魂卦也就是本宫的第7卦，伏神取第六卦的上卦本卦和第六卦下卦的综卦
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
        //每宫的2.3.4卦，都用上卦的综卦，下卦的本卦
        int remainder = hexagram.getId() % 8;
        List<Integer> array = new ArrayList<Integer>();
        array.add(2);
        array.add(3);
        array.add(4);

        if (array.contains(remainder))
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

    public void setFuShenOnHexagram(Hexagram hexagram, EnumFuShenType type)
    {
        //归魂卦  本宫最后一卦
        if (hexagram.getId() % 8 == 0)
        {
            //上本卦，下综卦
            HexagramDefault attachedGua =getHexagramDefaultById(hexagram.getId() - 4);
            TrigramDefault upperTrigram = getTrigramDefaultByName(attachedGua.getUpperPart());
            TrigramDefault lowerTrigram = getTrigramDefaultByName(attachedGua.getLowerPart());

            createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), upperTrigram, 4);
            createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), lowerTrigram, 1);
        }
        //纯卦 本宫第一卦
        else if (hexagram.getId() % 8 == 1)
        {
            //综卦
            TrigramDefault upperTrigram = getTrigramDefaultByName(Default.getTrigramReverseByName(hexagram.getUpper().getName()));
            TrigramDefault lowerTrigram = getTrigramDefaultByName(Default.getTrigramReverseByName(hexagram.getLower().getName()));

            createLineFuShen(hexagram.getPlace(), hexagram.getUpper().getLines(), upperTrigram, 4);
            createLineFuShen(hexagram.getPlace(), hexagram.getLower().getLines(), lowerTrigram, 1);
        }
        else
        {
            if (type == EnumFuShenType.YiLinBuYi)
            {
                SetFuShenByYiLinBuYi(hexagram);
            }
            else if (type == EnumFuShenType.YiMao)
            {
                SetFuShenByYiMao(hexagram);
            }
            else if (type == EnumFuShenType.YiYin)
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
        try {

            //得到本卦中已有的六亲
            ArrayList<EnumSixRelation> sixRelations = new ArrayList<EnumSixRelation>();
            for (Line line : gua.getLines()) {
                if (!sixRelations.contains(line.getSixRelation())) {
                    sixRelations.add(line.getSixRelation());
                }
            }

            ArrayList<EnumSixRelation> sixRelationAll = EnumSixRelation.getAll();

            ArrayList<EnumSixRelation> sixRelationNotExisted = new ArrayList<EnumSixRelation>();

            for (EnumSixRelation esrAll : sixRelationAll) {
                if (!sixRelations.contains(esrAll))
                    sixRelationNotExisted.add(esrAll);
            }

            //得卦所在默认宫位的头一个卦，也就是8纯卦
            TrigramDefault trigramDefault = null;
            for (TrigramDefault td : Default.getTrigrams()) {
                if (td.getPlace().equals(gua.getPlace())) {
                    trigramDefault = td;
                    break;
                }
            }

            for (Line line : gua.getLines()) {
                String earthlyBranch = "";
                Method method = TrigramDefault.class.getMethod("getC" + line.getPosition());
                earthlyBranch = method.invoke(trigramDefault, null).toString();

                createLineFuShen(gua.getPlace(), line, earthlyBranch, sixRelationNotExisted);
            }

        } catch (NoSuchMethodException e) {
            System.out.println(e.toString());
        }
    }

    public Hexagram getChangedHexagramByOriginal(Hexagram original, boolean includeFuShen) throws Exception {
        List<Line> lines = original.getLines();

        boolean noChangedHexagram = true;

        ArrayList<Line> targetSymbols = new ArrayList<Line>();
        for (Line line : lines) {
            
            EnumLineSymbol lineSymbol = line.getLineSymbol();
            
            if (line.getLineSymbol() == EnumLineSymbol.LaoYang || line.getLineSymbol() == EnumLineSymbol.LaoYin) {
                lineSymbol = lineChange(line.getLineSymbol());
                noChangedHexagram = false;
            }
            Line tempLine = new Line();
            tempLine.setLineSymbol(lineSymbol);
            tempLine.setPosition(line.getPosition());

            targetSymbols.add(tempLine);
        }

        if(noChangedHexagram)
            return null;

        Hexagram result = getHexagramByLines(targetSymbols, includeFuShen);

        for (Line y : result.getUpper().getLines()) {
            y.setSixRelation(parseSixRelationByFiveElement(original.getFiveElement(), y.getFiveElement()));
        }

        for (Line y : result.getLower().getLines()) {
            y.setSixRelation(parseSixRelationByFiveElement(original.getFiveElement(), y.getFiveElement()));
        }
        return result;

    }

    public EnumLineSymbol lineToInvariant(EnumLineSymbol symble)
    {
        if (symble == EnumLineSymbol.LaoYang)
            return EnumLineSymbol.Yang;
        else if (symble == EnumLineSymbol.LaoYin)
            return EnumLineSymbol.Yin;
        else
            return symble;
    }

    public EnumLineSymbol lineToVariant(EnumLineSymbol original)
    {
        if (original == EnumLineSymbol.Yang)
            return EnumLineSymbol.LaoYang;
        if (original == EnumLineSymbol.Yin)
            return EnumLineSymbol.LaoYin;
        return original;
    }

    public EnumLineSymbol lineChange(EnumLineSymbol original)
    {
        if (original == EnumLineSymbol.LaoYang)
            return EnumLineSymbol.Yin;
        else if (original == EnumLineSymbol.LaoYin)
            return EnumLineSymbol.Yang;
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

    public EnumLineSymbol getSybmleByString(String sybmle) {
        if (sybmle.equals("|"))
            return EnumLineSymbol.Yang;
        else if (sybmle.equals("||"))
            return EnumLineSymbol.Yin;
        else if (sybmle.equals("x"))
            return EnumLineSymbol.LaoYin;
        else
            return EnumLineSymbol.LaoYang;
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
        line.setLineSymbol(getSybmleByString(symble));
        line.setFiveElement(parseFiveElementByEarthlyBranch(earthlyBranch));
        line.setSixRelation(parseSixRelationByFiveElement(parseFiveElementByPalace(place), line.getFiveElement()));
        return line;
    }

    public void createLineFuShen(String place, ArrayList<Line> lines, TrigramDefault trigram, int beginLineIndex)
    {
        try {
            for (int i = 0; i < lines.size(); i++) {
                Method method = TrigramDefault.class.getMethod("getC" + (i + beginLineIndex));
                String earthlyBranch = method.invoke(trigram, null).toString();
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
                lines.get(i).setFiveElementAttached(fiveElement);
            }
        }
        catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public void createLineFuShen(String place, Line line,String earthlyBranch, ArrayList<EnumSixRelation> notExistedSixRelation)
    {
        EnumFiveElement fiveElement = parseFiveElementByEarthlyBranch(earthlyBranch);
        EnumSixRelation sixRelation = parseSixRelationByFiveElement(parseFiveElementByPalace(place), fiveElement);

        if (notExistedSixRelation.contains(sixRelation))
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

    public static HashMap<Integer,String> getSixAnimalsByCelestialStem(String celestialStem)
    {
        int variable = 0;
        HashMap<String,Integer> mapping = Default.getCelestialStemSixAnimalsMapping();
        for (String s: mapping.keySet())
        {
            if(s.contains(celestialStem))
            {
                variable = mapping.get(s);
            }
        }
        HashMap<Integer,String> sixAnimals = Default.getSixAnimals();
        HashMap<Integer,String> rebuild = new HashMap<Integer,String>();

        for(int i=variable;i<7;i++) {
            rebuild.put(rebuild.size() + 1, sixAnimals.get(i));
        }

        for(int i=1;i<variable; i++) {
            rebuild.put(rebuild.size() + 1, sixAnimals.get(i));
        }

        return rebuild;
    }
}







