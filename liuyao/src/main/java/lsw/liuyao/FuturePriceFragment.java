package lsw.liuyao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import lsw.library.DateExt;
import lsw.liuyao.common.DateTimePickerDialog;
import lsw.liuyao.data.future.DailyData;
import lsw.liuyao.data.future.FutureCodeSelectorDialog;
import lsw.liuyao.data.future.FuturePriceListAdapter;
import lsw.liuyao.data.future.SinaData;

/**
 * Created by swli on 4/19/2016.
 */
public class FuturePriceFragment extends Fragment {

    private static String StringDateFormat = "yyyy-MM-dd";
    private static final String ARG_SHAKE_DATE = "ARG_SHAKE_DATE";
    private String shakeDate;

    private TextView tvBeginDate, tvEndDate, tvFutureCode, tvBtnSearch, tvSummary;
    private ListView listView;
    private SinaData sinaData;

    public static FuturePriceFragment createFragment(String shakeDate) {

        FuturePriceFragment f = new FuturePriceFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_SHAKE_DATE, shakeDate);
        f.setArguments(args);
        return f;
    }

    public FuturePriceFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sinaData = new SinaData(getActivity());

        View v = inflater.inflate(R.layout.future_price_fragment, null);

        initialControls(v);

        return v;
    }

    private void initialControls(View v)
    {
        shakeDate = getArguments().getString(ARG_SHAKE_DATE);

        tvBeginDate = (TextView) v.findViewById(R.id.tvBeginDate);
        tvEndDate = (TextView) v.findViewById(R.id.tvEndDate);
        tvFutureCode = (TextView) v.findViewById(R.id.tvFutureCode);
        tvBtnSearch = (TextView)v.findViewById(R.id.tvBtnSearch);

        tvSummary = (TextView)v.findViewById(R.id.tvSummary);

        listView = (ListView)v.findViewById(R.id.lvPrice);

        String dateForShow = new DateExt(shakeDate).getFormatDateTime(StringDateFormat);

        tvBeginDate.setText(dateForShow);
        tvEndDate.setText(dateForShow);

        bindActions();
    }

    double highestPrice = 0, lowestPrice = 0, openPrice = 0, closePrice =0;

    private void bindActions()
    {
        tvBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sinaData.getResponeFromURL(SinaData.Sina_Url + SinaData.Sina_Day_Method + tvFutureCode.getText().toString(), new SinaData.IResult<ArrayList<DailyData>>() {
                    @Override
                    public void invoke(ArrayList<DailyData> s) {
                        ArrayList<DailyData> dailyDatas = s;

                        DateExt bd = new DateExt(tvBeginDate.getText().toString(), StringDateFormat);
                        DateExt ed = new DateExt(tvEndDate.getText().toString(), StringDateFormat);

                        dailyDatas = filterByDate(bd,ed,s);
                        FuturePriceListAdapter adapter = new FuturePriceListAdapter(getActivity(),dailyDatas);
                        listView.setAdapter(adapter);

                        if(dailyDatas.size() > 0) {
                            openPrice = dailyDatas.get(0).OpeningPrice;
                            closePrice = dailyDatas.get(dailyDatas.size() - 1).ClosingPrice;
                            String summary = "开:" + String.format("%.2f", openPrice) +
                                    "收:" + String.format("%.2f", closePrice) +
                                    "-----开/收:" + String.format("%.2f", closePrice - openPrice) + "\n" +
                                    "高:" + String.format("%.2f", highestPrice) +
                                    "低:" + String.format("%.2f", lowestPrice) +
                                    "-----高/低:" + String.format("%.2f", highestPrice - lowestPrice);
                            tvSummary.setText(summary);
                        }
                        else
                        {
                            tvSummary.setText("");
                        }
                    }
                });
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
    }

    private ArrayList<DailyData> filterByDate(DateExt beginDate, DateExt endDate, ArrayList<DailyData> dailyDatas)
    {
        ArrayList<DailyData> result = new ArrayList<DailyData>();
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

                result.add(dailyData);
            }
        }
        return result;
    }
}
