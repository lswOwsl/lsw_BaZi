package lsw.liuyao;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import lsw.PhotoImagesFullSizeFragment;
import lsw.hexagram.Analyzer;
import lsw.hexagram.Builder;
import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.library.LunarCalendar;
import lsw.library.LunarCalendarWrapper;
import lsw.library.StringHelper;
import lsw.library.Utility;
//import lsw.liuyao.advertising.BaiDuInterstitial;
import lsw.liuyao.common.DateTimePickerDialog;
import lsw.liuyao.common.IntentKeys;
import lsw.liuyao.common.NoteFragmentDialog;
import lsw.liuyao.data.Database;
import lsw.liuyao.model.HexagramRow;
import lsw.liuyao.model.ImageAttachment;
import lsw.model.Hexagram;
import lsw.model.Line;
import lsw.utility.CaptureImage;
import lsw.utility.Image.ImageSelectListener;
import lsw.utility.Image.SourceImage;

import net.simonvt.menudrawer.MenuDrawer;

/**
 * Created by swli on 8/7/2015.
 */
public class HexagramAnalyzerActivity extends FragmentActivity implements View.OnTouchListener, HexagramAutoAnalyzerFragment.OnFragmentInteractionListener{

    String formatDateTime = "yyyy年MM月dd日";
    LunarCalendarWrapper lunarCalendarWrapper;
    Hexagram original, changed;
    Analyzer analyzer;

    DateExt analyzeDate, initDate;

    TextView tvAnalyzeDateTitle, tvAnalyzeDate;
    TextView btnNote;

    Database database;
    HexagramRow hexagramRow;
    int hexagramRowId;

    private MenuDrawer mDrawer;
    //private SwipeListView swipeListView;

    //BaiDuInterstitial baiDuInterstitial;

    ArrayList<SourceImage> listImages = new ArrayList<SourceImage>();

    public void loadMenuFragment(int hexagramRowId)
    {
        final int hexagramId = hexagramRowId;
        listImages.clear();

        List<ImageAttachment> imageAttachments = database.getImageAttachmentByHexagramId(hexagramRowId);
        for(ImageAttachment attachment: imageAttachments)
        {
            SourceImage sourceImage = new SourceImage();
            sourceImage.setFullUrl(attachment.getUrl());
            listImages.add(sourceImage);
        }

        if(imageAttachments.size() > 0)
        {
            mDrawer.getMenuView().setEnabled(true);
            mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
            FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
            MenuPhotoImagesFragment menuPhotoImagesFragment = MenuPhotoImagesFragment.createFragment(listImages,true);
            menuPhotoImagesFragment.setImageSelectListener(new ImageSelectListener() {
                @Override
                public void invoke(ArrayList<SourceImage> sourceImages, int index) {
                    //full image view
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    PhotoImagesFullSizeFragment f = PhotoImagesFullSizeFragment.createFragment(sourceImages, index);
                    f.setCurrentFragmentManager(getSupportFragmentManager());
                    ft.replace(R.id.fl_Image_Select, f);

                    FuturePriceFragment futurePriceFragment = FuturePriceFragment.createFragment(initDate.getFormatDateTime(), hexagramId);
                    futurePriceFragment.setShowCondition(false);
                    ft.replace(R.id.fl_Price_List, futurePriceFragment);
                    ft.commit();

                    flPriceList.setVisibility(View.VISIBLE);

                }
            });
            ftt.replace(R.id.fl_Image_Select, menuPhotoImagesFragment, null);
            ftt.commit();
        }
        else {
            mDrawer.getMenuView().setEnabled(false);
            mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
        }
    }

    private int menuWidth;

    FrameLayout flImageSelect, flPriceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        final String formatDate = getIntent().getStringExtra(IntentKeys.FormatDate);

        String originalName = bundle.getString(IntentKeys.OriginalName);
        String changedName = bundle.getString(IntentKeys.ChangedName);
        final int hexagramRowId = bundle.getInt(IntentKeys.HexagramRowId);

