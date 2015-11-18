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
import com.example.swli.myapplication20150519.common.EnumPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import lsw.library.ColorHelper;
import lsw.xml.XmlModelCache;
import lsw.xml.model.XmlModelCelestialStem;
import lsw.xml.model.XmlModelTerrestrial;

public class MemberAnalyzeViewPager_XCHH {

    private MemberAnalyzeViewPager viewPager;
    private LinearLayout linearLayout;
    private Activity activity;
    private XmlModelTerrestrial xmlTerrestrial;
    private XmlModelCelestialStem xmlCelestialStem;
    private LinearLayout linearLayoutC;
    private View cChongHe;
    private boolean haveFlowMonth;
    private XmlModelCache xmlModelCache;
    private ColorHelper colorHelper;

    public MemberAnalyzeViewPager_XCHH(MemberAnalyzeViewPager viewPager)
    {
        this.viewPager = viewPager;
        this.activity = viewPager.getActivity();
        this.linearLayout = (LinearLayout)viewPager.getActivity().findViewById(R.id.ll_container);
        xmlModelCache = XmlModelCache.getInstance(activity);
        colorHelper = ColorHelper.getInstance(activity);
        xmlTerrestrial = xmlModelCache.getTerrestrial();
        xmlCelestialStem = xmlModelCache.getCelestialStem();

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

        buildControls(arrays,arraysC);
        buildControlsC(arraysC,arrays);
    }

    //天干
    private void buildControlsC(ArrayList<String> arrayList, ArrayList<String> arrayListT)
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
                    String tip = "合";
                    boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                    boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                    if(option1 || option2)
                    {
                        for(Pair<String,String> keyT: xmlTerrestrial.getSixSuits().keySet()) {
                            boolean option1T = arrayListT.get(i).equals(keyT.first) && arrayListT.get(j).equals(keyT.second);
                            boolean option2T = arrayListT.get(i).equals(keyT.second) && arrayListT.get(j).equals(keyT.first);
                            //地支合
                            if (option1T || option2T) {
                                tip = "双" + tip;
                            }
                        }
                        list.add(createRowView(arrayList.get(i), arrayList.get(j), tip, xmlCelestialStem.getPairSuits().get(key), i, j,false));
                    }
                }

                for(Pair<String,String> key: xmlCelestialStem.getPairInverses())
                {
                    String tip = "冲";

                    boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                    boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                    if(option1 || option2)
                    {
                        for(Pair<String,String> keyT: xmlTerrestrial.getSixInverses())
                        {
                            boolean option1T = arrayListT.get(i).equals(keyT.first) && arrayListT.get(j).equals(keyT.second);
                            boolean option2T = arrayListT.get(i).equals(keyT.second) && arrayListT.get(j).equals(keyT.first);
                            if(option1T || option2T)
                            {
                                tip = "双" + tip;
                            }
                        }
                        list.add(createRowView(arrayList.get(i), arrayList.get(j), tip, "", i, j,false));
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
    private void buildControls(ArrayList<String> arrayList, ArrayList<String> arrayListC)
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
                    //地支合
                    if(option1 || option2)
                    {
                        String tip = "合";
                        //天干是不是也合
                        for(Pair<String,String> keyC: xmlCelestialStem.getPairSuits().keySet())
                        {
                            boolean option1C = arrayListC.get(i).equals(keyC.first) && arrayListC.get(j).equals(keyC.second);
                            boolean option2C = arrayListC.get(i).equals(keyC.second) && arrayListC.get(j).equals(keyC.first);
                            if(option1C || option2C)
                            {
                                tip = "双" + tip;
                            }
                        }

                        linearLayout.addView(createRowView(arrayList.get(i), arrayList.get(j), tip, xmlTerrestrial.getSixSuits().get(key), i, j));
                    }
                }
                //六冲
                for(Pair<String,String> key: xmlTerrestrial.getSixInverses())
                {
                    boolean option1 = arrayList.get(i).equals(key.first) && arrayList.get(j).equals(key.second);
                    boolean option2 = arrayList.get(i).equals(key.second) && arrayList.get(j).equals(key.first);
                    if(option1 || option2)
                    {
                        String tip = "冲";

                        for(Pair<String,String> keyC: xmlCelestialStem.getPairInverses())
                        {
                            boolean option1C = arrayListC.get(i).equals(keyC.first) && arrayListC.get(j).equals(keyC.second);
                            boolean option2C = arrayListC.get(i).equals(keyC.second) && arrayListC.get(j).equals(keyC.first);
                            if(option1C || option2C)
                            {
                                tip = "双" + tip;
                            }
                        }

                        linearLayout.addView(createRowView(arrayList.get(i), arrayList.get(j), tip, "", i, j));
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

                    for(int n=m+1; n<arrayList.size(); n++)
                    {
                        for(ArrayList<String> array: xmlTerrestrial.getFourPunishment())
                        {
                            ArrayList<String> tempArray = new ArrayList<String>();
                            for(String str: array)
                            {
                                tempArray.add(str);
                            }

                            int index1 = tempArray.indexOf(arrayList.get(i));
                            if(index1 != -1)
                                tempArray.remove(index1);
                            int index2 = tempArray.indexOf(arrayList.get(j));
                            if(index2 != -1)
                                tempArray.remove(index2);
                            int index3 = tempArray.indexOf(arrayList.get(m));
                            if(index3 != -1)
                                tempArray.remove(index3);
                            int index4 = tempArray.indexOf(arrayList.get(n));
                            if(index4 != -1)
                                tempArray.remove(index4);

                            if(tempArray.size() == 0)
                            {
                                linearLayout.addView(createRowView(arrayList.get(i),arrayList.get(j),arrayList.get(m),arrayList.get(n), "四角刑",i,j,m,n));
                            }
                        }
                    }
                }
            }
        }

    }

    private LinearLayout createRowView(String text1, String text2, String text3, String text4,
                                       String tipText, int beginIndex, int secondIndex, int thirdIndex, int endIndex)
    {
        LinearLayout linearLayout = createHolder();

        float beginWeight = beginIndex*2f;
        if(beginWeight != 0f) {
            linearLayout.addView(createSpace(beginWeight));
        }
        linearLayout.addView(createTextView(text1, 1f, Gravity.CENTER, true));

        float range1 = (secondIndex - beginIndex)*1f + (secondIndex-beginIndex-1)*1f;
        linearLayout.addView(createWrapper(tipText, "", range1));

        linearLayout.addView(createTextView(text2, 1f, Gravity.CENTER,true));

        float range2 = (thirdIndex - secondIndex)*1f + (thirdIndex-secondIndex - 1)*1f;
        linearLayout.addView(createWrapper(tipText, "", range2));

        linearLayout.addView(createTextView(text3, 1f, Gravity.CENTER, true));

        float rang3 = (endIndex - thirdIndex)*1f + (endIndex - thirdIndex -1)*1f;
        linearLayout.addView(createWrapper(tipText, "", rang3));

        linearLayout.addView(createTextView(text4,1f,Gravity.CENTER,true));

        float endWeight = 11 - beginWeight - range1- range2 - rang3 - 1f*4;
        if(endWeight != 0f) {
            linearLayout.addView(createSpace(endWeight));
        }

        return linearLayout;
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
           str =colorHelper.getColorTerrestrial(text);
        }
        else {
            str = colorHelper.getColorCelestialStem(text);
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
