package lsw.liuyao.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import lsw.hexagram.LineComparator;
import lsw.library.ColorHelper;
import lsw.liuyao.HexagramAnalyzerActivity;
import lsw.liuyao.R;
import lsw.liuyao.model.HexagramLineNote;
import lsw.model.EnumLineSymbol;
import lsw.model.Hexagram;
import lsw.model.Line;
import lsw.value.Default;

/**
 * Created by swli on 8/7/2015.
 */
public class HexagramAdapter extends BaseAdapter {

    private ArrayList<Line> lines;
    private Hexagram hexagram;
    private LayoutInflater layoutInflater;
    private Context context;
    private boolean isChangedHexagram;
    private ColorHelper colorHelper;

    private HashMap<Integer,String> sixAnimals;
    public void setSixAnimals(HashMap<Integer,String> sixAnimals)
    {
        this.sixAnimals = sixAnimals;
    }
    private HashMap<Integer,String> getSixAnimals()
    {
        return this.sixAnimals;
    }

    private List<HexagramLineNote> lineNotes;
    private Database database;

    private int easyChangedLineIndex = 0;

    private int bodyIndex = 0;

    public HexagramAdapter(Hexagram hexagram, Context context)
    {
        this(hexagram,context,false);
    }

    public HexagramAdapter(Hexagram hexagram, Context context, boolean isChangedHexagram)
    {
        this.hexagram = hexagram;
        this.context = context;
        this.lines = hexagram.getLines();
        Collections.sort(this.lines,new LineComparator());
        this.layoutInflater = LayoutInflater.from(context);
        this.isChangedHexagram = isChangedHexagram;
        colorHelper = ColorHelper.getInstance(context);

        database = new Database(context);
        lineNotes = database.getHexagramByNameAndLinePosition(hexagram.getName());

        if(!isChangedHexagram) {
            int totalLineCount = 55;
            for (Line line : this.lines) {
                totalLineCount -= line.getLineSymbol().value();
            }

            //先用总数除于6，例如7就是从上爻开始数（除6后肯定是奇数），如果是15（除6后余的是偶数）从初爻开始数
            int totalMod = totalLineCount % 6;
            totalMod = totalMod == 0 ? 1 : totalMod;
            //如果不是偶数从上爻开始数
            if ((totalLineCount / 6) % 2 != 0) {
                easyChangedLineIndex = 7 - totalMod;
            } else {
                easyChangedLineIndex = totalMod;
            }
        }

        //卦身所在爻位
        for (Line line : this.lines) {
            if(hexagram.getSelf() == line.getPosition())
            {
                bodyIndex = Default.getHexagramBodyIndex(line.getEarthlyBranch().getId());
            }
        }
    }

    @Override
    public int getCount() {
        return lines.size();
    }

