package lsw.liuyao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lsw.library.DateExt;
import lsw.liuyao.common.DateTimePickerDialog;
import lsw.liuyao.data.Database;
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
    private static final String ARG_HEXAGRAM_ID = "ARG_HEXAGRAM_ID";
    private String shakeDate;

    private TextView tvBeginDate, tvEndDate, tvFutureCode, tvBtnSearch, tvSummary, tvBtnImporData;
    private ListView listView;
    private SinaData sinaData;
    private Database database;

    private LinearLayout llCondtion;

    int hexagramId;

    private boolean isShowCondtion = true;

    public void setShowCondtion(boolean isShowing)
    {
        isShowCondtion = isShowing;
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
        sinaData = new SinaData(getActivity());

        View v = inflater.inflate(R.layout.future_price_fragment, null);

        initialControls(v);

        return v;
    }

    private void initialControls(View v)
    {
        hexagramId = getArguments().getInt(ARG_HEXAGRAM_ID);
        shakeDate = getArguments().getString(ARG_SHAKE_DATE);

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

        bindContent();
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

        if(!isShowCondtion)
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
                "高:" + String.format("%.2f", highestPrice) +
                "低:" + String.format("%.2f", lowestPrice) +
                "-----高/低:" + String.format("%.2f", highestPrice - lowestPrice);
        tvSummary.setText(summary);
    }

    private void bindActions()
    {
        tvBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sinaData.getResponeFromURL(SinaData.Sina_Url + SinaData.Sina_Day_Method + tvFutureCode.getText().toString(), new SinaData.IResult<ArrayList<DailyData>>() {
                    @Override
                    public void invoke(ArrayList<DailyData> s) {
                        dailyDatas = s;
                        DateExt bd = new DateExt(tvBeginDate.getText().toString(), StringDateFormat);
                        DateExt ed = new DateExt(tvEndDate.getText().toString(), StringDateFormat);

                        dailyDatas = filterByDate(bd,ed,s);
                        FuturePriceListAdapter adapter = new FuturePriceListAdapter(getActivity(),dailyDatas);
                        listView.setAdapter(adapter);

                        if(dailyDatas.size() > 0) {
                            setSummary(dailyDatas);
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

            DateExt tempDateExt = new DateExt(dailyData.DateTime, StringDateFormat);
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
