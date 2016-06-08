package lsw.lunar_calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import lsw.library.BaZiHelper;
import lsw.library.ColorHelper;
import lsw.library.DateExt;
import lsw.library.DateLunar;
import lsw.library.LunarCalendarWrapper;
import lsw.library.SolarTerm;
import lsw.lunar_calendar.advertising.BaiDuBanner;
import lsw.lunar_calendar.common.DateSelectorDialog;
import lsw.lunar_calendar.common.IntentKeys;
import lsw.lunar_calendar.common.LunarDateSelectorDialog;
import lsw.lunar_calendar.common.ViewGesture;
import lsw.lunar_calendar.data_source.CalendarAdapter;
import lsw.lunar_calendar.data_source.CalendarTitleAdapter;

public class Month extends BaseMenu implements MonthFragment.OnFragmentInteractionListener {

    private RelativeLayout llMask;
    //private GridView gridView;
    private GridView gridViewTitle;

    private LayoutInflater linearLayout;


    private TextView tvDateSelect;
    private TextView tvDateLunarSelect;

    private TextView tvEraYear, tvEraMonth, tvEraDay, tvEraHour;

    private TextView tvAllBirthday;

    private LunarCalendarWrapper lunarCalendarWrapper;

    ViewGesture viewGesture;

    private TextView tvSolarTerm1, tvSolarTerm2;

