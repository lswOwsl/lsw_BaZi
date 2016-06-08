package lsw.model;

/**
 * Created by swli on 8/10/2015.
 */
public enum EnumLineSymbol {

    Yin(8),
    Yang(7),
    LaoYin(6),
    LaoYang(9);

    private int value = 0;

    private EnumLineSymbol(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static EnumLineSymbol valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 6:
                return LaoYin;
            case 9:
                return LaoYang;
            case 7:
                return Yang;
            case 8:
                return Yin;
            default:
                return null;
        }
    }

    public int value() {
        return this.value;
    }

    public int convertedValue()
    {
        if(this.value == 9)
            return 7;
        else if(this.value == 6)
            return 8;
        else
            return this.value;
    }

    public int changedValue()
    {
        if(this.value == 9)
            return 8;
        else if(this.value == 6)
            return 7;
        else
            return this.value;
    }
}
