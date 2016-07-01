package com.example.swli.myapplication20150519;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.example.swli.myapplication20150519.activity.DaYunPickDialog;
import com.example.swli.myapplication20150519.activity.FlowYearPickDialog;
import com.example.swli.myapplication20150519.activity.ICallBackDialog;
import com.example.swli.myapplication20150519.activity.MemberAnalyzeViewPager;
import com.example.swli.myapplication20150519.activity.sidebar.SortModel;
import com.example.swli.myapplication20150519.advertising.BaiDuBanner;
import com.example.swli.myapplication20150519.common.BaZiActivityWrapper;
import com.example.swli.myapplication20150519.common.BaZiHelperExtender;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.EnumPart;
import com.example.swli.myapplication20150519.model.CallBackArgs;
import com.example.swli.myapplication20150519.model.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import lsw.library.BaZiHelper;
import lsw.library.CrossAppKey;
import lsw.library.DatabaseManager;
import lsw.library.DateExt;
import lsw.library.LunarSolarTerm;
import lsw.library.SolarTerm;
import lsw.library.StringHelper;
import lsw.library.Utility;

public class MemberAnalyze extends MemberBase {

    TextView flowYearC, flowYearT, flowYearLiuQin, flowYearHidden,
            daYunC, daYunT, daYunLiuQin, daYunHidden,
            yearC, yearT, yearLiuQin, yearHidden,
            monthC, monthT, monthLiuQin, monthHidden,
            dayC, dayT, dayLiuQin, dayHidden,
            hourC, hourT, hourLiuQin, hourHidden,
            fy_dy_jg, dy_y_jg,y_m_jg,m_d_jg,d_h_jg;

    BaZiActivityWrapper baZiActivityWrapper;

    TextView tv_flowYear_title, tv_year_title, tv_month_title, tv_day_title, tv_hour_title, tv_dayun_title;
    TextView tv_flowYear_bottom, tv_year_bottom, tv_month_bottom, tv_day_bottom, tv_hour_bottom, tv_dayun_bottom;

    Integer currentAge;
    Integer currentDaYun;
    SolarTerm currentMonth;

