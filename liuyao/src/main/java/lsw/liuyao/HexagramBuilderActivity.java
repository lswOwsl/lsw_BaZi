package lsw.liuyao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.HashMap;

import lsw.hexagram.Builder;
import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.library.ShakeListener;
import lsw.library.StringHelper;
//import lsw.liuyao.advertising.BaiDuInterstitial;
import lsw.liuyao.common.DateTimePickerDialog;
import lsw.liuyao.common.HexagramTools;
import lsw.liuyao.common.IntentKeys;
import lsw.liuyao.common.LineDragListener;
import lsw.liuyao.data.Database;
import lsw.liuyao.model.HexagramRow;
import lsw.liuyao.wxapi.WeiXinSendMessageHelper;
import lsw.model.EnumLineSymbol;
import lsw.model.Hexagram;
import lsw.model.Line;
import lsw.model.TrigramDefault;
import lsw.value.Default;


public class HexagramBuilderActivity extends Activity implements LineDragListener.OnDropInteraction {

    private TextView tvUpperTrigram, tvLowerTrigram, tvUpperTrigramChanged, tvLowerTrigramChanged;

    private LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4,linearLayout5,linearLayout6;
    private ImageView ivYin,ivYang,ivLaoYin,ivLaoYang;

    private TextView tvDateSelector;
    private EditText etNote;

    private Database database;

    LinearLayout linearLayout1Changed,linearLayout2Changed,linearLayout3Changed,linearLayout4Changed,linearLayout5Changed,linearLayout6Changed;

    private TextView tvOriginalName, tvChangedName;

    HashMap<LinearLayout,LinearLayout> lineContainerMapping = new HashMap<LinearLayout, LinearLayout>();

    private Context context;

    private HashMap<EnumLineSymbol,ImageView> hashLineSymbolImageView = new HashMap<EnumLineSymbol, ImageView>();

