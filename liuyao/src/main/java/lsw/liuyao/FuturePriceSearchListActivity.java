package lsw.liuyao;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import lsw.library.DateExt;

/**
 * Created by swli on 4/26/2016.
 */
public class FuturePriceSearchListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.future_price_search_list);

        FragmentTransaction ftt = getSupportFragmentManager().beginTransaction();
        FuturePriceFragment futurePriceFragment = FuturePriceFragment.createFragment();
        futurePriceFragment.setSummaryByMonth(true);
        ftt.replace(R.id.fl_Price_List, futurePriceFragment, null);
        ftt.commit();

    }


}
