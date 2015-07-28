package com.example.swli.myapplication20150519.activity.calendar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;

/**
 * Created by swli on 7/27/2015.
 */
public class CalendarFragment extends android.support.v4.app.Fragment {

    DateExt dateExt;
    LayoutInflater linearLayout;
    static String paramDate;

    public static CalendarFragment newInstance(DateExt dateExt) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(paramDate,dateExt.getFormatDateTime());
        fragment.setArguments(args);
        return fragment;
    }

    public CalendarFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            dateExt = new DateExt(getArguments().getString(paramDate));
        }

        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        linearLayout = LayoutInflater.from(getActivity());
        gridView = (GridView) view.findViewById(R.id.gv_calendar);

        CalendarAdapter calendarAdapter = new CalendarAdapter(linearLayout, dateExt);
        gridView.setAdapter(calendarAdapter);
        gridView.setNumColumns(7);

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