    private void onVibrator() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            Vibrator localVibrator = (Vibrator) context.getApplicationContext()
                    .getSystemService(VIBRATOR_SERVICE);
            vibrator = localVibrator;
        }
        vibrator.vibrate(100L);
    }{}

    int shakeTimes = 1;
    HashMap<Integer,LinearLayout> hashMapLineIndex = new HashMap<Integer, LinearLayout>();

    private void initControls() {

        context = this;

        tvUpperTrigram = (TextView) findViewById(R.id.tvUpperTrigram);
        tvLowerTrigram = (TextView) findViewById(R.id.tvLowerTrigram);

        LinearLayout hexagramChangedView = (LinearLayout) findViewById(R.id.cGuaChanged);
        linearLayout1Changed = (LinearLayout) hexagramChangedView.findViewById(R.id.llLine1);
        linearLayout2Changed = (LinearLayout) hexagramChangedView.findViewById(R.id.llLine2);
        linearLayout3Changed = (LinearLayout) hexagramChangedView.findViewById(R.id.llLine3);
        linearLayout4Changed = (LinearLayout) hexagramChangedView.findViewById(R.id.llLine4);
        linearLayout5Changed = (LinearLayout) hexagramChangedView.findViewById(R.id.llLine5);
        linearLayout6Changed = (LinearLayout) hexagramChangedView.findViewById(R.id.llLine6);

        tvUpperTrigramChanged = (TextView) hexagramChangedView.findViewById(R.id.tvUpperTrigram);
        tvLowerTrigramChanged = (TextView) hexagramChangedView.findViewById(R.id.tvLowerTrigram);

        etNote = (EditText) findViewById(R.id.etNote);

        tvDateSelector = (TextView) findViewById(R.id.tvDateSelect);

        ivYin = (ImageView) findViewById(R.id.ivYin);
        ivLaoYin = (ImageView) findViewById(R.id.ivLaoYin);
        ivYang = (ImageView) findViewById(R.id.ivYang);
        ivLaoYang = (ImageView) findViewById(R.id.ivLaoYang);

        ivYang.setOnTouchListener(new MyTouchListener(EnumLineSymbol.Yang));
        ivYin.setOnTouchListener(new MyTouchListener(EnumLineSymbol.Yin));
        ivLaoYang.setOnTouchListener(new MyTouchListener(EnumLineSymbol.LaoYang));
        ivLaoYin.setOnTouchListener(new MyTouchListener(EnumLineSymbol.LaoYin));

        linearLayout1 = (LinearLayout) findViewById(R.id.llLine1);
        linearLayout2 = (LinearLayout) findViewById(R.id.llLine2);
        linearLayout3 = (LinearLayout) findViewById(R.id.llLine3);
        linearLayout4 = (LinearLayout) findViewById(R.id.llLine4);
        linearLayout5 = (LinearLayout) findViewById(R.id.llLine5);
        linearLayout6 = (LinearLayout) findViewById(R.id.llLine6);

        lineContainerMapping.put(linearLayout1, linearLayout1Changed);
        lineContainerMapping.put(linearLayout2, linearLayout2Changed);
        lineContainerMapping.put(linearLayout3, linearLayout3Changed);
        lineContainerMapping.put(linearLayout4, linearLayout4Changed);
        lineContainerMapping.put(linearLayout5, linearLayout5Changed);
        lineContainerMapping.put(linearLayout6, linearLayout6Changed);

        LineDragListener listener1 = new LineDragListener(1);
        listener1.setOnDropInteraction(this);
        LineDragListener listener2 = new LineDragListener(2);
        listener2.setOnDropInteraction(this);
        LineDragListener listener3 = new LineDragListener(3);
        listener3.setOnDropInteraction(this);
        LineDragListener listener4 = new LineDragListener(4);
        listener4.setOnDropInteraction(this);
        LineDragListener listener5 = new LineDragListener(5);
        listener5.setOnDropInteraction(this);
        LineDragListener listener6 = new LineDragListener(6);
        listener6.setOnDropInteraction(this);

        linearLayout1.setOnDragListener(listener1);
        linearLayout2.setOnDragListener(listener2);
        linearLayout3.setOnDragListener(listener3);
        linearLayout4.setOnDragListener(listener4);
        linearLayout5.setOnDragListener(listener5);
        linearLayout6.setOnDragListener(listener6);

        tvOriginalName = (TextView) findViewById(R.id.tvOriginalName);
        tvChangedName = (TextView) findViewById(R.id.tvChangedName);

        hashLineSymbolImageView.put(EnumLineSymbol.LaoYang, ivLaoYang);
        hashLineSymbolImageView.put(EnumLineSymbol.LaoYin, ivLaoYin);
        hashLineSymbolImageView.put(EnumLineSymbol.Yang, ivYang);
        hashLineSymbolImageView.put(EnumLineSymbol.Yin, ivYin);

        hashMapLineIndex.put(1, linearLayout1);
        hashMapLineIndex.put(2,linearLayout2);
        hashMapLineIndex.put(3,linearLayout3);
        hashMapLineIndex.put(4,linearLayout4);
        hashMapLineIndex.put(5,linearLayout5);
        hashMapLineIndex.put(6,linearLayout6);

        ShakeListener shakeListener = new ShakeListener(this);//创建一个对象
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {//调用setOnShakeListener方法进行监听

            public void onShake() {
                if (shakeTimes < 7) {
                    EnumLineSymbol enumLineSymbol = setLineImageView(hashMapLineIndex.get(shakeTimes));
                    lineSymbolHashMap.put(shakeTimes, enumLineSymbol);
                    onVibrator();
                    shakeTimes++;

                    setTrigramTitle();
                    setHexagramTitle();
                }
            }
        });
    }

    DateExt initialDateExt;
    String formatDateTime = "yyyy年MM月dd日 HH:mm";

    String[] trigramNames = new String[8];

    //BaiDuInterstitial baiDuInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hexagram_maintain_activity);

        for(int i=0; i< trigramNames.length; i++)
        {
            trigramNames[i] = Default.getTrigrams().get(i).getName();
        }

