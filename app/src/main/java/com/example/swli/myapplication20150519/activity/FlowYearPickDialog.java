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

/**
 * Created by swli on 6/10/2015.
 */
public class FlowYearPickDialog {
    private GridView gridView;
    private Dialog dialog;
    private Activity activity;
    private ArrayList<Integer> sourceDaYunsIndex;
    private ArrayList<SolarTerm> sourceFlowMonthIndex;
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

    private LunarSolarTerm lunarSolarTerm = new LunarSolarTerm();

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

        init();
    }

    private void setCursListener(Spinner spinner, BaseAdapter baseAdapter) {

        spinner.setAdapter(baseAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int tempAge = beginYunAge + position*10;
                String tempStr = ": "+ tempAge+"岁"+"-----"+(tempAge+9)+"岁";
                Toast.makeText(activity, "已选择" + tempStr, Toast.LENGTH_SHORT).show();
                initGridView(tempAge);
                tempDaYunReturnValue = sourceDaYunsIndex.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Integer selectedDaYunEraIndex = null;
        if (currentAge != null) {
            selectedDaYunEraIndex = BaZiHelper.getDaYunByFlowYear(currentAge.intValue(), beginYunAge, sourceDaYunsIndex);

            int index = sourceDaYunsIndex.indexOf(selectedDaYunEraIndex);
            if (index != -1)
                spinner.setSelection(index);
        }
    }
    private BaseAdapter getFlowMonthSpinnerAdapter()
    {
        return new BaseAdapter() {

            int beginMonthEraIndex = -1;
            private LayoutInflater layoutInflater = LayoutInflater.from(activity);

            @Override
            public int getCount() {
                return sourceFlowMonthIndex.size()-1;
            }

            @Override
            public Object getItem(int i) {
                return sourceFlowMonthIndex.get(i);
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
                    view = layoutInflater.inflate(R.layout.common_flowmonth_range_textview, null);
                    controls.tvTextItem = (TextView) view.findViewById(R.id.tv_flowmonth_item);
                    controls.tvTextMonthTip = (TextView) view.findViewById(R.id.tv_flowmonth_tip);
                    view.setTag(controls);
                } else {
                    controls = (PickerItem) view.getTag();
                }

                //因为12个月的eraIndex是顺序来的所以得到第一个立春的后面就不用再算了，加1就行了
                if(beginMonthEraIndex ==-1) {
                    beginMonthEraIndex = lunarSolarTerm.getChineseEraOfMonth(sourceFlowMonthIndex.get(i).getSolarTermDate());
                }
                else
                {
                    beginMonthEraIndex +=1;
                }
                String c = LunarSolarTerm.getCelestialStemTextBy(beginMonthEraIndex);
                String t = LunarSolarTerm.getTerrestrialBranch(beginMonthEraIndex);

                controls.tvTextItem.setText("");
                controls.tvTextItem.append(ColorHelper.getColorCelestialStem(activity, c));
                controls.tvTextItem.append(ColorHelper.getColorTerrestrial(activity, t));
                controls.tvTextItem.append(":");

                SolarTerm tempSt1 =sourceFlowMonthIndex.get(i);
                String tempStr1 = tempSt1.getSolarTermDate().getFormatDateTime("yyyy/MM/dd");
                //SolarTerm tempSt2 = sourceFlowMonthIndex.get(i+1);
                //String tempStr2 = tempSt2.getSolarTermDate().getFormatDateTime("yyyy/MM/dd");

                String tempStr = tempStr1 + "---";

                controls.tvTextMonthTip.setText(tempStr);

                return view;
            }

            class PickerItem {
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

                String c = LunarSolarTerm.getCelestialStemTextBy(sourceDaYunsIndex.get(i));
                String t = LunarSolarTerm.getTerrestrialBranch(sourceDaYunsIndex.get(i));

                controls.tvText.setText("");
                controls.tvText.append(ColorHelper.getColorCelestialStem(activity, c));
                controls.tvText.append(ColorHelper.getColorTerrestrial(activity, t));
                controls.tvText.append(":");

                controls.tvTextTCRange.setText(ColorHelper.getSmalllerText(activity, tempStr,1));
                controls.tvTextYearRange.setText(ColorHelper.getSmalllerText(activity, "" + (birthdayYear + tempAge) + "年-----" + (birthdayYear + tempAge + 9) + "年",0.6f));

                return view;
            }

            class PickerItem {
                public TextView tvText;
                public TextView tvTextYearRange;
                public TextView tvTextTCRange;
            }
        };
    }

    private void init() {
        dialog = new Dialog(activity,R.style.CustomDialog);
        dialog.setContentView(R.layout.common_flowyear_picker);
        spYearsRange = (Spinner) dialog.findViewById(R.id.spYearsRange);
        spMonthesRange = (Spinner)dialog.findViewById(R.id.spMonthesRange);
        BaseAdapter baseAdapter = getDaYunSpinnerAdapter();
        setCursListener(spYearsRange,baseAdapter);


        gridView = (GridView) dialog.findViewById(R.id.gv_flowYear_dialog);
        initGridView(this.currentAge);

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

    private void initGridView(Integer currentAge)
    {
        final ArrayList<Integer> gvSource = new ArrayList<Integer>();

        if(currentAge != null) {

            int ageSub = currentAge - beginYunAge;
            //当前运下的第几年
            int modAtWhichYun = ageSub%10;
            int atWhichYun = ageSub/10;
            if(atWhichYun != 0) {
                atWhichYun = atWhichYun*10;
            }

            int yunAgeBeginEraIndex = atWhichYun+beginYunFirstEraIndex;
            int yunAgeEnd = beginYunAge + 10;


            for(int i=0; i< 10; i++)
            {
                gvSource.add(yunAgeBeginEraIndex + i);
            }

            tempGVSource = gvSource;
            final int tempAtWhichYun = atWhichYun + beginYunAge;

            gridView.setAdapter(getAdapterGridView(gvSource,atWhichYun+beginYunAge));
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
                    for(int index=0;index<childCount;index++)
                    {
                        adapterView.getChildAt(index).setBackgroundResource(R.drawable.tv_border_rb_unpressed);
                    }
                    view.setBackgroundResource(R.drawable.tv_border_rb_pressed);

                    initFlowMonth(i);
                }

            });

        }
    }
    public void initFlowMonth(int flowYearIndex)
    {

        int ageSub = currentAge - beginYunAge;
        int atWhichYun = ageSub/10;
        if(atWhichYun != 0) {
            atWhichYun = atWhichYun*10;
        }
        int tempAtWhichYun = atWhichYun + beginYunAge;

        //选中当年年的时候查询对应年的月
        sourceFlowMonthIndex = BaZiHelper.getSolarTermsInLunarYear(birthdayYear+tempAtWhichYun+flowYearIndex);
        BaseAdapter baseAdapter = getFlowMonthSpinnerAdapter();
        spMonthesRange.setAdapter(baseAdapter);
    }

    public void show() {
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
    private BaseAdapter getAdapterGridView(final ArrayList<Integer> gvSource, final int ageBegin) {
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
                String c = LunarSolarTerm.getCelestialStemTextBy(gvSource.get(i));
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
                controls.tvAge.append(year+"年");

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
}
