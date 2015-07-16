package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.swli.myapplication20150519.common.ButtonHelper;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;
import com.example.swli.myapplication20150519.common.SpinnerHelper;

/**
 * Created by swli on 5/21/2015.
 */
public class ShenSha extends Activity {

    Spinner spYearT;
    Spinner spMonthT;
    Spinner spDayC;
    Spinner spDayT;

    TextView tvYearContent;
    TextView tvMonthContent;
    TextView tvDayContent;

    SpinnerHelper spinnerHelper;
    DBManager dbManager;
    ButtonHelper btnHelper;
    LunarCalendarWrapper lunarCalendarWrapper;

    private void onLoadContent() {
        Bundle bundle = this.getIntent().getExtras();

        /*获取Bundle中的数据，注意类型和key*/
        String name = bundle.getString("Name");
        boolean ismale = bundle.getBoolean("Ismale");
        String birthday = bundle.getString("Birthday");
        String isMaleText = ismale?"男":"女";

        lunarCalendarWrapper = new LunarCalendarWrapper(new DateExt(birthday));
        int yearIndex = lunarCalendarWrapper.getChineseEraOfYear();
        int monthIndex = lunarCalendarWrapper.getChineseEraOfMonth();
        int dayIndex = lunarCalendarWrapper.getChineseEraOfDay();
        String yearT = lunarCalendarWrapper.toStringWithTerrestrialBranch(yearIndex);
        String monthT = lunarCalendarWrapper.toStringWithTerrestrialBranch(monthIndex);
        String dayT = lunarCalendarWrapper.toStringWithTerrestrialBranch(dayIndex);
        String dayC = lunarCalendarWrapper.toStringWithCelestialStem(dayIndex);

        // 初始化控件
        spYearT = (Spinner) findViewById(R.id.spYearT);
        spMonthT = (Spinner) findViewById(R.id.spMonthT);
        spDayC = (Spinner) findViewById(R.id.spDayC);
        spDayT = (Spinner) findViewById(R.id.spDayT);

        tvYearContent = (TextView) findViewById(R.id.tvYearContent);
        tvMonthContent = (TextView) findViewById(R.id.tvMonthContent);
        tvDayContent = (TextView) findViewById(R.id.tvDayContent);

        spinnerHelper.BindTerrestrial(spYearT, yearT);
        spinnerHelper.BindTerrestrial(spMonthT,monthT);
        spinnerHelper.BindTerrestrial(spDayT,dayT);
        spinnerHelper.BindCelestialStem(spDayC,dayC);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shen_sha);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new DBManager(this);
        spinnerHelper = new SpinnerHelper(this);
        btnHelper = new ButtonHelper();
        onLoadContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void  onBtnSearchShenShaClick(View view)
    {
        tvYearContent.setText("年神煞：");
        tvMonthContent.setText("月神煞：");
        tvDayContent.setText("日神煞：");
        String yearContent = tvYearContent.getText().toString();
        String monthContent = tvMonthContent.getText().toString();
        String dayContent = tvDayContent.getText().toString();
        dbManager.openDatabase();
        Cursor yearCursor = dbManager.execute("Select * from ShenSha_YearTerrestrial where TerrestrialName = ?",new String[] {spYearT.getSelectedItem().toString()});
        if(yearCursor.getCount()>0) {
            yearCursor.moveToFirst();
            String guChen = dbManager.getColumnValue(yearCursor,"GuChen");
            String guaSu = dbManager.getColumnValue(yearCursor,"GuaSu");
            String daHao1 = dbManager.getColumnValue(yearCursor,"DaHao1");
            String daHao2 = dbManager.getColumnValue(yearCursor,"DaHao2");

            yearContent += "  孤辰-"+guChen;
            yearContent += ", 寡宿-"+guaSu;
            yearContent += ", 大耗-"+daHao1+"/"+daHao2;
            tvYearContent.setText(yearContent);
        }
        yearCursor.close();

        Cursor monthCursor = dbManager.execute("Select * from ShenSha_MonthTerrestrial where TerrestrialName = ?", new String[]{spMonthT.getSelectedItem().toString()});
        if(monthCursor.getCount()>0)
        {
            monthCursor.moveToFirst();
            String tianDe = dbManager.getColumnValue(monthCursor,"TianDe");
            String yueDe = dbManager.getColumnValue(monthCursor, "YueDe");

            monthContent+="  天德-"+tianDe;
            monthContent+=", 月德-"+yueDe;
            tvMonthContent.setText(monthContent);
        }
        monthCursor.close();

        Cursor dayCursorC = dbManager.execute("Select * from ShenSha_DayCelestialStem where CelestialStemName = ?", new String[]{spDayC.getSelectedItem().toString()});
        if(dayCursorC.getCount()>0)
        {
            dayCursorC.moveToFirst();
            String tianYiGuiRen1 = dbManager.getColumnValue(dayCursorC,"TianYiGuiRen1");
            String tianYiGuiRen2 = dbManager.getColumnValue(dayCursorC,"TianYiGuiRen2");
            String wenChangGuiRen = dbManager.getColumnValue(dayCursorC,"WenChangGuiRen");
            String yangRen = dbManager.getColumnValue(dayCursorC,"YangRen");
            String ganLu = dbManager.getColumnValue(dayCursorC,"GanLu");
            String hongYanSha = dbManager.getColumnValue(dayCursorC,"HongYanSha");

            dayContent+="  天乙贵人-"+tianYiGuiRen1+"/"+tianYiGuiRen2;
            dayContent+=", 文昌贵人-"+wenChangGuiRen;
            dayContent+=", 羊刃-"+yangRen;
            dayContent+=", 干禄-"+ganLu;
            dayContent+=", 红艳煞-"+hongYanSha;

            //tvDayContent.setText(dayContent);
        }
        dayCursorC.close();

        Cursor dayCursorT = dbManager.execute("Select * from ShenSha_DayTerrestrial where TerrestrialName = ?", new String[]{spDayT.getSelectedItem().toString()});
        if(dayCursorT.getCount()>0)
        {
            dayCursorT.moveToFirst();
            String jiangXing = dbManager.getColumnValue(dayCursorT,"JiangXing");
            String huaGai = dbManager.getColumnValue(dayCursorT,"HuaGai");
            String yiMa = dbManager.getColumnValue(dayCursorT,"YiMa");
            String jieSha = dbManager.getColumnValue(dayCursorT,"JieSha");
            String wangShen = dbManager.getColumnValue(dayCursorT,"WangShen");
            String taoHua = dbManager.getColumnValue(dayCursorT,"TaoHua");

            dayContent+=", 将星-"+jiangXing;
            dayContent+=", 华盖-"+huaGai;
            dayContent+=", 驿马-"+yiMa;
            dayContent+=", 亡神-"+wangShen;
            dayContent+=", 桃花-"+taoHua;
            tvDayContent.setText(dayContent);
        }
        dayCursorT.close();
        //while (cursor.moveToNext())
    }

    private  void test1()
    {

    }
}
