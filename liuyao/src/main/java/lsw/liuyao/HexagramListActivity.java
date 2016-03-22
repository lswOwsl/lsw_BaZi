package lsw.liuyao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;

import lsw.ContactAuthor;
import lsw.liuyao.advertising.BaiDuBanner;
import lsw.liuyao.common.MyApplication;
import lsw.liuyao.data.Database;
import lsw.liuyao.data.HexagramListAdapter;
import lsw.liuyao.model.HexagramRow;
import lsw.liuyao.wxapi.WeiXinSendMessageHelper;


/**
 * Created by swli on 8/18/2015.
 */
public class HexagramListActivity extends Activity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, HexagramListAdapter.OnReload {

    SwipeListView swipeListView;
    Database database;
    HexagramListAdapter hexagramListAdapter;
    private String searchText;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hexagram_list_activity);

        BaiDuBanner baiDuBanner = new BaiDuBanner(this);
        baiDuBanner.create();

        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);

        initControls();
        database = new Database(this);
        ArrayList<HexagramRow> hexagrams = database.getHexagramList("");
        hexagramListAdapter = new HexagramListAdapter(hexagrams,this);
        hexagramListAdapter.setOnReload(this);

        swipeListView.setAdapter(hexagramListAdapter);
        swipeListView.setChoiceMode(ListView.CHOICE_MODE_NONE);


        //SinaData sinaData = new SinaData(this);
//        sinaData.getResponeFromURL(SinaData.Sina_Url + SinaData.Sina_Day_Method + "RB1601", new SinaData.IResult<String>() {
//            @Override
//            public void invoke(String s) {
//                String rr = s;
//                ArrayList<DailyData> dailyDatas = SinaData.parseDataByString(s);
//            }
//        });

//        sinaData.getResponeFromURL(SinaData.Sina_Url + SinaData.Sina_OneHour_Method + "RB1601", new SinaData.IResult< ArrayList<DailyData>>() {
//            @Override
//            public void invoke( ArrayList<DailyData> s) {
//                String rr = "";
//            }
//        });

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);

        dialog.show();
        BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
    }

    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

        @Override
        public void onCheckComplete() {
            dialog.dismiss();
        }

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
