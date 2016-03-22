package lsw.hexagram;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import lsw.library.ListHelper;
import lsw.library.StringHelper;
import lsw.library.Utility;
import lsw.model.AnalyzeResultCollection;
import lsw.model.EarthlyBranch;
import lsw.model.EarthlyBranchDay;
import lsw.model.EarthlyBranchMonth;
import lsw.model.EnumFiveElement;
import lsw.model.EnumLineStatus;
import lsw.model.EnumLineSymbol;
import lsw.model.EnumLing;
import lsw.model.EnumLingRelation;
import lsw.model.EnumSixRelation;
import lsw.model.HeavenlyStem;
import lsw.model.Hexagram;
import lsw.model.Line;
import lsw.value.Default;
import lsw.xml.XmlModelCache;
import lsw.xml.model.XmlModelExtProperty;

/**
 * Created by swli on 8/12/2015.
 */
public class Analyzer  {

    private Context context;
    XmlModelCache xmlModelCache;
    public Analyzer(Context context)
    {
        this.context = context;
        xmlModelCache = XmlModelCache.getInstance(context);
    }

    public final String BreakLineSymbol = "\r\n";

    public ArrayList<String> orderedStringResult(String sb)
    {
        String[] items = sb.replace(BreakLineSymbol, "<").split("<");
        ArrayList<String> tempItems = new ArrayList<String>();
        for(String item: items)
        {
            if(!StringHelper.isNullOrEmpty(item))
                tempItems.add(item);
        }

        Collections.sort(tempItems, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                //return s.substring(0,1).compareTo(t1.substring(0,1));
                return t1.substring(0,1).compareTo(s.substring(0,1));

            }
        });

        return tempItems;
        //return items.OrderByDescending(p => p.Substring(0, 1)).ToList();
    }

    public StringBuilder analyzeHexagramResult(String month, String day, Hexagram originalHexagram, Hexagram resultHexagram)
    {

        String cM = month.substring(0,1);
        XmlModelExtProperty cMP = xmlModelCache.getCelestialStem().getCelestialStems().get(cM);
        HeavenlyStem mHS = new HeavenlyStem(cMP.getId(),cM,EnumFiveElement.toEnum(cMP.getWuXing()));

        String tM = month.substring(1);
        XmlModelExtProperty tMP = xmlModelCache.getTerrestrial().getTerrestrials().get(tM);
        EarthlyBranch mEB = new EarthlyBranch(tMP.getId(),tM, EnumFiveElement.toEnum(tMP.getWuXing()));

        String dM = day.substring(0,1);
        XmlModelExtProperty dMP = xmlModelCache.getCelestialStem().getCelestialStems().get(dM);
        HeavenlyStem dHS = new HeavenlyStem(dMP.getId(),dM,EnumFiveElement.toEnum(dMP.getWuXing()));

        String dtM = day.substring(1);
        XmlModelExtProperty dtMP = xmlModelCache.getTerrestrial().getTerrestrials().get(dtM);
        EarthlyBranch dEB = new EarthlyBranch(dtMP.getId(),dtM, EnumFiveElement.toEnum(dtMP.getWuXing()));

        return parseAnalyzeResultToString(analyzeHexagramResult(mEB, dHS, dEB, originalHexagram, resultHexagram));
    }

    public StringBuilder parseAnalyzeLineResultToString(List<EnumLineStatus> lineStatus)
    {
        StringBuilder sb = new StringBuilder();

        for (EnumLineStatus status : lineStatus)
        {
            sb.append(status.toString());
            sb.append(",");
        }
        return sb;
    }

    public StringBuilder parseAnalyzeThreeSuitResult(List<Object> threeSuit)
    {
        StringBuilder sb = new StringBuilder();

        for (Object o : threeSuit)
        {
            if (o instanceof EarthlyBranchDay)
            {
                sb.append("日");
                sb.append(",");
            }
            if (o instanceof EarthlyBranchMonth)
            {
                sb.append("月");
                sb.append(",");
            }
            if (o instanceof Line)
            {
                Line line = (Line) o;
                sb.append(line.getPosition());
                sb.append("爻,");
            }
        }

        return sb;
    }

    public StringBuilder parseAnalyzeEnterDynamicLineTombRepositoryResult(List<Line> lines)
    {
        StringBuilder sb = new StringBuilder();

        for (Line l : lines)
        {
            sb.append(l.getPosition());
            sb.append("爻,");
        }

        return sb;
    }

    public StringBuilder parseAnalyzeResultToString(AnalyzeResultCollection collection)
    {
        StringBuilder sb = new StringBuilder();

        HashMap<Pair<Integer, EnumSixRelation>, ArrayList<EnumLineStatus>>  lineResults = collection.getLineLevelResult();
        for (Pair<Integer, EnumSixRelation> key : lineResults.keySet())
        {
            sb.append(key.first);
            sb.append("-");
            sb.append(key.second.toString());
            sb.append(":");
            sb.append(parseAnalyzeLineResultToString(lineResults.get(key)));
            sb.append(BreakLineSymbol);
        }

        if (collection.getHexagramLevelThreeSuitResult() != null)
        {
            for (Pair<EnumSixRelation, ArrayList<Object>> suit : collection.getHexagramLevelThreeSuitResult())
            {
                sb.append("!-");
                sb.append(suit.first.toString());
                sb.append(":");
                sb.append("局,");
                sb.append(parseAnalyzeThreeSuitResult(suit.second));
                sb.append(BreakLineSymbol);
            }
        }
        if (collection.getLineLevelTombRepository() != null)
        {
            for (Line key : collection.getLineLevelTombRepository().keySet())
            {
                sb.append(key.getPosition());
                sb.append("-");
                sb.append(key.getSixRelation().toString());
                sb.append(":");
                sb.append(parseAnalyzeEnterDynamicLineTombRepositoryResult(collection.getLineLevelTombRepository().get(key)));
                sb.append("墓库");
                sb.append(BreakLineSymbol);
            }
        }

        return sb;
    }

    public AnalyzeResultCollection analyzeHexagramResult(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem, EarthlyBranch dayEarthlyBranch, Hexagram originalHexagram, Hexagram resultHexagram)
    {
        AnalyzeResultCollection result = new AnalyzeResultCollection();

        HashMap<Pair<Integer, EnumSixRelation>, ArrayList<EnumLineStatus>> lineStatus = new HashMap<Pair<Integer, EnumSixRelation>, ArrayList<EnumLineStatus>>();

        ArrayList<Pair<Line, Line>> linesSuitSet = new ArrayList<Pair<Line, Line>>();
        //tomb repository
        HashMap<Line, ArrayList<Line>> dynamicLineTombRepository = new HashMap<Line, ArrayList<Line>>();

        ArrayList<Line> lines = originalHexagram.getLines();

        //check tomb repositroy dynamic
        ArrayList<Line> dynamicLines = new ArrayList<Line>();
        for(Line line :lines)
        {
            if(isDynamicLine(line) && Default.getGrowsMapping(context).get(Default.Twelve_Grow_Mu).containsValue(line.getEarthlyBranch().getId()))
                dynamicLines.add(line);
        }

        Collections.sort(lines, new Comparator<Line>() {
            @Override
            public int compare(Line line, Line t1) {
                return line.getSixRelation().compareTo(t1.getSixRelation());
            }
        });

        for(Line oline : lines)
        {

            analyzeOneLineTombRepository(oline, dynamicLines, dynamicLineTombRepository);

            Line tLine = null;

            //check three suit
            if (resultHexagram != null)
            {
                for(Line line : resultHexagram.getLines()) {
                    if(line.getPosition() == oline.getPosition()) {
                        tLine = line;
                        break;
                    }
                }
                if (isDynamicLine(oline))
                {
                    //check three suit
                    linesSuitSet.add(Pair.create(oline, tLine));
                }
            }

            boolean isFuShen = oline.getEarthlyBranchAttached() != null && !StringHelper.isNullOrEmpty(oline.getEarthlyBranch().getName());

            Pair<EnumSixRelation, ArrayList<EnumLineStatus>> tupleAttachedLineResult = null, tupleLineResult = null;

            if (isFuShen)
            {
                Pair<EnumSixRelation, ArrayList<EnumLineStatus>> analyzeAttachedLineResult = analyzeLineResult(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oline, tLine, isFuShen);
                tupleAttachedLineResult = analyzeAttachedLineResult;
            }

            Pair<EnumSixRelation, ArrayList<EnumLineStatus>> analyzeLineResult = analyzeLineResult(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oline, tLine, false);
            tupleLineResult = analyzeLineResult;

            if (tupleAttachedLineResult != null)
                lineStatus.put(Pair.create(oline.getPosition(), tupleAttachedLineResult.first), tupleAttachedLineResult.second);

            if (tupleLineResult != null)
                lineStatus.put(Pair.create(oline.getPosition(), analyzeLineResult.first), analyzeLineResult.second);

            Line tempLine = null;
            if (tupleLineResult.second.contains(EnumLineStatus.AnDong))
                linesSuitSet.add(Pair.create(oline, tempLine));
        }

        //three suit
        ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> threeSuitResult = analyzeThreeSuit(monthEarthlyBranch, dayEarthlyBranch, originalHexagram, linesSuitSet);

        result.setHexagramLevelThreeSuitResult(threeSuitResult);
        result.setLineLevelResult(lineStatus);
        result.setLineLevelTombRepository(dynamicLineTombRepository);

        return result;
    }

    public void analyzeOneLineTombRepository(Line oline, ArrayList<Line> dynamicLines, HashMap<Line, ArrayList<Line>> dynamicLineTombRepository)
    {
        //check dynamic tomb repository
        for(Line dl : dynamicLines)
        {
            int dynamicLineEBIndex = dl.getEarthlyBranch().getId();
            int originalLineEBIndex = oline.getEarthlyBranch().getId();

            boolean hasMu = Default.getGrowsMapping(context).get(Default.Twelve_Grow_Mu).containsKey(originalLineEBIndex);
            if (!oline.equals(dl) && !isDynamicLine(oline) && hasMu)
            {
                if (dynamicLineEBIndex == Default.getGrowsMapping(context).get(Default.Twelve_Grow_Mu).get(originalLineEBIndex))
                {
                    if (!dynamicLineTombRepository.containsKey(dl)) {
                        ArrayList<Line> tempLine = new ArrayList<Line>();
                        tempLine.add(oline);
                        dynamicLineTombRepository.put(dl, tempLine);
                    }
                    else
                        dynamicLineTombRepository.get(dl).add(oline);
                }
            }
        }
    }

    public ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> analyzeThreeSuit(EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, Hexagram originalHexagram, List<Pair<Line, Line>> linesSuitSet)
    {
        ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> threeSuit = new ArrayList<Pair<EnumSixRelation, ArrayList<Object>>>();

        EarthlyBranchDay dayEarthlyBranchType = new EarthlyBranchDay();
        dayEarthlyBranchType.setId(dayEarthlyBranch.getId());
        dayEarthlyBranchType.setName(dayEarthlyBranch.getName());
        dayEarthlyBranchType.setFiveElement(dayEarthlyBranch.getFiveElement());

        EarthlyBranchMonth monthEarthlyBranchType = new EarthlyBranchMonth();
        monthEarthlyBranchType.setId(monthEarthlyBranch.getId());
        monthEarthlyBranchType.setName(monthEarthlyBranch.getName());
        monthEarthlyBranchType.setFiveElement(monthEarthlyBranch.getFiveElement());


        if (linesSuitSet.size() > 0)
        {
            int monthIndex = monthEarthlyBranch.getId();
            int dayIndex = dayEarthlyBranch.getId();

            ArrayList<Line> threeDynamicLineSuit = new ArrayList<Line>();

            for (Pair<Line,Line> tuple : linesSuitSet)
            {
                int[] oneDynamicLine = new int[] { tuple.first.getEarthlyBranch().getId(), monthIndex, dayIndex };

                ArrayList<Integer> distinctOneDynamicLine = new ArrayList<Integer>();

                for(int one : oneDynamicLine)
                {
                    if(!distinctOneDynamicLine.contains(one))
                        distinctOneDynamicLine.add(one);
                }

                if (distinctOneDynamicLine.size() == 3)
                {
                    HashMap<String,Integer> terrestrialMappingInverse = xmlModelCache.getTerrestrial().getTerrestrialMapsInverse();
                    HashMap<String,ArrayList<String>> threeSuits = xmlModelCache.getTerrestrial().getThreeSuits();
                    String tempFiveElement = "";
                    for(String fiveElement :  threeSuits.keySet())
                    {
                        ArrayList<Integer> tIndexs = new ArrayList<Integer>();
                        for(String t: threeSuits.get(fiveElement))
                        {
                            tIndexs.add(terrestrialMappingInverse.get(t));
                        }

                        int[] temp = ListHelper.toIntArray(tIndexs);
                        if(Arrays.equals(temp,oneDynamicLine))
                            tempFiveElement = fiveElement;
                    }

                    if(!StringHelper.isNullOrEmpty(tempFiveElement))
                    {
                        EnumSixRelation sixRelation = Builder.parseSixRelationByFiveElement(originalHexagram.getFiveElement(), EnumFiveElement.toEnum(tempFiveElement));
                        ArrayList<Object> tempObjests = new ArrayList<Object>();
                        tempObjests.add(tuple.first);
                        tempObjests.add(monthEarthlyBranchType);
                        tempObjests.add(dayEarthlyBranchType);
                        threeSuit.add(Pair.create(sixRelation, tempObjests));
                    }
                }

                //一、一爻动与日月组成三合局
                Pair<EnumSixRelation, ArrayList<Object>> dynamicResultWithMonthDay = createThreeSuitByDynamicLines(tuple.first, monthEarthlyBranchType, dayEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonthDay != null)
                    threeSuit.add(dynamicResultWithMonthDay);

                //五、四爻与六爻和变爻组成
                Pair<EnumSixRelation, ArrayList<Object>> dynamicResultWithMonth = createThreeSuitByDynamicLines(tuple.first, tuple.second, monthEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonth != null)
                    threeSuit.add(dynamicResultWithMonth);

                //六、动爻与变爻和日或月组成
                Pair<EnumSixRelation, ArrayList<Object>> dynamicResultWithDay = createThreeSuitByDynamicLines(tuple.first, tuple.second, dayEarthlyBranchType, originalHexagram);
                if (dynamicResultWithDay != null)
                    threeSuit.add(dynamicResultWithDay);

                for(String key: xmlModelCache.getTerrestrial().getThreeSuits().keySet())
                {
                    for(String value: xmlModelCache.getTerrestrial().getThreeSuits().get(key))
                    {
                        if(value.equals(tuple.first.getEarthlyBranch().getName())) {
                            threeDynamicLineSuit.add(tuple.first);
                    }
                    }
                }
            }

            //?????????
            if(threeDynamicLineSuit.size() >= 2)
            {
                ArrayList<Integer> listIncludeMonthAndDay = new ArrayList<Integer>();
                for (Line line: threeDynamicLineSuit)
                {
                    listIncludeMonthAndDay.add(line.getEarthlyBranch().getId());
                }
                listIncludeMonthAndDay.add(monthEarthlyBranchType.getId());
                listIncludeMonthAndDay.add(dayEarthlyBranchType.getId());

                ArrayList<Integer> listIncludeMonthAndDayDistinct = new ArrayList<Integer>();
                for(Integer i : listIncludeMonthAndDay)
                {
                    if(!listIncludeMonthAndDayDistinct.contains(i))
                        listIncludeMonthAndDayDistinct.add(i);
                }


                HashMap<ArrayList<Integer>, EnumFiveElement> existedSuit = funFindSuits(listIncludeMonthAndDayDistinct);

                ArrayList<Object> groupLines = new ArrayList<Object>();
                for (ArrayList<Integer> k : existedSuit.keySet())
                {
                    if (k.contains(monthEarthlyBranchType.getId()))
                    {
                        groupLines.add(monthEarthlyBranchType);
                    }
                    else if (k.contains(dayEarthlyBranchType.getId()))
                    {
                        groupLines.add(dayEarthlyBranchType);
                    }

                    ArrayList<Line> ls = new ArrayList<Line>();
                    for(Line line: threeDynamicLineSuit)
                    {
                        if(k.contains(line.getEarthlyBranch().getId()))
                            ls.add(line);
                    }
                    for(Line l : ls)
                    {
                        groupLines.add(l);
                    }

                    EnumSixRelation sixRelation = Builder.parseSixRelationByFiveElement(originalHexagram.getFiveElement(), existedSuit.get(k));
                    threeSuit.add(Pair.create(sixRelation, groupLines));
                }
            }

            if (threeDynamicLineSuit.size() >= 3)
            {
                ArrayList<Integer> tempInt = new ArrayList<Integer>();
                for(Line line : threeDynamicLineSuit)
                {
                    int currentId = line.getEarthlyBranch().getId();
                    if(!tempInt.contains(currentId))
                        tempInt.add(line.getEarthlyBranch().getId());
                }

                HashMap<ArrayList<Integer>, EnumFiveElement> existedSuit = funFindSuits(tempInt);

                ArrayList<Object> groupLines = new ArrayList<Object>();
                for (ArrayList<Integer> k : existedSuit.keySet())
                {
                    ArrayList<Line> tempLine = new ArrayList<Line>();

                    for(Line line: threeDynamicLineSuit)
                    {
                        if(k.contains(line.getEarthlyBranch().getId()))
                            tempLine.add(line);
                    }

                    for (Line l : tempLine)
                    {
                        groupLines.add(l);
                    }
                    EnumSixRelation sixRelation = Builder.parseSixRelationByFiveElement(originalHexagram.getFiveElement(), existedSuit.get(k));
                    threeSuit.add(Pair.create(sixRelation, groupLines));
                }
            }

            //????????????
            Pair<EnumSixRelation, ArrayList<Object>> lines13KindOne = createThreeSuitByDynamicLines(linesSuitSet, 1, originalHexagram, true);
            if (lines13KindOne != null)
                threeSuit.add(lines13KindOne);
            Pair<EnumSixRelation, ArrayList<Object>> lines13KindTwo = createThreeSuitByDynamicLines(linesSuitSet, 1, originalHexagram, false);
            if (lines13KindTwo != null)
                threeSuit.add(lines13KindTwo);

            //????????????
            Pair<EnumSixRelation, ArrayList<Object>> lines46KindOne = createThreeSuitByDynamicLines(linesSuitSet, 4, originalHexagram, true);
            if (lines46KindOne != null)
                threeSuit.add(lines46KindOne);
            Pair<EnumSixRelation, ArrayList<Object>> lines46KindTwo = createThreeSuitByDynamicLines(linesSuitSet, 4, originalHexagram, false);
            if (lines46KindTwo != null)
                threeSuit.add(lines46KindTwo);

            return threeSuit;
        }
        return null;
    }

    HashMap<ArrayList<Integer>, EnumFiveElement> funFindSuits(List<Integer> suitList)
    {
        HashMap<ArrayList<Integer>, EnumFiveElement> result = new HashMap<ArrayList<Integer>, EnumFiveElement>();

        HashMap<String,ArrayList<String>> threeSuits = xmlModelCache.getTerrestrial().getThreeSuits();

        for (String suit : threeSuits.keySet())
        {
            int count = 0;
            for (String k : threeSuits.get(suit))
            {
                for(Integer sl: suitList)
                {
                    String temp = xmlModelCache.getTerrestrial().getTerrestrialMaps().get(sl);
                    if(temp.equals(k))
                        count +=1;
                }
            }

            if (count == 3) {
                ArrayList<Integer> temp = new ArrayList<Integer>();
                for(String s: threeSuits.get(suit))
                {
                    temp.add(xmlModelCache.getTerrestrial().getTerrestrialMapsInverse().get(s));
                }
                result.put(temp, EnumFiveElement.toEnum(suit));
            }
        }

        return result;
    }

    Pair<EnumSixRelation, ArrayList<Object>> createThreeSuitByDynamicLines(List<Pair<Line, Line>> lines, int beginPosition, Hexagram hexagram, boolean withFirstResultLine)
    {
        ArrayList<Pair<Line,Line>> suitLines = new ArrayList<Pair<Line,Line>>();
        for(Pair<Line,Line> pair: lines)
        {
            if(pair.first.getPosition() == beginPosition || pair.first.getPosition() == beginPosition +2)
                suitLines.add(pair);
        }

        if (suitLines.size() >= 2)
        {
            Pair<Line,Line> linePairOne = suitLines.get(0);
            Pair<Line,Line> linePairTwo = suitLines.get(suitLines.size()-1);
            if (withFirstResultLine)
            {
                //means AnDong Line
                if (linePairOne.first == null)
                    return null;
                return createThreeSuitByDynamicLines(linePairOne.first, linePairOne.second, linePairTwo.first, hexagram);
            }
            else
            {
                if (linePairTwo.second == null)
                    return null;
                return createThreeSuitByDynamicLines(linePairOne.first, linePairTwo.first, linePairTwo.second, hexagram);
            }
        }
        return null;
    }

    Pair<EnumSixRelation, ArrayList<Object>> createThreeSuitByDynamicLines(Line line1, Line line2, EarthlyBranch dayOrMonthEarthlyBranch, Hexagram hexagram)
    {
        //line2 == null means this line is AnDong
        if (line2 == null)
            return null;

        int[] kind = createThreeSuitByLineOrMonthOrDay(
                line1.getEarthlyBranch().getId(),
                line2.getEarthlyBranch().getId(),
                dayOrMonthEarthlyBranch.getId()
        );

        EnumSixRelation kindResult = createThreeSuitByIndex(kind, hexagram);

        if (kindResult != null) {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(line1);
            list.add(line2);
            list.add(dayOrMonthEarthlyBranch);

            return Pair.create(kindResult, list);
        }
        else
            return null;
    }

    Pair<EnumSixRelation, ArrayList<Object>> createThreeSuitByDynamicLines(Line line1, EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, Hexagram hexagram) {
        int[] kind = createThreeSuitByLineOrMonthOrDay(
                line1.getEarthlyBranch().getId(),
                monthEarthlyBranch.getId(),
                dayEarthlyBranch.getId()
        );

        EnumSixRelation kindResult = createThreeSuitByIndex(kind, hexagram);

        if (kindResult != null) {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(line1);
            list.add(monthEarthlyBranch);
            list.add(dayEarthlyBranch);
            return Pair.create(kindResult, list);
        } else
            return null;
    }

    Pair<EnumSixRelation, ArrayList<Object>> createThreeSuitByDynamicLines(Line line1, Line line2, Line line3, Hexagram hexagram)
    {
        int[] kind = createThreeSuitByLineOrMonthOrDay(
                line1.getEarthlyBranch().getId(),
                line2.getEarthlyBranch().getId(),
                line3.getEarthlyBranch().getId()
        );

        EnumSixRelation kindResult = createThreeSuitByIndex(kind, hexagram);

        if (kindResult != null) {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(line1);
            list.add(line2);
            list.add(line3);
            return Pair.create(kindResult, list);
        }
        else
            return null;
    }

    int[] createThreeSuitByLineOrMonthOrDay(int earthBranchID1, int earthBranchID2, int earthBranchID3)
    {
        int[] list =  new int[] { earthBranchID1, earthBranchID2, earthBranchID3 };
        //����ظ���¼
        List<Integer> tempList= new ArrayList<Integer>();
        for(Integer i:list){
            if(!tempList.contains(i)){
                tempList.add(i);
            }
        }
        if (tempList.size() == 3)
            return list;
        else
            return null;
    }

    EnumSixRelation createThreeSuitByIndex(int[] indexSummary, Hexagram hexagram)
    {
        if (indexSummary != null && indexSummary.length == 3)
        {
            HashMap<String, ArrayList<String>> threeSuits = xmlModelCache.getTerrestrial().getThreeSuits();
            Pair<EnumFiveElement,ArrayList<Integer>> threeSuitOne = null;

            for(String key: threeSuits.keySet())
            {
                ArrayList<Integer> tempSuit = new ArrayList<Integer>();

                for(int iSummary : indexSummary) {
                    String t= xmlModelCache.getTerrestrial().getTerrestrialMaps().get(iSummary);
                    if(threeSuits.get(key).contains(t))
                    {
                        tempSuit.add(iSummary);
                    }
                }

                ArrayList<Integer> tempDistinctList = new ArrayList<Integer>();
                for(int tempI : tempSuit)
                {
                    if(!tempDistinctList.contains(tempI))
                        tempDistinctList.add(tempI);
                }

                if(tempDistinctList.size() == 3)
                    threeSuitOne = Pair.create(EnumFiveElement.toEnum(key),tempDistinctList);
            }

            if (threeSuitOne != null)
            {
                EnumSixRelation sixRelation = Builder.parseSixRelationByFiveElement(hexagram.getFiveElement(), threeSuitOne.first);

                return sixRelation;
            }
        }
        return null;
    }

    public Pair<EnumSixRelation, ArrayList<EnumLineStatus>> analyzeLineResult(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                                                              EarthlyBranch dayEarthlyBranch, Line oLine, Line tLine, boolean checkFuShen)
    {
        ArrayList<EnumLineStatus> lineStatus = new ArrayList<EnumLineStatus>();
        EnumSixRelation enumSixRelation;

        if (checkFuShen)
        {
            enumSixRelation = oLine.getSixRelationAttached();

            lineStatus.add(EnumLineStatus.Fu);
            EnumLineStatus kong = analyzeLineXunKong(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen);
            if (kong != null)
                lineStatus.add(kong);
        }
        else
        {
            enumSixRelation = oLine.getSixRelation();

            if (isDynamicLine(oLine))
            {
                lineStatus.add(EnumLineStatus.Dong);
            }
            else
            {
                EnumLineStatus kong = analyzeLineXunKong(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, false);
                if (kong != null)
                    lineStatus.add(kong);
            }
        }

        ArrayList<EnumLineStatus> lineStatusWithMonthAndDay = analyzeLineRelationWithMonthAndDay(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen);

        lineStatus.addAll(lineStatusWithMonthAndDay);

        if (!checkFuShen && isDynamicLine(oLine))
        {
            ArrayList<EnumLineStatus> dynamicLineChanged = analyzeDynamicLineChanged(oLine, tLine, monthEarthlyBranch, dayEarthlyBranch, dayHeavenlyStem);

            lineStatus.addAll(dynamicLineChanged);
        }

        return Pair.create(enumSixRelation, lineStatus);
    }

    public ArrayList<EnumLineStatus> analyzeLineRelationWithMonthAndDay(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                                                        EarthlyBranch dayEarthlyBranch, Line originalLine, boolean checkFuShen)
    {
        ArrayList<EnumLineStatus> listStatus = new ArrayList<EnumLineStatus>();

        int oLineEarthlyBranchIndex = originalLine.getEarthlyBranch().getId();
        EarthlyBranch oLineEarthlyBranch = originalLine.getEarthlyBranch();

        if (checkFuShen)
        {
            oLineEarthlyBranchIndex = originalLine.getEarthlyBranchAttached().getId();
            oLineEarthlyBranch = originalLine.getEarthlyBranchAttached();
        }
        //??????
        EnumLingRelation rMonth = analyzeLineLingRelation(monthEarthlyBranch, originalLine, checkFuShen);
        if (monthEarthlyBranch.getId() == oLineEarthlyBranch.getId())
        {
            listStatus.add(EnumLineStatus.LinYue);
        }
        else
        {
            listStatus.add(convertEarthlyBranchRelationToEnum(rMonth, true));
            //Yu Qi ex: ????
            EnumLineStatus yuQi = analyzeLineLingRelationYuQi(monthEarthlyBranch.getId(), originalLine, checkFuShen);
            if (yuQi != null)
            {
                listStatus.add(yuQi);
            }

            if (Default.getEarthlyBranchSixSuit(context).get(monthEarthlyBranch.getId()) == oLineEarthlyBranchIndex)
            {
                listStatus.add(EnumLineStatus.YueHe);
            }
            if (Default.getEarthlyBranchSixInverse(context).get(monthEarthlyBranch.getId()) == oLineEarthlyBranchIndex)
            {
                listStatus.add(EnumLineStatus.YuePo);
            }
        }
        //??????
        EnumLingRelation rDay = analyzeLineLingRelation(dayEarthlyBranch, originalLine, checkFuShen);
        if (dayEarthlyBranch.getId() == oLineEarthlyBranch.getId())
        {
            listStatus.add(EnumLineStatus.LinRi);
        }
        else
        {
            listStatus.add(convertEarthlyBranchRelationToEnum(rDay, false));
            if (Default.getEarthlyBranchSixSuit(context).get(dayEarthlyBranch.getId()) == oLineEarthlyBranchIndex)
            {
                listStatus.add(EnumLineStatus.RiHe);
            }
        }

        actionDayMonthDeadTombDesperate(listStatus,Default.Twelve_Grow_Mu,dayEarthlyBranch.getId(),EnumLineStatus.RiMuKu,oLineEarthlyBranchIndex);
        actionDayMonthDeadTombDesperate(listStatus,Default.Twelve_Grow_Si,dayEarthlyBranch.getId(),EnumLineStatus.RiSi,oLineEarthlyBranchIndex);
        actionDayMonthDeadTombDesperate(listStatus,Default.Twelve_Grow_Jue,dayEarthlyBranch.getId(),EnumLineStatus.RiJue,oLineEarthlyBranchIndex);

        actionDayMonthDeadTombDesperate(listStatus, Default.Twelve_Grow_Mu, monthEarthlyBranch.getId(), EnumLineStatus.YueMuKu, oLineEarthlyBranchIndex);
        actionDayMonthDeadTombDesperate(listStatus, Default.Twelve_Grow_Si, monthEarthlyBranch.getId(), EnumLineStatus.YueSi, oLineEarthlyBranchIndex);
        actionDayMonthDeadTombDesperate(listStatus, Default.Twelve_Grow_Jue, monthEarthlyBranch.getId(), EnumLineStatus.YueJue, oLineEarthlyBranchIndex);

        ArrayList<EnumLineStatus> lineExtensionStatus = analyzeLineExtension(rMonth, monthEarthlyBranch, originalLine, dayHeavenlyStem, dayEarthlyBranch, checkFuShen);
        listStatus.addAll(lineExtensionStatus);

        return listStatus;
    }

    public void actionDayMonthDeadTombDesperate(ArrayList<EnumLineStatus> lineStatuses, String key, int earthlyBranchIndex,EnumLineStatus lineStatus, int oLineEarthlyBranchIndex)
    {
        HashMap<Integer,Integer> dic = Default.getGrowsMapping(context).get(key);
        if (dic.containsKey(oLineEarthlyBranchIndex) &&
                dic.get(oLineEarthlyBranchIndex) == earthlyBranchIndex) {
            lineStatuses.add(lineStatus);
        }
    }


    public ArrayList<EnumLineStatus> analyzeLineExtension(EnumLingRelation rMonth, EarthlyBranch monthEarthlyBranch, Line oLine,
                                                          HeavenlyStem dayHeavenlyStem, EarthlyBranch dayEarthlyBranch, boolean checkFuShen)
    {
        ArrayList<EnumLineStatus> listStatus = new ArrayList<EnumLineStatus>();

        int oDiZhiIndex = oLine.getEarthlyBranch().getId();

        if (checkFuShen)
            oDiZhiIndex = oLine.getEarthlyBranchAttached().getId();

        if (rMonth == EnumLingRelation.Wang || rMonth == EnumLingRelation.Xiang)
        {
            if (Default.getEarthlyBranchSixInverse(context).get(dayEarthlyBranch.getId()) == oDiZhiIndex)
            {
                listStatus.add(EnumLineStatus.AnDong);
            }
        }
        else
        {
            EarthlyBranch earthlyBranchName = checkFuShen ? oLine.getEarthlyBranchAttached() : oLine.getEarthlyBranch();
            if (!(monthEarthlyBranch.getName().equals(earthlyBranchName.getName())
                    && !dayEarthlyBranch.getName().equals(earthlyBranchName.getName()))
                    && isXunKong(dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen))
            {
                if (Default.getEarthlyBranchSixInverse(context).get(dayEarthlyBranch.getId()) == oDiZhiIndex)
                {
                    listStatus.add(EnumLineStatus.ChongShi);
                }
            }
            else
            {
                if (Default.getEarthlyBranchSixInverse(context).get(dayEarthlyBranch.getId()) == oDiZhiIndex)
                {
                    listStatus.add(EnumLineStatus.RiPo);
                }
            }
        }
        return listStatus;
    }

    public EnumLineStatus analyzeLineXunKong(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                             EarthlyBranch dayEarthlyBranch, Line oLine, boolean isFuShen)
    {

        boolean monthEarthlyBranchCompare = !isFuShen ?
                !monthEarthlyBranch.getName().equals(oLine.getEarthlyBranch().getName()) && !dayEarthlyBranch.getName().equals(oLine.getEarthlyBranch().getName()) :
                !monthEarthlyBranch.getName().equals(oLine.getEarthlyBranchAttached().getName()) && !dayEarthlyBranch.getName().equals(oLine.getEarthlyBranchAttached().getName());

        if (monthEarthlyBranchCompare)
        {
            if (isXunKong(dayHeavenlyStem, dayEarthlyBranch, oLine, isFuShen))
            {
                return EnumLineStatus.Kong;
            }
        }

        return null;
    }

    public ArrayList<EnumLineStatus> analyzeDynamicLineChanged(Line oLine, Line tLine, EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, HeavenlyStem dayHeavenStem)
    {
        ArrayList listStatus = new ArrayList<EnumLineStatus>();

        int oDiZhiIndex = oLine.getEarthlyBranch().getId();
        int tDiZhiIndex = tLine.getEarthlyBranch().getId();

        //??
        if (Default.getEarthlyBranchBackward().containsKey(oDiZhiIndex))
        {
            int back = Default.getEarthlyBranchBackward().get(oDiZhiIndex);
            if (back == tDiZhiIndex)
            {
                listStatus.add(EnumLineStatus.HuaTui);
            }
        }
        //??
        if (Default.getEarthlyBranchForward().containsKey(oDiZhiIndex))
        {
            int forward = Default.getEarthlyBranchForward().get(oDiZhiIndex);
            if (forward == tDiZhiIndex)
            {
                listStatus.add(EnumLineStatus.HuaJin);
            }
        }
        //??
        if (Default.getEarthlyBranchSixSuit(context).containsKey(oDiZhiIndex))
        {
            int suit = Default.getEarthlyBranchSixSuit(context).get(oDiZhiIndex);
            if (suit == tDiZhiIndex)
                listStatus.add(EnumLineStatus.HuiTouHe);
        }
        //??
        if (Default.getEarthlyBranchSixInverse(context).containsKey(oDiZhiIndex))
        {
            int suit = Default.getEarthlyBranchSixInverse(context).get(oDiZhiIndex);
            if (suit == tDiZhiIndex)
                listStatus.add(EnumLineStatus.HuiTouChong);
        }
        //??
        if (Default.getEarthlyBranchSixInverse(context).get(monthEarthlyBranch.getId()) == tDiZhiIndex)
        {
            listStatus.add(EnumLineStatus.HuaPo);

            if (dayEarthlyBranch.getId() == tDiZhiIndex)
                listStatus.add(EnumLineStatus.HuaPoErBuPo);

            listStatus.add(EnumLineStatus.BianYaoYueChong);
        }
        //??
        if (isXunKong(dayHeavenStem, dayEarthlyBranch, tLine, false))
        {
            listStatus.add(EnumLineStatus.HuaKong);
        }
        //??
        if (Default.getEarthlyBranchSixInverse(context).get(dayEarthlyBranch.getId()) == tDiZhiIndex)
        {
            listStatus.add(EnumLineStatus.BianYaoRiChong);
        }
        //??
        if (Default.getEarthlyBranchSixSuit(context).get(dayEarthlyBranch.getId()) == tDiZhiIndex)
        {
            listStatus.add(EnumLineStatus.BianYaoRiHe);
        }
        //??
        if (Default.getEarthlyBranchSixSuit(context).get(monthEarthlyBranch.getId()) == tDiZhiIndex)
        {
            listStatus.add(EnumLineStatus.BianYaoYueHe);
        }
        //???
        if (Builder.FiveElementSupport(oLine.getFiveElement()) == tLine.getFiveElement())
        {
            listStatus.add(EnumLineStatus.HuiTouSheng);
        }
        //???
        if (Builder.FiveElementRestrain(oLine.getFiveElement()) == tLine.getFiveElement())
        {
            listStatus.add(EnumLineStatus.HuiTouKe);
        }

        getLineStatus(listStatus, Default.Twelve_Grow_Mu,EnumLineStatus.HuaMuKu,oLine,tLine);
        getLineStatus(listStatus, Default.Twelve_Grow_Jue,EnumLineStatus.HuaJue,oLine,tLine);
        getLineStatus(listStatus, Default.Twelve_Grow_Si,EnumLineStatus.HuaSi,oLine,tLine);
        getLineStatus(listStatus, Default.Twelve_Grow_ZhangSheng,EnumLineStatus.HuaZhangSheng,oLine,tLine);
        getLineStatus(listStatus, Default.Twelve_Grow_Tai,EnumLineStatus.HuaTai,oLine,tLine);
        getLineStatus(listStatus, Default.Twelve_Grow_Yang,EnumLineStatus.HuaYang,oLine,tLine);
        getLineStatus(listStatus, Default.Twelve_Grow_Bing,EnumLineStatus.HuaBing,oLine,tLine);
        getLineStatus(listStatus, Default.Twelve_Grow_MuYu,EnumLineStatus.HuaMuYu,oLine,tLine);

        EnumLingRelation rMonth = analyzeLineLingRelation(monthEarthlyBranch, tLine, false);
        EnumLingRelation rDay = analyzeLineLingRelation(dayEarthlyBranch, tLine, false);
        EnumLineStatus monthStatus = convertEarthlyBranchRelationToEnumChangedLine(rMonth, true);
        EnumLineStatus dayStatus = convertEarthlyBranchRelationToEnumChangedLine(rDay, false);
        if (monthStatus != null)
            listStatus.add(monthStatus);
        if (dayStatus != null)
            listStatus.add(dayStatus);

        EnumLineStatus yuQi = analyzeLineLingRelationYuQi(monthEarthlyBranch.getId(), tLine, false);
        if (yuQi != null)
        {
            listStatus.add(EnumLineStatus.BianYaoYuQiYue);
        }

        return listStatus;
    }

    private void getLineStatus(ArrayList<EnumLineStatus> lineStatuses, String key, EnumLineStatus enumLineStatus, Line oLine, Line tLine)
    {
        HashMap<Integer,Integer> source = Default.getGrowsMapping(context).get(key);
        if (source.containsKey(oLine.getEarthlyBranch().getId()) && source.get(oLine.getEarthlyBranch().getId()) == tLine.getEarthlyBranch().getId())
        {
            lineStatuses.add(enumLineStatus);
        }
    }

    public EnumLingRelation analyzeLineLingRelation(EarthlyBranch earthlyBranch, Line line, boolean checkFuShen) {
        EnumLing monthOrDayLing = getLingByEarthlyBranchIndex(earthlyBranch.getId());
        EnumLing yaoLing = getLingByEarthlyBranchIndex(line.getEarthlyBranch().getId());

        if (checkFuShen && !StringHelper.isNullOrEmpty(line.getEarthlyBranchAttached().getName()))
            yaoLing = getLingByEarthlyBranchIndex(line.getEarthlyBranchAttached().getId());

        return analyzeLineLingRelation(monthOrDayLing, yaoLing);
    }

    public EnumLineStatus analyzeLineLingRelationYuQi(int earthlyBranchID, Line line, boolean checkFuShen)
    {
        EnumLing monthLing = getLingByEarthlyBranchIndex(line.getEarthlyBranch().getId());
        if (checkFuShen)
            monthLing = getLingByEarthlyBranchIndex(line.getEarthlyBranchAttached().getId());
        if (Default.getLingYuQiMapping().keySet().contains(earthlyBranchID) && Default.getLingYuQiMapping().get(earthlyBranchID) == monthLing)
        {
            return EnumLineStatus.YuQiYue;
        }
        return null;
    }

    public boolean isDynamicLine(Line line)
    {
        return line.getLineSymbol() == EnumLineSymbol.LaoYin || line.getLineSymbol() == EnumLineSymbol.LaoYang;
    }

    public boolean isXunKong(HeavenlyStem dayHeavenlyStem, EarthlyBranch dayEarthlyBranch, Line line, boolean isFuShen)
    {
        Pair<String,String> kong = Utility.getXunKong(context, dayHeavenlyStem.getName(), dayEarthlyBranch.getName());

        if (isFuShen)
            return kong.first.contains(line.getEarthlyBranchAttached().getName()) || kong.second.contains(line.getEarthlyBranchAttached().getName());
        else
            return kong.first.contains(line.getEarthlyBranch().getName()) || kong.second.contains(line.getEarthlyBranch().getName());
    }

    public EnumLing getLingByEarthlyBranchIndex(int index)
    {
        for(EnumLing ling: Default.getLingEarthlyBranchMapping().keySet())
        {
            if(Arrays.binarySearch(Default.getLingEarthlyBranchMapping().get(ling), index) >= 0)
                return ling;
        }
        return null;
    }

    public EnumLingRelation analyzeLineLingRelation(EnumLing monthOrDayLing, EnumLing lineLing)
    {
        if (monthOrDayLing == EnumLing.Chun)
        {
            if (lineLing == EnumLing.Chun)
                return EnumLingRelation.Wang;
            else if (lineLing == EnumLing.Xia)
                return EnumLingRelation.Xiang;
            else if (lineLing == EnumLing.Dong)
                return EnumLingRelation.Xiu;
            else if (lineLing == EnumLing.Qiu)
                return EnumLingRelation.Qiu;
            else //EnumLing.Tu
                return EnumLingRelation.Si;
        }
        else if (monthOrDayLing == EnumLing.Xia)
        {
            if (lineLing == EnumLing.Xia)
                return EnumLingRelation.Wang;
            else if (lineLing == EnumLing.Tu)
                return EnumLingRelation.Xiang;
            else if (lineLing == EnumLing.Chun)
                return EnumLingRelation.Xiu;
            else if (lineLing == EnumLing.Dong)
                return EnumLingRelation.Qiu;
            else //EnumLing.Qiu
                return EnumLingRelation.Si;
        }
        else if (monthOrDayLing == EnumLing.Qiu)
        {
            if (lineLing == EnumLing.Qiu)
                return EnumLingRelation.Wang;
            else if (lineLing == EnumLing.Dong)
                return EnumLingRelation.Xiang;
            else if (lineLing == EnumLing.Tu)
                return EnumLingRelation.Xiu;
            else if (lineLing == EnumLing.Xia)
                return EnumLingRelation.Qiu;
            else //Mu
                return EnumLingRelation.Si;
        }
        else if (monthOrDayLing == EnumLing.Dong)
        {
            if (lineLing == EnumLing.Dong)
                return EnumLingRelation.Wang;
            else if (lineLing == EnumLing.Chun)
                return EnumLingRelation.Xiang;
            else if (lineLing == EnumLing.Qiu)
                return EnumLingRelation.Xiu;
            else if (lineLing == EnumLing.Tu)
                return EnumLingRelation.Qiu;
            else //Huo
                return EnumLingRelation.Si;
        }
        else //Tu
        {
            if (lineLing == EnumLing.Tu)
                return EnumLingRelation.Wang;
            else if (lineLing == EnumLing.Qiu)
                return EnumLingRelation.Xiang;
            else if (lineLing == EnumLing.Xia)
                return EnumLingRelation.Xiu;
            else if (lineLing == EnumLing.Chun)
                return EnumLingRelation.Qiu;
            else //Shui
                return EnumLingRelation.Si;
        }
    }

    public EnumLineStatus convertEarthlyBranchRelationToEnum(EnumLingRelation relation, boolean isMonth)
    {
        switch (relation)
        {
            case Si:
                return isMonth ? EnumLineStatus.YueKe : EnumLineStatus.RiKe;
            case Qiu:
                return isMonth ? EnumLineStatus.YueQiu : EnumLineStatus.RiQiu;
            case Wang:
                return isMonth ? EnumLineStatus.YueWang : EnumLineStatus.RiWang;
            case Xiang:
                return isMonth ? EnumLineStatus.YueXiang : EnumLineStatus.RiXiang;
            case Xiu:
                return isMonth ? EnumLineStatus.YueXiu : EnumLineStatus.RiXiu;
            default:
                return null;
        }
    }

    public EnumLineStatus convertEarthlyBranchRelationToEnumChangedLine(EnumLingRelation relation, boolean isMonth)
    {
        switch (relation)
        {
            case Si:
                return isMonth ? EnumLineStatus.BianYaoYueKe : EnumLineStatus.BianYaoRiKe;
            case Wang:
            case Xiang:
                return isMonth ? EnumLineStatus.BianYaoYueWang : EnumLineStatus.BianYaoRiWang;
            default:
                return null;
        }
    }

}
