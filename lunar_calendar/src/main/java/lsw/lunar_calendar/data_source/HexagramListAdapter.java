package lsw.lunar_calendar.data_source;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import lsw.library.DateExt;
import lsw.library.LunarCalendar;
import lsw.library.StringHelper;
import lsw.lunar_calendar.R;
import lsw.lunar_calendar.model.HexagramDataRow;

/**
 * Created by swli on 5/6/2016.
 */
public class HexagramListAdapter extends BaseAdapter {

    List<HexagramDataRow> rows;
    private LayoutInflater layoutInflater;

    public HexagramListAdapter(Context context, List<HexagramDataRow> rows)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.rows = rows;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Object getItem(int i) {
        return rows.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        ViewHolder holder;
        HexagramDataRow item = rows.get(i);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.hexagram_daily_list_item, null);
            holder = new ViewHolder();
            holder.tvOriginalName = (TextView) view.findViewById(R.id.tvOriginalName);
            holder.tvChangedName = (TextView) view.findViewById(R.id.tvChangedName);
            holder.tvNote = (TextView) view.findViewById(R.id.tvNote);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvOriginalName.setText("主卦: " + item.getOriginalName());
        String changedName = item.getChangedName();
        if (!StringHelper.isNullOrEmpty(changedName)) {
            holder.tvChangedName.setText("变卦: " + item.getChangedName());
        } else {
            holder.tvChangedName.setText("");
        }

        holder.tvNote.setText(item.getNote());
        holder.tvNote.setSelected(true);

        return view;
    }

    static class ViewHolder {
        TextView tvDate;
        TextView tvOriginalName;
        TextView tvChangedName;
        TextView tvNote;
    }
}
