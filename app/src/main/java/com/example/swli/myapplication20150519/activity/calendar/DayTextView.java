package com.example.swli.myapplication20150519.activity.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;

/**
 * Created by lsw_wsl on 8/2/15.
 */
public class DayTextView extends TextView {

    private boolean isThisMonth, isSelected, isToday;

    public DayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.DayTextView);

        isThisMonth = typedArray.getBoolean(R.styleable.DayTextView_IsCurrentMonth,true);
        isSelected = typedArray.getBoolean(R.styleable.DayTextView_IsCurrentSelected,false);
        isToday = typedArray.getBoolean(R.styleable.DayTextView_IsToday,false);

        setText(isThisMonth, isSelected, isToday, getText().toString());

        typedArray.recycle();

    }

    public void setText(boolean isThisMonth, boolean isSelected, boolean isToday, String text)
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
        else
        {
            this.setBackgroundResource(R.drawable.tv_circle_highlight_clear);
            this.setTextColor(Color.BLACK);
        }
        this.setText(text);
    }

    public boolean isThisMonth() {
        return isThisMonth;
    }

    public void setIsThisMonth(boolean isThisMonth) {
        this.isThisMonth = isThisMonth;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }
}
