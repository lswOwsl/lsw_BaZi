package lsw.liuyao;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import common.YaoDragListener;


public class QiGua extends Activity implements YaoDragListener.OnDropInteraction {

    private LinearLayout linearLayout1,linearLayout2,linearLayout3,linearLayout4,linearLayout5,linearLayout6;
    private ImageView ivYin,ivYang,ivLaoYin,ivLaoYang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qi_gua);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        ivYin = (ImageView)findViewById(R.id.ivYin);
        ivLaoYin = (ImageView)findViewById(R.id.ivLaoYin);
        ivYang = (ImageView)findViewById(R.id.ivYang);
        ivLaoYang = (ImageView)findViewById(R.id.ivLaoYang);

        ivYang.setOnTouchListener(new MyTouchListener(EnumYaoType.Yang));
        ivYin.setOnTouchListener(new MyTouchListener(EnumYaoType.Yin));
        ivLaoYang.setOnTouchListener(new MyTouchListener(EnumYaoType.LaoYang));
        ivLaoYin.setOnTouchListener(new MyTouchListener(EnumYaoType.LaoYin));

        linearLayout1 = (LinearLayout)findViewById(R.id.llLine1);
        linearLayout2 = (LinearLayout)findViewById(R.id.llLine2);
        linearLayout3 = (LinearLayout)findViewById(R.id.llLine3);
        linearLayout4 = (LinearLayout)findViewById(R.id.llLine4);
        linearLayout5 = (LinearLayout)findViewById(R.id.llLine5);
        linearLayout6 = (LinearLayout)findViewById(R.id.llLine6);

        YaoDragListener listener1 = new YaoDragListener(1);
        listener1.setOnDropInteraction(this);
        YaoDragListener listener2 = new YaoDragListener(2);
        listener1.setOnDropInteraction(this);
        YaoDragListener listener3 = new YaoDragListener(3);
        listener1.setOnDropInteraction(this);
        YaoDragListener listener4 = new YaoDragListener(4);
        listener1.setOnDropInteraction(this);
        YaoDragListener listener5 = new YaoDragListener(5);
        listener1.setOnDropInteraction(this);
        YaoDragListener listener6 = new YaoDragListener(6);
        listener1.setOnDropInteraction(this);

        linearLayout1.setOnDragListener(listener1);
        linearLayout2.setOnDragListener(listener2);
        linearLayout3.setOnDragListener(listener3);
        linearLayout4.setOnDragListener(listener4);
        linearLayout5.setOnDragListener(listener5);
        linearLayout6.setOnDragListener(listener6);

        return true;
    }

    private Point lastTouch;
    private ImageView moveImageView;
    private EnumYaoType tempEnumYaoType;
    private HashMap<Integer,EnumYaoType> dicYaos = new HashMap<Integer, EnumYaoType>();

    @Override
    public void OnDrop(View containerView, int position) {
        LinearLayout container = (LinearLayout) containerView;
        container.removeAllViews();
        container.addView(moveImageView);
        dicYaos.put(position,tempEnumYaoType);
    }

    enum EnumYaoType
    {
        Yin,
        Yang,
        LaoYin,
        LaoYang
    }

    private final class MyTouchListener implements View.OnTouchListener {

        EnumYaoType enumYaoType;

        public MyTouchListener(EnumYaoType type)
        {
            this.enumYaoType = type;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                tempEnumYaoType = enumYaoType;
                ImageView iv = (ImageView)view;
                Drawable drawable = iv.getDrawable();

                moveImageView = new ImageView(QiGua.this);
                moveImageView.setImageDrawable(drawable);

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
        public void onProvideShadowMetrics (Point size, Point touch) {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
