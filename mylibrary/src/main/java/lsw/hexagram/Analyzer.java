package lsw.hexagram;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import lsw.library.BaZiHelper;
import lsw.library.R;
import lsw.library.StringHelper;
import lsw.library.Utility;
import lsw.model.EarthlyBranch;
import lsw.model.EnumLineStatus;
import lsw.model.EnumLineSymbol;
import lsw.model.EnumLing;
import lsw.model.EnumLingRelation;
import lsw.model.HeavenlyStem;
import lsw.model.Line;
import lsw.value.Default;
import lsw.xml.XmlModelCache;

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

    public Dictionary<int, string> GetSixAnimalsByHeavenlyStem(string day)
    {
        var dayTianGan = DefaultValue.HeavenlyStem.Single(p => p.Name == day.Substring(0, 1));
        return GetSixAnimalsByHeavenlyStem(dayTianGan);
    }

    public Dictionary<int, string> GetSixAnimalsByHeavenlyStem(HeavenlyStem dayHeavenlyStem)
    {
        var variable = DefaultValue.SixAnimalsDayIndex.Single(p => p.Key.Contains(dayHeavenlyStem.ID));
        var result = new Dictionary<int, string>();

        var bigger = DefaultValue.SixAnimals.Where(p => p.Key > variable.Value).OrderBy(p => p.Key);
        var smaller = DefaultValue.SixAnimals.Where(p => p.Key < variable.Value).OrderBy(p => p.Key);
        result.Add(1, DefaultValue.SixAnimals[variable.Value]);
        foreach (var b in bigger)
        {
            result.Add(result.Count + 1, b.Value);
        }

        foreach (var s in smaller)
        {
            result.Add(result.Count + 1, s.Value);
        }

        return result;
    }

    public const string BreakLineSymble = "\r\n";

    public List<string> OrderedStringResult(string sb)
    {
        var items = sb.Replace(BreakLineSymble, "|").Split('|').Where(p => !string.IsNullOrEmpty(p));

        return items.OrderByDescending(p => p.Substring(0, 1)).ToList();
    }

    public StringBuilder AnalyzeHexagramResult(string month, string day, Hexagram originalHexagram, Hexagram resultHexagram = null)
    {
        var mHS = DefaultValue.HeavenlyStem.Single(p => p.Name == month.Substring(0, 1));
        var mEB = DefaultValue.EarthlyBranch.Single(p => p.Name == month.Substring(1, 1));

        var dHS = DefaultValue.HeavenlyStem.Single(p => p.Name == day.Substring(0, 1));
        var dEB = DefaultValue.EarthlyBranch.Single(p => p.Name == day.Substring(1, 1));

        return ParseAnalyzeResultToString(AnalyzeHexagramResult(mEB, dHS, dEB, originalHexagram, resultHexagram));
    }

    public StringBuilder ParseAnalyzeLineResultToString(List<EnumLineStatus> lineStatus)
    {
        var sb = new StringBuilder();

        foreach (var status in lineStatus)
        {
            sb.Append(status.ToText());
            sb.Append(",");
        }
        return sb;
    }

    public StringBuilder ParseAnalyzeThreeSuitResult(List<object> threeSuit)
    {
        var sb = new StringBuilder();

        foreach (var o in threeSuit)
        {
            if (o is EarthlyBranchDay)
            {
                sb.Append("?");
                sb.Append(",");
            }
            if (o is EarthlyBranchMonth)
            {
                sb.Append("?");
                sb.Append(",");
            }
            if (o is Line)
            {
                var line = o as Line;
                sb.Append(line.Position);
                sb.Append("?,");
            }
        }

        return sb;
    }

    public StringBuilder ParseAnalyzeEnterDynamicLineTombRepositoryResult(List<Line> lines)
    {
        var sb = new StringBuilder();

        foreach (var l in lines)
        {
            sb.Append(l.Position);
            sb.Append("?,");
        }

        return sb;
    }

    public StringBuilder ParseAnalyzeResultToString(AnalyzeResultCollection collection)
    {
        var sb = new StringBuilder();

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
        foreach (var dl in dynamicLines)
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

    public List<Tuple<EnumSixRelation, List<object>>> AnalyzeThreeSuit(EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, Hexagram originalHexagram, List<Tuple<Line, Line>> linesSuitSet)
    {
        var threeSuit = new List<Tuple<EnumSixRelation, List<object>>>();

        var dayEarthlyBranchType = new EarthlyBranchDay()
        {
            ID = dayEarthlyBranch.ID,
            Name = dayEarthlyBranch.Name,
            Property = dayEarthlyBranch.Property
        };

        var monthEarthlyBranchType = new EarthlyBranchMonth()
        {
            ID = monthEarthlyBranch.ID,
            Name = monthEarthlyBranch.Name,
            Property = monthEarthlyBranch.Property
        };

        if (linesSuitSet.Count > 0)
        {
            var monthIndex = monthEarthlyBranch.ID;
            var dayIndex = dayEarthlyBranch.ID;

            var threeDynamicLineSuit = new List<Line>();

            foreach (var tuple in linesSuitSet)
            {
                var oneDynamicLine = new int[] { tuple.Item1.EarthlyBranch.ID, monthIndex, dayIndex }.Distinct();
                if (oneDynamicLine.Count() == 3)
                {
                    var r = from k in DefaultValue.EarthlyBranchThreeSuitSet
                    where k.Key.Where(p => oneDynamicLine.Contains(p)).Count() == 3
                    select k;
                    if (r != null && r.Count() > 0)
                    {
                        var sixRelation = Builder.ParseSixRelationByFiveElement(originalHexagram.Property, r.Single().Value);
                        threeSuit.Add(Tuple.Create(sixRelation, new List<object> { tuple.Item1, monthEarthlyBranchType, dayEarthlyBranchType }));
                    }
                }
                //?????????????
                var dynamicResultWithMonthDay = CreateThreeSuitByDynamicLines(tuple.Item1, monthEarthlyBranchType, dayEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonthDay != null)
                    threeSuit.Add(dynamicResultWithMonthDay);

                //?????????????
                var dynamicResultWithMonth = CreateThreeSuitByDynamicLines(tuple.Item1, tuple.Item2, monthEarthlyBranchType, originalHexagram);
                if (dynamicResultWithMonth != null)
                    threeSuit.Add(dynamicResultWithMonth);

                var dynamicResultWithDay = CreateThreeSuitByDynamicLines(tuple.Item1, tuple.Item2, dayEarthlyBranchType, originalHexagram);
                if (dynamicResultWithDay != null)
                    threeSuit.Add(dynamicResultWithDay);

                if (DefaultValue.EarthlyBranchThreeSuitSet.Any(p => p.Key.Contains(tuple.Item1.EarthlyBranch.ID)))
                {
                    threeDynamicLineSuit.Add(tuple.Item1);
                }
            }

            //?????????????
            Func<List<int>, Dictionary<int[], EnumFiveElement>> funFindSuits = suitList =>
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
            if(threeDynamicLineSuit.Count >= 2)
            {
                var listIncludeMonthAndDay = threeDynamicLineSuit.Select(p => p.EarthlyBranch.ID).ToList();
                listIncludeMonthAndDay.Add(monthEarthlyBranchType.ID);
                listIncludeMonthAndDay.Add(dayEarthlyBranchType.ID);

                var existedSuit = funFindSuits(listIncludeMonthAndDay.Distinct().ToList());

                var groupLines = new List<object>();
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

    Tuple<EnumSixRelation, List<object>> CreateThreeSuitByDynamicLines(List<Tuple<Line, Line>> lines, int beginPosition, Hexagram hexagram, bool withFirstResultLine = true)
    {
        var suitLines = lines.Where(p => p.Item1.Position == beginPosition || p.Item1.Position == beginPosition + 2);
        if (suitLines.Count() >= 2)
        {
            var linePairOne = suitLines.First();
            var linePairTwo = suitLines.Last();
            if (withFirstResultLine)
            {
                //means AnDong Line
                if (linePairOne.Item2 == null)
                    return null;
                return CreateThreeSuitByDynamicLines(linePairOne.Item1, linePairOne.Item2, linePairTwo.Item1, hexagram);
            }
            else
            {
                if (linePairTwo.Item2 == null)
                    return null;
                return CreateThreeSuitByDynamicLines(linePairOne.Item1, linePairTwo.Item1, linePairTwo.Item2, hexagram);
            }
        }
        return null;
    }

    Tuple<EnumSixRelation, List<object>> CreateThreeSuitByDynamicLines(Line line1, Line line2, EarthlyBranch dayOrMonthEarthlyBranch, Hexagram hexagram)
    {
        //line2 == null means this line is AnDong
        if (line2 == null)
            return null;

        var kind = CreateThreeSuitByLineOrMonthOrDay(
                line1.EarthlyBranch.ID,
                line2.EarthlyBranch.ID,
                dayOrMonthEarthlyBranch.ID
        );

        var kindResult = CreateThreeSuitByIndex(kind, hexagram);

        if (kindResult.HasValue)
            return Tuple.Create(kindResult.Value, new List<object>() { line1, line2, dayOrMonthEarthlyBranch });
        else
            return null;
    }

    Tuple<EnumSixRelation, List<object>> CreateThreeSuitByDynamicLines(Line line1, EarthlyBranch monthEarthlyBranch, EarthlyBranch dayEarthlyBranch, Hexagram hexagram)
    {
        var kind = CreateThreeSuitByLineOrMonthOrDay(
                line1.EarthlyBranch.ID,
                monthEarthlyBranch.ID,
                dayEarthlyBranch.ID
        );

        var kindResult = CreateThreeSuitByIndex(kind, hexagram);

        if (kindResult.HasValue)
            return Tuple.Create(kindResult.Value, new List<object>() { line1, monthEarthlyBranch, dayEarthlyBranch });
        else
            return null;
    }

    Tuple<EnumSixRelation, List<object>> CreateThreeSuitByDynamicLines(Line line1, Line line2, Line line3, Hexagram hexagram)
    {
        var kind = CreateThreeSuitByLineOrMonthOrDay(
                line1.EarthlyBranch.ID,
                line2.EarthlyBranch.ID,
                line3.EarthlyBranch.ID
        );

        var kindResult = CreateThreeSuitByIndex(kind, hexagram);

        if (kindResult.HasValue)
            return Tuple.Create(kindResult.Value, new List<object>() { line1, line2, line3 });
        else
            return null;
    }

    int[] CreateThreeSuitByLineOrMonthOrDay(int earthBranchID1, int earthBranchID2, int earthBranchID3)
    {
        var list =  new int[] { earthBranchID1, earthBranchID2, earthBranchID3 };
        if (list.Distinct().Count() == 3)
            return list;
        else
            return null;
    }

    EnumSixRelation? CreateThreeSuitByIndex(int[] indexSummary, Hexagram hexagram)
    {
        if (indexSummary != null && indexSummary.Length == 3)
        {
            var result = from k in DefaultValue.EarthlyBranchThreeSuitSet
            where k.Key.Join(indexSummary, i => i, o => o, (i, o) => o).Distinct().Count() == 3
            select k;
            if (result != null && result.Count() > 0)
            {
                var sixRelation = Builder.ParseSixRelationByFiveElement(hexagram.Property, result.First().Value);

                return sixRelation;
            }
        }
        return null;
    }

    public Tuple<EnumSixRelation, List<EnumLineStatus>> AnalyzeLineResult(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                                                          EarthlyBranch dayEarthlyBranch, Line oLine, Line tLine, bool checkFuShen = false)
    {
        var lineStatus = new List<EnumLineStatus>();
        EnumSixRelation? enumSixRelation;

        if (checkFuShen)
        {
            enumSixRelation = oLine.SixRelationAttached;

            lineStatus.Add(EnumLineStatus.Fu);
            var kong = AnalyzeLineXunKong(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen);
            if (kong.HasValue)
                lineStatus.Add(kong.Value);
        }
        else
        {
            enumSixRelation = oLine.SixRelation;

            if (IsDynamicLine(oLine))
            {
                lineStatus.Add(EnumLineStatus.Dong);
            }
            else
            {
                var kong = AnalyzeLineXunKong(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine);
                if (kong.HasValue)
                    lineStatus.Add(kong.Value);
            }
        }

        var lineStatusWithMonthAndDay = AnalyzeLineRelationWithMonthAndDay(monthEarthlyBranch, dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen);

        lineStatus = lineStatus.Union(lineStatusWithMonthAndDay).ToList();

        if (!checkFuShen && IsDynamicLine(oLine))
        {
            var dynamicLineChanged = AnalyzeDynamicLineChanged(oLine, tLine, monthEarthlyBranch, dayEarthlyBranch, dayHeavenlyStem);

            lineStatus = lineStatus.Union(dynamicLineChanged).ToList();
        }

        return Tuple.Create(enumSixRelation.Value, lineStatus);
    }

    public List<EnumLineStatus> AnalyzeLineRelationWithMonthAndDay(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                                                   EarthlyBranch dayEarthlyBranch, Line originalLine, bool checkFuShen)
    {
        var listStatus = new List<EnumLineStatus>();

        var oLineEarthlyBranchIndex = originalLine.EarthlyBranch.ID;
        var oLineEarthlyBranch = originalLine.EarthlyBranch;

        if (checkFuShen)
        {
            oLineEarthlyBranchIndex = originalLine.EarthlyBranchAttached.ID;
            oLineEarthlyBranch = originalLine.EarthlyBranchAttached;
        }
        //??????
        var rMonth = AnalyzeLineLingRelation(monthEarthlyBranch, originalLine, checkFuShen);
        if (monthEarthlyBranch.ID == oLineEarthlyBranch.ID)
        {
            listStatus.Add(EnumLineStatus.LinYue);
        }
        else
        {
            listStatus.Add(ConvertEarthlyBranchRelationToEnum(rMonth, true));
            //Yu Qi ex: ????
            var yuQi = AnalyzeLineLingRelationYuQi(monthEarthlyBranch.ID, originalLine, checkFuShen);
            if (yuQi.HasValue)
            {
                listStatus.Add(yuQi.Value);
            }

            if (DefaultValue.EarthlyBranchSixSuit[monthEarthlyBranch.ID] == oLineEarthlyBranchIndex)
            {
                listStatus.Add(EnumLineStatus.YueHe);
            }
            if (DefaultValue.EarthlyBranchSixInverse[monthEarthlyBranch.ID] == oLineEarthlyBranchIndex)
            {
                listStatus.Add(EnumLineStatus.YuePo);
            }
        }
        //??????
        var rDay = AnalyzeLineLingRelation(dayEarthlyBranch, originalLine, checkFuShen);
        if (dayEarthlyBranch.ID == oLineEarthlyBranch.ID)
        {
            listStatus.Add(EnumLineStatus.LinRi);
        }
        else
        {
            listStatus.Add(ConvertEarthlyBranchRelationToEnum(rDay));
            if (DefaultValue.EarthlyBranchSixSuit[dayEarthlyBranch.ID] == oLineEarthlyBranchIndex)
            {
                listStatus.Add(EnumLineStatus.RiHe);
            }
        }

        //?????
        Action<Dictionary<int, int>, int, EnumLineStatus> actionDayMonthDeadTombDesperate = (dic, earthlyBranchIndex, @enum) =>
        {

            if (dic.ContainsKey(oLineEarthlyBranchIndex) &&
                    dic[oLineEarthlyBranchIndex] == earthlyBranchIndex)
            {
                listStatus.Add(@enum);
            }
        };

        Action<Dictionary<int, int>, EnumLineStatus> actionDayDeadTombDesperate = (dic, @enum) =>
        {
            actionDayMonthDeadTombDesperate(dic, dayEarthlyBranch.ID, @enum);
        };

        Action<Dictionary<int, int>, EnumLineStatus> actionMonthDeadTombDesperate = (dic, @enum) =>
        {
            actionDayMonthDeadTombDesperate(dic, monthEarthlyBranch.ID, @enum);
        };

        actionDayDeadTombDesperate(DefaultValue.FiveElementTombRepository, EnumLineStatus.RiMuKu);
        actionDayDeadTombDesperate(DefaultValue.FiveElementDead, EnumLineStatus.RiSi);
        actionDayDeadTombDesperate(DefaultValue.FiveElementDesperation, EnumLineStatus.RiJue);

        actionMonthDeadTombDesperate(DefaultValue.FiveElementTombRepository, EnumLineStatus.YueMuKu);
        actionMonthDeadTombDesperate(DefaultValue.FiveElementDead, EnumLineStatus.YueSi);
        actionMonthDeadTombDesperate(DefaultValue.FiveElementDesperation, EnumLineStatus.YueJue);

        //????????
        var lineExtensionStatus = AnalyzeLineExtension(rMonth, monthEarthlyBranch, originalLine, dayHeavenlyStem, dayEarthlyBranch, checkFuShen);
        listStatus = listStatus.Union(lineExtensionStatus).ToList();

        return listStatus;
    }


    public List<EnumLineStatus> AnalyzeLineExtension(EnumLingRelation rMonth, EarthlyBranch monthEarthlyBranch, Line oLine,
                                                     HeavenlyStem dayHeavenlyStem, EarthlyBranch dayEarthlyBranch, bool checkFuShen = false)
    {
        var listStatus = new List<EnumLineStatus>();

        var oDiZhiIndex = oLine.EarthlyBranch.ID;

        if (checkFuShen)
            oDiZhiIndex = oLine.EarthlyBranchAttached.ID;

        if (rMonth == EnumLingRelation.Wang || rMonth == EnumLingRelation.Xiang)
        {
            if (DefaultValue.EarthlyBranchSixInverse[dayEarthlyBranch.ID] == oDiZhiIndex)
            {
                listStatus.Add(EnumLineStatus.AnDong);
            }
        }
        else
        {
            var earthlyBranchName = checkFuShen ? oLine.EarthlyBranchAttached : oLine.EarthlyBranch;
            if ((monthEarthlyBranch.Name != earthlyBranchName.Name && dayEarthlyBranch.Name != earthlyBranchName.Name)
                    && IsXunKong(dayHeavenlyStem, dayEarthlyBranch, oLine, checkFuShen))
            {
                if (DefaultValue.EarthlyBranchSixInverse[dayEarthlyBranch.ID] == oDiZhiIndex)
                {
                    listStatus.Add(EnumLineStatus.ChongShi);
                }
            }
            else
            {
                if (DefaultValue.EarthlyBranchSixInverse[dayEarthlyBranch.ID] == oDiZhiIndex)
                {
                    listStatus.Add(EnumLineStatus.RiPo);
                }
            }
        }
        return listStatus;
    }

    public EnumLineStatus? AnalyzeLineXunKong(EarthlyBranch monthEarthlyBranch, HeavenlyStem dayHeavenlyStem,
                                              EarthlyBranch dayEarthlyBranch, Line oLine, bool isFuShen = false)
    {
        var builder = new StringBuilder();

        var monthEarthlyBranchCompare = !isFuShen ?
                monthEarthlyBranch.Name != oLine.EarthlyBranch.Name && dayEarthlyBranch.Name != oLine.EarthlyBranch.Name :
                monthEarthlyBranch.Name != oLine.EarthlyBranchAttached.Name && dayEarthlyBranch.Name != oLine.EarthlyBranchAttached.Name;

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

        Action<HashMap<Integer, Integer>, EnumLineStatus> actionHua = (dic, status) =>
        {
            if (dic.ContainsKey(oLine.EarthlyBranch.ID) && dic[oLine.EarthlyBranch.ID] == tLine.EarthlyBranch.ID)
            {
                listStatus.Add(status);
            }
        };
        //???
        actionHua(DefaultValue.FiveElementTombRepository, EnumLineStatus.HuaMuKu);
        //??
        actionHua(DefaultValue.FiveElementDesperation, EnumLineStatus.HuaJue);
        //??
        actionHua(DefaultValue.FiveElementDead, EnumLineStatus.HuaSi);
        //?
        actionHua(DefaultValue.FiveElementSick, EnumLineStatus.HuaBing);
        //???
        actionHua(DefaultValue.FiveElementGrow, EnumLineStatus.HuaZhangSheng);
        //???
        actionHua(DefaultValue.FiveElementBath, EnumLineStatus.HuaMuYu);
        //?
        actionHua(DefaultValue.FiveElementFoetus, EnumLineStatus.HuaTai);
        //?
        actionHua(DefaultValue.FiveElementRaise, EnumLineStatus.HuaYang);

        //???????????
        var rMonth = AnalyzeLineLingRelation(monthEarthlyBranch, tLine);
        var rDay = AnalyzeLineLingRelation(dayEarthlyBranch, tLine);
        var monthStatus = ConvertEarthlyBranchRelationToEnumChangedLine(rMonth, true);
        var dayStatus = ConvertEarthlyBranchRelationToEnumChangedLine(rDay);
        if (monthStatus.HasValue)
            listStatus.Add(monthStatus.Value);
        if (dayStatus.HasValue)
            listStatus.Add(dayStatus.Value);

        //??
        var yuQi = AnalyzeLineLingRelationYuQi(monthEarthlyBranch.ID, tLine);
        if (yuQi.HasValue)
        {
            listStatus.Add(EnumLineStatus.BianYaoYuQiYue);
        }

        return listStatus;
    }

    private EnumLineStatus getLineStatus(HashMap<Integer, Integer> source, EnumLineStatus enumLineStatus, Line oLine, Line tLine)
    {
        if (source.containsKey(oLine.getEarthlyBranch().getId()) && source.get(oLine.getEarthlyBranch().getId()) == tLine.getEarthlyBranch().getId())
        {
            return enumLineStatus;
        }
        else
            return null;
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
