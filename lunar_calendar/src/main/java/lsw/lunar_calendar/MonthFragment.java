package lsw.lunar_calendar;

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

import lsw.library.DateExt;
import lsw.lunar_calendar.data_source.CalendarAdapter;
import lsw.lunar_calendar.model.DayModel;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class MonthFragment extends Fragment {

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

    public static MonthFragment newInstance(DateExt dateExt) {
        MonthFragment fragment = new MonthFragment();
        Bundle args = new Bundle();
        args.putString(paramDate,dateExt.getFormatDateTime());
        fragment.setArguments(args);
        return fragment;
    }

    public MonthFragment() {}

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
        //????????¡®??¡§??¡¤?¨C¡ã????????¡­???¨¨????¡è????????????¨¦?????line92??¡è??????????????¡¤???¨¦¡ª?¨¦??
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

        View view = inflater.inflate(R.layout.fragment_month, container, false);
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

                //???????????¡ë?????¡°?¡ë?????????¡­????????????¨¦?¡ë????????¡ì???
                if(dateExt.getMonth() != selectedDate.getMonth()) {
                    preTextView = todayTextView = null;
                }
                else
                {
                    //??¡°?¡ë????????????¡ë?????¡ë?????????¨¦?¡ë???????¡è?
                    if(preTextView != null) {
                        preTextView.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
                        preTextView.setTextColor(Color.BLACK);
                    }
                    else
                    {
                        //??¡­???¨¨¡¤?¨¨?????¨¦??¨¨?¡è¨¦?¡ë???????¡ª????
                        int seletedTextViewIndex = 0;
                        for (DayModel dayModel: calendarAdapter.getDayModels())
                        {
                            //??¡ª??¡ã????¡ã?¨¨????????????????¡°?????¡¤?¨C¡ã?????????¨¦??????¡è????????????????????¡è????¨¦?¡ë????¡ª????
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

                    //¨¦???????¡°?¡è?
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
                            //??¡­???¨¨¡¤?¨¨?????¨¦??¨¨?¡è¨¦?¡ë???????¡ª????
                            int seletedTextViewIndex = 0;
                            for (DayModel dayModel: calendarAdapter.getDayModels())
                            {
                                //??¡ª??¡ã????¡ã?¨¨????????????????¡°?????¡¤?¨C¡ã?????????¨¦??????¡è????????????????????¡è????¨¦?¡ë????¡ª????
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

