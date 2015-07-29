package com.example.swli.myapplication20150519.activity.calendar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;

public class CalendarFragment extends android.support.v4.app.Fragment {

    DateExt dateExt;
    LayoutInflater linearLayout;
    static String paramDate;
    private GridView gridView;

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

        CalendarAdapter calendarAdapter = new CalendarAdapter(linearLayout, dateExt);
        calendarAdapter.setICallBack(new CalendarAdapter.ICallBack() {
            @Override
            public void invoke(DateExt dateExt) {
                if(mListener != null)
                    mListener.onFragmentInteraction(dateExt);
            }
        });
        gridView.setAdapter(calendarAdapter);
        gridView.setNumColumns(7);

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