        database = new Database(this);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 隐藏状态栏,全屏显示
        // 第一种：
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 第二种：（两种方法效果一样）
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDrawer = MenuDrawer.attach(this);
        mDrawer.setMenuView(R.layout.menu_left);

        mDrawer.setContentView(R.layout.hexagram_analyze_activity);

        mDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {

            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {

            }
        });

        loadMenuFragment(hexagramRowId);

        //侧边栏宽度，占整窗体的3/2
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        menuWidth = dm.widthPixels / 3 * 2;
        mDrawer.setMenuSize(menuWidth);

        flImageSelect = (FrameLayout) mDrawer.getMenuView().findViewById(R.id.fl_Image_Select);
        flImageSelect.setLayoutParams(new LinearLayout.LayoutParams(menuWidth, ViewGroup.LayoutParams.MATCH_PARENT));

        flPriceList = (FrameLayout) mDrawer.getMenuView().findViewById(R.id.fl_Price_List);
        flPriceList.setLayoutParams(new LinearLayout.LayoutParams(menuWidth, ViewGroup.LayoutParams.MATCH_PARENT));

        TextView tvResetImages =  (TextView)mDrawer.getMenuView().findViewById(R.id.tvResetImages);
        tvResetImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMenuFragment(hexagramRowId);

                flPriceList.setVisibility(View.GONE);
            }
        });

        TextView tvPrice = (TextView) mDrawer.getMenuView().findViewById(R.id.tvPrice);
        tvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
                FuturePriceFragment futurePriceFragment = FuturePriceFragment.createFragment(initDate.getFormatDateTime(), hexagramRowId);
                ftt.replace(R.id.fl_Image_Select, futurePriceFragment, null);
                ftt.commit();

                flPriceList.setVisibility(View.GONE);
            }
        });

        TextView tvCapture = (TextView)mDrawer.getMenuView().findViewById(R.id.tvCapture);
        tvCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b1 = CaptureImage.captureViewToImage(mDrawer.getContentContainer());
                Bitmap b2 = CaptureImage.captureViewToImage((FrameLayout)mDrawer.getMenuView().findViewById(R.id.fl_Image_Select));
                Bitmap b3 = CaptureImage.combineImages(b2, b1);
                ListView lvPrice = (ListView)mDrawer.getMenuView().findViewById(R.id.lvPrice);
                if(lvPrice != null) {
                    Bitmap b4 = CaptureImage.getWholeListViewItemsToBitmap(lvPrice);
                    b3 = CaptureImage.combineImages(b4, b3);
                }
                CaptureImage.saveBitmap(b3, "/" + CrossAppKey.PACKAGE_NAME_LIUYAO + "/"
                                + analyzeDate.getFormatDateTime("yyyy-MM-dd") +
                        hexagramRow.getOriginalName() + "-" + hexagramRow.getChangedName() +
                        "(" + new DateExt().getFormatDateTime("yyyy-MM-dd<HH:mm:ss>") + ")"+ ".jpg");
                        Toast.makeText(getApplicationContext(), "图片保存成功.文件位于" + CrossAppKey.PACKAGE_NAME_LIUYAO, Toast.LENGTH_SHORT).show();
            }
        });

