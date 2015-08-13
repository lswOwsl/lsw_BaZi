package lsw.library;

import java.util.ArrayList;

/**
 * Created by lsw_wsl on 8/13/15.
 */
public class ListHelper {

    public static int[] toIntArray(ArrayList<Integer> set) {
        int[] a = new int[set.size()];
        int i = 0;
        for (Integer val : set) a[i++] = val;
        return a;
    }
}
