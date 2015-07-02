package com.example.swli.myapplication20150519.activity;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.BaZiHelper;
import com.example.swli.myapplication20150519.common.ColorHelper;
import com.example.swli.myapplication20150519.common.LunarSolarTerm;
import com.example.swli.myapplication20150519.common.SolarTerm;
import com.example.swli.myapplication20150519.model.CallBackArgs;

import java.util.ArrayList;
import java.util.HashMap;

public class FlowYearPickDialog {
    private GridView gridView;
    private Dialog dialog;
    private Activity activity;
    private ArrayList<Integer> sourceDaYunsIndex;
    private ArrayList<SolarTerm> sourceFlowMonthSolarTerms;
    private int beginYunAge;
    private Integer currentAge;
    private Spinner spYearsRange;
    private Spinner spMonthesRange;
    private int beginYunFirstEraIndex;

    private ICallBackDialog<CallBackArgs> callBackDialog;
    private int tempDaYunReturnValue;
    private int tempFlowYearReturnValue;
    private ArrayList<Integer> tempGVSource;
    private int birthdayYear;

    private SolarTerm currentMonth;

    private LunarSolarTerm lunarSolarTerm = new LunarSolarTerm();

    public void setCurrentMonth(SolarTerm solarTerm)
    {
        this.currentMonth = solarTerm;
    }

    public void setCallBackDialog(ICallBackDialog<CallBackArgs> callbackAction) {
        this.callBackDialog = callbackAction;
    }

    public FlowYearPickDialog(Activity activity,  ArrayList<Integer> daYunsIndex,
                              int beginYunAge, int beginYunFirstEraIndex, Integer currentAge, int birthdayYear) {
        this.activity = activity;
        this.sourceDaYunsIndex = daYunsIndex;
        this.beginYunAge = beginYunAge;
        this.currentAge = currentAge;
        this.beginYunFirstEraIndex = beginYunFirstEraIndex;
        this.birthdayYear = birthdayYear;
    }


    private int getAtWhichYun() {
        int ageSub = currentAge - beginYunAge;
        int atWhichYun = ageSub / 10;
        if (atWhichYun != 0) {
            atWhichYun = atWhichYun * 10;
        }
        return atWhichYun;
    }

