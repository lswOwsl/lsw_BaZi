package lsw.lunar_calendar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class DayFragment<T> extends Fragment {

    private static final String arg_day = "day";
    private int paramDay;
    private static final String arg_lunar_day = "lunar_day";
    private int paramLunarDay;
    private static final String arg_era_day = "era_day";
    private int paramEraDay;

    public static DayFragment newInstance(Integer day, Integer lunarDay, Integer eraDay) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(arg_day, day);
        args.putInt(arg_lunar_day, lunarDay);
        args.putInt(arg_era_day, eraDay);
        fragment.setArguments(args);
        return fragment;
    }

    public DayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramDay = getArguments().getInt(arg_day);
            paramLunarDay = getArguments().getInt(arg_lunar_day);
            paramEraDay = getArguments().getInt(arg_era_day);
        }
    }

    private TextView tvEraDay, tvLunaryDay, tvDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_day, container, false);

        tvEraDay = (TextView) view.findViewById(R.id.tvEraDay);
        tvLunaryDay = (TextView) view.findViewById(R.id.tvLunarDay);
        tvDay = (TextView) view.findViewById(R.id.tvDay);

        tvEraDay.setText(paramEraDay);
        tvLunaryDay.setText(paramLunarDay);
        tvDay.setText(paramDay);

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
        mListener = null;
    }

    private OnFragmentInteractionListener<T> mListener;

    public interface OnFragmentInteractionListener<T> {
        // TODO: Update argument type and name
        public void onFragmentInteraction(T uri);
    }
}
