package lsw.liuyao;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;

import lsw.liuyao.data.Database;
import lsw.liuyao.data.HexagramListAdapter;
import lsw.liuyao.model.HexagramRow;
import lsw.model.Hexagram;


/**
 * Created by swli on 8/18/2015.
 */
public class HexagramListActivity extends Activity {

    SwipeListView swipeListView;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hexagram_list_activity);

        initControls();
        database = new Database(this);
        ArrayList<HexagramRow> hexagrams = database.getHexagramList();
        swipeListView.setAdapter(new HexagramListAdapter(hexagrams,this));
        swipeListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    private void initControls()
    {
        swipeListView = (SwipeListView) findViewById(R.id.slv_Hexagram);
    }
}
