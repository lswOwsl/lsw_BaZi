package lsw.model;

import java.io.Serializable;

/**
 * Created by swli on 8/10/2015.
 */

public class Line implements Serializable
{
    private int position;
    private EnumLineSymbol lineSymbol;

    private EnumFiveElement fiveElement;
    private EnumSixRelation sixRelation;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public EnumLineSymbol getLineSymbol() {
        return lineSymbol;
    }

    public void setLineSymbol(EnumLineSymbol lineSymbol) {
        this.lineSymbol = lineSymbol;
    }

    public EnumFiveElement getFiveElement() {
        return fiveElement;
    }

    public void setFiveElement(EnumFiveElement fiveElement) {
        this.fiveElement = fiveElement;
    }

    public EnumSixRelation getSixRelation() {
        return sixRelation;
    }

    public void setSixRelation(EnumSixRelation sixRelation) {
        this.sixRelation = sixRelation;
    }

    public EarthlyBranch getEarthlyBranch() {
        return earthlyBranch;
    }

    public void setEarthlyBranch(EarthlyBranch earthlyBranch) {
        this.earthlyBranch = earthlyBranch;
    }

    public EnumSixRelation getSixRelationAttached() {
        return sixRelationAttached;
    }

    public void setSixRelationAttached(EnumSixRelation sixRelationAttached) {
        this.sixRelationAttached = sixRelationAttached;
    }

    public EnumFiveElement getFiveElementAttached() {
        return fiveElementAttached;
    }

    public void setFiveElementAttached(EnumFiveElement fiveElementAttached) {
        this.fiveElementAttached = fiveElementAttached;
    }

    public EarthlyBranch getEarthlyBranchAttached() {
        return earthlyBranchAttached;
    }

    public void setEarthlyBranchAttached(EarthlyBranch earthlyBranchAttached) {
        this.earthlyBranchAttached = earthlyBranchAttached;
    }

    private EarthlyBranch earthlyBranch;

    private EnumSixRelation sixRelationAttached;
    private EnumFiveElement fiveElementAttached;
    private EarthlyBranch earthlyBranchAttached;

}