//        baiDuInterstitial = new BaiDuInterstitial(this);
//        baiDuInterstitial.create();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        initControls();

        database = new Database(this);

        //从万年历软件跳转过来
        Intent intent= getIntent();
        String value=intent.getStringExtra(CrossAppKey.DateTime);
        if(!StringHelper.isNullOrEmpty(value))
        {
            initialDateExt = new DateExt(StringHelper.getString(value));
        }
        else
        {
            initialDateExt = new DateExt();
        }
        tvDateSelector.setText(initialDateExt.getFormatDateTime(formatDateTime));

        tvDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTimePickerDialog dialog = new DateTimePickerDialog(initialDateExt, HexagramBuilderActivity.this);
                dialog.setCallBack(new DateTimePickerDialog.ICallBack() {
                    @Override
                    public void invoke(DateExt dateExt) {
                        tvDateSelector.setText(dateExt.getFormatDateTime(formatDateTime));
                        initialDateExt = dateExt;
                        //baiDuInterstitial.loadInterstitialAdOnButton();
                    }
                });
                dialog.show();
            }
        });

        setTrigramSelector(tvLowerTrigram , EnumFourTrigram.OriginalLower);
        setTrigramSelector(tvUpperTrigram , EnumFourTrigram.OriginalUpper);
        setTrigramSelector(tvLowerTrigramChanged, EnumFourTrigram.ChangedLower);
        setTrigramSelector(tvUpperTrigramChanged, EnumFourTrigram.ChangedUpper);
    }

    enum EnumFourTrigram
    {
        OriginalLower, OriginalUpper, ChangedLower, ChangedUpper;
    }

    void setTrigramSelector(final TextView tvTrigram, final EnumFourTrigram enumFourTrigram)
    {
        tvTrigram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HexagramBuilderActivity.this).setItems(trigramNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvTrigram.setText(trigramNames[i] + "\n");

                        for(TrigramDefault trigramDefault : Default.getTrigrams())
                        {
                            if(trigramDefault.getName().equals(trigramNames[i]))
                            {
                                setTrigrimView(enumFourTrigram, trigramDefault);
                                break;
                            }
                        }

                        setHexagramTitle();
                    }
                }).show();
            }
        });
    }

    private void setTrigrimView(EnumFourTrigram enumFourTrigram, TrigramDefault trigramDefault) {
        LinearLayout ll1 = linearLayout1;
        LinearLayout ll2 = linearLayout2;
        LinearLayout ll3 = linearLayout3;

        LinearLayout ll1Changed = linearLayout1Changed;
        LinearLayout ll2Changed = linearLayout2Changed;
        LinearLayout ll3Changed = linearLayout3Changed;

        int beginIndex = 1;
        if (enumFourTrigram == EnumFourTrigram.ChangedUpper || enumFourTrigram == EnumFourTrigram.OriginalUpper) {
            beginIndex = 4;

            ll1 = linearLayout4;
            ll2 = linearLayout5;
            ll3 = linearLayout6;

            ll1Changed = linearLayout4Changed;
            ll2Changed = linearLayout5Changed;
            ll3Changed = linearLayout6Changed;
        }
        if(enumFourTrigram == EnumFourTrigram.ChangedLower || enumFourTrigram == EnumFourTrigram.ChangedUpper)
        {
            tvLowerTrigram.setText(tvLowerTrigramChanged.getText());
            tvUpperTrigram.setText(tvUpperTrigramChanged.getText());
        }
        else
        {
            tvLowerTrigramChanged.setText(tvLowerTrigram.getText());
            tvUpperTrigramChanged.setText(tvUpperTrigram.getText());
        }

        EnumLineSymbol line1 = EnumLineSymbol.valueOf(trigramDefault.getLine1());
        EnumLineSymbol line2 = EnumLineSymbol.valueOf(trigramDefault.getLine2());
        EnumLineSymbol line3 = EnumLineSymbol.valueOf(trigramDefault.getLine3());

        setTrigramImageView(line1,ll1,ll1Changed);
        setTrigramImageView(line2,ll2,ll2Changed);
        setTrigramImageView(line3,ll3,ll3Changed);

        lineSymbolHashMap.put(beginIndex, line1);
        lineSymbolHashMap.put(beginIndex + 1, line2);
        lineSymbolHashMap.put(beginIndex + 2, line3);
    }

    HexagramTools hexagramTools = new HexagramTools();
    private EnumLineSymbol setLineImageView(LinearLayout ll)
    {
        EnumLineSymbol enumLineSymbol = EnumLineSymbol.valueOf(hexagramTools.createOneLine());

        ImageView ivLine = hashLineSymbolImageView.get(enumLineSymbol);
        Drawable drawable = ivLine.getDrawable();
        ivLine = new ImageView(HexagramBuilderActivity.this);
        ivLine.setImageDrawable(drawable);
        ll.setBackgroundResource(0);
        ll.removeAllViews();
        ll.addView(ivLine);

        LinearLayout llChanged = lineContainerMapping.get(ll);
        if(enumLineSymbol == EnumLineSymbol.LaoYang)
            drawable = getResources().getDrawable(R.drawable.yin);
        if(enumLineSymbol == EnumLineSymbol.LaoYin)
            drawable = getResources().getDrawable(R.drawable.yang);

        ImageView ivLineChanged = new ImageView(HexagramBuilderActivity.this);
        ivLineChanged.setImageDrawable(drawable);
        llChanged.setBackgroundResource(0);
        llChanged.removeAllViews();
        llChanged.addView(ivLineChanged);

        return enumLineSymbol;
    }

    private void setTrigramImageView(EnumLineSymbol line, LinearLayout ll, LinearLayout llChanged)
    {
        ImageView ivLine = hashLineSymbolImageView.get(line);
        Drawable drawable = ivLine.getDrawable();
        ivLine = new ImageView(HexagramBuilderActivity.this);
        ivLine.setImageDrawable(drawable);
        ll.setBackgroundResource(0);
        ll.removeAllViews();
        ll.addView(ivLine);

        ImageView ivLineChanged = new ImageView(HexagramBuilderActivity.this);
        ivLineChanged.setImageDrawable(drawable);
        llChanged.setBackgroundResource(0);
        llChanged.removeAllViews();
        llChanged.addView(ivLineChanged);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    private Point lastTouch;
    private ImageView moveImageView;
    private ImageView moveImageViewChanged;
    private EnumLineSymbol enumLineSymbol;
    private HashMap<Integer,EnumLineSymbol> lineSymbolHashMap = new HashMap<Integer, EnumLineSymbol>();

    @Override
    public void OnDrop(View containerView, int position) {
        //主卦的爻
        LinearLayout container = (LinearLayout) containerView;
        container.removeAllViews();
        container.addView(moveImageView);
        lineSymbolHashMap.put(position, enumLineSymbol);

        //变卦装爻
        LinearLayout containerChanged = lineContainerMapping.get(container);
        containerChanged.removeAllViews();
        containerChanged.addView(moveImageViewChanged);
        containerChanged.setBackgroundResource(0);

       setTrigramTitle();

        setHexagramTitle();
    }

    private void setTrigramTitle()
    {
        if(lineSymbolHashMap.size() >= 3 )
        {
            ArrayList<TrigramDefault> trigramDefaults =  Default.getTrigrams();
            for(TrigramDefault trigramDefault : trigramDefaults)
            {
                setTrigramTextView(trigramDefault,1,tvLowerTrigram,tvLowerTrigramChanged);
                setTrigramTextView(trigramDefault,4,tvUpperTrigram,tvUpperTrigramChanged);
            }
        }
    }

    private void setHexagramTitle()
    {
        if(lineSymbolCountCorrect())
        {
            Pair<Hexagram,Hexagram> pair = getHexagramsByLines();

            String orginalText = pair.first.getName();
            tvOriginalName.setText("主卦: "+ orginalText);
            if(pair.second != null) {
                tvChangedName.setText("变卦: " + pair.second.getName());
            }
            else {
                tvChangedName.setText("变卦: " + orginalText);
            }
        }
    }

    private void setTrigramTextView(TrigramDefault trigramDefault, int beginLineIndex, TextView tvTrigram, TextView tvTrigramChanged)
    {
        if(lineSymbolHashMap.containsKey(beginLineIndex) &&
                lineSymbolHashMap.containsKey(beginLineIndex+1) &&
                lineSymbolHashMap.containsKey(beginLineIndex+2)) {
            if (trigramDefault.getLine1() == lineSymbolHashMap.get(beginLineIndex).convertedValue() &&
                    trigramDefault.getLine2() == lineSymbolHashMap.get(beginLineIndex+1).convertedValue() &&
                    trigramDefault.getLine3() == lineSymbolHashMap.get(beginLineIndex+2).convertedValue()) {
                tvTrigram.setText(trigramDefault.getName() + "\n");
            }
            if (trigramDefault.getLine1() == lineSymbolHashMap.get(beginLineIndex).changedValue() &&
                    trigramDefault.getLine2() == lineSymbolHashMap.get(beginLineIndex+1).changedValue() &&
                    trigramDefault.getLine3() == lineSymbolHashMap.get(beginLineIndex+2).changedValue()) {
                tvTrigramChanged.setText(trigramDefault.getName()+ "\n");
            }
        }
    }

    @Override
    public void OnEntered(View containerView, int position)
    {
        LinearLayout container = (LinearLayout) containerView;
    }

    private final class MyTouchListener implements View.OnTouchListener {

        EnumLineSymbol enumYaoType;

        public MyTouchListener(EnumLineSymbol type)
        {
            this.enumYaoType = type;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                enumLineSymbol = enumYaoType;
                ImageView iv = (ImageView)view;
                Drawable drawable = iv.getDrawable();

                moveImageView = new ImageView(HexagramBuilderActivity.this);
                moveImageView.setImageDrawable(drawable);

                //老阳和老阴的变化爻
                Drawable drawableChanged = drawable;
                if(enumLineSymbol == EnumLineSymbol.LaoYang)
                    drawableChanged = getResources().getDrawable(R.drawable.yin);
                if(enumLineSymbol == EnumLineSymbol.LaoYin)
                    drawableChanged = getResources().getDrawable(R.drawable.yang);

                moveImageViewChanged = new ImageView(HexagramBuilderActivity.this);
                moveImageViewChanged.setImageDrawable(drawableChanged);

                lastTouch = new Point((int) motionEvent.getX(), (int) motionEvent.getY()) ;

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    private class MyDragShadowBuilder extends View.DragShadowBuilder {
        private View dragView;

        public MyDragShadowBuilder(View v) {
            super(v);
            this.dragView = v;
        }

        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            int width, height;
            width = getView().getWidth();
            height = getView().getHeight();
            size.set(width, height);

            if (lastTouch != null) {
                touch.set(lastTouch.x, lastTouch.y);
            }
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            dragView.draw(canvas);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //在androidManifest.xml里写了parent所以在点击左边回退按钮时就不用在这写了

        return super.onOptionsItemSelected(item);
    }

    public boolean lineSymbolCountCorrect()
    {
        if(lineSymbolHashMap.size() != 6)
        {
            return false;
        }
        return true;
    }

    Pair<Hexagram,Hexagram> getHexagramsByLines()
    {
        if(!lineSymbolCountCorrect())
            return null;

        Builder builder = Builder.getInstance(this);

        Pair<String,String> pair = null;
        Hexagram original = null, changed = null;
        try {
            ArrayList<Line> lines = getLines();
             original = builder.getHexagramByLines(lines, false);

            boolean hasDynamicLine = false;
            for(Line line : lines)
            {
                if(line.getLineSymbol() == EnumLineSymbol.LaoYang || line.getLineSymbol() == EnumLineSymbol.LaoYin) {
                    hasDynamicLine = true;
                    break;
                }
            }

            if(hasDynamicLine) {
                changed = builder.getChangedHexagramByOriginal(original, false);
            }

        }
        catch (Exception ex)
        {
            Log.e("Hexagram Builder", ex.getMessage());
        }

        return Pair.create(original,changed);
    }


    public void save(View view) {

        if (!lineSymbolCountCorrect()) {

            Toast.makeText(this,"爻位填充不全!", Toast.LENGTH_SHORT).show();
            return;
        }

        HexagramRow model = new HexagramRow();
        model.setNote(etNote.getText().toString());
        model.setDate(initialDateExt.getFormatDateTime());

        Pair<Hexagram, Hexagram> pair = getHexagramsByLines();
        model.setOriginalName(pair.first.getName());
        if (pair.second != null)
            model.setChangedName(pair.second.getName());

        database.insertHexagram(model);
        Toast.makeText(this, "保存卦例成功", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
    }

    public void reload(View view)
    {
        Intent intent = new Intent(HexagramBuilderActivity.this, HexagramBuilderActivity.class);
        startActivity(intent);
        finish();
    }

    ArrayList<Line> getLines()
    {
        ArrayList<Line> lines = new ArrayList<Line>();

        for(Integer key: lineSymbolHashMap.keySet())
        {
            Line line = new Line();
            line.setPosition(key);
            line.setLineSymbol(lineSymbolHashMap.get(key));
            lines.add(line);
        }

        return lines;
    }

    boolean isWeiXinFenxiang = false;

    public void zhuangGua(View view)
    {
        if(!lineSymbolCountCorrect())
            return;

        final String[] strings = new String[]{"个人分析","微信分享"};

        new AlertDialog.Builder(this)
                .setSingleChoiceItems(
                        strings, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(strings[which].equals("微信分享"))
                                {
                                    isWeiXinFenxiang = true;
                                }
                                else
                                {
                                    isWeiXinFenxiang = false;
                                }
                            }
                        })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();

                        if(isWeiXinFenxiang)
                        {
                            try {

                                String appId = "wx4c9850d2ade4b2e9";
                                IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, appId);
                                iwxapi.registerApp(appId);

                                Pair<Hexagram,Hexagram> hexagramPair = getHexagramsByLines();
                                String description = hexagramPair.first.getName();
                                if(hexagramPair != null)
                                    description = description + "-" + hexagramPair.second.getName();

                                WeiXinSendMessageHelper.sendAppMessage(context, iwxapi, initialDateExt.getFormatDateTime(), description);

                            } catch (Exception ex) {
                                Log.e("convert object to byte", ex.getMessage());
                            }
                        }
                        else {
                            ArrayList<Line> lines = getLines();

                            Intent mIntent = new Intent(context,HexagramAnalyzerActivity.class);
                            Bundle mBundle = new Bundle();
                            mBundle.putString(IntentKeys.FormatDate, initialDateExt.getFormatDateTime());
                            mBundle.putSerializable(IntentKeys.LineModelList, lines);
                            mIntent.putExtras(mBundle);

                            startActivity(mIntent);
                        }
                    }
                })
                .setNegativeButton("取消", null).show();
    }
}
