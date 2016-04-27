package lsw.liuyao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import lsw.library.DateExt;
import lsw.library.SolarTerm;
import lsw.liuyao.common.DateTimePickerDialog;
import lsw.liuyao.data.Database;
import lsw.liuyao.data.future.DailyData;
import lsw.liuyao.data.future.DailyDataSummary;
import lsw.liuyao.data.future.FutureCodeSelectorDialog;
import lsw.liuyao.data.future.FuturePriceListAdapter;
import lsw.liuyao.data.future.SinaData;
import lsw.liuyao.data.future.SinaDataSummary;

/**
 * Created by swli on 4/19/2016.
 */
public class FuturePriceFragment extends Fragment {

    private static String StringDateFormat = "yyyy-MM-dd";
    private static final String ARG_SHAKE_DATE = "ARG_SHAKE_DATE";
    private static final String ARG_HEXAGRAM_ID = "ARG_HEXAGRAM_ID";
    private static final String ARG_SUM_BY_MONTH = "ARG_SUM_BY_MONTH";
    private String shakeDate;

    private TextView tvBeginDate, tvEndDate, tvFutureCode, tvBtnSearch, tvSummary, tvBtnImporData;
    private ListView listView;
    private SinaDataSummary sinaData;
    private Database database;

    private LinearLayout llCondtion;
    private LinearLayout llDateRange;

    int hexagramId;

    private boolean isShowCondition = true;
    private boolean summaryByMonth = false;

    public void setShowCondition(boolean isShowing)
    {
        isShowCondition = isShowing;
    }

    public void setSummaryByMonth(boolean summaryByMonth)
    {
        this.summaryByMonth = summaryByMonth;
    }

    public static FuturePriceFragment createFragment()
    {
        return createFragment(new DateExt().getFormatDateTime(), 0);
    }

