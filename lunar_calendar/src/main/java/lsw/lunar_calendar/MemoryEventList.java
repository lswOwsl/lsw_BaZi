package lsw.lunar_calendar;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by swli on 6/3/2016.
 */
public class MemoryEventList extends Activity {

    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        MemoryListFragment f1 = MemoryListFragment.newInstance();
        ft.replace(R.id.flMemoryList, f1, "memory_list_all");

        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_list, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        searchView.setQueryHint("阴历日期");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchText = s;
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                MemoryListFragment f1 = MemoryListFragment.newInstance(s);
                ft.replace(R.id.flMemoryList, f1, "memory_list_all");

                ft.commit();
                return false;
            }
        });
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchText = "";
                return false;
            }
        });

        int searhViewId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(searhViewId);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.WHITE);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(textView, 0);
            //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {}

        return true;
    }
}
