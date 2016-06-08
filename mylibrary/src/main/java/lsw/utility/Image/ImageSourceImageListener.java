package lsw.utility.Image;

import android.graphics.drawable.Drawable;

/**
 * Created by swli on 4/7/2016.
 */
public interface ImageSourceImageListener {

    void didComplete(SourceImage image, Drawable drawable, Exception ex);

}
