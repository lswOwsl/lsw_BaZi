package lsw.liuyao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lsw.hexagram.Builder;
import lsw.library.ColorHelper;
import lsw.library.DateExt;
import lsw.library.LunarCalendarWrapper;
import lsw.library.Utility;
import lsw.liuyao.data.Database;
import lsw.liuyao.data.HexagramAdapter;
import lsw.liuyao.model.HexagramLineNote;
import lsw.model.Hexagram;
import lsw.value.Default;

/**
 * Created by swli on 8/11/2015.
 */
public class HexagramBuilderFragment extends Fragment {

    private static final String Param_Hexagram_Main = "param1";
    private static final String Param_Hexagram_Changed = "param2";
    private static final String Param_FormatDate="param3";

    private Hexagram mainHexagram;
    private Hexagram changedHexagram;
    private DateExt dateExt;

    private ListView lvHexagramMain;
    private ListView lvHexagramChanged;

    private TextView tvMainTitle, tvChangedTitle, tvEraDate, tvDate;

    private Database database;

    public static HexagramBuilderFragment newInstance(Hexagram mainHexagram, Hexagram changedHexagram, String formatDate) {

        HexagramBuilderFragment fragment = new HexagramBuilderFragment();
        Bundle args = new Bundle();
        args.putSerializable(Param_Hexagram_Main, mainHexagram);
        args.putSerializable(Param_Hexagram_Changed, changedHexagram);
        args.putString(Param_FormatDate, formatDate);
        fragment.setArguments(args);
        return fragment;
    }

    public HexagramBuilderFragment() {

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

        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        int eraMonthIndex = lunarCalendarWrapper.getChineseEraOfMonth();
        int eraDayIndex = lunarCalendarWrapper.getChineseEraOfDay();

        HexagramAdapter mainAdapter = new HexagramAdapter(mainHexagram,getActivity());
        mainAdapter.setSixAnimals(Builder.getSixAnimalsByCelestialStem(lunarCalendarWrapper.toStringWithCelestialStem(eraDayIndex)));
        lvHexagramMain.setAdapter(mainAdapter);

        if(changedHexagram != null)
        {
            String sixSuitOrInverse = Default.isSixInverseHexagram(changedHexagram.getName()) ? " (六冲)" : (Default.isSixSuitHexagram(changedHexagram.getName())? " (六合)" : "");
            tvChangedTitle.setText("变卦:"+ changedHexagram.getName() + sixSuitOrInverse +"  宫:" + changedHexagram.getPlace()+ "  位置:"+getPlacePostion(changedHexagram.getId()));
            lvHexagramChanged.setAdapter(new HexagramAdapter(changedHexagram, getActivity(), true));
        }
        String sixSuitOrInverse = Default.isSixInverseHexagram(mainHexagram.getName()) ? " (六冲)" : (Default.isSixSuitHexagram(mainHexagram.getName())? " (六合)" : "");
        tvMainTitle.setText("主卦:" + mainHexagram.getName() + sixSuitOrInverse + "  宫:" + mainHexagram.getPlace() + "  位置:" + getPlacePostion(mainHexagram.getId()));


        tvDate.setText(dateExt.getFormatDateTime(formatDateTime));

        Pair<String,String> xunKong = Utility.getXunKong(getActivity(), lunarCalendarWrapper.toStringWithCelestialStem(eraDayIndex), lunarCalendarWrapper.toStringWithTerrestrialBranch(eraDayIndex));
//        String eraText =
//                lunarCalendarWrapper.toStringWithSexagenary(eraMonthIndex) + "月   " +
//                lunarCalendarWrapper.toStringWithSexagenary(eraDayIndex) +"日   (" + xunKong.first+ xunKong.second+")空";

        ColorHelper colorHelper = ColorHelper.getInstance(getActivity());
        SpannableString sMonthC = colorHelper.getColorCelestialStem(lunarCalendarWrapper.toStringWithCelestialStem(eraMonthIndex));
        SpannableString sMonthT = colorHelper.getColorTerrestrial(lunarCalendarWrapper.toStringWithTerrestrialBranch(eraMonthIndex));

        SpannableString sDayC = colorHelper.getColorCelestialStem(lunarCalendarWrapper.toStringWithCelestialStem(eraDayIndex));
        SpannableString sDayT = colorHelper.getColorTerrestrial(lunarCalendarWrapper.toStringWithTerrestrialBranch(eraDayIndex));

        SpannableString xunKong1 = colorHelper.getColorTerrestrial(xunKong.first);
        SpannableString xunKong2 = colorHelper.getColorTerrestrial(xunKong.second);

        tvEraDate.setText("");
        tvEraDate.append(sMonthC);
        tvEraDate.append(sMonthT);
        tvEraDate.append(ColorHelper.getTextByColor("月   ", Color.GRAY));
        tvEraDate.append(sDayC);
        tvEraDate.append(sDayT);
        tvEraDate.append(ColorHelper.getTextByColor("日   (", Color.GRAY));
        tvEraDate.append(xunKong1);
        tvEraDate.append(ColorHelper.getTextByColor(",", Color.GRAY));
        tvEraDate.append(xunKong2);
        tvEraDate.append(ColorHelper.getTextByColor(")", Color.GRAY));
        tvEraDate.append(ColorHelper.getTextByColor("空",Color.GRAY));

        database = new Database(getActivity());
        final List<HexagramLineNote> lineNotesMain = database.getHexagramByNameAndLinePosition(mainHexagram.getName());
        tvMainTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getHexagramNote(lineNotesMain);
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .show();
            }
        });

        if(changedHexagram != null) {

            final List<HexagramLineNote> lineNotesChanged = database.getHexagramByNameAndLinePosition(changedHexagram.getName());

            tvChangedTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = getHexagramNote(lineNotesChanged);
                    new AlertDialog.Builder(getActivity())
                            .setMessage(message)
                            .show();
                }
            });
        }

        return view;
    }

    private String getHexagramNote(List<HexagramLineNote> noteList)
    {
        String result = "";
        for (HexagramLineNote lineNote : noteList)
        {
            if(lineNote.getPosition() == 8 || lineNote.getPosition() == 0)
            {
                if(!TextUtils.isEmpty(result)) {
                    result += "\n";
                }
                result = result + lineNote.getNoteType() + "词------------\n原文 : " + lineNote.getOriginalNote() + "\n译文 :" + lineNote.getDecoratedNote();
            }
        }

        return result;
    }

    private String getPlacePostion(int id)
    {
        int index = id%8;

        String positionName = "";
        if(index == 0)
            positionName = "8 归魂";
        else if(index == 7)
            positionName = "7 游魂";
        else
            positionName += index;

        return positionName;
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
