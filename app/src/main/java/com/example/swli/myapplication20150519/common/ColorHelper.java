package com.example.swli.myapplication20150519.common;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import com.example.swli.myapplication20150519.model.XmlCelestialStem;
import com.example.swli.myapplication20150519.model.XmlTerrestrial;

import java.util.HashMap;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.rgb;

/**
 * Created by swli on 6/9/2015.
 */
public class ColorHelper extends lsw.library.ColorHelper {

    public static SpannableString getTextByColor(String text, int color)
    {
        SpannableString spanString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static SpannableString getColorStr(String fiveElement,String text)
    {
        SpannableString spanString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(getColorByFiveElement(fiveElement));
        spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static SpannableString getColorCelestialStem(Context context, String text)
    {
        XmlCelestialStem xmlCelestialStem = XmlCelestialStem.getInstance(MyApplication.getInstance());
        String fe = xmlCelestialStem.getFiveElementBy(text);
        SpannableString spanString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(getColorByFiveElement(fe));
        spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static SpannableString getColorTerrestrial(Context context, String text)
    {
        XmlTerrestrial xmlTerrestrial = XmlTerrestrial.getInstance(context);
        String fe = xmlTerrestrial.getFiveElementBy(text);
        SpannableString spanString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(getColorByFiveElement(fe));
        spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static SpannableString getSmalllerText(Context context, String text, float scale)
    {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new RelativeSizeSpan(scale), 0, text.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spanString;
    }
}