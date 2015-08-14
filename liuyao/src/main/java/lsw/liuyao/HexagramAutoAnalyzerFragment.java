package lsw.liuyao;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import lsw.library.DateExt;
import lsw.liuyao.data.HexagramAutoAnalyzerAdapter;
import lsw.model.Hexagram;

/**
 * Created by swli on 8/14/2015.
 */
public class HexagramAutoAnalyzerFragment extends Fragment {

    private static final String Param_Result_List = "param1";

    private ArrayList<String> autoAnalyzeResult;

    public static HexagramAutoAnalyzerFragment newInstance(ArrayList<String> arrayList) {
        HexagramAutoAnalyzerFragment fragment = new HexagramAutoAnalyzerFragment();
        Bundle args = new Bundle();
        args.putSerializable(Param_Result_List, arrayList);
        fragment.setArguments(args);
        return fragment;
    }

    public HexagramAutoAnalyzerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            autoAnalyzeResult = (ArrayList<String>)getArguments().getSerializable(Param_Result_List);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hexagram_auto_analyze_fragment, container, false);

        ListView listView = (ListView)view.findViewById(R.id.lvAnalyzResult);
        HexagramAutoAnalyzerAdapter adapter = new HexagramAutoAnalyzerAdapter(autoAnalyzeResult,getActivity());
        listView.setAdapter(adapter);

        TextView tvAutoAnalyze = (TextView)view.findViewById(R.id.tvAutoAnalyze);
        tvAutoAnalyze.setOnTouchListener((View.OnTouchListener)getActivity());

        LinearLayout llContent = (LinearLayout)view.findViewById(R.id.llContent);
        llContent.setOnTouchListener((View.OnTouchListener)getActivity());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
