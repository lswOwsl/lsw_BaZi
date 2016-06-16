package lsw.lunar_calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.lunar_calendar.common.IntentKeys;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.data_source.HexagramListAdapter;
import lsw.lunar_calendar.data_source.MemoryListAdapter;
import lsw.lunar_calendar.model.EventRecord;
import lsw.lunar_calendar.model.HexagramDataRow;

/**
 * Created by swli on 6/3/2016.
 */
public class MemoryListFragment extends Fragment {

    private List<EventRecord> list;
    private MemoryListAdapter memoryListAdapter;
    private ListView lv;

    private TextView tvToday,tvCurrentWeek,tvCurrentMonth, tvAll, tvTitle;

    private static final String Bundle_SearchText = "search_text";
    private String searchText;

    private static final String Bundle_DateExt = "date_ext";

    private DataBase dataBase;
    private DateExt initDateExt;

    public static MemoryListFragment newInstance(DateExt dateExt) {
        MemoryListFragment fragment = new MemoryListFragment();
        Bundle args = new Bundle();
        args.putString(Bundle_DateExt, dateExt.getFormatDateTime());
        fragment.setArguments(args);
        return fragment;
    }

    public static MemoryListFragment newInstance(String searchText) {
        MemoryListFragment fragment = new MemoryListFragment();
        Bundle args = new Bundle();
        args.putString(Bundle_SearchText, searchText);
        fragment.setArguments(args);
        return fragment;
    }

    public static MemoryListFragment newInstance() {
        MemoryListFragment fragment = new MemoryListFragment();
        return fragment;
    }

    public MemoryListFragment() {
        dataBase = new DataBase();
        initDateExt = new DateExt();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memory_list, container, false);

        tvCurrentMonth = (TextView)view.findViewById(R.id.tvCurrentMonth);
        tvCurrentWeek = (TextView)view.findViewById(R.id.tvCurrentWeek);
        tvToday = (TextView)view.findViewById(R.id.tvToday);
        tvAll = (TextView)view.findViewById(R.id.tvAll);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);

        if(getArguments() != null)
        {
            if(getArguments().containsKey(Bundle_SearchText)) {
                searchText = getArguments().getString(Bundle_SearchText);
                list = dataBase.getEventRecordsByCondition(searchText);
            }

            if(getArguments().containsKey(Bundle_DateExt)) {
                String paramDate = getArguments().getString(Bundle_DateExt);
                initDateExt = new DateExt(paramDate);

                tvCurrentMonth.setText(initDateExt.getFormatDateTime("yyyy年MM月"));
                DateExt beginDay = new DateExt(initDateExt.getDate()).getThisWeekMonday();
                DateExt endDay = new DateExt(beginDay.getDate()).addDays(6);
                tvCurrentWeek.setText(beginDay.getFormatDateTime("MM-dd")+"至"+endDay.getFormatDateTime("MM-dd"));
                tvToday.setText(initDateExt.getFormatDateTime("MM-dd"));

                list = dataBase.getForecastEventRecordByMonth(initDateExt);

                tvCurrentWeek.setVisibility(View.GONE);
                tvAll.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
            }
        }
        else {
            list = dataBase.getEventRecords();
        }

        lv = (ListView)view.findViewById(R.id.lvEvents);
        memoryListAdapter = new MemoryListAdapter(getActivity(), list);
        lv.setAdapter(memoryListAdapter);

        bindAction();

        return view;
    }

    private void bindAction()
    {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int eventRecordId = list.get(i).getId();
                Intent intent = new Intent(getActivity(),Memory.class);
                Bundle bundle = new Bundle();
                bundle.putInt(IntentKeys.EventRecordId,eventRecordId);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int eventRecordId = list.get(i).getId();
                final String[] strings = new String[]{"删除"};

                new AlertDialog.Builder(getActivity()).setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (strings[i] == "删除") {
                            dataBase.deleteEventRecordById(eventRecordId);
                            list = dataBase.getEventRecords();
                            memoryListAdapter = new MemoryListAdapter(getActivity(), list);
                            lv.setAdapter(memoryListAdapter);
                        }
                    }
                }).show();

                return false;
            }
        });

        tvCurrentWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = dataBase.getEventRecordByWeek(initDateExt);
                memoryListAdapter = new MemoryListAdapter(getActivity(), list);
                lv.setAdapter(memoryListAdapter);
            }
        });

        tvCurrentMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = dataBase.getEventRecordByMonth(initDateExt);
                memoryListAdapter = new MemoryListAdapter(getActivity(), list);
                lv.setAdapter(memoryListAdapter);
            }
        });

        tvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = dataBase.getEventRecordByDay(initDateExt);
                memoryListAdapter = new MemoryListAdapter(getActivity(), list);
                lv.setAdapter(memoryListAdapter);
            }
        });

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = dataBase.getEventRecords();
                memoryListAdapter = new MemoryListAdapter(getActivity(), list);
                lv.setAdapter(memoryListAdapter);
            }
        });
    }

}
