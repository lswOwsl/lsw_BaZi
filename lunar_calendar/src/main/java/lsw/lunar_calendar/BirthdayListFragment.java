package lsw.lunar_calendar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import lsw.library.DateExt;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.data_source.MemberListAdapter;
import lsw.lunar_calendar.model.MemberDataRow;

/**
 * Created by swli on 5/9/2016.
 */
public class BirthdayListFragment extends Fragment {

    static String paramDate;

    public static BirthdayListFragment newInstance(DateExt dateExt) {
        BirthdayListFragment fragment = new BirthdayListFragment();
        Bundle args = new Bundle();
        args.putString(paramDate, dateExt.getFormatDateTime("yyyy-MM-dd"));
        fragment.setArguments(args);
        return fragment;
    }

    public BirthdayListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_birthday, container, false);

        String date = getArguments().getString(paramDate);

        DataBase dataBase = new DataBase();

        final List<MemberDataRow> list = dataBase.getBirthdayByDay(new DateExt(date, "yyyy-MM-dd").getFormatDateTime("MM-dd"));

        ListView lv = (ListView)view.findViewById(R.id.lvBirthday);
        lv.setAdapter(new MemberListAdapter(getActivity(), list));


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                Intent intent = new Intent();
//                ComponentName componetName = new ComponentName(
//                        "lsw.liuyao",
//                        "lsw.liuyao.HexagramAnalyzerActivity");
//                intent.setComponent(componetName);
//                Bundle bundle = new Bundle();
//                bundle.putInt(CrossAppKey.HexagramId, list.get(i).getId());
//                intent.putExtras(bundle);
//                startActivity(intent);

            }
        });

        return view;
    }
}