    MemberAnalyzeViewPager viewPagerHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_analyze);

        loadControl();
        initContent();

        BaiDuBanner banner = new BaiDuBanner(this);
        banner.create();
    }

    private void loadControl() {
        flowYearC = (TextView) findViewById(R.id.tv_flowYear_c);
        flowYearT = (TextView) findViewById(R.id.tv_flowYear_t);
        flowYearLiuQin = (TextView) findViewById(R.id.tv_flowYear_c_liuQin);
        flowYearHidden = (TextView) findViewById(R.id.tv_flowYear_t_hidden);
        daYunC = (TextView) findViewById(R.id.tv_daYun_c);
        daYunT = (TextView) findViewById(R.id.tv_daYun_t);
        daYunLiuQin = (TextView) findViewById(R.id.tv_daYun_c_liuQin);
        daYunHidden = (TextView) findViewById(R.id.tv_daYun_t_hidden);
        yearC = (TextView) findViewById(R.id.tv_year_c);
        yearT = (TextView) findViewById(R.id.tv_year_t);
        yearLiuQin = (TextView) findViewById(R.id.tv_year_c_liuQin);
        yearHidden = (TextView) findViewById(R.id.tv_year_t_hidden);
        monthC = (TextView) findViewById(R.id.tv_month_c);
        monthT = (TextView) findViewById(R.id.tv_month_t);
        monthLiuQin = (TextView) findViewById(R.id.tv_month_c_liuQin);
        monthHidden = (TextView) findViewById(R.id.tv_month_t_hidden);
        dayC = (TextView) findViewById(R.id.tv_day_c);
        dayT = (TextView) findViewById(R.id.tv_day_t);
        dayLiuQin = (TextView) findViewById(R.id.tv_day_c_liuQin);
        dayHidden = (TextView) findViewById(R.id.tv_day_t_hidden);
        hourC = (TextView) findViewById(R.id.tv_hour_c);
        hourT = (TextView) findViewById(R.id.tv_hour_t);
        hourLiuQin = (TextView) findViewById(R.id.tv_hour_c_liuQin);
        hourHidden = (TextView) findViewById(R.id.tv_hour_t_hidden);

        fy_dy_jg = (TextView)findViewById(R.id.tv_flowYear_daYun_t_jiaGong);
        dy_y_jg = (TextView)findViewById(R.id.tv_daYun_year_t_jiaGong);
        y_m_jg = (TextView)findViewById(R.id.tv_year_month_t_jiaGong);
        m_d_jg = (TextView)findViewById(R.id.tv_month_day_t_jiaGong);
        d_h_jg = (TextView)findViewById(R.id.tv_day_hour_t_jiaGong);

        tv_flowYear_title = (TextView)findViewById(R.id.tv_flowYear_title);
        tv_year_title = (TextView)findViewById(R.id.tv_year_title);
        tv_month_title = (TextView)findViewById(R.id.tv_month_title);
        tv_day_title = (TextView)findViewById(R.id.tv_day_title);
        tv_hour_title = (TextView)findViewById(R.id.tv_hour_title);
        tv_dayun_title = (TextView)findViewById(R.id.tv_dayun_title);

        tv_flowYear_bottom = (TextView)findViewById(R.id.tv_flowYear_bottom);
        tv_year_bottom = (TextView)findViewById(R.id.tv_year_bottom);
        tv_month_bottom = (TextView)findViewById(R.id.tv_month_bottom);
        tv_day_bottom = (TextView)findViewById(R.id.tv_day_bottom);
        tv_hour_bottom = (TextView)findViewById(R.id.tv_hour_bottom);
        tv_dayun_bottom = (TextView)findViewById(R.id.tv_dayun_bottom);

    }

    Member createMemberByCursor(Cursor cur)
    {
        int idIndex = cur.getColumnIndex("Id");
        int nameIndex = cur.getColumnIndex("Name");
        int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
        int isMaleIndex = cur.getColumnIndex("IsMale");

        String name = cur.getString(nameIndex);
        String birthdayStr = cur.getString(birthdayIndex);
        String isMale = cur.getString(isMaleIndex);
        DateExt birthdayDE = new DateExt(birthdayStr,"yyyy-MM-dd HH:mm:ss");
        int isMaleI = Integer.parseInt(isMale);
        int id = cur.getInt(idIndex);

        Member member = new Member();
        member.setId(id);
        member.setName(name);
        member.setIsMale(isMaleI == 1 ? true : false);
        member.setBirthday(birthdayDE);

        return member;
    }

    int memberId;
    String name;
    boolean ismale;
    DateExt birthdayDateExt;

    private void initContent() {
        Bundle bundle = this.getIntent().getExtras();

        memberId = this.getIntent().getIntExtra(CrossAppKey.MemberId, -1);
        if(memberId != -1) {
            DBManager dbManager = new DBManager(this);
            SQLiteDatabase database = dbManager.getDatabase();

            String[] params = new String[]{ memberId+"" };
            String sql = "SELECT * FROM Members where Id = ?";
            Cursor cur = database.rawQuery(sql, params);
            Member member = null;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                member = createMemberByCursor(cur);
                break;
            }

            name = member.getName();
            ismale = member.getIsMale();
            birthdayDateExt = member.getBirthday();

        }
        else {
            name = bundle.getString("Name");
            ismale = bundle.getBoolean("Ismale");
            String birthday = bundle.getString("Birthday");
            birthdayDateExt = new DateExt(birthday);
        }

        String isMaleText = ismale ? "男" : "女";

        getActionBar().setTitle("姓名: " + name);
        getActionBar().setSubtitle("性别:" + isMaleText);

        baZiActivityWrapper = new BaZiActivityWrapper(birthdayDateExt,this,ismale);
        viewPagerHandler = new MemberAnalyzeViewPager(this,baZiActivityWrapper);


        baZiActivityWrapper.setControl(this.hourC,this.hourT,this.hourLiuQin,this.hourHidden,baZiActivityWrapper.getHourEraIndex());
        baZiActivityWrapper.setControl(this.dayC,this.dayT,this.dayLiuQin,this.dayHidden,baZiActivityWrapper.getDayEraIndex());
        baZiActivityWrapper.setControl(this.monthC,this.monthT,this.monthLiuQin,this.monthHidden,baZiActivityWrapper.getMonthEraIndex());
        baZiActivityWrapper.setControl(this.yearC, this.yearT, this.yearLiuQin, this.yearHidden, baZiActivityWrapper.getYearEraIndex());

        final int fixBeginYunAge = fixBeginYunAge(birthdayDateExt);
        int beginYunYear = fixBeginYunAge + birthdayDateExt.getYear();
        final DateExt current = DateExt.getCurrentTime();
        final int indexFlowYear = baZiActivityWrapper.getFlowYearEraIndex(current.getYear(), current.getMonth(), current.getDay());
        boolean isBeginYun= false;
        if(current.getYear()>=beginYunYear) {
            baZiActivityWrapper.setControl(this.flowYearC, this.flowYearT, this.flowYearLiuQin, this.flowYearHidden, indexFlowYear);
            isBeginYun = true;
        }
        Pair<Integer,Integer> yearMonth = baZiActivityWrapper.getBeginYunAge_Month();
        getActionBar().setSubtitle(getActionBar().getSubtitle() + "     " + yearMonth.first + "岁" + yearMonth.second + "个月起运");
        final int indexDaYun = baZiActivityWrapper.getDaYunByFlowYear(current.getYear(),fixBeginYunAge);
        baZiActivityWrapper.setControl(this.daYunC, this.daYunT, this.daYunLiuQin, this.daYunHidden, indexDaYun);
        loadTitle((current.getYear() - birthdayDateExt.getYear()), birthdayDateExt, indexDaYun, indexFlowYear);

        Integer tempIndexFlowYear = null;
        if(isBeginYun) {
            tempIndexFlowYear = indexFlowYear;
            baZiActivityWrapper.setJiaGong(this.fy_dy_jg, Pair.create(EnumPart.FlowYear, EnumPart.DaYun), indexDaYun, indexFlowYear);
        }
        baZiActivityWrapper.setJiaGong(this.dy_y_jg,  Pair.create(EnumPart.DaYun, EnumPart.Year),indexDaYun);
        baZiActivityWrapper.setJiaGong(this.y_m_jg, Pair.create(EnumPart.Year, EnumPart.Month));
        baZiActivityWrapper.setJiaGong(this.m_d_jg, Pair.create(EnumPart.Month, EnumPart.Day));
        baZiActivityWrapper.setJiaGong(this.d_h_jg,  Pair.create(EnumPart.Day, EnumPart.Hour));

        viewPagerHandler.init(tempIndexFlowYear, indexDaYun, null);

        final ICallBackDialog<CallBackArgs> callBackDialogDaYun = new ICallBackDialog<CallBackArgs>() {
            @Override
            public void onCall(CallBackArgs callBackArgs) {
                clearFlowYear();
                loadDaYun(callBackArgs);
                loadTitle(callBackArgs.getCurrentAge(), birthdayDateExt, callBackArgs.getDaYunEraIndex(),null);
                viewPagerHandler.init(null, callBackArgs.getDaYunEraIndex(),null);
            }
        };

        View.OnClickListener onClickListenerDaYun = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentDaYun == null)
                    currentDaYun = indexDaYun;
                    DaYunPickDialog daYunPickDialog = new DaYunPickDialog(MemberAnalyze.this, baZiActivityWrapper.getDaYuns(10),
                            fixBeginYunAge, currentDaYun );
                    daYunPickDialog.setCallBackDialog(callBackDialogDaYun);
                    daYunPickDialog.show();

            }
        };

        this.daYunC.setOnClickListener(onClickListenerDaYun);
        this.daYunT.setOnClickListener(onClickListenerDaYun);

        this.daYunC.setBackgroundResource(R.drawable.text_view_border);
        this.daYunT.setBackgroundResource(R.drawable.text_view_border_all);
