package lsw.lunar_calendar;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.data_source.MemberListAdapter;
import lsw.lunar_calendar.model.MemberDataRow;

/**
 * Created by swli on 5/9/2016.
 */
public class BirthdayListFragment extends Fragment {

    static String paramDate;

    private TextView tvAllBirthday;
    List<MemberDataRow> list = null;
    List<MemberDataRow> listLunar = null;
    DataBase dataBase = new DataBase();
    DateExt tempDate = null;
    ListView lv;
    List<MemberDataRow> result = new ArrayList<MemberDataRow>();

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

        tempDate = new DateExt(date, "yyyy-MM-dd");

        tvAllBirthday = (TextView) view.findViewById(R.id.tvAllBirthday);
        lv  = (ListView)view.findViewById(R.id.lvBirthday);

        loadListView();

        tvAllBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isForCurrentMonth()) {
                    setForCurrentMonth(true);
                    loadListView();
                }
                else
                {
                    setForCurrentMonth(false);
                    loadListView();
                }
            }
        });

        return view;
    }

    private void loadListView()
    {
        if(isForCurrentMonth())
        {
            tvAllBirthday.setText("当日生日");
            list = dataBase.getBirthdayDataRowsByMonth(tempDate.getFormatDateTime("MM"));
            listLunar = dataBase.getLunarBirthdayDataRowsByMonth(tempDate);
        } else {
            tvAllBirthday.setText("本月全部生日");
            list = dataBase.getBirthdayByDay(tempDate.getFormatDateTime("MM-dd"));
            listLunar = dataBase.getLunarBithdayByDay(tempDate);
        }

        result.clear();
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

        lv.setAdapter(new MemberListAdapter(getActivity(), result));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                ComponentName componetName = new ComponentName(
                        "com.example.swli.myapplication20150519",
                        "com.example.swli.myapplication20150519.MemberAnalyze");
                intent.setComponent(componetName);
                Bundle bundle = new Bundle();
                bundle.putInt(CrossAppKey.MemberId, list.get(i).getId());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }
}
