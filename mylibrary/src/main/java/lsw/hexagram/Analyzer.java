package lsw.hexagram;

import android.content.Context;
import android.util.Pair;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lsw.library.BaZiHelper;
import lsw.library.R;
import lsw.library.StringHelper;
import lsw.library.Utility;
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

        return items.OrderByDescending(p => p.Substring(0, 1)).ToList();
    }

    public StringBuilder AnalyzeHexagramResult(String month, String day, Hexagram originalHexagram, Hexagram resultHexagram)
    {
        HeavenlyStem mHS = new HeavenlyStem();
        String cM = month.substring(0,1);
        XmlModelExtProperty cP = xmlModelCache.getCelestialStem().getCelestialStems().get(cM);
        mHS.setId(cP.getId());
        mHS.setFiveElement(EnumFiveElement.toEnum(cP.getWuXing()));
        mHS.setName(cM);

        String dM = day.substring(0,1);
        XmlModelExtProperty dP = xmlModelCache.getTerrestrial().getTerrestrials().get(dM);
        EarthlyBranch mEB = new EarthlyBranch(dP.getId(),dM, EnumFiveElement.toEnum(dP.getWuXing()));

        HeavenlyStem dHS = DefaultValue.HeavenlyStem.Single(p => p.Name == day.Substring(0, 1));
        EarthlyBranch dEB = DefaultValue.EarthlyBranch.Single(p => p.Name == day.Substring(1, 1));

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

        var lineResults = collection.LineLevelResult;
        foreach (var lineResult in lineResults)
        {
            sb.Append(lineResult.Key.Item1);
            sb.Append("-");
            sb.Append(lineResult.Key.Item2.ToText());
            sb.Append(":");
            sb.Append(ParseAnalyzeLineResultToString(lineResult.Value));
            sb.Append(BreakLineSymble);
        }

        if (collection.HexagramLevelThreeSuitResult != null)
        {
            foreach (var suit in collection.HexagramLevelThreeSuitResult)
            {
                sb.Append("!-");
                sb.Append(suit.Item1.ToText());
                sb.Append(":");
                sb.Append("?,");
                sb.Append(ParseAnalyzeThreeSuitResult(suit.Item2));
                sb.Append(BreakLineSymble);
            }
        }
        if (collection.LineLevelTombRepository != null)
        {
            foreach (var tombRepository in collection.LineLevelTombRepository)
            {
                sb.Append(tombRepository.Key.Position);
                sb.Append("-");
                sb.Append(tombRepository.Key.SixRelation.ToText());
                sb.Append(":");
                sb.Append(ParseAnalyzeEnterDynamicLineTombRepositoryResult(tombRepository.Value));
                sb.Append("??");
            }
        }

        return sb;
    }

    public AnalyzeResultCollection AnalyzeHexagramResult(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem, EarthlyBranch dayEarthlyBranch, Hexagram originalHexagram, Hexagram resultHexagram = null)
    {
        var result = new AnalyzeResultCollection();

        var lineStatus = new Dictionary<Tuple<int, EnumSixRelation>, List<EnumLineStatus>>();

        var linesSuitSet = new List<Tuple<Line, Line>>();
        //tomb repository
        var dynamicLineTombRepository = new Dictionary<Line, List<Line>>();

        var lines = originalHexagram.Upper.Lines.Union(originalHexagram.Lower.Lines);

        //check tomb repositroy dynamic
        var dynamicLines = lines.Where(p => IsDynamicLine(p))
        .Where(p => DefaultValue.FiveElementTombRepository.ContainsValue(p.EarthlyBranch.ID));


        foreach (var oline in lines.OrderBy(p => p.SixRelation))
        {

            AnalyzeOneLineTombRepository(oline, dynamicLines, dynamicLineTombRepository);

            Line tLine = null;

            //check three suit
            if (resultHexagram != null)
            {
                tLine = resultHexagram.Upper.Lines.Union(resultHexagram.Lower.Lines).Single(p => p.Position == oline.Position);

                if (IsDynamicLine(oline))
                {
                    //check three suit
                    linesSuitSet.Add(Tuple.Create(oline, tLine));
                }
            }

            bool isFuShen = oline.EarthlyBranchAttached != null && !string.IsNullOrEmpty(oline.EarthlyBranch.Name);

            Tuple<EnumSixRelation, List<EnumLineStatus>> tupleAttachedLineResult = null, tupleLineResult = null;

            if (isFuShen)
            {
                var analyzeAttachedLineResult = AnalyzeLineResult(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oline, tLine, isFuShen);
                tupleAttachedLineResult = analyzeAttachedLineResult;
            }

            var analyzeLineResult = AnalyzeLineResult(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oline, tLine);
            tupleLineResult = analyzeLineResult;

            if (tupleAttachedLineResult != null)
                lineStatus.Add(Tuple.Create(oline.Position, tupleAttachedLineResult.Item1), tupleAttachedLineResult.Item2);

            if (tupleLineResult != null)
                lineStatus.Add(Tuple.Create(oline.Position, analyzeLineResult.Item1), analyzeLineResult.Item2);

            if (tupleLineResult.Item2.Contains(EnumLineStatus.AnDong))
                linesSuitSet.Add(Tuple.Create(oline, default(Line)));
        }

        //three suit
        var threeSuitResult = AnalyzeThreeSuit(monthEarthlyBranch, dayEarthlyBranch, originalHexagram, linesSuitSet);

        result.HexagramLevelThreeSuitResult = threeSuitResult;
        result.LineLevelResult = lineStatus;
        result.LineLevelTombRepository = dynamicLineTombRepository;

        return result;
    }

    public void AnalyzeOneLineTombRepository(Line oline, IEnumerable<Line> dynamicLines, Dictionary<Line, List<Line>> dynamicLineTombRepository)
    {
        //check dynamic tomb repository
        foreach(var dl in dynamicLines)
        {
            var dynamicLineEBIndex = dl.EarthlyBranch.ID;
            var originalLineEBIndex = oline.EarthlyBranch.ID;

            if (!oline.Equals(dl) && !IsDynamicLine(oline) && DefaultValue.FiveElementTombRepository.ContainsKey(originalLineEBIndex))
            {
                if (dynamicLineEBIndex == DefaultValue.FiveElementTombRepository[originalLineEBIndex])
                {
                    if (!dynamicLineTombRepository.ContainsKey(dl))
                        dynamicLineTombRepository.Add(dl, new List<Line>() { oline });
                    else
                        dynamicLineTombRepository[dl].Add(oline);
                }
            }
        }
    }

    public List<Pair<EnumSixRelation, ArrayList<Object>>> AnalyzeThreeSuit(EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, Hexagram originalHexagram, List<Pair<Line, Line>> linesSuitSet)
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
                if (oneDynamicLine.length == 3)
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
                //?????????????
                var dynamicResultWithMonthDay = CreateThreeSuitByDynamicLines(tuple.first, monthEarthlyBranchType, dayEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonthDay != null)
                    threeSuit.Add(dynamicResultWithMonthDay);

                //?????????????
                var dynamicResultWithMonth = CreateThreeSuitByDynamicLines(tuple.first, tuple.second, monthEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonth != null)
                    threeSuit.Add(dynamicResultWithMonth);

                var dynamicResultWithDay = CreateThreeSuitByDynamicLines(tuple.first, tuple.second, dayEarthlyBranchType, originalHexagram);
                if (dynamicResultWithDay != null)
                    threeSuit.Add(dynamicResultWithDay);

                if (DefaultValue.EarthlyBranchThreeSuitSet.Any(p => p.Key.Contains(tuple.first.EarthlyBranch.ID)))
                {
                    threeDynamicLineSuit.add(tuple.first);
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
            var lines13KindOne = CreateThreeSuitByDynamicLines(linesSuitSet, 1, originalHexagram);
            if (lines13KindOne != null)
                threeSuit.Add(lines13KindOne);
            var lines13KindTwo = CreateThreeSuitByDynamicLines(linesSuitSet, 1, originalHexagram, false);
            if (lines13KindTwo != null)
                threeSuit.Add(lines13KindTwo);

            //????????????
            var lines46KindOne = CreateThreeSuitByDynamicLines(linesSuitSet, 4, originalHexagram);
            if (lines46KindOne != null)
                threeSuit.Add(lines46KindOne);
            var lines46KindTwo = CreateThreeSuitByDynamicLines(linesSuitSet, 4, originalHexagram, false);
            if (lines46KindTwo != null)
                threeSuit.Add(lines46KindTwo);

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
        //³ýµôÖØ¸´¼ÍÂ¼
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
            var result = from k in DefaultValue.EarthlyBranchThreeSuitSet
            where k.Key.Join(indexSummary, i => i, o => o, (i, o) => o).Distinct().Count() == 3
            select k;
            if (result != null && result.Count() > 0)
            {
                EnumSixRelation sixRelation = Builder.parseSixRelationByFiveElement(hexagram.getFiveElement(), result.First().Value);

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