    private Context currentContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_month);

        currentContext = this;


        mDrawer.setContentView(R.layout.activity_month);

        llMask = (RelativeLayout) findViewById(R.id.layoutMask);

        BaiDuBanner banner = new BaiDuBanner(this);
        banner.create();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvAllBirthday = (TextView) mDrawer.getMenuView().findViewById(R.id.tvAllBirthday);

        linearLayout = LayoutInflater.from(this);
        tvDateSelect = (TextView) findViewById(R.id.tvDateSelect);
        tvDateLunarSelect = (TextView) findViewById(R.id.tvLunarDateSelect);

        tvEraYear = (TextView) findViewById(R.id.tvEraYear);
        tvEraMonth = (TextView) findViewById(R.id.tvEraMonth);
        tvEraDay = (TextView) findViewById(R.id.tvEraDay);
        tvEraHour = (TextView) findViewById(R.id.tvEraHour);

        tvSolarTerm1 = (TextView) findViewById(R.id.tvSolarTerm1);
        tvSolarTerm2 = (TextView) findViewById(R.id.tvSolarTerm2);

        gridViewTitle = (GridView) findViewById(R.id.gv_calendarTitle);
        gridViewTitle.setAdapter(new CalendarTitleAdapter(linearLayout));
        gridViewTitle.setNumColumns(7);
        initialDate = new DateExt();

        lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
        loadTitileDate(initialDate);
        loadEraTextDetail(lunarCalendarWrapper);

        bindAction();
        //viewGesture.setGestureTo(gridView);

        initFragment(initialDate);


    }

    private void bindAction() {

        tvDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateSelectorDialog dialog = new DateSelectorDialog(initialDate, Month.this);
                dialog.setCallBack(new DateSelectorDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                        loadTitileDate(dateExt);
                        lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
                        loadEraTextDetail(lunarCalendarWrapper);

                        initFragment(dateExt);

                        initialDate = dateExt;

                    }
                });
                dialog.show();
            }
        });

        tvDateLunarSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LunarDateSelectorDialog dialog = new LunarDateSelectorDialog(initialDate, Month.this);
                dialog.setCallBack(new LunarDateSelectorDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                        loadTitileDate(dateExt);
                        lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
                        loadEraTextDetail(lunarCalendarWrapper);
                        initFragment(dateExt);
                        initialDate = dateExt;
                    }
                });
                dialog.show();
            }
        });

        viewGesture = new ViewGesture(this, new ViewGesture.OnViewGestureCallBack() {
            @Override
            public void moveUp() {

                if (!isExit) {
                    initialDate.addMonths(1);
                    loadTitileDate(initialDate);
                    lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
                    loadEraTextDetail(lunarCalendarWrapper);
                    loadFragment(initialDate, true);
                    isExit = true;
                    // 利用handler延迟发送更改状态信息
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                    mask.show("下个月");

                }
            }

            @Override
            public void moveDown() {
                if (!isExit) {
                    initialDate.addMonths(-1);
                    loadTitileDate(initialDate);
                    lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
                    loadEraTextDetail(lunarCalendarWrapper);
                    loadFragment(initialDate, false);
                    isExit = true;
                    // 利用handler延迟发送更改状态信息
                    mHandler.sendEmptyMessageDelayed(0, 2000);
                    mask.show("上个月");

                }
            }
        });
    }

    Mask mask = new Mask();

    // 定义一个变量，来标识是否滑动
    private static boolean isExit = false;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
            mask.remove();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    //å¿…é¡»é‡�å†™è¿™ä¸ªæ–¹æ³•è¦�ä¸�ç„¶ä¼šæŠ›å¼‚å¸¸ï¼Œå› ä¸ºgridviewçš„filingå�ˆonclickå†²çª�äº†
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (!mDrawer.isMenuVisible())
            viewGesture.getDetector().onTouchEvent(ev);

        return super.dispatchTouchEvent(ev);
    }

    private void loadTitileDate(DateExt dateExt) {
        DateLunar dateLunar = lunarCalendarWrapper.getDateLunar(dateExt);
        String lunarDate = lunarCalendarWrapper.toStringWithChineseYear(dateLunar.getLunarYear()) + "年" +
                (dateLunar.getIsLeapMonth() ? "闰" : "") +
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

        final String lunarYear = setEraTextDetail(tvEraYear, eraYearIndex);
        final String lunarMonth = setEraTextDetail(tvEraMonth, eraMonthIndex);
        final String lunarDay = setEraTextDetail(tvEraDay, eraDayIndex);
        setEraTextDetail(tvEraHour, eraHourIndex);

        tvEraDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle mBundle = createBundleBy(
                        initialDate.getFormatDateTime(),
                        initialDate.getFormatDateTime(),
                        "day",
                        lunarYear + "年 " + lunarMonth + "月 " + lunarDay + "日 ");

                showNoteDialog(mBundle);
            }
        });

        tvEraMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair<SolarTerm, SolarTerm> pair = BaZiHelper.getPairJie(initialDate);

                Bundle bundle = createBundleBy(pair.first.getSolarTermDate().getFormatDateTime(),
                        pair.second.getSolarTermDate().getFormatDateTime(),
                        "month",
                        lunarYear + "年 " + lunarMonth + "月");

                showNoteDialog(bundle);
            }
        });

        loadSolarTerms(lunarCalendarWrapper);
    }

    private Bundle createBundleBy(String beginTime,String endTime, String recordCycle, String lunarTime)
    {
        Bundle mBundle = new Bundle();
        mBundle.putString(IntentKeys.BeginDate, beginTime);
        mBundle.putString(IntentKeys.EndDate, endTime);
        mBundle.putString(IntentKeys.RecordCycle, recordCycle);
        mBundle.putString(IntentKeys.LunarTime, lunarTime);
        return mBundle;
    }

    int bundle_recordType = 0;
    private void showNoteDialog(final Bundle bundle)
    {
        String[] strings = new String[]{"预测 记录","事件 记录"};

        new AlertDialog.Builder(this)
                .setSingleChoiceItems(
                        strings, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                bundle_recordType = which;
                            }
                        })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(currentContext, Memory.class);
                        bundle.putInt(IntentKeys.RecordType, bundle_recordType);
                        intent.putExtras(bundle);
                        //跳转activity
                        currentContext.startActivity(intent);
                        //阻止回退后recordType没有变成默认
                        bundle_recordType = 0;
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void loadSolarTerms(LunarCalendarWrapper lunarCalendarWrapper) {
        Pair<SolarTerm, SolarTerm> solarTermPair = lunarCalendarWrapper.getPairSolarTerm();
        tvSolarTerm1.setText(solarTermPair.first.getName() + ": " + solarTermPair.first.getSolarTermDate().getFormatDateTime("MM月dd日 HH:mm"));
        tvSolarTerm2.setText(solarTermPair.second.getName() + ": " + solarTermPair.second.getSolarTermDate().getFormatDateTime("MM月dd日 HH:mm"));
    }

    private String setEraTextDetail(TextView tv, int eraIndex) {
        String celestialStem = lunarCalendarWrapper.toStringWithCelestialStem(eraIndex);
        String terrestrial = lunarCalendarWrapper.toStringWithTerrestrialBranch(eraIndex);
        tv.setText("");
        tv.append(ColorHelper.getInstance(this).getColorCelestialStem(celestialStem));
        tv.append(ColorHelper.getInstance(this).getColorTerrestrial(terrestrial));
        return celestialStem+terrestrial;
    }

    @Override
    public void onFragmentInteraction(DateExt dateExt) {

        lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        loadTitileDate(dateExt);
        loadEraTextDetail(lunarCalendarWrapper);
        //å› ä¸ºåœ¨ä¸€ä¸ªæœˆä»½ä¸Šï¼Œå�¯èƒ½ä¼šé€‰åˆ™ä¸Šä¸ªæœˆçš„æŸ�ä¸€å¤©ï¼Œä¹Ÿå�¯èƒ½é€‰æ‹©ä¸‹ä¸ªæœˆçš„æŸ�ä¸€å¤©
        //å½“æ»‘åŠ¨åˆ‡æ�¢æœˆä»½æ—¶ï¼Œåº”è¯¥å�ªç”¨å½“å‰�çš„æœˆä»½è¿›è¡Œæ»‘åŠ¨

        boolean flag = true;
        boolean willChange = false;

        if (initialDate.getYear() == dateExt.getYear()) {
            if (initialDate.getMonth() > dateExt.getMonth()) {
                mask.show("上个月");
                willChange = true;
                flag = false;
            } else if (initialDate.getMonth() < dateExt.getMonth()) {
                mask.show("下个月");
                willChange = true;
                flag = true;
            }
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            if (initialDate.getYear() > dateExt.getYear()) {
                mask.show("上个月");
                willChange = true;
                flag = false;
            } else {
                mask.show("下个月");
                willChange = true;
                flag = true;
            }
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }

        if (willChange) {
            loadFragment(dateExt, willChange && flag);
        }
        initialDate = dateExt;

        loadBirthdayAndHexagram();

    }

    public void initFragment(DateExt dateExt) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        MonthFragment f1 = MonthFragment.newInstance(dateExt);
        ft.replace(R.id.fl_calendar, f1, "begin_calendar");

        MonthFragment f2 = MonthFragment.newInstance(dateExt);
        if (!f2.isAdded()) {
            ft.add(R.id.fl_calendar, f2, "move_calendar");
            ft.hide(f2);
        }

        ft.commit();

        loadBirthdayAndHexagram();
    }

    public void loadFragment(DateExt dateExt, boolean moveDirection) {
        MonthFragment f2 = (MonthFragment) getSupportFragmentManager().findFragmentByTag("move_calendar");
        MonthFragment f1 = (MonthFragment) getSupportFragmentManager().findFragmentByTag("begin_calendar");
        f1.setDateExt(dateExt);
        f2.setDateExt(dateExt);
        GridView gridView = f1.getGridView();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (moveDirection) {
            ft.setCustomAnimations(R.anim.bottom_enter, R.anim.top_out);
        } else {
            ft.setCustomAnimations(R.anim.top_enter, R.anim.bottom_out);
        }

        if (f2.isHidden()) {
            gridView = f2.getGridView();
            ft.hide(f1).show(f2);
        } else {
            ft.hide(f2).show(f1);
        }

        CalendarAdapter calendarAdapter = (CalendarAdapter) gridView.getAdapter();
        calendarAdapter.setDayModels(CalendarAdapter.getOneMonthDays(dateExt));
        calendarAdapter.notifyDataSetChanged();
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuToday) {
            initialDate = new DateExt();
            initFragment(initialDate);
            loadTitileDate(initialDate);
            lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
            loadEraTextDetail(lunarCalendarWrapper);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private TextView textView = null;

    private class Mask {

        public void show(String text) {
            if (textView == null) {
                textView = new TextView(Month.this);
                textView.setTextColor(Color.BLUE);
                textView.setTextSize(20);
                textView.setText("");
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setBackgroundColor(Color.parseColor("#66000000"));
            }
            llMask.addView(textView);

            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            Toast toast = Toast.makeText(Month.this, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
                    0, 0);
            toast.show();
        }

        public void remove() {
            llMask.removeView(textView);
        }
    }
}
