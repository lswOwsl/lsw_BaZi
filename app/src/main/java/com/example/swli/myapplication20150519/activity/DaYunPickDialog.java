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
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.ColorHelper;
import com.example.swli.myapplication20150519.common.LunarSolarTerm;
import com.example.swli.myapplication20150519.model.CallBackArgs;

import java.util.ArrayList;

/**
 * Created by swli on 6/10/2015.
 */
public class DaYunPickDialog {

    private GridView gridView;
    private Dialog dialog;
    private Activity activity;
    private ArrayList<Integer> source;
    private int beginYunAge;
    private int selectedEraIndex;

    private ICallBackDialog<CallBackArgs> callBackDialog;

    public void setCallBackDialog(ICallBackDialog<CallBackArgs> deleteButtonClick) {
        this.callBackDialog = deleteButtonClick;
    }

    public DaYunPickDialog(Activity activity, ArrayList<Integer> daYunsIndex, int beginYunAge, int selectedEraIndex) {
        this.activity = activity;
        this.source = daYunsIndex;
        this.beginYunAge = beginYunAge;
        this.selectedEraIndex = selectedEraIndex;
        init();
    }

    private void init() {
        dialog = new Dialog(activity,R.style.CustomDialog);
        //dialog.setCancelable(false);
        dialog.setContentView(R.layout.common_dayun_picker);
        int size = source.size();

        gridView = (GridView) dialog.findViewById(R.id.gv_daYun_dialog);
        DisplayMetrics dm = new DisplayMetrics();
        dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        //默认dialog样式有padding，所以设置成0
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
              dm.widthPixels-100, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth((dm.widthPixels-100)/5); // 设置列表项宽
        gridView.setGravity(Gravity.CENTER);
        //gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size/2); // 设置列数量=列表集合数

        gridView.setAdapter(getAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (callBackDialog != null) {
                    Integer daYunEraIndex = source.get(position);
                    CallBackArgs callBackArgs = new CallBackArgs();
                    callBackArgs.setDaYunEraIndex(daYunEraIndex);
                    callBackArgs.setCurrentAge(position*10+beginYunAge);
                    callBackDialog.onCall(callBackArgs);
                    dialog.dismiss();
                }
            }
        });

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
        win.setLayout(dm.widthPixels, dm.heightPixels/3);
    }

    private BaseAdapter getAdapter() {
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
                    view = layoutInflater.inflate(R.layout.common_dayun_picker_item, null);
                    controls.tvText = (TextView) view.findViewById(R.id.tv_dayun_text);
                    controls.tvAge = (TextView) view.findViewById(R.id.tv_dayun_age);
                    view.setTag(controls);
                } else {
                    controls = (PickerItem) view.getTag();
                }
                String c = LunarSolarTerm.getCelestialStemTextBy(source.get(i));
                String t = LunarSolarTerm.getTerrestrialBranch(source.get(i));
                String age = beginYunAge + i * 10 + "岁";

                controls.tvText.setText("");
                controls.tvText.append(ColorHelper.getColorCelestialStem(activity,c));
                controls.tvText.append("\n");
                controls.tvText.append(ColorHelper.getColorTerrestrial(activity,t));
                controls.tvAge.setText(age);

                if(selectedEraIndex == source.get(i))
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
