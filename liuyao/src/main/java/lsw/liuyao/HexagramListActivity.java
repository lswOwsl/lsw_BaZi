package lsw.liuyao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;

import java.util.ArrayList;

import lsw.library.Utility;
import lsw.liuyao.data.Database;
import lsw.liuyao.data.HexagramAdapter;
import lsw.liuyao.data.HexagramListAdapter;
import lsw.liuyao.model.HexagramRow;
import lsw.model.Hexagram;


/**
 * Created by swli on 8/18/2015.
 */
public class HexagramListActivity extends Activity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, HexagramListAdapter.OnReload {

    SwipeListView swipeListView;
    Database database;
    HexagramListAdapter hexagramListAdapter;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hexagram_list_activity);

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);

        initControls();
        database = new Database(this);
        ArrayList<HexagramRow> hexagrams = database.getHexagramList("");
        hexagramListAdapter = new HexagramListAdapter(hexagrams,this);
        hexagramListAdapter.setOnReload(this);

        swipeListView.setAdapter(hexagramListAdapter);
        swipeListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        searchView.setQueryHint("主卦 日期(年-月-日) 备注");
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnCloseListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.menuAdd) {
            Intent intent = new Intent();
            intent.setClass(HexagramListActivity.this, HexagramBuilderActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls()
    {
        swipeListView = (SwipeListView) findViewById(R.id.slv_Hexagram);
    }

    @Override
    public boolean onClose() {
        searchText = "";
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchText = s;
        hexagramListAdapter.setRows(database.getHexagramList(searchText));
        hexagramListAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
            case RESULT_CANCELED:
                hexagramListAdapter.setRows(database.getHexagramList(""));
                hexagramListAdapter.notifyDataSetChanged();
                swipeListView.closeOpenedItems();
                break;
            default:
                break;
        }
    }

    @Override
    public void invoke(int index) {
        hexagramListAdapter.setRows(database.getHexagramList(searchText));
        hexagramListAdapter.notifyDataSetChanged();
        //close open items 必须最后调用，要不删除后不能反应到list页面上
        swipeListView.closeOpenedItems();
    }
}