    public static FuturePriceFragment createFragment(String shakeDate, int hexagramRowId) {

        FuturePriceFragment f = new FuturePriceFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_SHAKE_DATE, shakeDate);
        args.putInt(ARG_HEXAGRAM_ID,hexagramRowId);
        f.setArguments(args);
        return f;
    }

    public FuturePriceFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        database = new Database(getActivity());
        sinaData = new SinaDataSummary(getActivity());

        View v = inflater.inflate(R.layout.future_price_fragment, null);

        initialControls(v);

        return v;
    }

    private void initialControls(View v)
    {
        hexagramId = getArguments().getInt(ARG_HEXAGRAM_ID);
        shakeDate = getArguments().getString(ARG_SHAKE_DATE);

        llDateRange = (LinearLayout) v.findViewById(R.id.llDateRange);
        llCondtion = (LinearLayout) v.findViewById(R.id.llcondtion);
        tvBeginDate = (TextView) v.findViewById(R.id.tvBeginDate);
        tvEndDate = (TextView) v.findViewById(R.id.tvEndDate);
        tvFutureCode = (TextView) v.findViewById(R.id.tvFutureCode);
        tvBtnSearch = (TextView)v.findViewById(R.id.tvBtnSearch);
        tvBtnImporData = (TextView)v.findViewById(R.id.tvBtnImportData);

        tvSummary = (TextView)v.findViewById(R.id.tvSummary);

        listView = (ListView)v.findViewById(R.id.lvPrice);

        String dateForShow = new DateExt(shakeDate).getFormatDateTime(StringDateFormat);

        tvBeginDate.setText(dateForShow);
        tvEndDate.setText(dateForShow);

        bindActions();

        if(!this.summaryByMonth) {
            bindContent();
        }
        else
        {
            llDateRange.setVisibility(View.GONE);
            tvBtnImporData.setVisibility(View.GONE);
        }
    }

    private void bindContentByLunarMonth()
    {

    }

    private void bindContent()
    {
        List<DailyData> source = database.getDailyDataByHexagramId(hexagramId);
        if(source != null && source.size() >0) {
            tvFutureCode.setText(source.get(0).FutureCode);
            tvBeginDate.setText(source.get(0).DateTime);
            tvEndDate.setText(source.get(source.size() - 1).DateTime);
            DateExt bd = new DateExt(tvBeginDate.getText().toString(), StringDateFormat);
            DateExt ed = new DateExt(tvEndDate.getText().toString(), StringDateFormat);
            source = filterByDate(bd,ed,source);
            FuturePriceListAdapter adapter = new FuturePriceListAdapter(getActivity(), source);
            listView.setAdapter(adapter);

            setSummary(source);
        }

        if(!isShowCondition)
        {
            llCondtion.setVisibility(View.GONE);
        }
    }

    double highestPrice = 0, lowestPrice = 0, openPrice = 0, closePrice =0;

    List<DailyData> dailyDatas;

    private void setSummary(List<DailyData> dailyDatas)
    {
        openPrice = dailyDatas.get(0).OpeningPrice;
        closePrice = dailyDatas.get(dailyDatas.size() - 1).ClosingPrice;
        String summary = "开:" + String.format("%.2f", openPrice) +
                "收:" + String.format("%.2f", closePrice) +
                "-----开/收:" + String.format("%.2f", closePrice - openPrice) + "\n" +
                "<低/开>:" + String.format("%.2f", lowestPrice - openPrice) + "\n" +
                "高:" + String.format("%.2f", highestPrice) +
                "低:" + String.format("%.2f", lowestPrice) +
                "-----高/低:" + String.format("%.2f", highestPrice - lowestPrice);
        tvSummary.setText(summary);
    }

    private void searchAction()
    {
        new AlertDialog.Builder(getActivity()).setItems(searhMethodArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(i == 0)
                {
                    searchPriceByLunarYear();
                }

                if(i== 1)
                {
                    String[] arrayMonth = new String[12];
                    for(int j = 1; j <= 12; j++)
                    {
                        arrayMonth[j-1] = j + "月";
                    }
                    new AlertDialog.Builder(getActivity()).setItems(arrayMonth, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            searchPriceByMonth(i+1);
                        }
                    }).show();
                }

            }
        }).show();
    }

    private void searchPriceByMonth(int month)
    {
       sinaData.getWeeklyDailyDataByMonth(tvFutureCode.getText().toString(), month, new SinaData.IResult<HashMap<Pair<DateExt, DateExt>, DailyDataSummary>>() {
           @Override
           public void invoke(HashMap<Pair<DateExt, DateExt>, DailyDataSummary> pairDailyDataSummaryHashMap) {

               if (pairDailyDataSummaryHashMap != null && pairDailyDataSummaryHashMap.size() > 0) {
                   List<DailyData> datas = new ArrayList<DailyData>();
                   for (Pair<DateExt, DateExt> pair : pairDailyDataSummaryHashMap.keySet()) {
                       DailyData dailyData = new DailyData();
                       dailyData.BeginDate = pair.first;
                       dailyData.EndDate = pair.second;

                       dailyData.OpeningPrice = pairDailyDataSummaryHashMap.get(pair).getOpenPrice();
                       dailyData.ClosingPrice = pairDailyDataSummaryHashMap.get(pair).getClosePrice();
                       dailyData.HighestPrice = pairDailyDataSummaryHashMap.get(pair).getHighestPrice();
                       dailyData.LowestPrice = pairDailyDataSummaryHashMap.get(pair).getLowestPrice();
                       datas.add(dailyData);
                   }

                   Collections.sort(datas, new Comparator<DailyData>() {
                       @Override
                       public int compare(DailyData d1, DailyData d2) {
                           return d1.BeginDate.compareTo(d2.BeginDate).value();
                       }
                   });

                   DateExt beginDate = datas.get(0).BeginDate;
                   DateExt endDate = datas.get(datas.size() - 1).BeginDate;
                   datas = filterByDate(beginDate, endDate, datas);

                   FuturePriceListAdapter adapter = new FuturePriceListAdapter(getActivity(), datas);
                   listView.setAdapter(adapter);

                   setSummary(datas);
               }
           }
       });
    }

    private void searchPriceByLunarYear()
    {
        sinaData.getMonthlyDailyDataBySolarTerm(tvFutureCode.getText().toString(), new SinaData.IResult<HashMap<Pair<SolarTerm, SolarTerm>, DailyDataSummary>>() {
            @Override
            public void invoke(HashMap<Pair<SolarTerm, SolarTerm>, DailyDataSummary> pairDailyDataSummaryHashMap) {

                if (pairDailyDataSummaryHashMap != null && pairDailyDataSummaryHashMap.size() > 0) {
                    List<DailyData> datas = new ArrayList<DailyData>();
                    for (Pair<SolarTerm, SolarTerm> pair : pairDailyDataSummaryHashMap.keySet()) {
                        DailyData dailyData = new DailyData();
                        dailyData.BeginDate = pair.first.getSolarTermDate();
                        dailyData.EndDate = pair.second.getSolarTermDate();

                        dailyData.OpeningPrice = pairDailyDataSummaryHashMap.get(pair).getOpenPrice();
                        dailyData.ClosingPrice = pairDailyDataSummaryHashMap.get(pair).getClosePrice();
                        dailyData.HighestPrice = pairDailyDataSummaryHashMap.get(pair).getHighestPrice();
                        dailyData.LowestPrice = pairDailyDataSummaryHashMap.get(pair).getLowestPrice();
                        datas.add(dailyData);
                    }

                    Collections.sort(datas, new Comparator<DailyData>() {
                        @Override
                        public int compare(DailyData d1, DailyData d2) {
                            return d1.BeginDate.compareTo(d2.BeginDate).value();
                        }
                    });

                    DateExt beginDate = datas.get(0).BeginDate;
                    DateExt endDate = datas.get(datas.size() - 1).BeginDate;
                    datas = filterByDate(beginDate, endDate, datas);

                    FuturePriceListAdapter adapter = new FuturePriceListAdapter(getActivity(), datas);
                    listView.setAdapter(adapter);

                    setSummary(datas);
                }
            }
        });
    }

    String [] searhMethodArray = new String[]{"阴历按节气月查询","阳历按周查询"};

    private void bindActions()
    {

        tvBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lowestPrice = highestPrice = openPrice = closePrice = 0;

                if (summaryByMonth) {

                    searchAction();

                } else {

                    sinaData.getResponeFromURL(SinaData.Sina_Url + SinaData.Sina_Day_Method + tvFutureCode.getText().toString(), new SinaData.IResult<ArrayList<DailyData>>() {
                        @Override
                        public void invoke(ArrayList<DailyData> s) {
                            dailyDatas = s;
                            DateExt bd = new DateExt(tvBeginDate.getText().toString(), StringDateFormat);
                            DateExt ed = new DateExt(tvEndDate.getText().toString(), StringDateFormat);

                            dailyDatas = filterByDate(bd, ed, s);
                            FuturePriceListAdapter adapter = new FuturePriceListAdapter(getActivity(), dailyDatas);
                            listView.setAdapter(adapter);

                            if (dailyDatas.size() > 0) {
                                setSummary(dailyDatas);
                            } else {
                                tvSummary.setText("");
                            }
                        }
                    });
                }
            }
        });
