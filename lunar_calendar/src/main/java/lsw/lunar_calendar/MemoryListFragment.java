package lsw.lunar_calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import lsw.library.CrossAppKey;
import lsw.lunar_calendar.common.IntentKeys;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.data_source.HexagramListAdapter;
import lsw.lunar_calendar.data_source.MemoryListAdapter;
import lsw.lunar_calendar.model.EventRecord;
import lsw.lunar_calendar.model.HexagramDataRow;

/**
 * Created by swli on 6/3/2016.
 */
public class MemoryListFragment extends Fragment{

    private List<EventRecord> list;
    private MemoryListAdapter memoryListAdapter;
    private ListView lv;

    private static final String Bundle_SearchText = "search_text";

    private String searchText;

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memory_list, container, false);

        final DataBase dataBase = new DataBase();

        if(getArguments() != null)
        {
            searchText = getArguments().getString(Bundle_SearchText);
            list = dataBase.getEventRecordsByCondition(searchText);
        }
        else {
            list = dataBase.getEventRecords();
        }

        lv = (ListView)view.findViewById(R.id.lvEvents);
        memoryListAdapter = new MemoryListAdapter(getActivity(), list);
        lv.setAdapter(memoryListAdapter);

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
                final String[] strings = new String[]{ "删除"};

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

        return view;
    }

}
