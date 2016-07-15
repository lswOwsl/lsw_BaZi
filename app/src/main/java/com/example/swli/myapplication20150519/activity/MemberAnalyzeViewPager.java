package com.example.swli.myapplication20150519.activity;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.BaZiActivityWrapper;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.EnumPart;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import lsw.library.StringHelper;

public class MemberAnalyzeViewPager {

    private Activity activity;
    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private View vJiaGong, vShenSha,vTiaoHou,vChongXing, vJinBuHuan;
    private List<View> viewList;
    private List<String> titleList;
    private TextView tv_column_l1 ,tv_column_l3,tv_column_l5,tv_column_l7,tv_column_l9,
            tv_column_d1 ,tv_column_d3,tv_column_d5,tv_column_d7,tv_column_d9;

    private TextView tvFlowYearBad;

    TextView tvYearContent;
    TextView tvMonthContent;

    TextView tv_vp_tiaoHou;

    TextView tv_jinbuhuan_title, tv_tiaohou_title, tv_jinbuhuan_detail, tv_jinbuhuan_note;

    public Activity getActivity() {
        return activity;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    public BaZiActivityWrapper getBaZiActivityWrapper() {
        return baZiActivityWrapper;
    }

    TextView tvDayContent;
    TextView tvShenShaContent;
    private DBManager dbManager;

    private BaZiActivityWrapper baZiActivityWrapper;

    public MemberAnalyzeViewPager(Activity activity, BaZiActivityWrapper baZiActivityWrapper)
    {
        this.activity = activity;
        this.viewPager = (ViewPager)activity.findViewById(R.id.vPager);
        this.pagerTabStrip = (PagerTabStrip)activity.findViewById(R.id.vPagertab);
        this.baZiActivityWrapper = baZiActivityWrapper;
    }

    public void init(Integer flowYearEraIndex, int daYunEraIndex, Integer flowMonthEraIndex)
    {
        pagerTabStrip.setTabIndicatorColor(Color.GRAY);
        pagerTabStrip.setDrawFullUnderline(false);
        //pagerTabStrip.setBackgroundColor(Color.GRAY);
        ///pagerTabStrip.setTextSpacing(50);

        LayoutInflater inflater = LayoutInflater.from(activity);
        this.vJiaGong = inflater.inflate(R.layout.common_viewpager_jiagong,null);
        this.vShenSha = inflater.inflate(R.layout.common_viewpager_shensha,null);
        this.vChongXing = inflater.inflate(R.layout.common_viewpager_xingchonghehui,null);
        this.vTiaoHou = inflater.inflate(R.layout.common_viewpager_tiaohou,null);
        this.vJinBuHuan = inflater.inflate(R.layout.common_viewpager_jinbuhuandayun,null);

        viewList = new ArrayList<View>();
        viewList.add(vJiaGong);
        viewList.add(vShenSha);
        viewList.add(vChongXing);
        viewList.add(vTiaoHou);
        viewList.add(vJinBuHuan);

        titleList = new ArrayList<String>();
        titleList.add("夹拱");
        titleList.add("神煞");
        titleList.add("行冲合会");
        titleList.add("调候");
        titleList.add("金不换大运");

        final View cChongHe = activity.findViewById(R.id.cChongHe);
        cChongHe.setVisibility(View.GONE);

        viewPager.setAdapter(getPagerAdapter(flowYearEraIndex, daYunEraIndex, flowMonthEraIndex));
        dbManager = new DBManager(activity);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 2) {
                    //Animation animation = AnimationUtils.loadAnimation(activity, R.anim.celestial_stem_enter);
                    //cChongHe.startAnimation(animation);
                    cChongHe.setVisibility(View.VISIBLE);

                }
                else {
                    cChongHe.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public PagerAdapter getPagerAdapter(final Integer flowYearEraIndex, final int daYunEraIndex, final Integer flowMonthEraIndex)
    {

        final MemberAnalyzeViewPager memberAnalyzeViewPager = this;

        return new PagerAdapter() {


            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,Object object)
            {
                container.removeView(viewList.get(position));
            }

            @Override
            public int getItemPosition(Object object) {

                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return titleList.get(position);//直接用适配器来完成标题的显示，所以从上面可以看到，我们没有使用PagerTitleStrip。当然你可以使用。
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));

                MemberAnalyzeViewPager_XCHH xchh = new MemberAnalyzeViewPager_XCHH(memberAnalyzeViewPager);

                if(position == 0)
                {
                    //每次从新选择流年或大运都会从新刷新第一个页面，在这清空下冲刑页面天干冲和的高度
                    xchh.clearTopScrollHeight();

                    loadJiaGongControls();
                    if(flowMonthEraIndex != null) {
                        loadJiaGongView(null,daYunEraIndex);
                    }
                    else {
                        loadJiaGongView(flowYearEraIndex, daYunEraIndex);
                    }
                }
                else if(position == 1)
                {
                    Pair<ArrayList<String>,ArrayList<String>> pair = loadShenShaControls();
                    if(flowMonthEraIndex != null) {
                        loadShenShaInUsing(pair, null, daYunEraIndex, flowMonthEraIndex);
                    }
                    else
                    {
                        loadShenShaInUsing(pair, flowYearEraIndex, daYunEraIndex, null);
                    }
                }
                else if(position == 2)
                {

                    if(flowMonthEraIndex != null)
                    {
                        xchh.init(daYunEraIndex, null, flowMonthEraIndex);
                    }
                    else {
                        xchh.init(daYunEraIndex, flowYearEraIndex, null);
                    }
                }
                else if(position == 3)
                {
                    loadTiaoHouControls();
                    loadTiaoHouView();
                }
                else if(position == 4)
                {
                    loadJinBuHuanControls();
                    loadJinBuHuanView();
                }
                return viewList.get(position);
            }
        };
    }

    private Pair<ArrayList<String>,ArrayList<String>> loadShenShaControls()
    {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();

        tvYearContent = (TextView) activity.findViewById(R.id.tvYearContent);
        tvMonthContent = (TextView) activity.findViewById(R.id.tvMonthContent);
        tvDayContent = (TextView) activity.findViewById(R.id.tvDayContent);

        String yearContent = "";
        String monthContent = "";
        String dayContent = "";

        dbManager.openDatabase();
        Cursor yearCursor = dbManager.execute("Select * from ShenSha_YearTerrestrial where TerrestrialName = ?"
                ,new String[] { baZiActivityWrapper.getT(baZiActivityWrapper.getYearEraIndex())});
        if(yearCursor.getCount()>0) {
            yearCursor.moveToFirst();
            String guChen = dbManager.getColumnValue(yearCursor,"GuChen");
            String guaSu = dbManager.getColumnValue(yearCursor,"GuaSu");
            String daHao1 = dbManager.getColumnValue(yearCursor,"DaHao1");
            String daHao2 = dbManager.getColumnValue(yearCursor,"DaHao2");

            yearContent += " 孤辰-"+guChen+"\n";
            yearContent += " 寡宿-"+guaSu+"\n";
            yearContent += " 大耗-"+daHao1+"/"+daHao2;
            tvYearContent.setText(yearContent);

            names.add(guChen);
            names.add(guaSu);
            names.add(daHao1);
            names.add(daHao2);
            values.add("孤辰");
            values.add("寡宿");
            values.add("大耗");
            values.add("大耗");

        }
        yearCursor.close();

        Cursor monthCursor = dbManager.execute("Select * from ShenSha_MonthTerrestrial where TerrestrialName = ?", new String[]{ baZiActivityWrapper.getT(baZiActivityWrapper.getMonthEraIndex())});
        if(monthCursor.getCount()>0)
        {
            monthCursor.moveToFirst();
            String tianDe = dbManager.getColumnValue(monthCursor,"TianDe");
            String yueDe = dbManager.getColumnValue(monthCursor, "YueDe");

            monthContent+=" 天德-"+tianDe+"\n";
            monthContent+=" 月德-"+yueDe;
            tvMonthContent.setText(monthContent);

            names.add(tianDe);
            names.add(yueDe);
            values.add("天德");
            values.add("月德");
        }
        monthCursor.close();

        Cursor dayCursorC = dbManager.execute("Select * from ShenSha_DayCelestialStem where CelestialStemName = ?", new String[]{baZiActivityWrapper.getC(baZiActivityWrapper.getDayEraIndex())});
        if(dayCursorC.getCount()>0)
        {
            dayCursorC.moveToFirst();
            String tianYiGuiRen1 = dbManager.getColumnValue(dayCursorC,"TianYiGuiRen1");
            String tianYiGuiRen2 = dbManager.getColumnValue(dayCursorC,"TianYiGuiRen2");
            String wenChangGuiRen = dbManager.getColumnValue(dayCursorC,"WenChangGuiRen");
            String yangRen = dbManager.getColumnValue(dayCursorC,"YangRen");
            String ganLu = dbManager.getColumnValue(dayCursorC,"GanLu");
            String hongYanSha = dbManager.getColumnValue(dayCursorC,"HongYanSha");

            dayContent+=" 天乙-"+tianYiGuiRen1+"/"+tianYiGuiRen2+"\n";
            dayContent+=" 文昌-"+wenChangGuiRen+"\n";
            dayContent+=" 羊刃-"+yangRen+"\n";
            dayContent+=" 干禄-"+ganLu+"\n";
            dayContent+=" 红艳煞-"+hongYanSha+"\n";

            //tvDayContent.setText(dayContent);

            names.add(tianYiGuiRen1);
            names.add(tianYiGuiRen2);
            names.add(wenChangGuiRen);
            names.add(yangRen);
            names.add(ganLu);
            names.add(hongYanSha);

            values.add("天乙");
            values.add("天乙");
            values.add("文昌");
            values.add("羊刃");
            values.add("干禄");
            values.add("红艳");
        }
        dayCursorC.close();

        Cursor dayCursorT = dbManager.execute("Select * from ShenSha_DayTerrestrial where TerrestrialName = ?", new String[]{baZiActivityWrapper.getT(baZiActivityWrapper.getDayEraIndex())});
        if(dayCursorT.getCount()>0)
        {
            dayCursorT.moveToFirst();
            String jiangXing = dbManager.getColumnValue(dayCursorT,"JiangXing");
            String huaGai = dbManager.getColumnValue(dayCursorT,"HuaGai");
            String yiMa = dbManager.getColumnValue(dayCursorT,"YiMa");
            String jieSha = dbManager.getColumnValue(dayCursorT,"JieSha");
            String wangShen = dbManager.getColumnValue(dayCursorT,"WangShen");
            String taoHua = dbManager.getColumnValue(dayCursorT,"TaoHua");

            dayContent+=" 将星-"+jiangXing+"\n";
            dayContent+=" 华盖-"+huaGai+"\n";
            dayContent+=" 驿马-"+yiMa+"\n";
            dayContent+=" 劫煞-"+jieSha+"\n";
            dayContent+=" 亡神-"+wangShen+"\n";
            dayContent+=" 桃花-"+taoHua;
            tvDayContent.setText(dayContent);

            names.add(jiangXing);
            names.add(huaGai);
            names.add(yiMa);
            names.add(jieSha);
            names.add(wangShen);
            names.add(taoHua);

            values.add("将星");
            values.add("华盖");
            values.add("驿马");
            values.add("劫煞");
            values.add("亡神");
            values.add("桃花");
        }
        dayCursorT.close();
        dbManager.closeDatabase();
        return Pair.create(names,values);
    }

    private void setClickableString(SpannableString spStr, int begin, int end, final String compareValue)
    {
        spStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(true);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                showShenShaDialog(compareValue, getShenShaContentByName(compareValue));
            }
        }, begin, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private String getShenShaContentByName(String name)
    {
        String content = "";
        dbManager.openDatabase();
        Cursor cursor = dbManager.execute("SELECT * FROM ShenSha where name = ?"
                ,new String[] { name });
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            content = dbManager.getColumnValue(cursor, "Note");
        }

        cursor.close();
        dbManager.closeDatabase();

        return content;
    }

    private void loadShenShaInUsing(Pair<ArrayList<String>,ArrayList<String>> pair,Integer flowYearEraIndex, int daYunEraIndex, Integer flowMonthEraIndex)
    {
        tvShenShaContent = (TextView) activity.findViewById(R.id.tvShenShaContent);
        tvShenShaContent.setText("");

        SpannableString contentDaYun = null;
        SpannableString contentFlowYear = null;
        SpannableString contentYear= null;
        SpannableString contentMonth= null;
        SpannableString contentDay= null;
        SpannableString contentHour = null;

        List<SpannableString> daYunList = new ArrayList<SpannableString>();
        List<SpannableString> flowYearList = new ArrayList<SpannableString>();
        List<SpannableString> yearList = new ArrayList<SpannableString>();
        List<SpannableString> monthList = new ArrayList<SpannableString>();
        List<SpannableString> dayList = new ArrayList<SpannableString>();
        List<SpannableString> hourList = new ArrayList<SpannableString>();

        for(int i=0; i<pair.first.size(); i++)
        {
            if(flowYearEraIndex != null)
            {
                contentFlowYear = buildShenShaContentClick(flowYearEraIndex, "流年", pair.first.get(i), pair.second.get(i));
                flowYearList.add(contentFlowYear);
            }

            if(flowMonthEraIndex != null)
            {
                contentFlowYear = buildShenShaContentClick(flowMonthEraIndex, "流月", pair.first.get(i), pair.second.get(i));
                flowYearList.add(contentFlowYear);
            }

            contentDaYun = buildShenShaContentClick(daYunEraIndex, "大运", pair.first.get(i), pair.second.get(i));
            daYunList.add(contentDaYun);

            contentYear = buildShenShaContentClick(baZiActivityWrapper.getYearEraIndex(),"年",pair.first.get(i),pair.second.get(i));
            yearList.add(contentYear);

            contentMonth = buildShenShaContentClick(baZiActivityWrapper.getMonthEraIndex(), "月", pair.first.get(i), pair.second.get(i));
            monthList.add(contentMonth);

            contentDay = buildShenShaContentClick(baZiActivityWrapper.getDayEraIndex(), "日", pair.first.get(i), pair.second.get(i));
            dayList.add(contentDay);

            contentHour = buildShenShaContentClick(baZiActivityWrapper.getHourEraIndex(), "时", pair.first.get(i), pair.second.get(i));
            hourList.add(contentHour);
        }

        setTvShenShaContent(flowYearList);
        tvShenShaContent.append("\n");
        setTvShenShaContent(daYunList);
        tvShenShaContent.append("\n");
        setTvShenShaContent(yearList);
        setTvShenShaContent(monthList);
        setTvShenShaContent(dayList);
        setTvShenShaContent(hourList);
        tvShenShaContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setTvShenShaContent(List<SpannableString> list)
    {
        for(SpannableString sp: list)
        {
            appendContentToShenSha(sp);
        }
    }

    private void appendContentToShenSha(SpannableString spannableString)
    {
        if(spannableString != null && spannableString.length() > 0)
        {
            tvShenShaContent.append(spannableString);
        }
    }

    private void showShenShaDialog(String title, String content)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View lineNoteView = inflater.inflate(R.layout.common_shensha_description, null);
        TextView tvTitle = (TextView)lineNoteView.findViewById(R.id.tvTitle);
        TextView tvShenSha = (TextView)lineNoteView.findViewById(R.id.tvShenShaContent);

        tvTitle.setText(title);
        tvShenSha.setText(content);

        Dialog dialog = new Dialog(getActivity(),R.style.CustomDialog);
        dialog.setContentView(lineNoteView);
        dialog.show();

        WindowManager windowManager = getActivity().getWindowManager();

        Display display = windowManager.getDefaultDisplay();
        Window win = dialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        win.setLayout(dm.widthPixels/3*2, dm.heightPixels /3*2);
    }

    private String buildShenShaContent(int eraIndex, String name, String compareName, String compareValue)
    {
        String content = "";
        String fT = baZiActivityWrapper.getT(eraIndex);
        String fC = baZiActivityWrapper.getC(eraIndex);

        if(fC.equals(compareName))
        {
            content += name + "("+fC+")-" + compareValue + "\n";
        }
        if(fT.equals(compareName))
        {
            content += name + "("+fT+")-" + compareValue + "\n";
        }

        return content;
    }

    private SpannableString buildShenShaContentClick(int eraIndex, String name, String compareName, String compareValue)
    {
        String content = "";
        String fT = baZiActivityWrapper.getT(eraIndex);
        String fC = baZiActivityWrapper.getC(eraIndex);

        int firstBegin = 0;
        int firstEnd = 0;
        int secondBegin = 0;
        int secondEnd = 0;

        if(fC.equals(compareName))
        {
            String part = name + "("+fC+")-";
            firstBegin = part.length();
            content += part + compareValue + "\n";
            firstEnd = content.length();
        }
        if(fT.equals(compareName))
        {
            String part = name + "("+fT+")-";
            secondBegin = part.length() + content.length();
            content += part + compareValue + "\n";
            secondEnd = content.length();
        }

        SpannableString spannableString = new SpannableString(content);
        setClickableString(spannableString,firstBegin,firstEnd, compareValue);
        setClickableString(spannableString,secondBegin,secondEnd, compareValue);

        return spannableString;
    }

    private void loadJiaGongControls()
    {
        tv_column_l1=(TextView) activity.findViewById(R.id.tv_column_l1);
        tv_column_l3=(TextView) activity.findViewById(R.id.tv_column_l3);
        tv_column_l5=(TextView) activity.findViewById(R.id.tv_column_l5);
        tv_column_l7=(TextView) activity.findViewById(R.id.tv_column_l7);
        tv_column_l9=(TextView) activity.findViewById(R.id.tv_column_l9);

        tv_column_d1=(TextView) activity.findViewById(R.id.tv_column_d1);
        tv_column_d3=(TextView) activity.findViewById(R.id.tv_column_d3);
        tv_column_d5=(TextView) activity.findViewById(R.id.tv_column_d5);
        tv_column_d7=(TextView) activity.findViewById(R.id.tv_column_d7);
        tv_column_d9=(TextView) activity.findViewById(R.id.tv_column_d9);

        tvFlowYearBad = (TextView) activity.findViewById(R.id.tv_flowYear_Bad);
    }

    private void loadJiaGongView(Integer flowYearEraIndex, int daYunEraIndex)
    {
        if(flowYearEraIndex != null) {
            baZiActivityWrapper.setJiaGong(this.tv_column_l1, Pair.create(EnumPart.FlowYear, EnumPart.DaYun), daYunEraIndex, flowYearEraIndex);
            baZiActivityWrapper.setJiaGong(this.tv_column_l3, Pair.create(EnumPart.FlowYear, EnumPart.Year), daYunEraIndex, flowYearEraIndex);
            baZiActivityWrapper.setJiaGong(this.tv_column_l5, Pair.create(EnumPart.FlowYear, EnumPart.Month), daYunEraIndex, flowYearEraIndex);
            baZiActivityWrapper.setJiaGong(this.tv_column_l7, Pair.create(EnumPart.FlowYear, EnumPart.Day), daYunEraIndex, flowYearEraIndex);
            baZiActivityWrapper.setJiaGong(this.tv_column_l9, Pair.create(EnumPart.FlowYear, EnumPart.Hour), daYunEraIndex, flowYearEraIndex);
        }
        baZiActivityWrapper.setJiaGong(this.tv_column_d3, Pair.create(EnumPart.DaYun, EnumPart.Year),daYunEraIndex);
        baZiActivityWrapper.setJiaGong(this.tv_column_d5, Pair.create(EnumPart.DaYun, EnumPart.Month),daYunEraIndex);
        baZiActivityWrapper.setJiaGong(this.tv_column_d7, Pair.create(EnumPart.DaYun, EnumPart.Day),daYunEraIndex);
        baZiActivityWrapper.setJiaGong(this.tv_column_d9, Pair.create(EnumPart.DaYun, EnumPart.Hour), daYunEraIndex);

        loadFlowYearBad(flowYearEraIndex, daYunEraIndex);
    }

    private void loadFlowYearBad(Integer flowYearEraIndex, int daYunEraIndex)
    {
        String content = "流年提示:";
        dbManager.openDatabase();
        String riZhu = baZiActivityWrapper.getC(baZiActivityWrapper.getDayEraIndex());

        String sql = "SELECT * FROM FlowYearTips_Bad where RiZhu=?";
        Cursor cur = dbManager.execute(sql, new String[]
                {riZhu});

        String[] baZiExistedArray = new String[4];
        baZiExistedArray[0] = baZiActivityWrapper.getT(baZiActivityWrapper.getYearEraIndex());
        baZiExistedArray[1] = baZiActivityWrapper.getT(baZiActivityWrapper.getMonthEraIndex());
        baZiExistedArray[2] = baZiActivityWrapper.getT(baZiActivityWrapper.getDayEraIndex());
        baZiExistedArray[3] = baZiActivityWrapper.getT(baZiActivityWrapper.getHourEraIndex());

        String[] flowYearAndDaYunArray = new String[2];
        flowYearAndDaYunArray[0] = baZiActivityWrapper.getT(daYunEraIndex);
        if(flowYearEraIndex != null) {
            flowYearAndDaYunArray[1] = baZiActivityWrapper.getT(flowYearEraIndex);
        }
        else
        {
            flowYearAndDaYunArray[1] = "";
        }


        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {

            int countHits = 0;

            String category = dbManager.getColumnValue(cur,"Category");
            String riZhuStr = dbManager.getColumnValue(cur,"RiZhu");
            String basicCondition = dbManager.getColumnValue(cur,"BasicCondition");
            String meetCondition = dbManager.getColumnValue(cur,"MeetCondition");
            String[] basicConditionArray = basicCondition.split(",");
            String[] meetCondtionArray = meetCondition.split(",");

            for (String temp: baZiExistedArray)
            {
                for (String compare : basicConditionArray)
                {
                    if(temp.compareTo(compare) ==0 ) {
                        countHits++;
                        break;
                    }
                }
            }

            if(countHits == 2)
            {
                for(String temp: flowYearAndDaYunArray)
                {
                    for(String compare: meetCondtionArray)
                    {
                        if(compare.indexOf("|") > 0)
                        {
                            String[] strArrays = compare.split("|");
                            for(String str: strArrays)
                            {
                                if(!StringHelper.isNullOrEmpty(str) && str.compareTo(temp) == 0) {
                                    countHits++;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            if(compare.compareTo(temp) == 0)
                            {
                                countHits ++;
                                break;
                            }
                        }

                    }
                }
            }

            if(countHits >= 4)
            {
                content += " | "+category;
            }
        }

        cur.close();
        dbManager.closeDatabase();

        tvFlowYearBad.setText(content);
    }

    private void loadTiaoHouControls()
    {
        tv_vp_tiaoHou = (TextView) activity.findViewById(R.id.tv_vp_tiaohou);
    }

    private void loadTiaoHouView()
    {
        dbManager.openDatabase();
        String riZhu = baZiActivityWrapper.getC(baZiActivityWrapper.getDayEraIndex());
        String yuFen = baZiActivityWrapper.getT(baZiActivityWrapper.getMonthEraIndex());

        String sql = "SELECT * FROM YuShiTiaoHou where RiZhu=? and YueFen=?";
        Cursor cur = dbManager.execute(sql, new String[]
                {riZhu, yuFen});
        String result = "";

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int commentIndex = cur.getColumnIndex("Comment");
            int yongShenIndex1 = cur.getColumnIndex("YongShen1");
            int yongShenIndex2 = cur.getColumnIndex("YongShen2");
            int jiShenIndex = cur.getColumnIndex("JiShen");
            int shiYongZhouQiIndex = cur.getColumnIndex("ShiYongZhouQi");

            String comment = cur.getString(commentIndex);
            String yongShen1 = cur.getString(yongShenIndex1);
            String yongShen2 = cur.getString(yongShenIndex2);
            String jiShen = cur.getString(jiShenIndex);
            String shiYongZhouqi = cur.getString(shiYongZhouQiIndex);

            result += "用神:" + StringHelper.getString(yongShen1) + " " + StringHelper.getString(yongShen2) + "\n";
            result += "忌神:" + StringHelper.getString(jiShen) + "\n\n";
            result += "适用周期:" + StringHelper.getString(shiYongZhouqi) + "\n";
            result += comment + "\n";
        }

        cur.close();

        String sqlDetail = "SELECT * FROM YuShiTiaoHouDetail where RiZhu=? and YueFen=?";
        Cursor curDetail = dbManager.execute(sqlDetail, new String[]
                {riZhu, yuFen});
        String resultDetail = "";

        for (curDetail.moveToFirst(); !curDetail.isAfterLast(); curDetail.moveToNext()) {
            int geJu1Index = curDetail.getColumnIndex("GeJu1");
            int geJu2Index = curDetail.getColumnIndex("GeJu2");
            int geJu3Index = curDetail.getColumnIndex("GeJu3");
            int yongShen1Index = curDetail.getColumnIndex("YongShen1");
            int yongShen2Index = curDetail.getColumnIndex("YongShen2");
            int yongShen3Index = curDetail.getColumnIndex("YongShen3");
            int noteIndex = curDetail.getColumnIndex("Note");
            int likeIndex = curDetail.getColumnIndex("Like");
            int hateIndex = curDetail.getColumnIndex("Hate");

            String geJu1 = curDetail.getString(geJu1Index);
            String tempValue = getValue(geJu1, ",");
            resultDetail = resultDetail+"\n\n格局:"+tempValue;

            String geJu2 = curDetail.getString(geJu2Index);
            tempValue = getValue(geJu2,",");
            resultDetail = resultDetail + tempValue;

            String geJu3 = curDetail.getString(geJu3Index);
            tempValue = getValue(geJu3,",");
            resultDetail = resultDetail + tempValue;

            String yongShen1 = curDetail.getString(yongShen1Index);
            String yongShen2 = curDetail.getString(yongShen2Index);
            String yongShen3 = curDetail.getString(yongShen3Index);

            resultDetail = resultDetail + "\n用神:";
            tempValue = getValue(yongShen1,",");
            resultDetail = resultDetail + tempValue;
            tempValue = getValue(yongShen2,",");
            resultDetail = resultDetail + tempValue;
            tempValue = getValue(yongShen3,",");
            resultDetail = resultDetail + tempValue;

            resultDetail = resultDetail +"\n注释:";
            String note = curDetail.getString(noteIndex);
            resultDetail = resultDetail + note;

            resultDetail = resultDetail +"\n喜神:";
            String like = curDetail.getString(likeIndex);
            tempValue = getValue(like,"");
            resultDetail = resultDetail + tempValue;

            resultDetail = resultDetail +"\n忌神:";
            String hate = curDetail.getString(hateIndex);
            tempValue = getValue(hate,"");
            resultDetail = resultDetail + tempValue;

        }

        curDetail.close();

        dbManager.closeDatabase();

        tv_vp_tiaoHou.setText(result +resultDetail+"\n\n\n");
    }

    private String getValue(String dbValue, String preFix)
    {
        if(dbValue != null && !dbValue.equals(""))
            return dbValue+preFix;
        else
            return "";
    }

    private void loadJinBuHuanControls()
    {
        tv_jinbuhuan_title = (TextView) activity.findViewById(R.id.tv_jinbuhuan_title);
        tv_jinbuhuan_detail = (TextView) activity.findViewById(R.id.tv_jinbuhuan_detail);
        tv_tiaohou_title = (TextView) activity.findViewById(R.id.tv_tiaohou_title);
        tv_jinbuhuan_note = (TextView) activity.findViewById(R.id.tv_jinbuhuan_note);
    }

    private void loadJinBuHuanView()
    {
        dbManager.openDatabase();
        String riZhu = baZiActivityWrapper.getC(baZiActivityWrapper.getDayEraIndex());
        String yuFen = baZiActivityWrapper.getT(baZiActivityWrapper.getMonthEraIndex());

        String sql = "SELECT * FROM JinBuHuanDaYun where RiZhu=? and YueFen=?";
        Cursor cur = dbManager.execute(sql, new String[]
                {riZhu, yuFen});
        String resultJinBuHuanTitle = "日主:"+riZhu +"\n"+"月份:"+yuFen;
        String resultTiaoHouTitle = "";
        String resultJinBuHuanDetail = "";
        String resultNote = "";

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int detailIndex = cur.getColumnIndex("Detail");
            int tiaoHouXiIndex = cur.getColumnIndex("TiaoHouXi");
            int tiaoHouJiIndex = cur.getColumnIndex("TiaoHouJi");
            int noteIndex = cur.getColumnIndex("Note");
            int poemIndex = cur.getColumnIndex("Poem");

            String poem = cur.getString(poemIndex);
            resultJinBuHuanDetail ="诗:\n"+poem+"\n\n解释:\n"+ cur.getString(detailIndex).replaceAll("\n", "");
            String tiaoHouXi = cur.getString(tiaoHouXiIndex);
            String tiaoHouJi = cur.getString(tiaoHouJiIndex);

            resultTiaoHouTitle = "调候喜:"+tiaoHouXi + "\n" +"调候忌:"+tiaoHouJi;
            resultNote = cur.getString(noteIndex);
            if(resultNote != null && !resultNote.equals(""))
                resultNote = "备注:\n"+ resultNote;
        }

        cur.close();

        String sqlTip = "SELECT * FROM TiRuoDuoBing_MuNa where RiZhu=? and YueFen=?";
        Cursor curTip = dbManager.execute(sqlTip, new String[]
                {riZhu, yuFen});
        for (curTip.moveToFirst(); !curTip.isAfterLast(); curTip.moveToNext()) {
            String xingGeMuNa = dbManager.getColumnValue(curTip,"TiRuoDuoBing");
            String tiRuoDuoBing = dbManager.getColumnValue(curTip,"XingGeMuNa");
            if(!StringHelper.isNullOrEmpty(xingGeMuNa))
                resultNote ="\n" +"格局:"+xingGeMuNa+ "-性格木讷";
            if(!StringHelper.isNullOrEmpty(tiRuoDuoBing))
                resultNote ="\n" +"格局:"+tiRuoDuoBing+ "-体弱多病";
        }
        curTip.close();
        dbManager.closeDatabase();

        tv_jinbuhuan_title.setText(resultJinBuHuanTitle);
        tv_jinbuhuan_detail.setText(resultJinBuHuanDetail);
        tv_tiaohou_title.setText(resultTiaoHouTitle);
        tv_jinbuhuan_note.setText(resultNote);
    }
}
