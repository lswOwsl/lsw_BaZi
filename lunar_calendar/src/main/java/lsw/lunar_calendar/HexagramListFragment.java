package lsw.lunar_calendar;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.data_source.HexagramListAdapter;
import lsw.lunar_calendar.model.HexagramDataRow;

/**
 * Created by swli on 5/6/2016.
 */
public class HexagramListFragment extends Fragment {

    static String paramDate;

    public static HexagramListFragment newInstance(DateExt dateExt) {
        HexagramListFragment fragment = new HexagramListFragment();
        Bundle args = new Bundle();
        args.putString(paramDate, dateExt.getFormatDateTime("yyyy-MM-dd"));
        fragment.setArguments(args);
        return fragment;
    }

    public HexagramListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_list_hexagram, container, false);

        String date = getArguments().getString(paramDate);

        DataBase dataBase = new DataBase();

        final List<HexagramDataRow> list = dataBase.getHexagramDataRowByDate(date);

        ListView lv = (ListView)view.findViewById(R.id.lvHexagrams);
        lv.setAdapter(new HexagramListAdapter(getActivity(), list));


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                ComponentName componetName = new ComponentName(
                        "lsw.liuyao",
                        "lsw.liuyao.HexagramAnalyzerActivity");
                intent.setComponent(componetName);
                Bundle bundle = new Bundle();
                bundle.putInt(CrossAppKey.HexagramId, list.get(i).getId());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        return view;
    }
}