    @Override
    public Object getItem(int i) {
        return lines.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Controls controls;
        if (view == null) {
            controls = new Controls();
            //view = layoutInflater.inflate(R.layout.member_row,null);

            view = layoutInflater.inflate(R.layout.line_item, null);
            controls.tvAttachedLine = (TextView) view.findViewById(R.id.tvAttachedLine);
            controls.ivLineSymbol = (ImageView) view.findViewById(R.id.ivLineSymbol);
            controls.tvLine = (TextView) view.findViewById(R.id.tvLine);
            controls.tvSelfTarget = (TextView)view.findViewById(R.id.tvSelfOrTarget);
            controls.tvSixAnimal = (TextView)view.findViewById(R.id.tvSixAnimal);

            view.setTag(controls);
        } else {
            controls = (Controls) view.getTag();
        }
        final Line line = lines.get(i);
        //String lineText= line.getSixRelation().toString() +" " + line.getEarthlyBranch().getName() + " " + line.getEarthlyBranch().getFiveElement().toString();
        //controls.tvLine.setText(lineText);
        controls.tvLine.setText("");
        SpannableString ssSixRelation = ColorHelper.getTextByColor(line.getSixRelation().toString(), Color.GRAY);
        SpannableString ssName = colorHelper.getColorTerrestrial(line.getEarthlyBranch().getName());
        SpannableString ssFiveElement = ColorHelper.getTextByColor(line.getEarthlyBranch().getFiveElement().toString(),Color.GRAY);
        controls.tvLine.append(ssSixRelation);
        controls.tvLine.append(" ");
        controls.tvLine.append(ssName);
        controls.tvLine.append(" ");
        controls.tvLine.append(ssFiveElement);

        if(!isChangedHexagram) {

            if(line.getPosition() == easyChangedLineIndex)
            {
                controls.tvSixAnimal.setBackgroundColor(Color.YELLOW);
            }
            else
                controls.tvSixAnimal.setBackgroundColor(Color.WHITE);

            String self = hexagram.getSelf() == line.getPosition() ? "世" : "";
            String target = hexagram.getTarget() == line.getPosition() ? "应" : "";

            controls.tvSelfTarget.setText(self+target);

            if(bodyIndex != 0 && bodyIndex == line.getPosition()) {
                if((self+target).isEmpty())
                    controls.tvSelfTarget.setText("身");
                else
                    controls.tvSelfTarget.setText(self+target+"/身");
            }

            if (line.getEarthlyBranchAttached() != null) {
                controls.tvAttachedLine.setText("");
                SpannableString ssSixRelationAttached = ColorHelper.getTextByColor(line.getSixRelationAttached().toString(), Color.GRAY);
                SpannableString ssNameAttached = colorHelper.getColorTerrestrial(line.getEarthlyBranchAttached().getName());
                SpannableString ssFiveElementAttached = ColorHelper.getTextByColor(line.getEarthlyBranchAttached().getFiveElement().toString(),Color.GRAY);
                controls.tvAttachedLine.append(ssSixRelationAttached);
                controls.tvAttachedLine.append(" ");
                controls.tvAttachedLine.append(ssNameAttached);
                controls.tvAttachedLine.append(" ");
                controls.tvAttachedLine.append(ssFiveElementAttached);
            } else {
                controls.tvAttachedLine.setText("");
            }
        }
        else {
            controls.tvSelfTarget.setText("");
            controls.tvSelfTarget.setVisibility(View.GONE);
            controls.tvAttachedLine.setVisibility(View.GONE);
        }

        if(getSixAnimals() == null)
            controls.tvSixAnimal.setText("");
        else
        {
            controls.tvSixAnimal.setText(getSixAnimals().get(line.getPosition()));
        }

        int resourceId = R.drawable.yang;
        final EnumLineSymbol lineSymbol = line.getLineSymbol();
        if(lineSymbol.equals(EnumLineSymbol.LaoYang))
            resourceId = R.drawable.laoyang;
        else if(lineSymbol.equals(EnumLineSymbol.LaoYin))
            resourceId = R.drawable.laoyin;
        else if(lineSymbol.equals(EnumLineSymbol.Yin))
            resourceId = R.drawable.yin;
        controls.ivLineSymbol.setImageResource(resourceId);



        final HashMap<Integer,String> mappingPosition = new HashMap<Integer, String>();
        mappingPosition.put(2,"二");
        mappingPosition.put(3,"三");
        mappingPosition.put(4,"四");
        mappingPosition.put(5,"五");

        final int linePosition = 6 - i;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTuanCi = "";
                String noteXiangCi = "";
                String noteTuanCiD = "";
                String noteXiangCiD = "";

                for(HexagramLineNote lineNote : lineNotes )
                {
                    if(lineNote.getPosition() == linePosition)
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
                    }
                }

                String title = linePosition + "";

                if (lineSymbol == EnumLineSymbol.LaoYang || lineSymbol == EnumLineSymbol.Yang)
                    title = "九";
                else
                    title = "六";

                if(linePosition == 1)
                    title = "初" + title;
                else if(linePosition == 6)
                    title = "上" + title;
                else
                {
                    title = title + mappingPosition.get(linePosition);
                }

                LayoutInflater inflater = LayoutInflater.from(context);
                View lineNoteView = inflater.inflate(R.layout.common_hexagram_description, null);
                TextView tvTitle = (TextView)lineNoteView.findViewById(R.id.tvTitle);
                TextView tvTuanCiNote = (TextView)lineNoteView.findViewById(R.id.tvTuanCiNote);
                TextView tvTuanCiNoteDecorated = (TextView)lineNoteView.findViewById(R.id.tvTuanCiNoteDecorated);

                TextView tvXiangCiNote = (TextView)lineNoteView.findViewById(R.id.tvXiangCiNote);
                TextView tvXiangCiNoteDecorated = (TextView)lineNoteView.findViewById(R.id.tvXiangCiNoteDecorated);

                tvTitle.setText(title);
                tvTuanCiNote.setText(noteTuanCi);
                tvTuanCiNoteDecorated.setText(noteTuanCiD);
                tvXiangCiNote.setText(noteXiangCi);
                tvXiangCiNoteDecorated.setText(noteXiangCiD);

                Dialog dialog = new Dialog(context,R.style.CustomDialog);
                dialog.setContentView(lineNoteView);
                dialog.show();

                WindowManager windowManager = ((Activity)context).getWindowManager();

                Display display = windowManager.getDefaultDisplay();
                Window win = dialog.getWindow();
                DisplayMetrics dm = new DisplayMetrics();
                display.getMetrics(dm);
                win.setLayout(dm.widthPixels/3*2, dm.heightPixels /3*2);

            }
        });

        return view;
    }

    public final class Controls
    {
        public TextView tvAttachedLine;
        public ImageView ivLineSymbol;
        public TextView tvLine;
        public TextView tvSelfTarget;
        public TextView tvSixAnimal;
    }
}
