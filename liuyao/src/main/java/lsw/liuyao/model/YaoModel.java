package lsw.liuyao.model;

import java.io.Serializable;

import lsw.liuyao.common.EnumYaoType;

/**
 * Created by swli on 8/7/2015.
 */
public class YaoModel  implements Serializable {

    private String attachedYaoDetail;
    private EnumYaoType enumYaoType;
    private String yaoDetail;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getAttachedYaoDetail() {
        return attachedYaoDetail;
    }

    public void setAttachedYaoDetail(String attachedYaoDetail) {
        this.attachedYaoDetail = attachedYaoDetail;
    }

    public EnumYaoType getEnumYaoType() {
        return enumYaoType;
    }

    public void setEnumYaoType(EnumYaoType enumYaoType) {
        this.enumYaoType = enumYaoType;
    }

    public String getYaoDetail() {
        return yaoDetail;
    }

    public void setYaoDetail(String yaoDetail) {
        this.yaoDetail = yaoDetail;
    }
}
