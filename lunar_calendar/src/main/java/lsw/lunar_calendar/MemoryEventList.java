package lsw.lunar_calendar;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Created by swli on 6/3/2016.
 */
public class MemoryEventList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        MemoryListFragment f1 = MemoryListFragment.newInstance();
        ft.replace(R.id.flMemoryList, f1, "memory_list_all");

        ft.commit();
    }
}
