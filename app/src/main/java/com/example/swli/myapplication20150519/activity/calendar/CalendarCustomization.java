package com.example.swli.myapplication20150519.activity.calendar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.ColorHelper;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.DateLunar;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;

import java.util.Date;
import java.util.Random;

/**
 * Created by swli on 7/24/2015.
 */
public class CalendarCustomization extends FragmentActivity implements CalendarFragment.OnFragmentInteractionListener {

    //private GridView gridView;
    private GridView gridViewTitle;

    private LayoutInflater linearLayout;
    private DateExt initialDate;

    private TextView tvDateSelect;
    private TextView tvDateLunarSelect;

    private TextView tvEraYear, tvEraMonth, tvEraDay, tvEraHour;

    private LunarCalendarWrapper lunarCalendarWrapper;

    ViewGesture viewGesture;

    //private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //frameLayout = (FrameLayout)findViewById(R.id.fl_calendar);

        linearLayout = LayoutInflater.from(this);
        tvDateSelect  = (TextView) findViewById(R.id.tvDateSelect);
        tvDateLunarSelect = (TextView) findViewById(R.id.tvLunarDateSelect);

        tvEraYear = (TextView) findViewById(R.id.tvEraYear);
        tvEraMonth = (TextView) findViewById(R.id.tvEraMonth);
        tvEraDay = (TextView) findViewById(R.id.tvEraDay);
        tvEraHour = (TextView) findViewById(R.id.tvEraHour);

        gridViewTitle = (GridView) findViewById(R.id.gv_calendarTitle);
        //gridView = (GridView) findViewById(R.id.gv_calendar);

        gridViewTitle.setAdapter(new CalendarTitleAdapter(linearLayout));
        gridViewTitle.setNumColumns(7);
        initialDate = new DateExt();

        lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
        loadTitileDate(initialDate);
        loadEraTextDetail(lunarCalendarWrapper);

        tvDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSelectorDialog dialog = new DateSelectorDialog(initialDate, CalendarCustomization.this);
                dialog.setCallBack(new DateSelectorDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                        loadTitileDate(dateExt);
                        lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
                        loadEraTextDetail(lunarCalendarWrapper);
                        initialDate = dateExt;

                        CalendarFragment f2 = CalendarFragment.newInstance(initialDate);
                        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.replace(R.id.fl_calendar, f2);
                        ft.commit();
                    }
                });
                dialog.show();
            }
        });

        viewGesture = new ViewGesture(this, new ViewGesture.ICallBack() {
            @Override
            public void moveUp() {
                initialDate.addMonths(1);
                loadTitileDate(initialDate);
                lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
                loadEraTextDetail(lunarCalendarWrapper);

                CalendarFragment f2 = CalendarFragment.newInstance(initialDate);
                pushFragment(f2,true);
            }

            @Override
            public void moveDown() {
                initialDate.addMonths(-1);
                loadTitileDate(initialDate);
                lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
                loadEraTextDetail(lunarCalendarWrapper);

                CalendarFragment f2 = CalendarFragment.newInstance(initialDate);
                pushFragment(f2,false);
            }
        });
        //viewGesture.setGestureTo(gridView);

        CalendarFragment f2 = CalendarFragment.newInstance(initialDate);
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_calendar, f2, "begin_calendar");
        ft.commit();
    }

    public void pushFragment(CalendarFragment calendarFragment,boolean isUp) {

        String tag = new Random().toString();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(isUp) {
            ft.setCustomAnimations(R.anim.vertical_bottom_enter, R.anim.vertical_top_out);
        }
        else {
            ft.setCustomAnimations(R.anim.vertical_top_enter, R.anim.vertical_bottom_out);
        }
        ft .replace(R.id.fl_calendar, calendarFragment, tag);
        ft .commit();

    }

    //必须重写这个方法要不然会抛异常，因为gridview的filing合onclick冲突了
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        viewGesture.getDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void loadTitileDate(DateExt dateExt)
    {
        DateLunar dateLunar = lunarCalendarWrapper.getDateLunar(dateExt);
        String lunarDate = lunarCalendarWrapper.toStringWithChineseYear(dateLunar.getLunarYear()) + "年" +
                (dateLunar.getIsLeapMonth() ? "闰":"") +
                lunarCalendarWrapper.toStringWithChineseMonth(dateLunar.getLunarMonth()) + "月" +
                lunarCalendarWrapper.toStringWithChineseDay(dateLunar.getLunarDay());

        tvDateLunarSelect.setText(lunarDate);
        tvDateSelect.setText(dateExt.getFormatDateTime("yyyy年MM月dd日 HH:mm"));
    }

    private void loadEraTextDetail(LunarCalendarWrapper lunarCalendarWrapper) {
        int eraYearIndex = lunarCalendarWrapper.getChineseEraOfYear();
        int eraMonthIndex = lunarCalendarWrapper.getChineseEraOfMonth();
        int eraDayIndex = lunarCalendarWrapper.getChineseEraOfDay();
        int eraHourIndex = lunarCalendarWrapper.getChineseEraOfHour();

        setEraTextDetail(tvEraYear, eraYearIndex);
        setEraTextDetail(tvEraMonth,eraMonthIndex);
        setEraTextDetail(tvEraDay, eraDayIndex);
        setEraTextDetail(tvEraHour, eraHourIndex);
    }

    private void setEraTextDetail(TextView tv, int eraIndex)
    {
        tv.setText("");
        tv.append(ColorHelper.getColorCelestialStem(this, lunarCalendarWrapper.toStringWithCelestialStem(eraIndex)));
        tv.append(ColorHelper.getColorTerrestrial(this, lunarCalendarWrapper.toStringWithTerrestrialBranch(eraIndex)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.menuToday) {
            initialDate = new DateExt();
            CalendarFragment f2 = CalendarFragment.newInstance(initialDate);
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.replace(R.id.fl_calendar, f2);
            ft.commit();
            loadTitileDate(initialDate);
            lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
            loadEraTextDetail(lunarCalendarWrapper);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(DateExt dateExt) {
        lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        loadTitileDate(dateExt);
        loadEraTextDetail(lunarCalendarWrapper);
        //因为在一个月份上，可能会选则上个月的某一天，也可能选择下个月的某一天
        //当滑动切换月份时，应该只用当前的月份进行滑动

        boolean flag = true;
        boolean willChange = false;

        if (initialDate.getYear() == dateExt.getYear())
        {
            if(initialDate.getMonth() > dateExt.getMonth())
            {
                willChange = true;
                flag = false;
            }
            else if(initialDate.getMonth() < dateExt.getMonth())
            {
                willChange = true;
                flag = true;
            }
        }
        else
        {
            if(initialDate.getYear() > dateExt.getYear())
            {
                willChange = true;
                flag = false;
            }
            else {
                willChange = true;
                flag = true;
            }
        }

        if(willChange) {
            CalendarFragment f2 = CalendarFragment.newInstance(dateExt);
            pushFragment(f2, flag);
            initialDate = dateExt;
        }

    }
}