///////////////////////////////////////////////////////////////////////////////////
        this.flowYearC.setBackgroundResource(R.drawable.text_view_border);
        this.flowYearT.setBackgroundResource(R.drawable.text_view_border_all);


        final ICallBackDialog<CallBackArgs> callBackDialogFlowYear = new ICallBackDialog<CallBackArgs>() {
            @Override
            public void onCall(CallBackArgs args) {

                if(args.isFlowMonthClick())
                {
                    loadFlowMonth(args);
                    loadTitle(args.getCurrentAge(), birthdayDateExt, args.getDaYunEraIndex(), args.getFlowYearEraIndex(),args.getFlowMonthEraIndex(), args.getFlowMonthSolarTerm());
                    viewPagerHandler.init(args.getFlowYearEraIndex(), args.getDaYunEraIndex(), args.getFlowMonthEraIndex());
                    currentMonth = args.getFlowMonthSolarTerm();
                }
                else {
                    loadFlowYear(args);
                    loadTitle(args.getCurrentAge(), birthdayDateExt, args.getDaYunEraIndex(), args.getFlowYearEraIndex());
                    viewPagerHandler.init(args.getFlowYearEraIndex(), args.getDaYunEraIndex(),null);
                    currentMonth = null;
                }
            }
        };

        View.OnClickListener onClickListenerFlowYear = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentAge == null) {
                    currentAge = current.getYear() - birthdayDateExt.getYear();
                }
                //修正1月底出生的人的年龄，例如2015年是甲午年，但2015年的1月低还是癸巳年
                int tempBeginYunAge = fixBeginYunAge(birthdayDateExt);

                int numYuns = currentAge/10 <10 ? 10 : currentAge/10+1;
                ArrayList<Integer> daYuns = baZiActivityWrapper.getDaYuns(numYuns);
                FlowYearPickDialog flowYearPickDialog = new FlowYearPickDialog(MemberAnalyze.this, daYuns,
                        tempBeginYunAge, baZiActivityWrapper.getYearEraIndex(baZiActivityWrapper.getBeginYunAge()),
                        currentAge, birthdayDateExt.getYear());
                flowYearPickDialog.setCallBackDialog(callBackDialogFlowYear);
                flowYearPickDialog.setCurrentMonth(currentMonth);
                flowYearPickDialog.show();
            }
        };

        this.flowYearC.setOnClickListener(onClickListenerFlowYear);
        this.flowYearT.setOnClickListener(onClickListenerFlowYear);

    }

    private int fixBeginYunAge(DateExt birthdate)
    {
        //修正1月低出生的人的年龄，例如2015年是甲午年，但2015年的1月低还是癸巳年
        int tempBeginYunAge = baZiActivityWrapper.getBeginYunAge();
        LunarSolarTerm lunarSolarTerm = new LunarSolarTerm();
        int yearEraIndex = baZiActivityWrapper.getYearEraIndex();
        DateExt newTempForHalfYear = new DateExt(birthdate.getYear(),6,1,0,0,0);
        int tempYearEraIndex = lunarSolarTerm.getChineseEraOfYear(newTempForHalfYear);
        if (yearEraIndex != tempYearEraIndex)
            tempBeginYunAge = tempBeginYunAge - 1;
        return tempBeginYunAge;
    }
    private void loadTitle(int currentAge, DateExt birthdate, Integer eraDaYunIndex, Integer eraFlowYearIndex, Integer eraFlowMonthIndex, SolarTerm flowMonth)
    {
        int birthdayYear = birthdate.getYear();
        int month = birthdate.getMonth();
        int day = birthdate.getDay();
        int hour = birthdate.getHour();

        if(flowMonth != null)
        {
            tv_flowYear_title.setText("" + flowMonth.getSolarTermDate().getFormatDateTime("yyyy") + "\n"+flowMonth.getSolarTermDate().getFormatDateTime("M月")+"\n"+
                            BaZiHelper.getNaYinFiveElement(flowYearC.getText().toString()+flowYearT.getText().toString()) + "\n" +
                    loadXunByEraIndex(eraFlowMonthIndex));
        }
        else
        {
            tv_flowYear_title.setText("" + (birthdayYear + currentAge) + "年"+"\n"+currentAge+"岁"+"\n"+
                    BaZiHelper.getNaYinFiveElement(flowYearC.getText().toString()+flowYearT.getText().toString()) + "\n" +
                    loadXunByEraIndex(eraFlowYearIndex));
        }


        tv_year_title.setText(""+birthdayYear + "年" +"\n"+getKuiGang(yearC.getText().toString(),yearT.getText().toString())+ "\n" +
                BaZiHelper.getNaYinFiveElement(yearC.getText().toString() + yearT.getText().toString()) + "\n" +
                loadXunByEraIndex(baZiActivityWrapper.getYearEraIndex()));
        tv_month_title.setText(""+month + "月" +"\n"+ getKuiGang(monthC.getText().toString(),monthT.getText().toString())  +"\n" +
                BaZiHelper.getNaYinFiveElement(monthC.getText().toString() + monthT.getText().toString()) + "\n" +
                loadXunByEraIndex(baZiActivityWrapper.getMonthEraIndex()));
        tv_day_title.setText("" + day + "日" +"\n"+ getKuiGang(dayC.getText().toString(),dayT.getText().toString())  +"\n" +
                BaZiHelper.getNaYinFiveElement(dayC.getText().toString() + dayT.getText().toString()) + "\n" +
                loadXunByEraIndex(baZiActivityWrapper.getDayEraIndex()));
        tv_hour_title.setText("" + hour + "时" +"\n"+ getKuiGang(hourC.getText().toString(),hourT.getText().toString()) +"\n" +
                BaZiHelper.getNaYinFiveElement(hourC.getText().toString() + hourT.getText().toString()) + "\n" +
                loadXunByEraIndex(baZiActivityWrapper.getHourEraIndex()));
        tv_dayun_title.setText("大运\n\n" +
                BaZiHelper.getNaYinFiveElement(daYunC.getText().toString() + daYunT.getText().toString()) + "\n" +
                loadXunByEraIndex(eraDaYunIndex));

        setStemBottom(flowYearC.getText().toString(), flowYearT.getText().toString(), tv_flowYear_bottom);
        setStemBottom(daYunC.getText().toString(), daYunT.getText().toString(), tv_dayun_bottom);
        setStemBottom(yearC.getText().toString(), yearT.getText().toString(), tv_year_bottom);
        setStemBottom(monthC.getText().toString(), monthT.getText().toString(), tv_month_bottom);
        setStemBottom(hourC.getText().toString(), hourT.getText().toString(), tv_hour_bottom);
        setStemBottom(dayC.getText().toString(),dayT.getText().toString(),tv_day_bottom);
    }

    private String getKuiGang(String c, String t)
    {
        String kuiGang = "魁罡";
        String[] array = new String[]{"壬辰","庚戌","庚辰", "戊戌"};
        ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(array));
        return arrayList.contains(c+t) ? kuiGang : "";
    }

    private void loadTitle(int currentAge, DateExt birthdate, Integer eraDaYunIndex, Integer eraFlowYearIndex)
    {
       loadTitle(currentAge, birthdate, eraDaYunIndex, eraFlowYearIndex, null, null);
    }

    private String loadXunByEraIndex(Integer eraIndex)
    {
        if(eraIndex == null)
            return "";

        int eraXunEraIndex = 0;
        if(eraIndex != 0)
        {
            while (eraIndex != 0) {
                if (eraIndex % 10 == 1) {
                    eraXunEraIndex = eraIndex;
                    break;
                }
                eraIndex = eraIndex - 1;
            }
        }
        else
        {
            eraXunEraIndex = eraIndex;
        }
        return baZiActivityWrapper.getC(eraXunEraIndex)+baZiActivityWrapper.getT(eraXunEraIndex);
    }

    private void loadFlowMonth(CallBackArgs args) {
        clearFlowYear();

        LunarSolarTerm lunarSolarTerm = new LunarSolarTerm();
        int flowMonthEraIndex = lunarSolarTerm.getChineseEraOfMonth(args.getFlowMonthSolarTerm().getSolarTermDate());

        baZiActivityWrapper.setControl(this.flowYearC, this.flowYearT, this.flowYearLiuQin, this.flowYearHidden, flowMonthEraIndex);
        baZiActivityWrapper.setJiaGong(this.fy_dy_jg, Pair.create(EnumPart.FlowYear, EnumPart.DaYun), args.getDaYunEraIndex(), flowMonthEraIndex);
        loadDaYun(args);

        currentAge = args.getCurrentAge();
    }

    private void loadFlowYear(CallBackArgs args) {
        clearFlowYear();

        baZiActivityWrapper.setControl(this.flowYearC, this.flowYearT, this.flowYearLiuQin, this.flowYearHidden, args.getFlowYearEraIndex());
        baZiActivityWrapper.setJiaGong(this.fy_dy_jg, Pair.create(EnumPart.FlowYear, EnumPart.DaYun), args.getDaYunEraIndex(), args.getFlowYearEraIndex());
        loadDaYun(args);

        currentAge = args.getCurrentAge();
    }

    private void loadDaYun(CallBackArgs callBackArgs) {
        baZiActivityWrapper.setControl(this.daYunC, this.daYunT, this.daYunLiuQin, this.daYunHidden, callBackArgs.getDaYunEraIndex());
        baZiActivityWrapper.setJiaGong(this.dy_y_jg, Pair.create(EnumPart.DaYun, EnumPart.Year), callBackArgs.getDaYunEraIndex());
        //this.flowYearC.setBackgroundResource(0);
        //this.flowYearT.setBackgroundResource(0);

        currentAge = callBackArgs.getCurrentAge();
        currentDaYun = callBackArgs.getDaYunEraIndex();
    }

    private void clearFlowYear() {
        this.flowYearHidden.setText("");
        this.flowYearLiuQin.setText("流年");
        //空格是为了占位用
        this.flowYearT.setText("   ");
        this.flowYearC.setText("   ");
        this.fy_dy_jg.setText("");
        this.dy_y_jg.setText("");
        //this.flowYearLiuQin.setTextColor(Color.GRAY);
    }

    private void setStemBottom(String celestrialStem, String terrestrial, TextView tv)
    {
        String result = BaZiHelperExtender.getGrowTrick(this, celestrialStem, terrestrial);
        boolean isXunKong = isXunKongText(terrestrial);
        if(isXunKong)
        {
            result = result + "\n空";
        }
        tv.setText(result);
    }

    private boolean isXunKongText(String terrestrialText)
    {
        Pair<String,String> kong = Utility.getXunKong(this,
                baZiActivityWrapper.getC(baZiActivityWrapper.getDayEraIndex()),
                baZiActivityWrapper.getT(baZiActivityWrapper.getDayEraIndex()));

        if(terrestrialText.equals(kong.first) || terrestrialText.equals(kong.second))
        {
            return true;
        }
        return false;
    }

}

