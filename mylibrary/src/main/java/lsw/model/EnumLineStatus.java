package lsw.model;

/**
 * Created by swli on 8/12/2015.
 */
public enum EnumLineStatus {

    Dong("动"),
    HuaTui("化退"),
    HuaJin("化进"),
    HuaPo("化破"),
    HuaKong ("化空"),
    HuaPoErBuPo("而不破"),
    HuiTouSheng("回头生"),
    HuiTouKe("回头克"),
    HuiTouHe("回头合"),
    HuiTouChong("回头冲"),
    HuaZhangSheng("化长生"),
    HuaMuYu("化沐浴"),
    //冠带,临官,帝旺,衰
    HuaBing("化病"),
    HuaSi("化死"),
    HuaMuKu("化墓库"),
    HuaJue ("化绝"),
    HuaTai("化胎"),
    HuaYang("化养"),
    BianYaoRiHe("变爻日合"),
    BianYaoYueHe("变爻月合"),
    BianYaoRiChong("变爻日冲"),
    BianYaoYueChong("变爻月冲"),
    //可以继续添加其它详细的
    BianYaoYueWang("变爻月旺"),
    BianYaoYueKe("变爻月克"),
    BianYaoRiWang("变爻日旺"),
    BianYaoRiKe("变爻日克"),
    BianYaoYuQiYue("变爻月余气"),
    Fu("伏"),
    Kong("空"),
    ChongShi("冲实"),
    YuePo("月破"),
    RiPo("日破"),
    YueKe("月克"),
    RiKe("日克"),
    YueQiu("月囚"),
    RiQiu("日囚"),
    YueWang("月旺"),
    RiWang("日旺"),
    YueXiang("月相"),
    RiXiang("日相"),
    YueXiu ("月休"),
    RiXiu("日休"),
    YueHe ("月合"),
    RiHe("日合"),
    LinRi("临日"),
    LinYue("临月"),
    RiChong("日冲"),
    YueChong("月冲"),
    RiMuKu("日墓库"),
    YueMuKu("月墓库"),
    YueJue("绝在月"),
    YueSi("死在月"),
    RiJue("绝在日"),
    RiSi("死在日"),
    YuQiYue("月余气"),
    AnDong("暗动");

    private String text;
    EnumLineStatus(String text) {
        this.text = text;
    }

    public String toString() {
        return String.format(text);
    }
}
