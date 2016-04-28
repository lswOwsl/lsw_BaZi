package lsw.liuyao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

//import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
//import com.baidu.autoupdatesdk.UICheckUpdateCallback;


import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.List;

import lsw.ContactAuthor;
//import lsw.liuyao.advertising.BaiDuBanner;
import lsw.liuyao.common.MyApplication;
import lsw.liuyao.data.Database;
import lsw.liuyao.data.HexagramListAdapter;
import lsw.liuyao.data.HexagramListMenuExpandableAdapter;
import lsw.liuyao.data.xml.XmlInitialData;
import lsw.liuyao.model.HexagramMenuData;
import lsw.liuyao.model.HexagramRow;


/**
 * Created by swli on 8/18/2015.
 */
public class HexagramListActivity extends Activity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, HexagramListAdapter.OnReload {

    ListView swipeListView;
    Database database;
    HexagramListAdapter hexagramListAdapter;
    private String searchText;

    private ProgressDialog dialog;

    private MenuDrawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.hexagram_list_activity);
        mDrawer = MenuDrawer.attach(this);
        mDrawer.setMenuView(R.layout.hexagram_list_menu);
        mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
        mDrawer.setContentView(R.layout.hexagram_list_activity);

        final List<HexagramMenuData> menuData = XmlInitialData.getInstance().getMenuData();

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.lvMenu);
        expandableListView.setAdapter(new HexagramListMenuExpandableAdapter(this, menuData));
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                HexagramMenuData selectedChild = menuData.get(i).getSecondLevelData().get(i1);
                MyApplication.getInstance().setSearchCondition(selectedChild.getCondition());

                hexagramListAdapter.setRows(database.getHexagramList(searchText));
                hexagramListAdapter.notifyDataSetChanged();

                mDrawer.closeMenu();

                return false;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                HexagramMenuData selectedParent = menuData.get(i);
                MyApplication.getInstance().setSearchCondition(selectedParent.getCondition());
                if(selectedParent.getSecondLevelData() == null || selectedParent.getSecondLevelData().size() == 0) {
                    hexagramListAdapter.setRows(database.getHexagramList(searchText));
                    hexagramListAdapter.notifyDataSetChanged();
                    mDrawer.closeMenu();
                }
                return false;
            }
        });

        TextView tvPriceSearch = (TextView) findViewById(R.id.tvFuturePriceSearch);
        tvPriceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(HexagramListActivity.this, FuturePriceSearchListActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        //BaiDuBanner baiDuBanner = new BaiDuBanner(this);
        //baiDuBanner.create();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        swipeListView = (ListView) findViewById(R.id.slv_Hexagram);

        database = new Database(this);
        ArrayList<HexagramRow> hexagrams = database.getHexagramList("");
        hexagramListAdapter = new HexagramListAdapter(hexagrams,this);
        hexagramListAdapter.setOnReload(this);

        swipeListView.setAdapter(hexagramListAdapter);
        swipeListView.setChoiceMode(ListView.CHOICE_MODE_NONE);


        //dialog = new ProgressDialog(this);
        //dialog.setIndeterminate(true);

        //dialog.show();
        //BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
    }

//    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
//
//        @Override
//        public void onCheckComplete() {
//            dialog.dismiss();
//        }
//
//    }

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
        if(id == android.R.id.home)
        {
            if(mDrawer.isMenuVisible())
                mDrawer.closeMenu();
            else
                mDrawer.openMenu();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuAdd) {

//            String appId = "wx4c9850d2ade4b2e9";
//            IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, appId);
//            iwxapi.registerApp(appId);
//
//
//            WeiXinSendMessageHelper.sendAppMessage(this,iwxapi);

            Intent intent = new Intent();
            intent.setClass(HexagramListActivity.this, HexagramBuilderActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        if(id == R.id.menuContact)
        {
            Intent intentContact = new Intent();
            intentContact.setClass(HexagramListActivity.this, ContactAuthor.class);
            startActivityForResult(intentContact, 0);
            return true;
        }
        if(id == R.id.menuExportHexagram) {

            List<HexagramRow> hexagramList = database.getHexagramList("");
            String path = Environment.getExternalStorageDirectory() +"/"+
                    MyApplication.getInstance().getResources().getString(R.string.externalSavingFolder)+"/";
            database.saveHexagramsToXML(hexagramList, path);

            AlertDialog.Builder dialog = new AlertDialog.Builder(HexagramListActivity.this);
            dialog.setTitle("倒出记录成功!")
                    .setMessage("文件位于目录"+path)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();

        }
        if(id == R.id.menuImportHexagram){
            Intent intentImport = new Intent();
            intentImport.setClass(HexagramListActivity.this, HexagramImportActivity.class);
            startActivityForResult(intentImport,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                //swipeListView.closeOpenedItems();
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
        //swipeListView.closeOpenedItems();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {

            MyApplication.getInstance().setSearchCondition("");

            finish();
            System.exit(0);
        }
    }

}
