package lsw.lunar_calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import lsw.lunar_calendar.R;

/**
 * Created by swli on 8/5/2015.
 */
public class DayNotifyPointView extends TextView {

    private boolean isVisable;

    public DayNotifyPointView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.DayNotifyPointView);

        isVisable = typedArray.getBoolean(R.styleable.DayNotifyPointView_IsVisable,false);

        setVisibility(isVisable);

        typedArray.recycle();

    }

    public void setVisibility(boolean isVisable)
    {
        this.setVisibility(isVisable? VISIBLE: INVISIBLE);
    }
}
