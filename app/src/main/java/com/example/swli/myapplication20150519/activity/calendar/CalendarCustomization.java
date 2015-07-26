package com.example.swli.myapplication20150519.activity.calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.swli.myapplication20150519.ContactAuthor;
import com.example.swli.myapplication20150519.MemberMaintain;
import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.SlideNote;
import com.example.swli.myapplication20150519.common.BaZiActivityWrapper;
import com.example.swli.myapplication20150519.common.ColorHelper;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.DateLunar;
import com.example.swli.myapplication20150519.common.LunarCalendar;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;
import com.example.swli.myapplication20150519.common.MyApplication;
import com.example.swli.myapplication20150519.data.handler.MemberDataHandler;
import com.example.swli.myapplication20150519.model.Member;
import com.example.swli.myapplication20150519.phone.base.Contact;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swli on 7/24/2015.
 */
public class CalendarCustomization extends Activity {

    private GridView gridView;
    private GridView gridViewTitle;

    private LayoutInflater linearLayout;
    private DateExt initialDate;

    private TextView tvDateSelect;
    private TextView tvDateLunarSelect;

    private TextView tvEraYear, tvEraMonth, tvEraDay, tvEraHour;

    private LunarCalendarWrapper lunarCalendarWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = LayoutInflater.from(this);
        tvDateSelect  = (TextView) findViewById(R.id.tvDateSelect);
        tvDateLunarSelect = (TextView) findViewById(R.id.tvLunarDateSelect);

        tvEraYear = (TextView) findViewById(R.id.tvEraYear);
        tvEraMonth = (TextView) findViewById(R.id.tvEraMonth);
        tvEraDay = (TextView) findViewById(R.id.tvEraDay);
        tvEraHour = (TextView) findViewById(R.id.tvEraHour);

        gridViewTitle = (GridView) findViewById(R.id.gv_calendarTitle);
        gridView = (GridView) findViewById(R.id.gv_calendar);

        gridViewTitle.setAdapter(new CalendarTitleAdapter(linearLayout));
        gridViewTitle.setNumColumns(7);
        initialDate = new DateExt();
        lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
        loadCalendar(initialDate);

        tvDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSelectorDialog dialog = new DateSelectorDialog(initialDate, CalendarCustomization.this);
                dialog.setCallBack(new DateSelectorDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                        loadCalendar(dateExt);
                        loadTitileDate(dateExt);
                        initialDate = dateExt;
                    }
                });
                dialog.show();
            }
        });

    }

    private void loadCalendar(DateExt dateExt) {

       loadTitileDate(dateExt);

        CalendarAdapter calendarAdapter = new CalendarAdapter(linearLayout, dateExt);
        gridView.setAdapter(calendarAdapter);
        gridView.setNumColumns(7);

        calendarAdapter.setICallBack(new CalendarAdapter.ICallBack() {
            @Override
            public void invoke(DateExt dateExt) {
                lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
                loadTitileDate(dateExt);
                loadEraTextDetail(lunarCalendarWrapper);
                initialDate = dateExt;
            }
        });

        loadEraTextDetail(lunarCalendarWrapper);
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
            loadCalendar(initialDate);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
