package com.example.swli.myapplication20150519.activity.calendar;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.DateExt;

import java.util.Calendar;

public class CalendarFragment extends Fragment {

    DateExt dateExt;
    LayoutInflater linearLayout;
    static String paramDate;
    private GridView gridView;

    public GridView getGridView()
    {
        return this.gridView;
    }
    public void setDateExt(DateExt dateExt) {
        this.dateExt = dateExt;
    }

    public static CalendarFragment newInstance(DateExt dateExt) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(paramDate,dateExt.getFormatDateTime());
        fragment.setArguments(args);
        return fragment;
    }

    public CalendarFragment() {}

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(DateExt dateExt);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView preTextView, todayTextView;

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        //因为滑动刷新后不清空这两个值，会造成line92两个月份一样的问题
        if(!hidden)
            preTextView = todayTextView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        long startTimeM = System.currentTimeMillis();

        if (getArguments() != null) {
            dateExt = new DateExt(getArguments().getString(paramDate));
        }

        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        linearLayout = LayoutInflater.from(getActivity());
        gridView = (GridView) view.findViewById(R.id.gv_calendar);

        final CalendarAdapter calendarAdapter = new CalendarAdapter(linearLayout, dateExt);
        gridView.setAdapter(calendarAdapter);
        gridView.setNumColumns(7);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DateExt selectedDate = calendarAdapter.getDayModels().get(i).getDateExt();
                TextView tvDay = (TextView) view.findViewById(R.id.tvDay);

                //如果不等于当前月，清空上一个选中的控件
                if(dateExt.getMonth() != selectedDate.getMonth()) {
                    preTextView = todayTextView = null;
                }
                else
                {
                    //当前月份，有没有上次一选中的天
                    if(preTextView != null) {
                        preTextView.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
                        preTextView.setTextColor(Color.BLACK);
                    }
                    else
                    {
                        //清空跳转后默认选中的日子
                        int seletedTextViewIndex = 0;
                        for (DayModel dayModel: calendarAdapter.getDayModels())
                        {
                            //得到的永远是第一个当月刷新出来的那一天，不是点了好多次选的日期
                            if(dayModel.isSelected())
                            {
                                View view1 = adapterView.getChildAt(seletedTextViewIndex);
                                TextView tvDay1 = (TextView) view1.findViewById(R.id.tvDay);
                                tvDay1.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
                                tvDay1.setTextColor(Color.BLACK);
                                break;
                            }
                            seletedTextViewIndex ++;
                        }

                    }

                    //高亮当天
                    if(selectedDate.getFormatDateTime("yyyyMMdd").equals(new DateExt().getFormatDateTime("yyyyMMdd")))
                    {
                        todayTextView = tvDay;
                    }
                    else
                    {
                        if(todayTextView != null)
                        {
                            todayTextView.setBackgroundResource(R.drawable.tv_circle_highlight);
                            todayTextView.setTextColor(Color.WHITE);
                        }
                        else
                        {
                            //清空跳转后默认选中的日子
                            int seletedTextViewIndex = 0;
                            for (DayModel dayModel: calendarAdapter.getDayModels())
                            {
                                //得到的永远是第一个当月刷新出来的那一天，不是点了好多次选的日期
                                if(dayModel.isToday())
                                {
                                    View view1 = adapterView.getChildAt(seletedTextViewIndex);
                                    TextView tvDay1 = (TextView) view1.findViewById(R.id.tvDay);
                                    tvDay1.setBackgroundResource(R.drawable.tv_circle_highlight);
                                    tvDay1.setTextColor(Color.WHITE);
                                    break;
                                }
                                seletedTextViewIndex ++;
                            }

                        }
                    }

                    tvDay.setBackgroundResource(R.drawable.tv_circle_highlight_temp);
                    tvDay.setTextColor(Color.WHITE);
                    preTextView = tvDay;
                }

                if (mListener != null) {
                    mListener.onFragmentInteraction(selectedDate);
                }
            }
        });

        long consumingTimeM = System.currentTimeMillis() - startTimeM;
        Log.d("lsw fragment view", consumingTimeM + "ms.");
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CalendarFragment class OnFragmentInteractionListener ");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
