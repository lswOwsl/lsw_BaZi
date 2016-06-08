package lsw.lunar_calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import lsw.library.ColorHelper;
import lsw.lunar_calendar.R;

/**
 * Created by swli on 8/3/2015.
 */
public class DayTextView extends TextView {

    private boolean isThisMonth, isSelected, isToday, isWeekend;

    public DayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.DayTextView);

        isThisMonth = typedArray.getBoolean(R.styleable.DayTextView_IsCurrentMonth,true);
        isSelected = typedArray.getBoolean(R.styleable.DayTextView_IsCurrentSelected,false);
        isToday = typedArray.getBoolean(R.styleable.DayTextView_IsToday,false);


        setText(isThisMonth, isSelected, isToday, isWeekend, false, getText().toString());

        typedArray.recycle();

    }

    public void setText(boolean isThisMonth, boolean isSelected, boolean isToday, boolean isWeekend, boolean isSolarTerm, String text)
    {

        if(isToday)
        {
            this.setBackgroundResource(R.drawable.tv_circle_highlight);
            this.setTextColor(Color.WHITE);
        }
        else if(isSelected)
        {
            this.setBackgroundResource(R.drawable.tv_circle_highlight_temp);
            this.setTextColor(Color.WHITE);
        }
        else if(!isThisMonth)
        {
            this.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
            this.setTextColor(Color.LTGRAY);
        }
        else if(isWeekend && !isSolarTerm)
        {
            this.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
            this.setTextColor(Color.RED);
        }
        else if(isSolarTerm)
        {
            this.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
            this.setTextColor(ColorHelper.getSolarTermColor());
        }
        else
        {
            this.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
            this.setTextColor(Color.BLACK);
        }
        this.setText(text);
    }
}