//        baiDuInterstitial = new BaiDuInterstitial(this);
//        baiDuInterstitial.create();

        ArrayList<Line> models = null;
        if(StringHelper.isNullOrEmpty(originalName)) {
            models = (ArrayList<Line>) bundle.getSerializable(IntentKeys.LineModelList);
        }
        initDate = new DateExt(formatDate);
        analyzeDate = initDate;

        Builder builder = Builder.getInstance(this);
        analyzer = new Analyzer(this);

        btnNote = (TextView) findViewById(R.id.btnNote);
        //如果不是从列表页 分析 跳转过来的，要隐藏存储备注按钮
        if( hexagramRowId <= 0)
        {
            btnNote.setVisibility(View.GONE);
        }
        else
        {
            this.hexagramRowId = hexagramRowId;
            hexagramRow =  database.getHexagramById(hexagramRowId);
        }

        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NoteFragmentDialog dialog = NoteFragmentDialog.newInstance(hexagramRow);
                dialog.setOnSaveListener(new NoteFragmentDialog.OnSaveListener() {
                    @Override
                    public void afterSave() {
                        loadMenuFragment(hexagramRowId);
                    }
                });

                dialog.show(getSupportFragmentManager(), "");

            }
        });

        try {
            if(!StringHelper.isNullOrEmpty(originalName))
            {
                Pair<Hexagram,Hexagram> pair = builder.getHexagramByNames(originalName, changedName);
                original = pair.first;
                changed = pair.second;
            }
            else {
                original = builder.getHexagramByLines(models, true);
                changed = builder.getChangedHexagramByOriginal(original, false);
            }

            lunarCalendarWrapper = new LunarCalendarWrapper(initDate);
            String eraMonth = lunarCalendarWrapper.toStringWithSexagenary(lunarCalendarWrapper.getChineseEraOfMonth());
            String eraDay = lunarCalendarWrapper.toStringWithSexagenary(lunarCalendarWrapper.getChineseEraOfDay());

            StringBuilder stringBuilder = analyzer.analyzeHexagramResult(eraMonth, eraDay, original, changed);
            ArrayList<String> list = analyzer.orderedStringResult(stringBuilder.toString());

            HexagramBuilderFragment analyzerFragment = HexagramBuilderFragment.newInstance(original, changed, formatDate);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_Hexagram_Analyzer, analyzerFragment, null);

            HexagramAutoAnalyzerFragment autoAnalyzerFragment = HexagramAutoAnalyzerFragment.newInstance(list);
            ft.replace(R.id.fl_Hexagram_Auto_Analyzer, autoAnalyzerFragment, null);

            ft.commit();
        }
        catch (Exception ex)
        {
            Log.e("Hexagram Builder", ex.getMessage());
        }

        initValues();

        tvAnalyzeDateTitle = (TextView) findViewById(R.id.tvAnalyzeDateTitle);
        tvAnalyzeDate = (TextView)findViewById(R.id.tvAnalyzeDate);

        tvAnalyzeDateTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindAnalyzeResult(initDate);
                analyzeDate = initDate;
                tvAnalyzeDate.setText("自选");
            }
        });

        tvAnalyzeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickerDialog pickerDialog = new DateTimePickerDialog(analyzeDate,HexagramAnalyzerActivity.this,false);
                pickerDialog.show();
                pickerDialog.setCallBack(new DateTimePickerDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                        bindAnalyzeResult(dateExt);
                        analyzeDate = dateExt;
                    }
                });
            }
        });
    }

    private void bindAnalyzeResult(DateExt dateExt)
    {
        lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        int eraMonthIndex = lunarCalendarWrapper.getChineseEraOfMonth(true);
        int eraDayIndex = lunarCalendarWrapper.getChineseEraOfDay();
        Pair<String,String> xunKong = Utility.getXunKong(HexagramAnalyzerActivity.this, lunarCalendarWrapper.toStringWithCelestialStem(eraDayIndex), lunarCalendarWrapper.toStringWithTerrestrialBranch(eraDayIndex));
        String eraText =
                lunarCalendarWrapper.toStringWithSexagenary(eraMonthIndex) + "月   " +
                        lunarCalendarWrapper.toStringWithSexagenary(eraDayIndex) +"日   (" + xunKong.first+ xunKong.second+")空";

        DateExt tempDateExt = new DateExt(dateExt.getDate());
        int indexOfWeek = tempDateExt.getIndexOfWeek();
        String weekDay = indexOfWeek == 0 ? "日" : LunarCalendar.toChineseDayInWeek(indexOfWeek);

        tvAnalyzeDate.setText(eraText +"     "+ dateExt.getFormatDateTime(formatDateTime) + " (星期"+weekDay+")");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        String eraMonth = lunarCalendarWrapper.toStringWithSexagenary(eraMonthIndex);
        String eraDay = lunarCalendarWrapper.toStringWithSexagenary(eraDayIndex);

        StringBuilder stringBuilder = analyzer.analyzeHexagramResult(eraMonth, eraDay, original, changed);
        ArrayList<String> list = analyzer.orderedStringResult(stringBuilder.toString());

        HexagramAutoAnalyzerFragment autoAnalyzerFragment = HexagramAutoAnalyzerFragment.newInstance(list);
        ft.replace(R.id.fl_Hexagram_Auto_Analyzer, autoAnalyzerFragment,null);
        ft.commit();
    }

    public static final int SNAP_VELOCITY = 200;
    private int screenWidth;
    private int leftEdge;
    private int rightEdge = 0;
    private int menuPadding = 900;
    private View content;
    private View menu;
    private LinearLayout.LayoutParams menuParams;
    private float xDown;
    private float xMove;
    private float xUp;
    private boolean isMenuVisible;
    private VelocityTracker mVelocityTracker;

    private void initValues() {
        //WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        //screenWidth = window.getDefaultDisplay().getWidth();

        content = findViewById(R.id.content);
        menu = findViewById(R.id.menu);

        menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
        //menuParams.width = screenWidth - menuPadding;
        menuParams.width = screenWidth/5*2;
        leftEdge = -menuParams.width + 90;
        menuParams.leftMargin = leftEdge;
        content.getLayoutParams().width = screenWidth-90;

        scrollToMenu();

        content.setOnTouchListener(this);
        //menu.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

//        if(isMenuVisible) {
//            scrollToContent();
//            return false;
//        }

        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                int distanceX = (int) (xMove - xDown);
                if (isMenuVisible) {
                    menuParams.leftMargin = distanceX;
                } else {
                    menuParams.leftMargin = leftEdge + distanceX;
                }
                if (menuParams.leftMargin < leftEdge) {
                    menuParams.leftMargin = leftEdge;
                } else if (menuParams.leftMargin > rightEdge) {
                    menuParams.leftMargin = rightEdge;
                }
                menu.setLayoutParams(menuParams);
                break;
            case MotionEvent.ACTION_UP:
                xUp = event.getRawX();
                if (wantToShowMenu()) {
                    if (shouldScrollToMenu()) {
                        scrollToMenu();
                    } else {
                        scrollToContent();
                    }
                } else if (wantToShowContent()) {
                    if (shouldScrollToContent()) {
                        scrollToContent();
                    } else {
                        scrollToMenu();
                    }
                }
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    private boolean wantToShowContent() {
        return xUp - xDown < 0 && isMenuVisible;
    }


    private boolean wantToShowMenu() {
        return xUp - xDown > 0 && !isMenuVisible;
    }


    private boolean shouldScrollToMenu() {
        return xUp - xDown > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }


    private boolean shouldScrollToContent() {
        return xDown - xUp + menuPadding > screenWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }


    private void scrollToMenu() {
        new ScrollTask().execute(30);

        //baiDuInterstitial.loadInterstitialAdOnButton();
    }


    private void scrollToContent() {
        new ScrollTask().execute(-30);

        //baiDuInterstitial.loadInterstitialAdOnButton();
    }

    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }


    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }


    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    @Override
    public void onFragmentInteraction() {
        if(isMenuVisible)
            scrollToContent();
        else
            scrollToMenu();
    }


    class ScrollTask extends AsyncTask<Integer,Integer,Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = menuParams.leftMargin;
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (leftMargin > rightEdge) {
                    leftMargin = rightEdge;
                    break;
                }
                if (leftMargin < leftEdge) {
                    leftMargin = leftEdge;
                    break;
                }
                publishProgress(leftMargin);
                sleep(5);
            }
            if (speed[0] > 0) {
                isMenuVisible = true;
            } else {
                isMenuVisible = false;
            }
            return leftMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            menuParams.leftMargin = leftMargin[0];
            menu.setLayoutParams(menuParams);
        }

        @Override
        protected void onPostExecute(Integer leftMargin) {
            menuParams.leftMargin = leftMargin;
            menu.setLayoutParams(menuParams);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK )
//        {
//            Intent intent = new Intent(this,HexagramListActivity.class);
//            setResult(RESULT_OK, intent);
//            startActivity(intent);
//            finish();
//        }
//        return false;
//    }

}
