package lsw.model;

/**
 * Created by swli on 8/12/2015.
 */
//一、一爻动与日月组成三合局
//二、两爻动、包括暗动爻组成
//三、三爻动组成
//四、初爻与三爻和变爻组成
//五、四爻与六爻和变爻组成
//六、动爻与变爻和日或月组成
public enum EnumLineSuit {

    DynamicLineDayMonth,
    DynamicTwoLineDayOrMonth,
    DynamicThreeLine,
    DynamicFirstThirdAndResultLine,
    DynamicForthSixthAndResultLine,
    DynamicAndResultLineWithDayOrMonth

}
