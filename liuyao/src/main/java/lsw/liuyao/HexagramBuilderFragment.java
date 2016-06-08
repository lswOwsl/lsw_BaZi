package lsw.liuyao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import lsw.hexagram.Builder;
import lsw.library.ColorHelper;
import lsw.library.DateExt;
import lsw.library.LunarCalendar;
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
        int eraMonthIndex = lunarCalendarWrapper.getChineseEraOfMonth(true);
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

        DateExt tempDateExt = new DateExt(dateExt.getDate());
        int indexOfWeek = tempDateExt.getIndexOfWeek();
        String weekDay =  LunarCalendar.toChineseDayInWeek(indexOfWeek);

        tvDate.setText(dateExt.getFormatDateTime(formatDateTime) +" (星期"+ weekDay +")");

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
                showHexagramNote(lineNotesMain);
            }
        });

        if(changedHexagram != null) {

            final List<HexagramLineNote> lineNotesChanged = database.getHexagramByNameAndLinePosition(changedHexagram.getName());

            tvChangedTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showHexagramNote(lineNotesChanged);

                }
            });
        }

        return view;
    }

    private void showHexagramNote(List<HexagramLineNote> noteList)
    {
        String noteTuanCi = "";
        String noteXiangCi = "";
        String noteTuanCiD = "";
        String noteXiangCiD = "";

        for (HexagramLineNote lineNote : noteList)
        {
            //lineNote.getPosition() == 8 ||
            if(lineNote.getPosition() == 0)
            {
                if(lineNote.getNoteType().trim().equals("彖"))
                {
                    noteTuanCi = lineNote.getOriginalNote();
                    noteTuanCiD = lineNote.getDecoratedNote();
                }
                else
                {
                    noteXiangCi = lineNote.getOriginalNote();
                    noteXiangCiD = lineNote.getDecoratedNote();
                }
                //差用九用六的没有显示也就是等于8时
            }
        }

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View lineNoteView = inflater.inflate(R.layout.common_hexagram_description, null);
        TextView tvTitle = (TextView)lineNoteView.findViewById(R.id.tvTitle);
        TextView tvTuanCiNote = (TextView)lineNoteView.findViewById(R.id.tvTuanCiNote);
        TextView tvTuanCiNoteDecorated = (TextView)lineNoteView.findViewById(R.id.tvTuanCiNoteDecorated);

        TextView tvXiangCiNote = (TextView)lineNoteView.findViewById(R.id.tvXiangCiNote);
        TextView tvXiangCiNoteDecorated = (TextView)lineNoteView.findViewById(R.id.tvXiangCiNoteDecorated);

        tvTitle.setText(noteList.get(0).getName());
        tvTuanCiNote.setText(noteTuanCi);
        tvTuanCiNoteDecorated.setText(noteTuanCiD);
        tvXiangCiNote.setText(noteXiangCi);
        tvXiangCiNoteDecorated.setText(noteXiangCiD);

        Dialog dialog = new Dialog(getActivity(),R.style.CustomDialog);
        dialog.setContentView(lineNoteView);
        dialog.show();

        WindowManager windowManager = getActivity().getWindowManager();

        Display display = windowManager.getDefaultDisplay();
        Window win = dialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        win.setLayout(dm.widthPixels/3*2, dm.heightPixels /3*2);
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
