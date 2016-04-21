package lsw.utility;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by swli on 4/21/2016.
 */
public class CaptureImage {

    public static void captureView(View view,String filename){
        //Create a Bitmap with the same dimensions
        Bitmap image = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),
                Bitmap.Config.RGB_565);
        //Draw the view inside the Bitmap
        view.draw(new Canvas(image));

        //Store to sdcard
        try {
            String path = Environment.getExternalStorageDirectory().toString();
            File myFile = new File(path,filename);
            FileOutputStream out = new FileOutputStream(myFile);

            image.compress(Bitmap.CompressFormat.PNG, 90, out); //Output
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap captureViewToImage(View view) {
        Bitmap image = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),
                Bitmap.Config.RGB_565);
        //Draw the view inside the Bitmap
        view.draw(new Canvas(image));

        return image;
    }

    public static Bitmap combineImages(Bitmap c, Bitmap s)
    {
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }
}
