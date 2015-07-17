package com.example.swli.myapplication20150519.activity;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.ColorHelper;
import com.example.swli.myapplication20150519.common.EnumPart;
import com.example.swli.myapplication20150519.model.XmlCelestialStem;
import com.example.swli.myapplication20150519.model.XmlTerrestrial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MemberAnalyzeViewPager_XCHH {

    private MemberAnalyzeViewPager viewPager;
    private LinearLayout linearLayout;
    private Activity activity;
    private XmlTerrestrial xmlTerrestrial;
    private XmlCelestialStem xmlCelestialStem;
    private LinearLayout linearLayoutC;
    private View cChongHe;
    private boolean haveFlowMonth;

    public MemberAnalyzeViewPager_XCHH(MemberAnalyzeViewPager viewPager)
    {
        this.viewPager = viewPager;
        this.activity = viewPager.getActivity();
        this.linearLayout = (LinearLayout)viewPager.getActivity().findViewById(R.id.ll_container);

        xmlTerrestrial = XmlTerrestrial.getInstance(activity);
        xmlCelestialStem = XmlCelestialStem.getInstance(activity);

        cChongHe = activity.findViewById(R.id.cChongHe);
        this.linearLayoutC = (LinearLayout)cChongHe.findViewById(R.id.ll_containerC);
    }

    public void clearTopScrollHeight()
    {
        ViewGroup.LayoutParams lp = cChongHe.getLayoutParams();
        lp.height = 0;
        cChongHe.setLayoutParams(lp);
    }

    public void init(Integer daYunEraIndex, Integer flowYearEraIndex, Integer flowMonthEraIndex)
    {
        if(flowMonthEraIndex != null)
            haveFlowMonth = true;

        HashMap<EnumPart,Pair<String,String>> tList = new HashMap<EnumPart, Pair<String,String>>();

        if(flowMonthEraIndex != null)
        {
            tList.put(EnumPart.FlowMonth, Pair.create(viewPager.getBaZiActivityWrapper().getT(flowMonthEraIndex),
                    viewPager.getBaZiActivityWrapper().getC(flowMonthEraIndex)));
        }
        else
        {
            tList.put(EnumPart.FlowMonth, Pair.create("", ""));
        }

        if(flowYearEraIndex != null) {
            tList.put(EnumPart.FlowYear, Pair.create(viewPager.getBaZiActivityWrapper().getT(flowYearEraIndex),
                    viewPager.getBaZiActivityWrapper().getC(flowYearEraIndex)));
        }
        else
        {
            tList.put(EnumPart.FlowYear, Pair.create("", ""));
        }
        if(daYunEraIndex != null) {
            tList.put(EnumPart.DaYun, Pair.create(viewPager.getBaZiActivityWrapper().getT(daYunEraIndex),
                    viewPager.getBaZiActivityWrapper().getC(daYunEraIndex)));
        }
        else
        {
            tList.put(EnumPart.DaYun, Pair.create("", ""));
        }
        tList.put(EnumPart.Year,
                Pair.create(viewPager.getBaZiActivityWrapper().getT(viewPager.getBaZiActivityWrapper().getYearEraIndex()),
                        viewPager.getBaZiActivityWrapper().getC(viewPager.getBaZiActivityWrapper().getYearEraIndex())));
        tList.put(EnumPart.Month,
                Pair.create(viewPager.getBaZiActivityWrapper().getT(viewPager.getBaZiActivityWrapper().getMonthEraIndex()),
                        viewPager.getBaZiActivityWrapper().getC(viewPager.getBaZiActivityWrapper().getMonthEraIndex())));
        tList.put(EnumPart.Day,
                Pair.create(viewPager.getBaZiActivityWrapper().getT(viewPager.getBaZiActivityWrapper().getDayEraIndex()),
                        viewPager.getBaZiActivityWrapper().getC(viewPager.getBaZiActivityWrapper().getDayEraIndex())));
        tList.put(EnumPart.Hour,
                Pair.create(viewPager.getBaZiActivityWrapper().getT(viewPager.getBaZiActivityWrapper().getHourEraIndex()),
                        viewPager.getBaZiActivityWrapper().getC(viewPager.getBaZiActivityWrapper().getHourEraIndex())));

        ArrayList<String> arrays = new ArrayList<String>();
        if(haveFlowMonth){
            arrays.add(tList.get(EnumPart.FlowMonth).first);
        }
        else {
            arrays.add(tList.get(EnumPart.FlowYear).first);
        }
        arrays.add(tList.get(EnumPart.DaYun).first);
        arrays.add(tList.get(EnumPart.Year).first);
        arrays.add(tList.get(EnumPart.Month).first);
        arrays.add(tList.get(EnumPart.Day).first);
        arrays.add(tList.get(EnumPart.Hour).first);

        ArrayList<String> arraysC = new ArrayList<String>();
        if(haveFlowMonth) {
            arraysC.add(tList.get(EnumPart.FlowMonth).second);
        }
        else {
            arraysC.add(tList.get(EnumPart.FlowYear).second);
        }
        arraysC.add(tList.get(EnumPart.DaYun).second);
        arraysC.add(tList.get(EnumPart.Year).second);
        arraysC.add(tList.get(EnumPart.Month).second);
        arraysC.add(tList.get(EnumPart.Day).second);
        arraysC.add(tList.get(EnumPart.Hour).second);

        buildControls(arrays);
        buildControlsC(arraysC);
    }

    //天干
    private void buildControlsC(ArrayList<String> arrayList)
    {
        linearLayoutC.removeAllViews();
        List<View> list = new ArrayList<View>();
        for (int i=0; i<arrayList.size();i++)
        {
            for (int j= i+1; j<arrayList.size();j++)
            {
                //因为流月占了流年的位置是0,并且流月只跟大运有关，大运的索引是1，所以大于大运的索引全不处理
                if(i==0 && haveFlowMonth && j>1)
                    break;

                for(Pair<String,String> key: xmlCelestialStem.getPairSuits().keySet())
                {
                    boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                    boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                    if(option1 || option2)
                    {
                        list.add(createRowView(arrayList.get(i), arrayList.get(j), "合", xmlCelestialStem.getPairSuits().get(key), i, j,false));
                    }
                }

                for(Pair<String,String> key: xmlCelestialStem.getPairInverses())
                {
                    boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                    boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                    if(option1 || option2)
                    {
                        list.add(createRowView(arrayList.get(i), arrayList.get(j), "冲", "", i, j,false));
                    }
                }
            }
        }

        Collections.reverse(list);

        for (View v : list)
        {
            linearLayoutC.addView(v);
        }

        cChongHe.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                int innerHeight = ((ScrollView) cChongHe).getChildAt(0).getHeight();
                if (300 > innerHeight) {
                    ViewGroup.LayoutParams lp = cChongHe.getLayoutParams();
                    lp.height = innerHeight;
                    cChongHe.setLayoutParams(lp);
                } else {
                    ViewGroup.LayoutParams lp = cChongHe.getLayoutParams();
                    lp.height = 300;
                    cChongHe.setLayoutParams(lp);
                    cChongHe.scrollTo(0, innerHeight - lp.height);
                }
            }
        });

    }

    //地支
    private void buildControls(ArrayList<String> arrayList)
    {
        linearLayout.removeAllViews();
        for (int i=0; i<arrayList.size();i++)
        {
            for (int j= i+1; j<arrayList.size();j++)
            {
                //因为流月占了流年的位置是0,并且流月只跟大运有关，大运的索引是1，所以大于大运的索引全不处理
                if(i==0 && haveFlowMonth && j>1)
                    break;

                //六合
                for(Pair<String,String> key: xmlTerrestrial.getSixSuits().keySet())
                {
                    boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                    boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                    if(option1 || option2)
                    {
                        linearLayout.addView(createRowView(arrayList.get(i), arrayList.get(j), "合", xmlTerrestrial.getSixSuits().get(key), i, j));
                    }
                }
                //六冲
                for(Pair<String,String> key: xmlTerrestrial.getSixInverses())
                {
                    boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                    boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                    if(option1 || option2)
                    {
                        linearLayout.addView(createRowView(arrayList.get(i), arrayList.get(j), "冲", "", i, j));
                    }
                }
                //刑
                for (Pair<String,String> key: xmlTerrestrial.getPunishment())
                {
                    if(key.first.equals(key.second))
                    {
                        boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.first);
                        if(option1)
                            linearLayout.addView(createRowView(arrayList.get(i), arrayList.get(j), "自刑", "", i, j));
                    }
                    else {
                        boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                        boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                        if (option1 || option2) {
                            linearLayout.addView(createRowView(arrayList.get(i), arrayList.get(j), "刑", "", i, j));
                        }
                    }
                }

                for(int m=j+1; m<arrayList.size(); m++)
                {
                    //因为流月占了流年的位置是0,并且流月只跟大运有关，大运的索引是1，所以大于大运的索引全不处理
                    if(i==0 && haveFlowMonth && j==1)
                        break;

                    //三合
                    for(String key: xmlTerrestrial.getThreeSuits().keySet())
                    {
                        ArrayList<String> threeSuit = xmlTerrestrial.getThreeSuits().get(key);
                        int index1 = threeSuit.indexOf(arrayList.get(i));
                        int index2 = threeSuit.indexOf(arrayList.get(j));
                        int index3 = threeSuit.indexOf(arrayList.get(m));
                        if(index1 != -1 && index2 !=-1 && index3 !=-1 &&
                                ((index1 != index2) && (index1!=index3) && (index2 != index3)))
                        {
                            linearLayout.addView(createRowView(arrayList.get(i),arrayList.get(j),arrayList.get(m),"半合",key,i,j,m));
                        }
                    }
                    //三会
                    for(String key: xmlTerrestrial.getThreeConverge().keySet())
                    {
                        ArrayList<String> threeSuit = xmlTerrestrial.getThreeConverge().get(key);
                        int index1 = threeSuit.indexOf(arrayList.get(i));
                        int index2 = threeSuit.indexOf(arrayList.get(j));
                        int index3 = threeSuit.indexOf(arrayList.get(m));
                        if(index1 != -1 && index2 !=-1 && index3 !=-1 &&
                                ((index1 != index2) && (index1!=index3) && (index2 != index3)))
                        {
                            linearLayout.addView(createRowView(arrayList.get(i),arrayList.get(j),arrayList.get(m),"半会",key,i,j,m));
                        }
                    }

                    //三刑
                    for (ArrayList<String> array : xmlTerrestrial.getThreePunishment())
                    {
                        int index1 = array.indexOf(arrayList.get(i));
                        int index2 = array.indexOf(arrayList.get(j));
                        int index3 = array.indexOf(arrayList.get(m));
                        if(index1 != -1 && index2 !=-1 && index3 !=-1 &&
                                ((index1 != index2) && (index1!=index3) && (index2 != index3)))
                        {
                            linearLayout.addView(createRowView(arrayList.get(i),arrayList.get(j),arrayList.get(m),"三刑","",i,j,m));
                        }
                    }
                }


            }
        }

    }

    private LinearLayout createRowView(String textBegin, String textMiddle, String textEnd, String preTipText, String tipFiveElement, int beginIndex, int middleIndex, int endIndex)
    {
        LinearLayout linearLayout = createHolder();

        float beginWeight = beginIndex*2f;
        if(beginWeight != 0f) {
            linearLayout.addView(createSpace(beginWeight));
        }
        linearLayout.addView(createTextView(textBegin, 1f, Gravity.CENTER,true));

        float range1 = (middleIndex - beginIndex)*1f + (middleIndex-beginIndex-1)*1f;
        linearLayout.addView(createWrapper(preTipText, tipFiveElement, range1));

        linearLayout.addView(createTextView(textMiddle, 1f, Gravity.CENTER,true));

        float range2 = (endIndex - middleIndex)*1f + (endIndex-middleIndex - 1)*1f;
        linearLayout.addView(createWrapper(preTipText, tipFiveElement, range2));

        linearLayout.addView(createTextView(textEnd,1f,Gravity.CENTER,true));
        float endWeight = 11 - beginWeight - range1- range2 - 1f*3;
        if(endWeight != 0f) {
            linearLayout.addView(createSpace(endWeight));
        }

        return linearLayout;
    }

    private LinearLayout createRowView(String textBegin, String textEnd, String preTipText, String tipFiveElement, int beginIndex, int endIndex) {
        return createRowView(textBegin,textEnd,preTipText,tipFiveElement,beginIndex,endIndex,true);
    }


    private LinearLayout createRowView(String textBegin, String textEnd, String preTipText, String tipFiveElement, int beginIndex, int endIndex, boolean isTerrestrial)
    {
        LinearLayout linearLayout = createHolder();

        float beginWeight = beginIndex*2f;
        if(beginWeight != 0f) {
            linearLayout.addView(createSpace(beginWeight));
        }
        linearLayout.addView(createTextView(textBegin, 1f, Gravity.CENTER, isTerrestrial));

        float range = (endIndex - beginIndex)*1f + (endIndex-beginIndex-1)*1f;
        linearLayout.addView(createWrapper(preTipText, tipFiveElement, range));

        linearLayout.addView(createTextView(textEnd, 1f, Gravity.CENTER, isTerrestrial));

        float endWeight = 11 - beginWeight - range - 1f*2;
        if(endWeight != 0f) {
            linearLayout.addView(createSpace(endWeight));
        }

        return linearLayout;
    }

    private LinearLayout createHolder()
    {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setPadding(0, 3, 0, 0);
        return linearLayout;
    }

    private TextView createTextView(String text, float weight, int gravity, boolean isTerrestrial) {
        TextView textView = new TextView(activity);

        SpannableString str;
        if(isTerrestrial) {
           str =ColorHelper.getColorTerrestrial(activity, text);
        }
        else {
            str = ColorHelper.getColorCelestialStem(activity,text);
        }
        textView.setText(str);
        textView.setTextAppearance(activity, R.style.Style_CXHH_Text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        textView.setLayoutParams(params);
        textView.setGravity(gravity);
        return textView;
    }

    private LinearLayout createWrapper(String preText, String fiveElementEn, float weight)
    {
        LinearLayout linearLayout = new LinearLayout(activity,null,R.style.Style_CXHH_Wrapper);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(createTip(preText, fiveElementEn));
        linearLayout.addView(createLine());
        return linearLayout;
    }

    private TextView createTip(String preText, String text)
    {
        TextView tv = new TextView(activity);
        tv.setTextAppearance(activity, R.style.Style_CXHH_Tip);
        String chsStr = ColorHelper.getFiveElementChsByEn(text);
        SpannableString strTip = ColorHelper.getColorStr(text,preText+chsStr);
        tv.setText(strTip);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(params);
        return tv;
    }

    private TextView createLine()
    {
        TextView tv = new TextView(activity);
        tv.setTextAppearance(activity, R.style.Style_CXHH_Line);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        tv.setLayoutParams(params);
        tv.setBackgroundColor(Color.GRAY);
        return tv;
    }

    private LinearLayout createSpace(float weight)
    {
        LinearLayout linearLayout = new LinearLayout(activity,null,R.style.Style_CXHH_Wrapper);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        return linearLayout;
    }


}
