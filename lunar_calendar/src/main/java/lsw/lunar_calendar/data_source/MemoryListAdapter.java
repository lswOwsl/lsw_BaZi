package lsw.lunar_calendar.data_source;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import lsw.library.DateExt;
import lsw.lunar_calendar.R;
import lsw.lunar_calendar.model.EventRecord;

/**
 * Created by swli on 6/3/2016.
 */
public class MemoryListAdapter extends BaseAdapter {

    private List<EventRecord> data;
    private Context context;
    private LayoutInflater layoutInflater;

    private final static String Date_Format= "yyyy-MM-dd";

    public MemoryListAdapter(Context context, List<EventRecord> data)
    {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Controls controls;
        if (view == null) {
            controls = new Controls();

            view = layoutInflater.inflate(R.layout.memory_list_item, null);
            controls.tvBeginTime = (TextView) view.findViewById(R.id.tvBeginTime);
            controls.tvEndTime = (TextView) view.findViewById(R.id.tvEndTime);
            controls.tvLunarTime = (TextView) view.findViewById(R.id.tvlunarTime);
            controls.tvForecast = (TextView) view.findViewById(R.id.tvForecast);
            controls.tvNote = (TextView) view.findViewById(R.id.tvNote);

            view.setTag(controls);
        } else {
            controls = (Controls) view.getTag();
        }

        final EventRecord eventRecord = data.get(i);

        controls.tvBeginTime.setText(eventRecord.getBeginDateExt().getFormatDateTime(Date_Format));
        controls.tvEndTime.setText(eventRecord.getEndDateExt().getFormatDateTime(Date_Format));
        controls.tvLunarTime.setText(eventRecord.getLunarTime());
        controls.tvForecast.setText(eventRecord.getAnalyzeResult());
        controls.tvNote.setText(eventRecord.getActualResult());

        return view;
    }

    public final class Controls
    {
        public TextView tvBeginTime;
        public TextView tvEndTime;
        public TextView tvLunarTime;
        public TextView tvForecast;
        public TextView tvNote;
    }
}
