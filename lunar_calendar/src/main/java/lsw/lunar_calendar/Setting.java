package lsw.lunar_calendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import lsw.lunar_calendar.common.MyApplication;

/**
 * Created by swli on 5/31/2016.
 */
public class Setting  extends Activity {


    private Switch swithMonthFirstDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        swithMonthFirstDay = (Switch)findViewById(R.id.swMonthFirstDay);

        swithMonthFirstDay.setChecked(MyApplication.getInstance().isSaturdayForMonthFirstDay());

        swithMonthFirstDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MyApplication.getInstance().setMonthFirstDayToSaturday(b);
            }
        });
    }
}
