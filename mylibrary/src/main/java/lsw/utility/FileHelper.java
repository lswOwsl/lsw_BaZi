package lsw.utility;

/**
 * Created by swli on 4/13/2016.
 */
public class FileHelper {

    public static void createFolder(String path)
    {
        java.io.File folder = new java.io.File(path);
        boolean success = true;
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
}
