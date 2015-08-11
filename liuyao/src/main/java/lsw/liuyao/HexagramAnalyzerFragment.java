package lsw.liuyao;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;

import lsw.library.BaZiHelper;
import lsw.library.DateExt;
import lsw.library.LunarCalendarWrapper;
import lsw.liuyao.data.HexagramAdapter;
import lsw.model.Hexagram;

/**
 * Created by swli on 8/11/2015.
 */
public class HexagramAnalyzerFragment extends Fragment {

    private static final String Param_Hexagram_Main = "param1";
    private static final String Param_Hexagram_Changed = "param2";
    private static final String Param_FormatDate="param3";

    private Hexagram mainHexagram;
    private Hexagram changedHexagram;
    private DateExt dateExt;

    private ListView lvHexagramMain;
    private ListView lvHexagramChanged;

    private TextView tvMainTitle, tvChangedTitle, tvEraDate, tvDate;

    public static HexagramAnalyzerFragment newInstance(Hexagram mainHexagram, Hexagram changedHexagram, String formatDate) {
        HexagramAnalyzerFragment fragment = new HexagramAnalyzerFragment();
        Bundle args = new Bundle();
        args.putSerializable(Param_Hexagram_Main, mainHexagram);
        args.putSerializable(Param_Hexagram_Changed, changedHexagram);
        args.putString(Param_FormatDate,formatDate);
        fragment.setArguments(args);
        return fragment;
    }

    public HexagramAnalyzerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mainHexagram = (Hexagram)getArguments().getSerializable(Param_Hexagram_Main);
            changedHexagram = (Hexagram)getArguments().getSerializable(Param_Hexagram_Changed);
            dateExt = new DateExt(getArguments().getString(Param_FormatDate));
        }
    }

    String formatDateTime = "yyyy年MM月dd日 HH:mm";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hexagram_analyze_fragment, container, false);

        tvDate  = (TextView) view.findViewById(R.id.tvDate);
        tvEraDate = (TextView) view.findViewById(R.id.tvEraDate);
        tvMainTitle = (TextView) view.findViewById(R.id.tvTitleMain);
        tvChangedTitle = (TextView) view.findViewById(R.id.tvTitleChanged);

        lvHexagramMain = (ListView) view.findViewById(R.id.lvHexagramMain);
        lvHexagramChanged = (ListView) view.findViewById(R.id.lvHexagramChanged);

        lvHexagramMain.setAdapter(new HexagramAdapter(mainHexagram,getActivity()));
        if(changedHexagram != null)
        {
            tvChangedTitle.setText("变卦:"+ mainHexagram.getName() + "  宫:" + mainHexagram.getPlace()+ "  位置:"+mainHexagram.getId() % 8);
            lvHexagramChanged.setAdapter(new HexagramAdapter(changedHexagram, getActivity(), true));
        }

        tvMainTitle.setText("主卦:" + mainHexagram.getName() + "  宫:" + mainHexagram.getPlace() + "  位置:" + mainHexagram.getId() % 8);


        tvDate.setText(dateExt.getFormatDateTime(formatDateTime));
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        int eraMonthIndex = lunarCalendarWrapper.getChineseEraOfMonth();
        int eraDayIndex = lunarCalendarWrapper.getChineseEraOfDay();
        Pair<String,String> xunKong = BaZiHelper.getXunKong(getActivity(), lunarCalendarWrapper.toStringWithCelestialStem(eraMonthIndex), lunarCalendarWrapper.toStringWithTerrestrialBranch(eraDayIndex));
        String eraText =
                lunarCalendarWrapper.toStringWithSexagenary(eraMonthIndex) + "月   " +
                lunarCalendarWrapper.toStringWithSexagenary(eraDayIndex) +"日   (" + xunKong.first+ xunKong.second+")空";
        tvEraDate.setText(eraText);
        // Inflate the layout for this fragment
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
