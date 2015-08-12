package lsw.hexagram;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

    public final String BreakLineSymble = "\r\n";

    public List<String> OrderedStringResult(String sb)
    {
        String[] items = sb.replace(BreakLineSymble, "|").split("|");
        ArrayList<String> tempItems = new ArrayList<String>();
        for(String item: items)
        {
            if(!StringHelper.isNullOrEmpty(item))
                tempItems.add(item);
        }

        Collections.sort(tempItems, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.substring(0,1).compareTo(t1.substring(0,1));

            }
        });

        return tempItems;
        //return items.OrderByDescending(p => p.Substring(0, 1)).ToList();
    }

    public StringBuilder AnalyzeHexagramResult(String month, String day, Hexagram originalHexagram, Hexagram resultHexagram)
    {
        HeavenlyStem mHS = new HeavenlyStem();
        String cM = month.substring(0,1);
        XmlModelExtProperty cMP = xmlModelCache.getCelestialStem().getCelestialStems().get(cM);
        mHS.setId(cMP.getId());
        mHS.setFiveElement(EnumFiveElement.toEnum(cMP.getWuXing()));
        mHS.setName(cM);

        String tM = month.substring(1,1);
        XmlModelExtProperty tMP = xmlModelCache.getTerrestrial().getTerrestrials().get(tM);
        EarthlyBranch mEB = new EarthlyBranch(tMP.getId(),tM, EnumFiveElement.toEnum(tMP.getWuXing()));

        String dM = day.substring(0,1);
        XmlModelExtProperty dMP = xmlModelCache.getCelestialStem().getCelestialStems().get(dM);
        HeavenlyStem dHS = new HeavenlyStem(dMP.getId(),dM,EnumFiveElement.toEnum(dMP.getWuXing()));

        String dtM = day.substring(1,1);
        XmlModelExtProperty dtMP = xmlModelCache.getTerrestrial().getTerrestrials().get(dtM);
        EarthlyBranch dEB = new EarthlyBranch(dtMP.getId(),dtM, EnumFiveElement.toEnum(dtMP.getWuXing()));


        return ParseAnalyzeResultToString(AnalyzeHexagramResult(mEB, dHS, dEB, originalHexagram, resultHexagram));
    }

    public StringBuilder ParseAnalyzeLineResultToString(List<EnumLineStatus> lineStatus)
    {
        StringBuilder sb = new StringBuilder();

        for (EnumLineStatus status : lineStatus)
        {
            sb.append(status.toString());
            sb.append(",");
        }
        return sb;
    }

    public StringBuilder ParseAnalyzeThreeSuitResult(List<Object> threeSuit)
    {
        StringBuilder sb = new StringBuilder();

        for (Object o : threeSuit)
        {
            if (o instanceof EarthlyBranchDay)
            {
                sb.append("?");
                sb.append(",");
            }
            if (o instanceof EarthlyBranchMonth)
            {
                sb.append("?");
                sb.append(",");
            }
            if (o instanceof Line)
            {
                Line line = (Line) o;
                sb.append(line.getPosition());
                sb.append("?,");
            }
        }

        return sb;
    }

    public StringBuilder ParseAnalyzeEnterDynamicLineTombRepositoryResult(List<Line> lines)
    {
        StringBuilder sb = new StringBuilder();

        for (Line l : lines)
        {
            sb.append(l.getPosition());
            sb.append("?,");
        }

        return sb;
    }

    public StringBuilder ParseAnalyzeResultToString(AnalyzeResultCollection collection)
    {
        StringBuilder sb = new StringBuilder();

        HashMap<Pair<Integer, EnumSixRelation>, ArrayList<EnumLineStatus>>  lineResults = collection.getLineLevelResult();
        for (Pair<Integer, EnumSixRelation> key : lineResults.keySet())
        {
            sb.append(key.first);
            sb.append("-");
            sb.append(key.second.toString());
            sb.append(":");
            sb.append(ParseAnalyzeLineResultToString(lineResults.get(key)));
            sb.append(BreakLineSymble);
        }

        if (collection.getHexagramLevelThreeSuitResult() != null)
        {
            for (Pair<EnumSixRelation, ArrayList<Object>> suit : collection.getHexagramLevelThreeSuitResult())
            {
                sb.append("!-");
                sb.append(suit.first.toString());
                sb.append(":");
                sb.append("?,");
                sb.append(ParseAnalyzeThreeSuitResult(suit.second));
                sb.append(BreakLineSymble);
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
                sb.append(ParseAnalyzeEnterDynamicLineTombRepositoryResult(collection.getLineLevelTombRepository().get(key)));
                sb.append("??");
            }
        }

        return sb;
    }

    public AnalyzeResultCollection AnalyzeHexagramResult(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem, EarthlyBranch dayEarthlyBranch, Hexagram originalHexagram, Hexagram resultHexagram)
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
            if(IsDynamicLine(line) && Default.getGrowsMapping(context).get(Default.Twelve_Grow_Mu).containsValue(line.getEarthlyBranch().getId()))
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

            AnalyzeOneLineTombRepository(oline, dynamicLines, dynamicLineTombRepository);

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
                if (IsDynamicLine(oline))
                {
                    //check three suit
                    linesSuitSet.add(Pair.create(oline, tLine));
                }
            }

            boolean isFuShen = oline.getEarthlyBranchAttached() != null && !StringHelper.isNullOrEmpty(oline.getEarthlyBranch().getName());

            Pair<EnumSixRelation, ArrayList<EnumLineStatus>> tupleAttachedLineResult = null, tupleLineResult = null;

            if (isFuShen)
            {
                Pair<EnumSixRelation, ArrayList<EnumLineStatus>> analyzeAttachedLineResult = AnalyzeLineResult(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oline, tLine, isFuShen);
                tupleAttachedLineResult = analyzeAttachedLineResult;
            }

            Pair<EnumSixRelation, ArrayList<EnumLineStatus>> analyzeLineResult = AnalyzeLineResult(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oline, tLine, false);
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
        ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> threeSuitResult = AnalyzeThreeSuit(monthEarthlyBranch, dayEarthlyBranch, originalHexagram, linesSuitSet);

        result.setHexagramLevelThreeSuitResult(threeSuitResult);
        result.setLineLevelResult(lineStatus);
        result.setLineLevelTombRepository(dynamicLineTombRepository);

        return result;
    }

    public void AnalyzeOneLineTombRepository(Line oline, ArrayList<Line> dynamicLines, HashMap<Line, ArrayList<Line>> dynamicLineTombRepository)
    {
        //check dynamic tomb repository
        for(Line dl : dynamicLines)
        {
            int dynamicLineEBIndex = dl.getEarthlyBranch().getId();
            int originalLineEBIndex = oline.getEarthlyBranch().getId();

            boolean hasMu = Default.getGrowsMapping(context).get(Default.Twelve_Grow_Mu).containsKey(originalLineEBIndex);
            if (!oline.equals(dl) && !IsDynamicLine(oline) && hasMu)
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

    public ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> AnalyzeThreeSuit(EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, Hexagram originalHexagram, List<Pair<Line, Line>> linesSuitSet)
    {
        List<Pair<EnumSixRelation, ArrayList<Object>>> threeSuit = new ArrayList<Pair<EnumSixRelation, ArrayList<Object>>>();

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
                int[] oneDynamicLine = new int[] { tuple.first.getEarthlyBranch().getId(), monthIndex, dayIndex }.Distinct();

                ArrayList<Integer> distinctOneDynamicLine = new ArrayList<Integer>();

                for(int one : oneDynamicLine)
                {
                    if(!distinctOneDynamicLine.contains(one))
                        distinctOneDynamicLine.add(one);
                }

                if (distinctOneDynamicLine.size() == 3)
                {
                    var r = from k in DefaultValue.EarthlyBranchThreeSuitSet
                    where k.Key.Where(p => oneDynamicLine.Contains(p)).Count() == 3
                    select k;
                    if (r != null && r.Count() > 0)
                    {
                        var sixRelation = Builder.parseSixRelationByFiveElement(originalHexagram.getFiveElement(), r.Single().Value);
                        threeSuit.Add(Tuple.Create(sixRelation, new List<object> { tuple.Item1, monthEarthlyBranchType, dayEarthlyBranchType }));
                    }
                }

                //一、一爻动与日月组成三合局
                Pair<EnumSixRelation, ArrayList<Object>> dynamicResultWithMonthDay = CreateThreeSuitByDynamicLines(tuple.first, monthEarthlyBranchType, dayEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonthDay != null)
                    threeSuit.add(dynamicResultWithMonthDay);

                //五、四爻与六爻和变爻组成
                Pair<EnumSixRelation, ArrayList<Object>> dynamicResultWithMonth = CreateThreeSuitByDynamicLines(tuple.first, tuple.second, monthEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonth != null)
                    threeSuit.add(dynamicResultWithMonth);

                //六、动爻与变爻和日或月组成
                Pair<EnumSixRelation, ArrayList<Object>> dynamicResultWithDay = CreateThreeSuitByDynamicLines(tuple.first, tuple.second, dayEarthlyBranchType, originalHexagram);
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

            //?????????????
            Func<List<int>, HashMap<int[], EnumFiveElement>> funFindSuits = suitList =>
            {
                var result = new Dictionary<int[], EnumFiveElement>();

                foreach (var suit in DefaultValue.EarthlyBranchThreeSuitSet)
                {
                    int count = 0;
                    foreach (var k in suit.Key)
                    {
                        if (suitList.Any(p => p == k))
                        {
                            count += 1;
                        }
                    }

                    if (count == 3)
                        result.Add(suit.Key, suit.Value);
                }

                return result;
            };

            //?????????
            if(threeDynamicLineSuit.size() >= 2)
            {
                var listIncludeMonthAndDay = threeDynamicLineSuit.Select(p => p.EarthlyBranch.ID).ToList();
                listIncludeMonthAndDay.Add(monthEarthlyBranchType.getId());
                listIncludeMonthAndDay.Add(dayEarthlyBranchType.getId());

                var existedSuit = funFindSuits(listIncludeMonthAndDay.Distinct().ToList());

                var groupLines = new List<Object>();
                foreach (var k in existedSuit)
                {
                    if (k.Key.Contains(monthEarthlyBranchType.ID))
                    {
                        groupLines.Add(monthEarthlyBranchType);
                    }
                    else if (k.Key.Contains(dayEarthlyBranchType.ID))
                    {
                        groupLines.Add(dayEarthlyBranchType);
                    }

                    var ls = threeDynamicLineSuit.Where(p => k.Key.Contains(p.EarthlyBranch.ID));
                    foreach (var l in ls)
                    {
                        groupLines.Add(l);
                    }

                    var sixRelation = Builder.ParseSixRelationByFiveElement(originalHexagram.Property, k.Value);
                    threeSuit.Add(Tuple.Create(sixRelation, groupLines));
                }
            }

            //?????????????
            //???????
            if (threeDynamicLineSuit.Count >= 3)
            {

                var existedSuit = funFindSuits(threeDynamicLineSuit.Select(p => p.EarthlyBranch.ID).ToList());

                var groupLines = new List<object>();
                foreach (var k in existedSuit)
                {
                    var ls = threeDynamicLineSuit.Where(p => k.Key.Contains(p.EarthlyBranch.ID));
                    foreach (var l in ls)
                    {
                        groupLines.Add(l);
                    }
                    var sixRelation = Builder.ParseSixRelationByFiveElement(originalHexagram.Property, k.Value);
                    threeSuit.Add(Tuple.Create(sixRelation, groupLines));
                }
            }

            //????????????
            Pair<EnumSixRelation, ArrayList<Object>> lines13KindOne = CreateThreeSuitByDynamicLines(linesSuitSet, 1, originalHexagram, true);
            if (lines13KindOne != null)
                threeSuit.add(lines13KindOne);
            Pair<EnumSixRelation, ArrayList<Object>> lines13KindTwo = CreateThreeSuitByDynamicLines(linesSuitSet, 1, originalHexagram, false);
            if (lines13KindTwo != null)
                threeSuit.add(lines13KindTwo);

            //????????????
            Pair<EnumSixRelation, ArrayList<Object>> lines46KindOne = CreateThreeSuitByDynamicLines(linesSuitSet, 4, originalHexagram, true);
            if (lines46KindOne != null)
                threeSuit.add(lines46KindOne);
            Pair<EnumSixRelation, ArrayList<Object>> lines46KindTwo = CreateThreeSuitByDynamicLines(linesSuitSet, 4, originalHexagram, false);
            if (lines46KindTwo != null)
                threeSuit.add(lines46KindTwo);

            return threeSuit;
        }
        return null;
    }

    Pair<EnumSixRelation, ArrayList<Object>> CreateThreeSuitByDynamicLines(List<Pair<Line, Line>> lines, int beginPosition, Hexagram hexagram, boolean withFirstResultLine)
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
                return CreateThreeSuitByDynamicLines(linePairOne.first, linePairOne.second, linePairTwo.first, hexagram);
            }
            else
            {
                if (linePairTwo.second == null)
                    return null;
                return CreateThreeSuitByDynamicLines(linePairOne.first, linePairTwo.first, linePairTwo.second, hexagram);
            }
        }
        return null;
    }

    Pair<EnumSixRelation, ArrayList<Object>> CreateThreeSuitByDynamicLines(Line line1, Line line2, EarthlyBranch dayOrMonthEarthlyBranch, Hexagram hexagram)
    {
        //line2 == null means this line is AnDong
        if (line2 == null)
            return null;

        int[] kind = CreateThreeSuitByLineOrMonthOrDay(
                line1.getEarthlyBranch().getId(),
                line2.getEarthlyBranch().getId(),
                dayOrMonthEarthlyBranch.getId()
        );

        EnumSixRelation kindResult = CreateThreeSuitByIndex(kind, hexagram);

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

    Pair<EnumSixRelation, ArrayList<Object>> CreateThreeSuitByDynamicLines(Line line1, EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, Hexagram hexagram) {
        int[] kind = CreateThreeSuitByLineOrMonthOrDay(
                line1.getEarthlyBranch().getId(),
                monthEarthlyBranch.getId(),
                dayEarthlyBranch.getId()
        );

        EnumSixRelation kindResult = CreateThreeSuitByIndex(kind, hexagram);

        if (kindResult != null) {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(line1);
            list.add(monthEarthlyBranch);
            list.add(dayEarthlyBranch);
            return Pair.create(kindResult, list);
        } else
            return null;
    }

    Pair<EnumSixRelation, ArrayList<Object>> CreateThreeSuitByDynamicLines(Line line1, Line line2, Line line3, Hexagram hexagram)
    {
        int[] kind = CreateThreeSuitByLineOrMonthOrDay(
                line1.getEarthlyBranch().getId(),
                line2.getEarthlyBranch().getId(),
                line3.getEarthlyBranch().getId()
        );

        EnumSixRelation kindResult = CreateThreeSuitByIndex(kind, hexagram);

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

    int[] CreateThreeSuitByLineOrMonthOrDay(int earthBranchID1, int earthBranchID2, int earthBranchID3)
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

    EnumSixRelation CreateThreeSuitByIndex(int[] indexSummary, Hexagram hexagram)
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

    public Pair<EnumSixRelation, ArrayList<EnumLineStatus>> AnalyzeLineResult(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                                                          EarthlyBranch dayEarthlyBranch, Line oLine, Line tLine, boolean checkFuShen)
    {
        ArrayList<EnumLineStatus> lineStatus = new ArrayList<EnumLineStatus>();
        EnumSixRelation enumSixRelation;

        if (checkFuShen)
        {
            enumSixRelation = oLine.getSixRelationAttached();

            lineStatus.add(EnumLineStatus.Fu);
            EnumLineStatus kong = AnalyzeLineXunKong(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen);
            if (kong != null)
                lineStatus.add(kong);
        }
        else
        {
            enumSixRelation = oLine.getSixRelation();

            if (IsDynamicLine(oLine))
            {
                lineStatus.add(EnumLineStatus.Dong);
            }
            else
            {
                EnumLineStatus kong = AnalyzeLineXunKong(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, false);
                if (kong != null)
                    lineStatus.add(kong);
            }
        }

        ArrayList<EnumLineStatus> lineStatusWithMonthAndDay = AnalyzeLineRelationWithMonthAndDay(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen);

        lineStatus.addAll(lineStatusWithMonthAndDay);

        if (!checkFuShen && IsDynamicLine(oLine))
        {
            ArrayList<EnumLineStatus> dynamicLineChanged = AnalyzeDynamicLineChanged(oLine, tLine, monthEarthlyBranch, dayEarthlyBranch, dayHeavenlyStem);

            lineStatus.addAll(dynamicLineChanged);
        }

        return Pair.create(enumSixRelation, lineStatus);
    }

    public ArrayList<EnumLineStatus> AnalyzeLineRelationWithMonthAndDay(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
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
        EnumLingRelation rMonth = AnalyzeLineLingRelation(monthEarthlyBranch, originalLine, checkFuShen);
        if (monthEarthlyBranch.getId() == oLineEarthlyBranch.getId())
        {
            listStatus.add(EnumLineStatus.LinYue);
        }
        else
        {
            listStatus.add(ConvertEarthlyBranchRelationToEnum(rMonth, true));
            //Yu Qi ex: ????
            EnumLineStatus yuQi = AnalyzeLineLingRelationYuQi(monthEarthlyBranch.getId(), originalLine, checkFuShen);
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
        EnumLingRelation rDay = AnalyzeLineLingRelation(dayEarthlyBranch, originalLine, checkFuShen);
        if (dayEarthlyBranch.getId() == oLineEarthlyBranch.getId())
        {
            listStatus.add(EnumLineStatus.LinRi);
        }
        else
        {
            listStatus.add(ConvertEarthlyBranchRelationToEnum(rDay, false));
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

        ArrayList<EnumLineStatus> lineExtensionStatus = AnalyzeLineExtension(rMonth, monthEarthlyBranch, originalLine, dayHeavenlyStem, dayEarthlyBranch, checkFuShen);
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


    public ArrayList<EnumLineStatus> AnalyzeLineExtension(EnumLingRelation rMonth, EarthlyBranch monthEarthlyBranch, Line oLine,
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
                    && IsXunKong(dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen))
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

    public EnumLineStatus AnalyzeLineXunKong(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                              EarthlyBranch dayEarthlyBranch, Line oLine, boolean isFuShen)
    {

        boolean monthEarthlyBranchCompare = !isFuShen ?
                !monthEarthlyBranch.getName().equals(oLine.getEarthlyBranch().getName()) && !dayEarthlyBranch.getName().equals(oLine.getEarthlyBranch().getName()) :
                !monthEarthlyBranch.getName().equals(oLine.getEarthlyBranchAttached().getName()) && !dayEarthlyBranch.getName().equals(oLine.getEarthlyBranchAttached().getName());

        if (monthEarthlyBranchCompare)
        {
            if (IsXunKong(dayHeavenlyStem, dayEarthlyBranch, oLine, isFuShen))
            {
                return EnumLineStatus.Kong;
            }
        }

        return null;
    }

    public ArrayList<EnumLineStatus> AnalyzeDynamicLineChanged(Line oLine, Line tLine, EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, HeavenlyStem dayHeavenStem)
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
        if (IsXunKong(dayHeavenStem, dayEarthlyBranch, tLine, false))
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
        getLineStatus(listStatus, Default.Twelve_Grow_MuYu,EnumLineStatus.HuaMuKu,oLine,tLine);

        EnumLingRelation rMonth = AnalyzeLineLingRelation(monthEarthlyBranch, tLine,false);
        EnumLingRelation rDay = AnalyzeLineLingRelation(dayEarthlyBranch, tLine,false);
        EnumLineStatus monthStatus = ConvertEarthlyBranchRelationToEnumChangedLine(rMonth, true);
        EnumLineStatus dayStatus = ConvertEarthlyBranchRelationToEnumChangedLine(rDay,false);
        if (monthStatus != null)
            listStatus.add(monthStatus);
        if (dayStatus != null)
            listStatus.add(dayStatus);

        EnumLineStatus yuQi = AnalyzeLineLingRelationYuQi(monthEarthlyBranch.getId(), tLine,false);
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

    public EnumLingRelation AnalyzeLineLingRelation(EarthlyBranch earthlyBranch, Line line, boolean checkFuShen) {
        EnumLing monthOrDayLing = GetLingByEarthlyBranchIndex(earthlyBranch.getId());
        EnumLing yaoLing = GetLingByEarthlyBranchIndex(line.getEarthlyBranch().getId());

        if (checkFuShen && !StringHelper.isNullOrEmpty(line.getEarthlyBranchAttached().getName()))
            yaoLing = GetLingByEarthlyBranchIndex(line.getEarthlyBranchAttached().getId());

        return AnalyzeLineLingRelation(monthOrDayLing, yaoLing);
    }

    public EnumLineStatus AnalyzeLineLingRelationYuQi(int earthlyBranchID, Line line, boolean checkFuShen)
    {
        EnumLing monthLing = GetLingByEarthlyBranchIndex(line.getEarthlyBranch().getId());
        if (checkFuShen)
            monthLing = GetLingByEarthlyBranchIndex(line.getEarthlyBranchAttached().getId());
        if (Default.getLingYuQiMapping().keySet().contains(earthlyBranchID) && Default.getLingYuQiMapping().get(earthlyBranchID) == monthLing)
        {
            return EnumLineStatus.YuQiYue;
        }
        return null;
    }

    public boolean IsDynamicLine(Line line)
    {
        return line.getLineSymbol() == EnumLineSymbol.LaoYin || line.getLineSymbol() == EnumLineSymbol.LaoYang;
    }

    public boolean IsXunKong(HeavenlyStem dayHeavenlyStem, EarthlyBranch dayEarthlyBranch, Line line, boolean isFuShen)
    {
        Pair<String,String> kong = Utility.getXunKong(context, dayHeavenlyStem.getName(), dayEarthlyBranch.getName());

        if (isFuShen)
            return kong.first.contains(line.getEarthlyBranchAttached().getName()) || kong.second.contains(line.getEarthlyBranchAttached().getName());
        else
            return kong.first.contains(line.getEarthlyBranch().getName()) || kong.second.contains(line.getEarthlyBranch().getName());
    }

    public EnumLing GetLingByEarthlyBranchIndex(int index)
    {
        for(EnumLing ling: Default.getLingEarthlyBranchMapping().keySet())
        {
            if(Arrays.asList(Default.getLingEarthlyBranchMapping().get(ling)).contains(index))
                return ling;
        }
        return null;
    }

    public EnumLingRelation AnalyzeLineLingRelation(EnumLing monthOrDayLing, EnumLing lineLing)
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

    public EnumLineStatus ConvertEarthlyBranchRelationToEnum(EnumLingRelation relation, boolean isMonth)
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

    public EnumLineStatus ConvertEarthlyBranchRelationToEnumChangedLine(EnumLingRelation relation, boolean isMonth)
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
