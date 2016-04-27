package lsw.liuyao.data.future;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lsw.library.DateExt;
import lsw.liuyao.R;

/**
 * Created by swli on 4/20/2016.
 */
public class FutureCodeSelectorDialog {

    public interface ICallBack
    {
        void invoke(String code);
    }

    private ICallBack callBack;
    public void setCallBack(ICallBack callBack)
    {
        this.callBack = callBack;
    }

    private NumberPicker npFutureCode;
    private NumberPicker npFutureCircle;

    private AlertDialog ad;
    private Activity activity;

    private int initFutureName =1, initCircle=0;

    static List<String> futureCirlecList = new ArrayList<String>();

    private String futureCode;

    public FutureCodeSelectorDialog(String futureCode, Activity activity)
    {
        getFutureNameList();
        getFutureCirclList();

        if(!futureCode.isEmpty()) {
            for (Integer integer : futureNameMapping.keySet()) {
                if (futureNameMapping.get(integer).equals(futureCode.trim().substring(0, 2))) {
                    initFutureName = integer;
                    break;
                }
            }

            for (int i = 0; i < futureCirlecList.size(); i++) {
                if (futureCode.trim().substring(2).equals(futureCirlecList.get(i))) {
                    initCircle = i;
                    break;
                }
            }
        }

        this.futureCode = futureCode;
        this.activity = activity;
    }

    static List<String> futureNameTextList;
    static HashMap<Integer,String> futureNameMapping = new HashMap<Integer, String>();

    public List<String> getFutureNameList()
    {
        if(futureNameTextList == null) {
            futureNameTextList = new ArrayList<String>();

            futureNameTextList.add("鸡蛋");
            futureNameTextList.add("菜粕");
            futureNameTextList.add("豆粕");
            futureNameTextList.add("螺纹");
            futureNameTextList.add("玉米");
            futureNameTextList.add("郑煤");


            futureNameMapping.put(1, "JD");
            futureNameMapping.put(2, "RM");
            futureNameMapping.put(3, "M");
            futureNameMapping.put(4, "RB");
            futureNameMapping.put(5, "C");
            futureNameMapping.put(6, "ZC");

        }
        return futureNameTextList;
    }

    private List<String> getFutureCirclList() {
        futureCirlecList.clear();

        DateExt dateExt = new DateExt();
        int year = dateExt.getYear();

        for(int i = year; i>year-3; i-- )
        {
            for(int j=1; j<=12; j++)
            {
                DateExt de = new DateExt(i,j,1,0,0,0);
                String code = de.getFormatDateTime("yyMM");
                futureCirlecList.add(code);
            }
        }

        futureCirlecList.add("0");

        return futureCirlecList;
    }

    public AlertDialog show() {

        View dateTimeLayout = LayoutInflater.from(activity).inflate(R.layout.future_code_selector, null);

        npFutureCircle = (NumberPicker) dateTimeLayout.findViewById(R.id.npCircle);
        npFutureCode = (NumberPicker) dateTimeLayout.findViewById(R.id.npName);

        init();

        npFutureCode.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                initFutureName = i1;
                //futureCirlecList = getFutureCirclList();
            }
        });

        npFutureCircle.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                initCircle = i1-1;
            }
        });

        ad = new AlertDialog.Builder(this.activity)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(callBack != null) {
                            String futureCode = futureNameMapping.get(initFutureName) + futureCirlecList.get(initCircle);
                            callBack.invoke(futureCode);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();

        return ad;
    }

    private void init() {

        initFutureName();
        initFutureCircle();
    }

    private void initFutureName()
    {
        npFutureCode.setDisplayedValues(getFutureNameList().toArray(new String []{}));
        npFutureCode.setMinValue(1);
        npFutureCode.setMaxValue(futureNameMapping.size());
        npFutureCode.setValue(initFutureName);
    }

    private void initFutureCircle()
    {
        npFutureCircle.setDisplayedValues(null);
        npFutureCircle.setMinValue(1);
        npFutureCircle.setMaxValue(futureCirlecList.size());
        npFutureCircle.setDisplayedValues(futureCirlecList.toArray(new String[]{}));
        npFutureCircle.setValue(initCircle+1);
    }

}