    private void init() {
        dialog = new Dialog(activity,R.style.CustomDialog);
        dialog.setContentView(R.layout.common_flowyear_picker);
        spYearsRange = (Spinner) dialog.findViewById(R.id.spYearsRange);
        spMonthesRange = (Spinner)dialog.findViewById(R.id.spMonthesRange);

        BaseAdapter baseAdapter = getDaYunSpinnerAdapter();
        initDaYun(spYearsRange, baseAdapter);

        gridView = (GridView) dialog.findViewById(R.id.gv_flowYear_dialog);

        //因为有可能没起运，所以设定当前年龄为起运年龄
        if (currentAge < beginYunAge)
            currentAge = beginYunAge;

        initFlowYear(this.currentAge);

        //int size = gvSource.size();
        DisplayMetrics dm = new DisplayMetrics();
        dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //默认dialog样式有padding，所以设置成0
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dm.widthPixels-100, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth((dm.widthPixels - 100) / 5); // 设置列表项宽
        //gridView.setGravity(Gravity.CENTER);
        //gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(5); // 设置列数量=列表集合数

    }
    private void initDaYun(Spinner spinner, BaseAdapter baseAdapter) {

        spinner.setAdapter(baseAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstLoad = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(isFirstLoad)
                {
                    isFirstLoad = false;
                    return;
                }

                int tempAge = beginYunAge + position*10;
                String tempStr = ": "+ tempAge+"岁"+"-----"+(tempAge+9)+"岁";
                Toast.makeText(activity, "已选择" + tempStr, Toast.LENGTH_SHORT).show();
                tempDaYunReturnValue = sourceDaYunsIndex.get(position);
                //这个是用来选定大运后，默认成该大运下头一年被选中，在view的adpter里会对全局的currentAge做比较
                currentAge = tempAge;
                initFlowYear(tempAge);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Integer selectedDaYunEraIndex;
        if (currentAge != null) {
            selectedDaYunEraIndex = BaZiHelper.getDaYunByFlowYear(currentAge.intValue(), beginYunAge, sourceDaYunsIndex);
            int index = sourceDaYunsIndex.indexOf(selectedDaYunEraIndex);
            if (index != -1)
                spinner.setSelection(index);

            tempDaYunReturnValue = selectedDaYunEraIndex;
        }
    }
    private void initFlowYear(Integer currentAge) {
        final ArrayList<Integer> gvSource = new ArrayList<Integer>();

        if(currentAge != null) {

            int ageSub = currentAge - beginYunAge;
            //当前运下的第几年
            int modAtWhichYun = ageSub % 10;
            int atWhichYun = ageSub / 10;
            if (atWhichYun != 0) {
                atWhichYun = atWhichYun * 10;
            }

            int yunAgeBeginEraIndex = atWhichYun + beginYunFirstEraIndex;
            int yunAgeEnd = beginYunAge + 10;


            for (int i = 0; i < 10; i++) {
                gvSource.add(yunAgeBeginEraIndex + i);
            }

            tempGVSource = gvSource;
            final int tempAtWhichYun = atWhichYun + beginYunAge;

            gridView.setAdapter(getFlowYearGridViewAdapter(gvSource, atWhichYun + beginYunAge));
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                    if (callBackDialog != null) {
                        tempFlowYearReturnValue = tempGVSource.get(position);
                        CallBackArgs callBackArgs = new CallBackArgs();
                        callBackArgs.setDaYunEraIndex(tempDaYunReturnValue);
                        callBackArgs.setFlowYearEraIndex(tempFlowYearReturnValue);
                        callBackArgs.setCurrentAge(tempAtWhichYun + position);

                        callBackDialog.onCall(callBackArgs);
                        dialog.dismiss();

                        return true;
                    }
                    return false;
                }
            });

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(activity, "test", Toast.LENGTH_SHORT).show();
                    int childCount = adapterView.getChildCount();
                    for (int index = 0; index < childCount; index++) {
                        adapterView.getChildAt(index).setBackgroundResource(R.drawable.tv_border_rb_unpressed);
                    }
                    view.setBackgroundResource(R.drawable.tv_border_rb_pressed);

                    tempFlowYearReturnValue = tempGVSource.get(i);
                    CallBackArgs callBackArgs = new CallBackArgs();
                    callBackArgs.setDaYunEraIndex(tempDaYunReturnValue);
                    callBackArgs.setFlowYearEraIndex(tempFlowYearReturnValue);
                    callBackArgs.setCurrentAge(tempAtWhichYun + i);

