package lsw.utility.Image;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by swli on 4/13/2016.
 */
public class Common {

    public static Drawable getDrawableSelect(Context context, int drawableId, int overlayColorId)
    {
        return new BitmapDrawable(context.getResources(), getBitmapWithOverlayColorId(context,drawableId,overlayColorId));
    }

    public static Bitmap getBitmapWithOverlayColorId(Context context, int drawableId, int overlayColorId) {
        final Paint paint = new Paint();
        Canvas c;

        int overlayColor = context.getResources().getColor(overlayColorId);

        final Bitmap src = BitmapFactory.decodeResource(context.getResources(), drawableId);

        final Bitmap bm1 = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        c = new Canvas(bm1);

        paint.setColorFilter(new PorterDuffColorFilter(overlayColor, PorterDuff.Mode.OVERLAY));

        c.drawBitmap(src, 0, 0, paint);

        paint.setColor(overlayColor);
        paint.setStyle(Paint.Style.FILL);
        c.drawRect(0, 0, src.getWidth(), src.getHeight(), paint);

        final Bitmap bm2 = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        c = new Canvas(bm2);
        paint.setColorFilter(new PorterDuffColorFilter(overlayColor, PorterDuff.Mode.SRC_ATOP));
        c.drawBitmap(src, 0, 0, paint);

        paint.setColorFilter(null);
        paint.setXfermode(new AvoidXfermode(overlayColor, 0, AvoidXfermode.Mode.TARGET));
        c.drawBitmap(bm1, 0, 0, paint);

        return bm2;
    }
}
