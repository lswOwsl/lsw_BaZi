package lsw.model;

import java.io.Serializable;

/**
 * Created by swli on 8/10/2015.
 */

public class Line implements Serializable
{
    public int position;
    public EnumLineSymble symble;

    public EnumFiveElement fiveElement;
    public EnumSixRelation sixRelation;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public EnumLineSymble getSymble() {
        return symble;
    }

    public void setSymble(EnumLineSymble symble) {
        this.symble = symble;
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

    public EarthlyBranch earthlyBranch;

    public EnumSixRelation sixRelationAttached;
    public EnumFiveElement fiveElementAttached;
    public EarthlyBranch earthlyBranchAttached;

}
