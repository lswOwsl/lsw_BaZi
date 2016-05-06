package lsw.lunar_calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import lsw.library.ColorHelper;
import lsw.lunar_calendar.R;

/**
 * Created by swli on 8/5/2015.
 */
public class LunarDayTextView   extends TextView {

    private boolean isSolarTerm;

    public LunarDayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.LunarDayTextView);

        isSolarTerm = typedArray.getBoolean(R.styleable.LunarDayTextView_IsSolarTerm,false);

        setBackground(isSolarTerm);

        typedArray.recycle();

    }

    public void setBackground(boolean isSolarTerm) {
        if (isSolarTerm) {
            this.setTextColor(ColorHelper.getSolarTermColor());
            this.setBackgroundResource(R.drawable.item_border_r);
        } else {
            this.setTextColor(Color.LTGRAY);
            this.setBackgroundColor(Color.WHITE);
        }
    }
}
