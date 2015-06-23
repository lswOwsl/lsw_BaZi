package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.swli.myapplication20150519.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by swli on 5/21/2015.
 */
public class SpinnerHelper {

    private Context context;

    public  SpinnerHelper(Context context)
    {
        this.context = context;
    }

    public  void BindTerrestrial(Spinner spinner, String selected)
    {
        String[] array = context.getResources().getStringArray(R.array.diZhi);

        bind(spinner, array);
        if(selected != "") {
            List<String> list = Arrays.asList(array);
            int index = list.lastIndexOf(selected);
            spinner.setSelection(index);
        }
    }

    public void  BindCelestialStem(Spinner spinner, String selected)
    {
        String[] array = context.getResources().getStringArray(R.array.tianGan);

        bind(spinner, array);
        if(selected != "") {
            List<String> list = Arrays.asList(array);
            int index = list.indexOf(selected);
            spinner.setSelection(index);
        }
    }

    public void bind(Spinner spinner, String[] source) {

        // 建立数据源
        final String[] mItems = source;

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                Toast.makeText(context, "已选择:" + mItems[pos], 100).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        };


        bind(spinner, source, listener);

    }

    public void bind(Spinner spinner, String[] source, AdapterView.OnItemSelectedListener listener) {
        // 建立数据源
        final String[] mItems = source;
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(listener);
    }
}
