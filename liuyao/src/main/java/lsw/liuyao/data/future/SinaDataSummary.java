package lsw.liuyao.data.future;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void getDailyDataBySolarTerm(final String futureCode, final IResult<HashMap<Pair<SolarTerm, SolarTerm>, DailyDataSummary>> complete) {
        final HashMap<Pair<SolarTerm, SolarTerm>, DailyDataSummary> result = new HashMap<Pair<SolarTerm, SolarTerm>, DailyDataSummary>();
        final ArrayList<SolarTerm> solarTerms;
        int year = new DateExt().getYear();
        //小于4说明 不是查某一期的而是查连续指数，那么就从当前日期往前倒两年
        if(futureCode.length() < 4)
        {
            solarTerms = BaZiHelper.getSolarTermsInLunarYear(year);
            solarTerms.addAll(BaZiHelper.getSolarTermsInLunarYear(year-1));
            solarTerms.addAll(BaZiHelper.getSolarTermsInLunarYear(year-2));
        }
        else
        {
            final String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(futureCode);
            year = Integer.valueOf("20" + m.replaceAll("").substring(0, 2));

            solarTerms = BaZiHelper.getSolarTermsInLunarYear(year);
        }

        getResponeFromURL(Sina_Url + Sina_Day_Method + futureCode, new IResult<ArrayList<DailyData>>() {
            @Override
            public void invoke(ArrayList<DailyData> dailyDatas) {

                for (int i = 0; i < solarTerms.size(); i++) {

                    if (i + 1 < solarTerms.size()) {
                        SolarTerm beginDate = solarTerms.get(i);
                        SolarTerm endDate = solarTerms.get(i + 1);

                        Pair<SolarTerm, SolarTerm> pair = new Pair<SolarTerm, SolarTerm>(beginDate, endDate);
                        DailyDataSummary filterDate = filterByDate(beginDate.getSolarTermDate(), endDate.getSolarTermDate(), dailyDatas, futureCode);
                        if(filterDate.getDailyDataList().size() > 0)
                            result.put(pair, filterDate);
                    }
                }

                if(complete != null)
                    complete.invoke(result);
            }
        });
    }

    private DailyDataSummary filterByDate(DateExt beginDate, DateExt endDate, List<DailyData> dailyDatas, String futureCode)
    {
        DailyDataSummary result = new DailyDataSummary();

        double highestPrice = 0, lowestPrice = 0;
        String StringDateFormat = "yyyy-MM-dd";

        ArrayList<DailyData> dailyDataArrayList = new ArrayList<DailyData>();
        for(DailyData dailyData : dailyDatas)
        {
            DateExt tempDateExt = new DateExt(dailyData.DateTime, StringDateFormat);
            if((tempDateExt.compareTo(beginDate) == DateExt.EnumDateCompareResult.Later || tempDateExt.compareTo(beginDate) == DateExt.EnumDateCompareResult.Equal) &&
                    (tempDateExt.compareTo(endDate) == DateExt.EnumDateCompareResult.Earlier || tempDateExt.compareTo(endDate) == DateExt.EnumDateCompareResult.Equal)    )
            {
                if(lowestPrice == 0)
                    lowestPrice = dailyData.LowestPrice;

                if(dailyData.HighestPrice > highestPrice)
                    highestPrice = dailyData.HighestPrice;
                if(dailyData.LowestPrice < lowestPrice)
                    lowestPrice = dailyData.LowestPrice;

                dailyData.FutureCode = futureCode;
                dailyDataArrayList.add(dailyData);
            }
        }


        result.setDailyDataList(dailyDataArrayList);

        if(dailyDataArrayList.size() > 0) {
            result.setHighestPrice(highestPrice);
            result.setLowestPrice(lowestPrice);
            result.setOpenPrice(dailyDataArrayList.get(0).OpeningPrice);
            result.setClosePrice(dailyDataArrayList.get(dailyDataArrayList.size() - 1).ClosingPrice);
        }
        return result;
    }

}
