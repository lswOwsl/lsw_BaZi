package lsw.lunar_calendar.data_source;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import lsw.lunar_calendar.R;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class CalendarTitleAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public CalendarTitleAdapter(LayoutInflater layoutInflater)
    {
        source = getOneMonthTitle();
        this.layoutInflater = layoutInflater;
    }

    public String[] getOneMonthTitle() {
        return new String[]{"一","二","三","四","五","六","日"};
    }

    private String[] source;

    @Override
    public int getCount() {
        return source.length;
    }

    @Override
    public Object getItem(int i) {
        return source[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemHolder controls;
        if (view == null) {
            controls = new ItemHolder();
            view = layoutInflater.inflate(R.layout.activity_day_title, null);
            controls.tvTitle = (TextView) view.findViewById(R.id.tvDayTitle);
            view.setTag(controls);
        } else {
            controls = (ItemHolder) view.getTag();
        }

        controls.tvTitle.setText(source[i]);

        return view;
    }

    class ItemHolder {
        public TextView tvTitle;
    }


}