//        sinaData.getResponeFromURL(SinaData.Sina_Url + SinaData.Sina_OneHour_Method + "RB1601", new SinaData.IResult< ArrayList<DailyData>>() {
//            @Override
//            public void invoke( ArrayList<DailyData> s) {
//                String rr = "";
//            }
//        });


        tvBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateExt beginDateExt = new DateExt(tvBeginDate.getText().toString(), StringDateFormat);
                DateTimePickerDialog pickerDialog = new DateTimePickerDialog(beginDateExt,getActivity(),false);
                pickerDialog.show();
                pickerDialog.setCallBack(new DateTimePickerDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                       tvBeginDate.setText(dateExt.getFormatDateTime(StringDateFormat));
                    }
                });
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateExt endDateExt = new DateExt(tvEndDate.getText().toString(), StringDateFormat);
                DateTimePickerDialog pickerDialog = new DateTimePickerDialog(endDateExt,getActivity(),false);
                pickerDialog.show();
                pickerDialog.setCallBack(new DateTimePickerDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                        tvEndDate.setText(dateExt.getFormatDateTime(StringDateFormat));
                    }
                });
            }
        });

        tvFutureCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FutureCodeSelectorDialog selectorDialog = new FutureCodeSelectorDialog(tvFutureCode.getText().toString(),getActivity());
                selectorDialog.setCallBack(new FutureCodeSelectorDialog.ICallBack() {
                    @Override
                    public void invoke(String code) {
                        tvFutureCode.setText(code);
                    }
                });
                selectorDialog.show();
            }
        });

        tvBtnImporData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dailyDatas!= null && dailyDatas.size() > 0)
                {
                    database.insertFutureData(dailyDatas);
                    Toast.makeText(getActivity(),"导入价格数据成功!",Toast.LENGTH_SHORT).show();
                    //tvBtnImporData.setEnabled(false);
                }
            }
        });
    }

    private List<DailyData> filterByDate(DateExt beginDate, DateExt endDate, List<DailyData> dailyDatas)
    {
        ArrayList<DailyData> result = new ArrayList<DailyData>();
        for(DailyData dailyData : dailyDatas)
        {
            if(lowestPrice == 0)
                lowestPrice = dailyData.LowestPrice;

            DateExt tempDateExt;

            //按节气查询时给Date赋值了，按日的查询没有
            if(dailyData.BeginDate != null)
            {
                tempDateExt = dailyData.BeginDate;
            }
            else
            {
                tempDateExt = new DateExt(dailyData.DateTime, StringDateFormat);
            }
            if((tempDateExt.compareTo(beginDate) == DateExt.EnumDateCompareResult.Later || tempDateExt.compareTo(beginDate) == DateExt.EnumDateCompareResult.Equal) &&
                    (tempDateExt.compareTo(endDate) == DateExt.EnumDateCompareResult.Earlier || tempDateExt.compareTo(endDate) == DateExt.EnumDateCompareResult.Equal)    )
            {
                if(dailyData.HighestPrice > highestPrice)
                    highestPrice = dailyData.HighestPrice;
                if(dailyData.LowestPrice < lowestPrice)
                    lowestPrice = dailyData.LowestPrice;
                dailyData.HexagramId = hexagramId;
                dailyData.FutureCode = tvFutureCode.getText().toString();
                result.add(dailyData);
            }
        }
        return result;
    }
}
