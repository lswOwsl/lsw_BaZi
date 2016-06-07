package lsw.lunar_calendar;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
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

    private boolean forCurrentMonth;

    public boolean isForCurrentMonth() {
        return forCurrentMonth;
    }

    public void setForCurrentMonth(boolean forCurrentMonth) {
        this.forCurrentMonth = forCurrentMonth;
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

        DateExt tempDate = new DateExt(date, "yyyy-MM-dd");

        List<MemberDataRow> result = new ArrayList<MemberDataRow>();
        List<MemberDataRow> list = null;
        List<MemberDataRow> listLunar = null;
        if(isForCurrentMonth())
        {
            list = dataBase.getBirthdayDataRowsByMonth(tempDate.getFormatDateTime("MM"));
            listLunar = dataBase.getLunarBirthdayDataRowsByMonth(tempDate);
        } else {
            list = dataBase.getBirthdayByDay(tempDate.getFormatDateTime("MM-dd"));
            listLunar = dataBase.getLunarBithdayByDay(tempDate);
        }

        result.addAll(list);

        for (MemberDataRow dataRow : listLunar) {
            boolean hasRecord = false;
            for (MemberDataRow dataRow1 : list) {

                if (dataRow1.getName().equals(dataRow.getName()) &&
                        dataRow.isMale() == dataRow1.isMale() &&
                        dataRow1.getBirthday().getFormatDateTime().equals(dataRow.getBirthday().getFormatDateTime())) {
                    hasRecord = true;
                    break;
                }
            }

            if(!hasRecord)
                result.add(dataRow);
        }

        ListView lv = (ListView)view.findViewById(R.id.lvBirthday);
        lv.setAdapter(new MemberListAdapter(getActivity(), result));


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
