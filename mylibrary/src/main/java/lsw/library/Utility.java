package lsw.library;

import android.content.Context;
import android.util.Pair;

import java.util.Arrays;

/**
 * Created by swli on 8/12/2015.
 */
public class Utility {

    public static Pair<String,String> getXunKong(Context context, String celestialStem,String terrestrial)
    {
        int eraIndexC = Arrays.asList(context.getResources().getStringArray(R.array.tianGan)).indexOf(celestialStem);
        int eraIndexT = Arrays.asList(context.getResources().getStringArray(R.array.diZhi)).indexOf(terrestrial);

        if(eraIndexC >= eraIndexT)
        {
            eraIndexT +=12;
        }

        int result = eraIndexT - eraIndexC;
        String[] array = context.getResources().getStringArray(R.array.diZhi);
        return Pair.create(array[result-2],array[result-1]);
    }
}
