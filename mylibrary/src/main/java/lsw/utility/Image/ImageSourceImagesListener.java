package lsw.utility.Image;

import java.util.List;

/**
 * Created by swli on 4/7/2016.
 */
public interface ImageSourceImagesListener {
    void didComplete(SourceFolder folder, List<SourceImage> images, Exception ex);
}
