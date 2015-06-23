package com.example.swli.myapplication20150519.common;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.widget.Button;

/**
 * Created by swli on 5/25/2015.
 */
public class ButtonHelper {

    public void setRounded(Button button, int btnBackgroundColor, int r)
    {
        float outRectr[] = new float[] { r, r, r, r, r, r, r, r };

        float[] hsv = new float[3];
        Color.colorToHSV(btnBackgroundColor, hsv);
        hsv[2] *= 0.8f; // value component
        //if shadow color was not defined, generate shadow color = 80% brightness
        int mShadowColor = Color.HSVToColor(hsv);
        //Bottom shadow
        RoundRectShape roundRectShape = new RoundRectShape(outRectr, null, null);
        ShapeDrawable bottomShapeDrawable = new ShapeDrawable(roundRectShape);
        bottomShapeDrawable.getPaint().setColor(mShadowColor);
        //Button round
        RoundRectShape rectShape = new RoundRectShape(outRectr, null, null);
        ShapeDrawable btnShapeDrawable = new ShapeDrawable();
        btnShapeDrawable.getPaint().setColor(btnBackgroundColor);
        btnShapeDrawable.setShape(rectShape);
        //sd.getPaint().setStyle(Paint.Style.STROKE);
        //sd.getPaint().setStrokeWidth(2);

        Drawable[] drawArray = {bottomShapeDrawable,btnShapeDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(drawArray);
        layerDrawable.setLayerInset(1, 0, 0, 0, 5);

        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed},btnShapeDrawable);
        bg.addState(new int[]{ },layerDrawable);
        button.setBackgroundDrawable(bg);
    }

}
