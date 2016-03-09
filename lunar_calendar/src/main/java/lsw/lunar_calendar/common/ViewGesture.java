package lsw.lunar_calendar.common;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by lsw_wsl on 8/4/15.
 */
public class ViewGesture {

    public interface OnViewGestureCallBack
    {
        void moveUp();
        void moveDown();
    }

    Activity activity;
    OnViewGestureCallBack callBack;
    GestureDetector detector;

    public ViewGesture(Activity activity, OnViewGestureCallBack callBack)
    {
        this.activity = activity;
        this.callBack = callBack;
        intiGesture();
    }

    public GestureDetector getDetector()
    {
        return this.detector;
    }

    public void setGestureTo(View view)
    {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    private void intiGesture() {

        detector = new GestureDetector(activity, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
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
                if (motionEvent.getY() - motionEvent1.getY() > 60) {
                    if(callBack != null)
                    {
                        callBack.moveUp();
                    }
                    Toast.makeText(activity, "下个月", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (motionEvent.getY() - motionEvent1.getY() < -60) {
                    if(callBack != null)
                    {
                        callBack.moveDown();
                    }
                    Toast.makeText(activity, "上个月", Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }
        });
    }
}
