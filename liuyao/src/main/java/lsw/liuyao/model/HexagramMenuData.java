package lsw.liuyao.model;

import java.util.List;

/**
 * Created by swli on 4/25/2016.
 */
public class HexagramMenuData {

    private String name;
    private String condition;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<HexagramMenuData> getSecondLevelData() {
        return secondLevelData;
    }

    public void setSecondLevelData(List<HexagramMenuData> secondLevelData) {
        this.secondLevelData = secondLevelData;
    }

    private List<HexagramMenuData> secondLevelData;
}