                    initFlowMonth(i, getAtWhichYun(), callBackArgs);
                }

            });

            CallBackArgs callBackArgs = new CallBackArgs();
            callBackArgs.setCurrentAge(currentAge);
            callBackArgs.setDaYunEraIndex(tempDaYunReturnValue);
            int tempAtWhichDaYun = getAtWhichYun();
            int flowYearIndex = currentAge - (tempAtWhichDaYun + beginYunAge);

            tempFlowYearReturnValue = tempGVSource.get(flowYearIndex % 10);
            callBackArgs.setFlowYearEraIndex(tempFlowYearReturnValue);
            //为了算出来当前的年的索引currentAge-(atWhichYun+ beginYunAge)，为了跟在每一年上调用一至，所以减来减去
            initFlowMonth(flowYearIndex, tempAtWhichDaYun, callBackArgs);

        }
    }
    private void initFlowMonth(int flowYearIndex, int atWhichYun, final CallBackArgs callBackArgs) {

        final int tempAtWhichYun = atWhichYun + beginYunAge;

        //选中当年年的时候查询对应年的月
        sourceFlowMonthSolarTerms = BaZiHelper.getSolarTermsInLunarYear(birthdayYear + tempAtWhichYun + flowYearIndex);
        sourceFlowMonthSolarTerms.add(0,null);
        BaseAdapter baseAdapter = getFlowMonthSpinnerAdapter(callBackArgs);
        spMonthesRange.setAdapter(baseAdapter);

        //阻止第一次加载调用
        spMonthesRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstLoad = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isFirstLoad)
                {
                    isFirstLoad = false;
                    return;
                }

                if (callBackDialog != null) {
                    callBackArgs.setFlowMonthSolarTerm(sourceFlowMonthSolarTerms.get(position));
                    callBackArgs.setFlowMonthEraIndex(getSolarTermIndexs(sourceFlowMonthSolarTerms.get(position)));
                    callBackArgs.setIsFlowMonthClick(true);
                    callBackDialog.onCall(callBackArgs);
                    dialog.dismiss();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(currentMonth != null) {
            for (int i=0; i<sourceFlowMonthSolarTerms.size(); i++)
            {
                SolarTerm solarTerm = sourceFlowMonthSolarTerms.get(i);
                if(solarTerm != null &&
                        solarTerm.getName().equals(currentMonth.getName()) &&
                        solarTerm.getSolarTermDate().getFormatDateTime().equals(currentMonth.getSolarTermDate().getFormatDateTime())) {
                    spMonthesRange.setSelection(i);
                    break;
                }
            }
        }
    }

    private BaseAdapter getFlowYearGridViewAdapter(final ArrayList<Integer> gvSource, final int ageBegin) {
        return new BaseAdapter() {
            private LayoutInflater layoutInflater = LayoutInflater.from(activity);

            @Override
            public int getCount() {
                return gvSource.size();
            }

            @Override
            public Object getItem(int i) {
                return gvSource.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                PickerItem controls;
                if (view == null) {
                    controls = new PickerItem();
                    view = layoutInflater.inflate(R.layout.common_flowyear_picker_item, null);
                    controls.tvText = (TextView) view.findViewById(R.id.tv_flowyear_text);
                    controls.tvAge = (TextView) view.findViewById(R.id.tv_flowyear_age);
                    view.setTag(controls);
                } else {
                    controls = (PickerItem) view.getTag();
                }
                String c = LunarSolarTerm.getCelestialStem(gvSource.get(i));
                String t = LunarSolarTerm.getTerrestrialBranch(gvSource.get(i));
                String age = ageBegin + i + "岁";

                controls.tvText.setText("");
                controls.tvAge.setText("");
                controls.tvText.append(ColorHelper.getColorCelestialStem(activity, c));
                controls.tvText.append("\n");
                controls.tvText.append(ColorHelper.getColorTerrestrial(activity, t));
                controls.tvAge.setText(age);
                controls.tvAge.append("\n");
                int year = birthdayYear+ageBegin+i;
                controls.tvAge.append(year + "年");

                if (currentAge == ageBegin + i) {
                    view.setBackgroundResource(R.drawable.tv_border_rb_pressed);
                }
                return view;
            }

            class PickerItem {
                public TextView tvText;
                public TextView tvAge;
            }
        };
    }
    private BaseAdapter getFlowMonthSpinnerAdapter(final CallBackArgs callBackArgs) {
        return new BaseAdapter() {

            //int beginMonthEraIndex = -1;
            private LayoutInflater layoutInflater = LayoutInflater.from(activity);

            @Override
            public int getCount() {
                //第一个是默认的空，最后一个是默认的下一年的第二月
                return sourceFlowMonthSolarTerms.size()-1;
            }

            @Override
            public Object getItem(int i) {
                return sourceFlowMonthSolarTerms.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                PickerItemMonth controls;
                if (view == null) {
                    controls = new PickerItemMonth();
                    view = layoutInflater.inflate(R.layout.common_flowmonth_range_textview, null);
                    controls.tvTextItem = (TextView) view.findViewById(R.id.tv_flowmonth_item);
                    controls.tvTextMonthTip = (TextView) view.findViewById(R.id.tv_flowmonth_tip);
                    view.setTag(controls);
                } else {
                    controls = (PickerItemMonth) view.getTag();
                }

                if(i != 0) {

                    //beginMonthEraIndex = lunarSolarTerm.getChineseEraOfMonth(sourceFlowMonthSolarTerms.get(i).getSolarTermDate());

                    String c = LunarSolarTerm.getCelestialStem(getSolarTermIndexs(sourceFlowMonthSolarTerms.get(i)));
                    String t = LunarSolarTerm.getTerrestrialBranch(getSolarTermIndexs(sourceFlowMonthSolarTerms.get(i)));

                    controls.tvTextItem.setText("");
                    controls.tvTextItem.append(ColorHelper.getColorCelestialStem(activity, c));
                    controls.tvTextItem.append(ColorHelper.getColorTerrestrial(activity, t));
                    controls.tvTextItem.append(": ");

                    SolarTerm tempSt1 = sourceFlowMonthSolarTerms.get(i);
                    String tempStr1 = tempSt1.getSolarTermDate().getFormatDateTime("MM/dd");
                    SolarTerm tempSt2 = sourceFlowMonthSolarTerms.get(i + 1);
                    String tempStr2 = tempSt2.getSolarTermDate().getFormatDateTime("MM/dd");

                    String tempStr = sourceFlowMonthSolarTerms.get(i).getSolarTermDate().getFormatDateTime("MM月 (") + tempStr1 + "---" + tempStr2 + ")";

                    controls.tvTextMonthTip.setText(ColorHelper.getSmalllerText(activity, tempStr, 0.6f));
                }
                else
                {
                    controls.tvTextItem.setText("干支: ");
                    controls.tvTextMonthTip.setText(ColorHelper.getSmalllerText(activity, "月份 (周期)", 0.6f));
                }
                return view;
            }

            class PickerItemMonth {
                public TextView tvTextItem;
                public TextView tvTextMonthTip;
            }
        };
    }
    private BaseAdapter getDaYunSpinnerAdapter() {

        return new BaseAdapter() {
            private LayoutInflater layoutInflater = LayoutInflater.from(activity);

            @Override
            public int getCount() {
                return sourceDaYunsIndex.size();
            }

            @Override
            public Object getItem(int i) {
                return sourceDaYunsIndex.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                PickerItem controls;
                if (view == null) {
                    controls = new PickerItem();
                    view = layoutInflater.inflate(R.layout.common_dayun_range_textview, null);
                    controls.tvText = (TextView) view.findViewById(R.id.tv_dayun_range);
                    controls.tvTextTCRange = (TextView) view.findViewById(R.id.tv_dayun_range_tc);
                    controls.tvTextYearRange = (TextView) view.findViewById(R.id.tv_dayun_range_year);
                    view.setTag(controls);
                } else {
                    controls = (PickerItem) view.getTag();
                }

                int tempAge = beginYunAge + i*10;
                String tempStr = tempAge+"岁"+"-----"+(tempAge+9)+"岁";

                String c = LunarSolarTerm.getCelestialStem(sourceDaYunsIndex.get(i));
                String t = LunarSolarTerm.getTerrestrialBranch(sourceDaYunsIndex.get(i));

                controls.tvText.setText("");
                controls.tvText.append(ColorHelper.getColorCelestialStem(activity, c));
                controls.tvText.append(ColorHelper.getColorTerrestrial(activity, t));
                controls.tvText.append(":");

                controls.tvTextTCRange.setText(ColorHelper.getSmalllerText(activity, tempStr, 1));
                controls.tvTextYearRange.setText(ColorHelper.getSmalllerText(activity, "" + (birthdayYear + tempAge) + "年-----" + (birthdayYear + tempAge + 9) + "年", 0.6f));

                return view;
            }

            class PickerItem {
                public TextView tvText;
                public TextView tvTextYearRange;
                public TextView tvTextTCRange;
            }
        };
    }

    private HashMap<SolarTerm,Integer> solarTermIndexs;
    private int getSolarTermIndexs(SolarTerm solarTerm)
    {
        if(solarTermIndexs == null) {
            solarTermIndexs = new HashMap<SolarTerm, Integer>();
            //因为第0个索引是用来创建“请流月”的空值，所以直接得第1个
            int eraIndex = lunarSolarTerm.getChineseEraOfMonth(sourceFlowMonthSolarTerms.get(1).getSolarTermDate());
            for (int i = 1; i < sourceFlowMonthSolarTerms.size(); i++) {
                solarTermIndexs.put(sourceFlowMonthSolarTerms.get(i), eraIndex + (i - 1));
            }
        }

        if(solarTermIndexs.containsKey(solarTerm))
            return solarTermIndexs.get(solarTerm);
        else
        {
            return lunarSolarTerm.getChineseEraOfMonth(solarTerm.getSolarTermDate());
        }
    }

    public void show() {

        init();

        dialog.show();
        WindowManager windowManager = activity.getWindowManager();

        Display display = windowManager.getDefaultDisplay();
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.CustomDialog_Animation);
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        win.setLayout(dm.widthPixels, dm.heightPixels / 2);
    }
}
