package lsw.silkbag;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by swli on 5/5/2016.
 */
public class DemoActivity extends ActionBarActivity implements View.OnClickListener {
    private static final int JUMP_TO_LEFT = MyAdapter.NON_VISIBLE_ITEMS + MyAdapter.VISIBLE_ITEMS;
    private static final int JUMP_TO_RIGHT = MyAdapter.NON_VISIBLE_ITEMS+1;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        findViewById(android.R.id.button1).setOnClickListener(this);
        mRecycler = (RecyclerView)findViewById(R.id.recycler);
        MyAdapter mAdapter = new MyAdapter();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setHasFixedSize(true);
        mRecycler.scrollToPosition(MyAdapter.NON_VISIBLE_ITEMS);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        int pos = mLayoutManager.findFirstVisibleItemPosition();
        int outer = (MyAdapter.VISIBLE_ITEMS - 1) / 2;
        if(pos + outer >= MyAdapter.ITEM_IN_CENTER) {
            mRecycler.smoothScrollToPosition(JUMP_TO_RIGHT);
        } else {
            mRecycler.smoothScrollToPosition(JUMP_TO_LEFT);
        }

//        int pos = mLayoutManager.findFirstVisibleItemPosition();
//        int outer = (MyAdapter.VISIBLE_ITEMS + 1) / 2;
//        int delta = pos + outer - MyAdapter.ITEM_IN_CENTER;
//        //Log.d("Scroll", "delta=" + delta);
//        View firstChild = mRecycler.getChildAt(0);
//        if(firstChild != null) {
//            mRecycler.smoothScrollBy(firstChild.getWidth() * -delta, 0);
//        }
    }
}