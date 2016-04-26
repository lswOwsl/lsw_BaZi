package lsw.liuyao.data.future;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lsw.library.BaZiHelper;
import lsw.library.DateExt;
import lsw.library.SolarTerm;

/**
 * Created by swli on 4/26/2016.
 */
public class SinaDataSummary extends SinaData {

    public SinaDataSummary(Context context) {
        super(context);
    }

    public HashMap<Pair<SolarTerm,SolarTerm>,DailyDataSummary> getDailyDataBySolarTerm(int year, final String futureCode)
    {
        final HashMap<Pair<SolarTerm,SolarTerm>,DailyDataSummary> result = new HashMap<Pair<SolarTerm, SolarTerm>, DailyDataSummary>();

        final ArrayList<SolarTerm> solarTerms = BaZiHelper.getSolarTermsInLunarYear(year);

        getResponeFromURL(Sina_Url + Sina_Day_Method + futureCode, new IResult<ArrayList<DailyData>>() {
            @Override
            public void invoke(ArrayList<DailyData> dailyDatas) {

                for (int i = 0; i < solarTerms.size(); i++) {

                    if (i + 1 <= solarTerms.size()) {
                        SolarTerm beginDate = solarTerms.get(i);
                        SolarTerm endDate = solarTerms.get(i + 1);

                        Pair<SolarTerm,SolarTerm> pair = new Pair<SolarTerm, SolarTerm>(beginDate,endDate);
                        DailyDataSummary filterDate = filterByDate(beginDate.getSolarTermDate(),endDate.getSolarTermDate(),dailyDatas,futureCode);
                        result.put(pair,filterDate);
                    }

                }
            }
        });

        return result;
    }

    private DailyDataSummary filterByDate(DateExt beginDate, DateExt endDate, List<DailyData> dailyDatas, String futureCode)
    {
        DailyDataSummary result = new DailyDataSummary();

        double highestPrice = 0, lowestPrice = 0;
        String StringDateFormat = "yyyy-MM-dd";

        ArrayList<DailyData> dailyDataArrayList = new ArrayList<DailyData>();
        for(DailyData dailyData : dailyDatas)
        {
            if(lowestPrice == 0)
                lowestPrice = dailyData.LowestPrice;

            DateExt tempDateExt = new DateExt(dailyData.DateTime, StringDateFormat);
            if((tempDateExt.compareTo(beginDate) == DateExt.EnumDateCompareResult.Later || tempDateExt.compareTo(beginDate) == DateExt.EnumDateCompareResult.Equal) &&
                    (tempDateExt.compareTo(endDate) == DateExt.EnumDateCompareResult.Earlier || tempDateExt.compareTo(endDate) == DateExt.EnumDateCompareResult.Equal)    )
            {
                if(dailyData.HighestPrice > highestPrice)
                    highestPrice = dailyData.HighestPrice;
                if(dailyData.LowestPrice < lowestPrice)
                    lowestPrice = dailyData.LowestPrice;

                dailyData.FutureCode = futureCode;
                dailyDataArrayList.add(dailyData);
            }
        }

        result.setDailyDataList(dailyDataArrayList);
        result.setHighestPrice(highestPrice);
        result.setLowestPrice(lowestPrice);
        result.setOpenPrice(dailyDataArrayList.get(0).OpeningPrice);
        result.setClosePrice(dailyDataArrayList.get(dailyDataArrayList.size()-1).ClosingPrice);

        return result;
    }

}
