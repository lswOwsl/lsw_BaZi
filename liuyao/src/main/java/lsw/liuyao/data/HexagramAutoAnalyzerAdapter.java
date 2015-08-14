package lsw.liuyao.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lsw.liuyao.R;

/**
 * Created by swli on 8/14/2015.
 */
public class HexagramAutoAnalyzerAdapter extends BaseAdapter {

    private ArrayList<String> source;
    private LayoutInflater layoutInflater;

    public HexagramAutoAnalyzerAdapter(ArrayList<String> source, Context context)
    {
        this.source = source;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return source.size();
    }

    @Override
    public Object getItem(int i) {
        return source.get(i);
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
            view = layoutInflater.inflate(R.layout.line_item_analyze, null);
            controls.tvLineAnalyzeResult = (TextView) view.findViewById(R.id.tvLineAnalyzeResult);

            view.setTag(controls);
        } else {
            controls = (Controls) view.getTag();
        }

        controls.tvLineAnalyzeResult.setText(source.get(i));

        return view;
    }

    public final class Controls
    {
        public TextView tvLineAnalyzeResult;
    }
}
