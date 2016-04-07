package lsw.utility.Image;

import java.util.List;

/**
 * Created by swli on 4/7/2016.
 */
public interface ImageSourceFoldersListener {
    void didComplete(List<SourceFolder> folders, Exception ex);
}
