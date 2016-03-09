package lsw.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lsw_wsl on 8/12/15.
 */
public class AnalyzeResultCollection {

    private HashMap<Pair<Integer, EnumSixRelation>, ArrayList<EnumLineStatus>> lineLevelResult;

    private ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> hexagramLevelThreeSuitResult;

    private HashMap<Line, ArrayList<Line>> lineLevelTombRepository;

    public EarthlyBranchDay parseDayBranchFromObject(Object o)
    {
        return (EarthlyBranchDay)o;
    }

    public EarthlyBranchMonth parseMonthBranchFromObject(Object o)
    {
        return (EarthlyBranchMonth) o;
    }

    public Line parseLineFromObject(Object o)
    {
        return (Line)o;
    }

    public HashMap<Pair<Integer, EnumSixRelation>, ArrayList<EnumLineStatus>> getLineLevelResult() {
        return lineLevelResult;
    }

    public void setLineLevelResult(HashMap<Pair<Integer, EnumSixRelation>, ArrayList<EnumLineStatus>> lineLevelResult) {
        this.lineLevelResult = lineLevelResult;
    }

    public ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> getHexagramLevelThreeSuitResult() {
        return hexagramLevelThreeSuitResult;
    }

    public void setHexagramLevelThreeSuitResult(ArrayList<Pair<EnumSixRelation, ArrayList<Object>>> hexagramLevelThreeSuitResult) {
        this.hexagramLevelThreeSuitResult = hexagramLevelThreeSuitResult;
    }

    public HashMap<Line, ArrayList<Line>> getLineLevelTombRepository() {
        return lineLevelTombRepository;
    }

    public void setLineLevelTombRepository(HashMap<Line, ArrayList<Line>> lineLevelTombRepository) {
        this.lineLevelTombRepository = lineLevelTombRepository;
    }
}
