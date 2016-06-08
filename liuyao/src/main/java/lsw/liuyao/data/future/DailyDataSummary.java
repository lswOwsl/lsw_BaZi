package lsw.liuyao.data.future;

import java.util.List;

/**
 * Created by swli on 4/26/2016.
 */
public class DailyDataSummary {

    public double openPrice;
    public double closePrice;
    public double highestPrice;
    public double lowestPrice;

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(double highestPrice) {
        this.highestPrice = highestPrice;
    }

    public double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public List<DailyData> getDailyDataList() {
        return dailyDataList;
    }

    public void setDailyDataList(List<DailyData> dailyDataList) {
        this.dailyDataList = dailyDataList;
    }

    public List<DailyData> dailyDataList;
}
