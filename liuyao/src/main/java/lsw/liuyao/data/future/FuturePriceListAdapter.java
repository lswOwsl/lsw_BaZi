package lsw.liuyao.data.future;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import lsw.liuyao.R;

/**
 * Created by swli on 4/20/2016.
 */
public class FuturePriceListAdapter extends BaseAdapter {

    ArrayList<DailyData> dailyDatas;
    private LayoutInflater layoutInflater;

    public FuturePriceListAdapter(Context context, ArrayList<DailyData> dailyDatas)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.dailyDatas = dailyDatas;
    }

    @Override
    public int getCount() {
        return dailyDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return dailyDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Controls controls;
        //if (view == null) {
            controls = new Controls();
            view = layoutInflater.inflate(R.layout.future_price_list_item,null);

            controls.tvDate = (TextView) view.findViewById(R.id.tvDate);
            controls.tvOpenPrice = (TextView) view.findViewById(R.id.tvOpenPrice);
            controls.tvClosePrice = (TextView) view.findViewById(R.id.tvClosePrice);
            controls.tvResultOpenClose = (TextView) view.findViewById(R.id.tvResultOpenClose);
            controls.tvHighestPrice = (TextView) view.findViewById(R.id.tvHighestPrice);
            controls.tvLowestPrice = (TextView) view.findViewById(R.id.tvLowestPrice);
            controls.tvResultHighestLowest = (TextView) view.findViewById(R.id.tvResultHighestLowest);

            DailyData dailyData = dailyDatas.get(i);

            controls.tvDate.setText(dailyData.DateTime);

            controls.tvOpenPrice.setText("开:" + String.format("%.2f",dailyData.OpeningPrice));
            controls.tvClosePrice.setText("收:" +String.format("%.2f", dailyData.ClosingPrice));
            controls.tvResultOpenClose.setText(String.format("%.2f",dailyData.ClosingPrice - dailyData.OpeningPrice));

            controls.tvHighestPrice.setText("高:" +String.format("%.2f",dailyData.HighestPrice));
            controls.tvLowestPrice.setText("低:" +String.format("%.2f",dailyData.LowestPrice));
            controls.tvResultHighestLowest.setText(String.format("%.2f",dailyData.HighestPrice - dailyData.LowestPrice));


            view.setTag(controls);
//        } else {
//            controls = (Controls) view.getTag();
//        }

        return view;
    }

    public final class Controls
    {
        public TextView tvDate, tvOpenPrice, tvClosePrice,tvResultOpenClose, tvHighestPrice, tvLowestPrice, tvResultHighestLowest;
    }
}
