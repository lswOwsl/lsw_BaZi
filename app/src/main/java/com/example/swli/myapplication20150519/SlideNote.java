package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

/**
 * Created by swli on 7/22/2015.
 */
public class SlideNote extends Activity implements View.OnTouchListener {

    //???????menu?????????????
    public static final int SNAP_VELOCITY = 200;
    //?????
    private int screenWidth;
    //menu??????????????menu????????marginLeft?????????????
    private int leftEdge;
    //menu???????????????0??marginLeft??0???????
    private int rightEdge = 0;
    //menu????????content????
    private int menuPadding = 300;
    //??????
    private View content;
    //menu???
    private View menu;
    //menu??????????????leftMargin??
    private LinearLayout.LayoutParams menuParams;
    //???????????
    private float xDown;
    //???????????
    private float xMove;
    //???????????
    private float xUp;
    //menu???????????????????menu????????
    //??????????
    private boolean isMenuVisible;
    //????????????
    private VelocityTracker mVelocityTracker;

    public void btnChangeText (View v)
    {
       // scrollToContent();
        v.setBackgroundColor(Color.WHITE);
    }

    public void btnChangeText1 (View v)
    {
        //scrollToContent();
        v.setBackgroundColor(Color.BLUE);
    }

    GestureDetector gestureDetector;

    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_note);
        initValues();

        LinearLayout innerContent = (LinearLayout)findViewById(R.id.innerContent);
        innerContent.setOnTouchListener(this);

        final ViewFlipper viewFlipper = (ViewFlipper)findViewById(R.id.vfYao);

        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                if (motionEvent.getX() - motionEvent1.getX() > 60) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(SlideNote.this, R.anim.slide_in_right));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(SlideNote.this, R.anim.slide_out_left));
                    viewFlipper.showNext();
                    return true;
                } else if (motionEvent.getX() - motionEvent1.getX() < -60) {
//                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(SlideNote.this, android.R.anim.slide_in_left));
//                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(SlideNote.this, android.R.anim.slide_out_right));
//                    viewFlipper.showPrevious();
                    return true;
                }
                return false;
            }
        });


        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return true;
            }
        });

        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // ---Call it directly---
                scrollToContent();
                return false;
            }
        });


    }

    private void initValues() {
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

        //screenWidth = window.getDefaultDisplay().getWidth();

        content = findViewById(R.id.content);
        menu = findViewById(R.id.menu);

        menuParams = (LinearLayout.LayoutParams) menu.getLayoutParams();
        //?menu????????????menuPadding
        menuParams.width = screenWidth - menuPadding;
        //????????menu?????
        leftEdge = -menuParams.width;
        //menu?leftMargin???????????????menu??????
        menuParams.leftMargin = leftEdge;
        //?content??????????
        content.getLayoutParams().width = screenWidth;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //if (gestureDetector.onTouchEvent(event)) {
            if(isMenuVisible) {
                scrollToContent();
                return false;
            }
        //}

        // TODO Auto-generated method stub
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //???????????????
                xDown = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                //????????????????????????????
                //menu?leftMargin?????????menu
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
                //??????????????????????????menu?????
                //???content??
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
    }


    private void scrollToContent() {
        new ScrollTask().execute(-30);
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


    class ScrollTask extends AsyncTask<Integer,Integer,Integer> {

        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = menuParams.leftMargin;
            // ????????????????????????????????
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
                // ????????????????????20?????????????????
                sleep(5 );
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
}
