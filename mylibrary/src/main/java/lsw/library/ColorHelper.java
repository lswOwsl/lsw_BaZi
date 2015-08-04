package lsw.library;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import java.util.HashMap;

import lsw.xml.XmlModelCache;
import lsw.xml.model.XmlModelCelestialStem;
import lsw.xml.model.XmlModelTerrestrial;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.rgb;

/**
 * Created by swli on 8/3/2015.
 */
public class ColorHelper {
    private final static String fiveElement_Fire = "fire";
    private final static String fiveElement_Metal = "metal";
    private final static String fiveElement_Water = "water";
    private final static String fiveElement_Wood = "wood";
    private final static String fiveElement_Earth = "earth";

    private static HashMap<String,Integer> fiveElement_Color;
    private static HashMap<String,String> fiveElement_Chs;

    public static int getColorByFiveElement(String fiveElement)
    {
        if(fiveElement_Color == null)
        {
            fiveElement_Color = new HashMap<String,Integer>();
            fiveElement_Color.put(fiveElement_Earth,rgb(156, 102, 31));
            fiveElement_Color.put(fiveElement_Metal,rgb(255, 215, 0));
            fiveElement_Color.put(fiveElement_Wood,GREEN);
            fiveElement_Color.put(fiveElement_Water,BLUE);
            fiveElement_Color.put(fiveElement_Fire,RED);
        }

        if(fiveElement_Color.containsKey(fiveElement))
        {
            return fiveElement_Color.get(fiveElement);
        }
        else
        {
            return BLACK;
        }
    }

    public static String getFiveElementChsByEn(String fiveElement)
    {
        if(fiveElement_Chs == null)
        {
            fiveElement_Chs = new HashMap<String, String>();
            fiveElement_Chs.put(fiveElement_Earth,"土");
            fiveElement_Chs.put(fiveElement_Fire,"火");
            fiveElement_Chs.put(fiveElement_Water,"水");
            fiveElement_Chs.put(fiveElement_Wood,"木");
            fiveElement_Chs.put(fiveElement_Metal,"金");
        }

        if(fiveElement_Chs.containsKey(fiveElement))
        {
            return fiveElement_Chs.get(fiveElement);
        }

        return "";
    }

    private static ColorHelper colorHelper;

    public static ColorHelper getInstance(Context context)
    {
        if(colorHelper == null)
            colorHelper = new ColorHelper(context);
        return colorHelper;
    }

    private ColorHelper(Context context)
    {
        xmlModelCache = XmlModelCache.getInstance(context);
    }

    private static XmlModelCache xmlModelCache;

    public SpannableString getColorCelestialStem(String text)
    {
        XmlModelCelestialStem xmlCelestialStem = xmlModelCache.getCelestialStem();
        String fe = xmlCelestialStem.getFiveElementBy(text);
        SpannableString spanString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(getColorByFiveElement(fe));
        spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public SpannableString getColorTerrestrial(String text)
    {
        XmlModelTerrestrial xmlTerrestrial = xmlModelCache.getTerrestrial();
        String fe = xmlTerrestrial.getFiveElementBy(text);
        SpannableString spanString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(getColorByFiveElement(fe));
        spanString.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

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

    public static SpannableString getSmalllerText(String text, float scale)
    {
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new RelativeSizeSpan(scale), 0, text.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spanString;
    }
}
