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
import com.example.swli.myapplication20150519.model.CallBackArgs;

import java.util.ArrayList;

/**
 * Created by swli on 6/10/2015.
 */
public class FlowYearPickDialog {
    private GridView gridView;
    private Dialog dialog;
    private Activity activity;
    private ArrayList<Integer> source;
    private int beginYunAge;
    private Integer currentAge;
    private Spinner spYearsRange;
    private int beginYunFirstEraIndex;

    private ICallBackDialog<CallBackArgs> callBackDialog;
    private int tempDaYunReturnValue;
    private int tempFlowYearReturnValue;
    private ArrayList<Integer> tempGVSource;
    private int birthdayYear;

    public void setCallBackDialog(ICallBackDialog<CallBackArgs> callbackAction) {
        this.callBackDialog = callbackAction;
    }

    public FlowYearPickDialog(Activity activity,  ArrayList<Integer> daYunsIndex,
                              int beginYunAge, int beginYunFirstEraIndex, Integer currentAge, int birthdayYear) {
        this.activity = activity;
        this.source = daYunsIndex;
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
                tempDaYunReturnValue = source.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Integer selectedDaYunEraIndex = null;
        if (currentAge != null) {
            selectedDaYunEraIndex = BaZiHelper.getDaYunByFlowYear(currentAge.intValue(), beginYunAge, source);

            int index = source.indexOf(selectedDaYunEraIndex);
            if (index != -1)
                spinner.setSelection(index);
        }
    }

    private BaseAdapter getSpinnerAdapter() {

        return new BaseAdapter() {
            private LayoutInflater layoutInflater = LayoutInflater.from(activity);

            @Override
            public int getCount() {
                return source.size();
            }

            @Override
            public Object getItem(int i) {
                return source.get(i);
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

                String c = LunarSolarTerm.getCelestialStemTextBy(source.get(i));
                String t = LunarSolarTerm.getTerrestrialBranch(source.get(i));

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
        BaseAdapter baseAdapter = getSpinnerAdapter();
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
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (callBackDialog != null) {
                        tempFlowYearReturnValue = tempGVSource.get(position);
                        CallBackArgs callBackArgs = new CallBackArgs();
                        callBackArgs.setDaYunEraIndex(tempDaYunReturnValue);
                        callBackArgs.setFlowYearEraIndex(tempFlowYearReturnValue);
                        callBackArgs.setCurrentAge(tempAtWhichYun+position);

                        callBackDialog.onCall(callBackArgs);
                        dialog.dismiss();
                    }
                }
            });

        }

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
                controls.tvAge.append((birthdayYear+ageBegin+i)+"年");

                if (currentAge == ageBegin + i)
                    view.setBackgroundResource(R.drawable.tv_border_rb_pressed);
                return view;
            }

            class PickerItem {
                public TextView tvText;
                public TextView tvAge;
            }
        };
    }
}
