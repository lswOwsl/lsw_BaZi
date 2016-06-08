package lsw.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsw_wsl on 4/10/16.
 */
public class XmlLoader {

    public static List<String> loadFilesFromFolder(String folder)
    {
        List<String> fileNames= new ArrayList<String>();

        File[] files = new File(folder).listFiles();
        if(files !=null) {
            for (File file : files) {
                if (getFileExtension(file).toLowerCase().equals("xml")) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

}
