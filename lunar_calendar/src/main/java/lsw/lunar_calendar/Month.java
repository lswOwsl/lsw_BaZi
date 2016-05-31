package lsw.lunar_calendar;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import lsw.ContactAuthor;
import lsw.PhotoImagesFullSizeFragment;
import lsw.library.ColorHelper;
import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.library.DateLunar;
import lsw.library.LunarCalendarWrapper;
import lsw.library.SolarTerm;
import lsw.lunar_calendar.advertising.BaiDuBanner;
import lsw.lunar_calendar.common.DateSelectorDialog;
import lsw.lunar_calendar.common.LunarDateSelectorDialog;
import lsw.lunar_calendar.common.ViewGesture;
import lsw.lunar_calendar.data_source.CalendarAdapter;
import lsw.lunar_calendar.data_source.CalendarTitleAdapter;
import lsw.utility.Image.ImageSelectListener;
import lsw.utility.Image.SourceImage;

import net.simonvt.menudrawer.MenuDrawer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class Month extends Activity implements MonthFragment.OnFragmentInteractionListener {

    private RelativeLayout llMask;
    //private GridView gridView;
    private GridView gridViewTitle;

    private LayoutInflater linearLayout;
    private DateExt initialDate;

    private TextView tvDateSelect;
    private TextView tvDateLunarSelect;

    private TextView tvEraYear, tvEraMonth, tvEraDay, tvEraHour;

    private TextView tvAllBirthday;

    private LunarCalendarWrapper lunarCalendarWrapper;

    ViewGesture viewGesture;

    private TextView tvSolarTerm1, tvSolarTerm2;

    private FrameLayout flHexagrams;

    private MenuDrawer mDrawer;
    private int menuWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_month);

        mDrawer = MenuDrawer.attach(this);
        mDrawer.setMenuView(R.layout.menu_left);
        mDrawer.setContentView(R.layout.activity_month);

        llMask = (RelativeLayout) findViewById(R.id.layoutMask);

        mDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_CLOSED) {
                    loadBirthdayAndHexagram(null);
                    flHexagrams.setVisibility(View.VISIBLE);
                    tvAllBirthday.setText("本月全部生日");
                }

            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {

            }
        });


        //侧边栏宽度，占整窗体的3/2
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        menuWidth = dm.widthPixels / 3 * 2;
        mDrawer.setMenuSize(menuWidth);

        BaiDuBanner banner = new BaiDuBanner(this);
        banner.create();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvAllBirthday = (TextView) mDrawer.getMenuView().findViewById(R.id.tvAllBirthday);

        flHexagrams = (FrameLayout) findViewById(R.id.fl_list);

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
        tvAllBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAllBirthday.getText().toString().equals("本月全部生日")) {
                    FragmentTransaction ftt = getFragmentManager().beginTransaction();
                    BirthdayListFragment birthdayListFragment = BirthdayListFragment.newInstance(initialDate);
                    birthdayListFragment.setForCurrentMonth(true);
                    ftt.replace(R.id.fl_list_birthday, birthdayListFragment, null);
                    ftt.commit();

                    flHexagrams.setVisibility(View.GONE);
                    tvAllBirthday.setText("恢复默认");
                } else {
                    loadBirthdayAndHexagram(null);
                    flHexagrams.setVisibility(View.VISIBLE);
                    tvAllBirthday.setText("本月全部生日");
                }
            }
        });

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

        setEraTextDetail(tvEraYear, eraYearIndex);
        setEraTextDetail(tvEraMonth, eraMonthIndex);
        setEraTextDetail(tvEraDay, eraDayIndex);
        setEraTextDetail(tvEraHour, eraHourIndex);

        tvEraDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        loadSolarTerms(lunarCalendarWrapper);
    }

    private void loadSolarTerms(LunarCalendarWrapper lunarCalendarWrapper) {
        Pair<SolarTerm, SolarTerm> solarTermPair = lunarCalendarWrapper.getPairSolarTerm();
        tvSolarTerm1.setText(solarTermPair.first.getName() + ": " + solarTermPair.first.getSolarTermDate().getFormatDateTime("MM月dd日 HH:mm"));
        tvSolarTerm2.setText(solarTermPair.second.getName() + ": " + solarTermPair.second.getSolarTermDate().getFormatDateTime("MM月dd日 HH:mm"));
    }

    private void setEraTextDetail(TextView tv, int eraIndex) {
        tv.setText("");
        tv.append(ColorHelper.getInstance(this).getColorCelestialStem(lunarCalendarWrapper.toStringWithCelestialStem(eraIndex)));
        tv.append(ColorHelper.getInstance(this).getColorTerrestrial(lunarCalendarWrapper.toStringWithTerrestrialBranch(eraIndex)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_month, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            if (mDrawer.isMenuVisible())
                mDrawer.closeMenu();
            else
                mDrawer.openMenu();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.menuToday) {
            initialDate = new DateExt();
            initFragment(initialDate);
            loadTitileDate(initialDate);
            lunarCalendarWrapper = new LunarCalendarWrapper(initialDate);
            loadEraTextDetail(lunarCalendarWrapper);
            return true;
        }

        if (id == R.id.menuContact) {
            Intent intentContact = new Intent();
            intentContact.setClass(Month.this, ContactAuthor.class);
            startActivityForResult(intentContact, 0);
            return true;
        }

        if(id == R.id.menuSetting)
        {
            Intent intent = new Intent();
            intent.setClass(Month.this, Setting.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(DateExt dateExt) {

        if (isExit) {
            return;
        }
        isExit = true;
        // 利用handler延迟发送更改状态信息
        mHandler.sendEmptyMessageDelayed(0, 2000);

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
        }

        if (willChange) {
            loadFragment(dateExt, willChange && flag);
        }
        initialDate = dateExt;

        loadBirthdayAndHexagram(null);
    }

    private void loadBirthdayAndHexagram(FragmentTransaction ftt) {
        if (ftt == null)
            ftt = getFragmentManager().beginTransaction();
        HexagramListFragment fragment = HexagramListFragment.newInstance(initialDate);
        ftt.replace(R.id.fl_list, fragment, null);

        BirthdayListFragment birthdayListFragment = BirthdayListFragment.newInstance(initialDate);
        ftt.replace(R.id.fl_list_birthday, birthdayListFragment, null);

        ftt.commit();
    }

    public void initFragment(DateExt dateExt) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        MonthFragment f1 = MonthFragment.newInstance(dateExt);
        ft.replace(R.id.fl_calendar, f1, "begin_calendar");

        MonthFragment f2 = MonthFragment.newInstance(dateExt);
        if (!f2.isAdded()) {
            ft.add(R.id.fl_calendar, f2, "move_calendar");
            ft.hide(f2);
        }

        loadBirthdayAndHexagram(ft);
    }

    public void loadFragment(DateExt dateExt, boolean moveDirection) {
        MonthFragment f2 = (MonthFragment) getFragmentManager().findFragmentByTag("move_calendar");
        MonthFragment f1 = (MonthFragment) getFragmentManager().findFragmentByTag("begin_calendar");
        f1.setDateExt(dateExt);
        f2.setDateExt(dateExt);
        GridView gridView = f1.getGridView();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (moveDirection) {
            ft.setCustomAnimations(R.animator.vertical_bottom_enter, R.animator.vertical_top_out, 0, 0);
        } else {
            ft.setCustomAnimations(R.animator.vertical_top_enter, R.animator.vertical_bottom_out, 0, 0);
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
