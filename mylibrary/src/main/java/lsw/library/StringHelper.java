package lsw.library;

/**
 * Created by swli on 8/3/2015.
 */
public class StringHelper {

    public  static boolean isNullOrEmpty(String str)
    {
        if (str == null || str.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String getString(String str)
    {
        if(isNullOrEmpty(str))
            return "";
        else
            return str;
    }
}
