package com.example.swli.myapplication20150519;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swli.myapplication20150519.activity.ICallBackDialog;
import com.example.swli.myapplication20150519.activity.MemberAdapter;
import com.example.swli.myapplication20150519.activity.MemberPinYinComparator;
import com.example.swli.myapplication20150519.activity.sidebar.CharacterParser;
import com.example.swli.myapplication20150519.activity.sidebar.SideBar;
import com.example.swli.myapplication20150519.activity.sidebar.SortModel;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.SwipeListView;
import com.example.swli.myapplication20150519.data.handler.MemberDataHandler;
import com.example.swli.myapplication20150519.model.Member;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by swli on 5/27/2015.
 */
public class MemberHome extends Activity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener  {

    private ListView listView=null;
    private SearchView searchView;
    private TextView dialog;
    private SideBar sideBar;

    private DBManager dbManager;
    private CharacterParser characterParser;
    private MemberPinYinComparator pinyinComparator;
    private List<Member> sourceDateList;
    private MemberAdapter adapter;

    private String searchText;

    public String getSearchText()
    {
        return this.searchText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_home);

        initActionBar();

        dbManager = new DBManager(this);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new MemberPinYinComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }

            }
        });

        listView=(SwipeListView)findViewById(R.id.lvMember);

        sourceDateList = getData("");
        adapter = initMemeberrAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Member member = sourceDateList.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/
                bundle.putInt("Id", member.getId());
                bundle.putString("Name", member.getName());
                bundle.putBoolean("Ismale", member.getIsMale());
                bundle.putString("Birthday", member.getBirthday().getFormatDateTime());
                intent.putExtras(bundle);
                intent.setClass(MemberHome.this, MemberAnalyze.class);
                startActivityForResult(intent, 0);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(getApplicationContext(), "Long click item " + position,
                        Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        initTimerService();
    }

    private static final int INTERVAL = 1000 * 60 * 60 * 24;//一天
    private static final int INTERVAL_10s = 1000*10;

    private void initTimerService()
    {
       // IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        //TimerReceiver receiver = new TimerReceiver();
        //registerReceiver(receiver, filter);

//        Intent intent = new Intent(this, TimerReceiver.class);
//        intent.setAction(TimerConstant.Send_Message);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
//        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL_10s, pendingIntent);
    }

    int mSortMode = -1;

    public void menuOnSort(MenuItem item) {
        mSortMode = item.getItemId();
        // Request a call to onPrepareOptionsMenu so we can change the sort icon
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSortMode != -1) {
//            Drawable icon = menu.findItem(mSortMode).getIcon();
//            menu.findItem(R.id.action_sort).setIcon(icon);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        searchView.setQueryHint(this.getBaseContext().getResources().getString(R.string.member_search_hint));
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnCloseListener(this);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuAdd) {
            Intent intent = new Intent();
            intent.setClass(MemberHome.this, MemberMaintain.class);
            startActivityForResult(intent, 0);
            return true;
        }
        if(id == R.id.menuContact)
        {
            Intent intentContact = new Intent();
            intentContact.setClass(MemberHome.this, ContactAuthor.class);
            startActivityForResult(intentContact, 0);
            return true;
        }
        if(id == R.id.menuExportMember) {
            MemberDataHandler memberDataHandler = new MemberDataHandler();
            List<Member> members = memberDataHandler.loadMembersFromDb();
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/八字lsw/";
            memberDataHandler.saveMembersToXML(members,path);

            AlertDialog.Builder dialog = new AlertDialog.Builder(MemberHome.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                Bundle b=data.getExtras(); //data为B中回传的Intent
                boolean result =b.getBoolean("Result");//str即为回传的值
                String searchText = b.getString("SearchText");
                if(true)
                {
                    sourceDateList = getData(searchText);
                    adapter = initMemeberrAdapter();
                    listView.setAdapter(adapter);
                }
                break;
            default:
                break;
        }
    }

    public List<Member> getData(String str){
        List<Member> list=new ArrayList<Member>();

        dbManager.openDatabase();
        SQLiteDatabase database = dbManager.getDatabase();
        String sqlCondition = " ";
        String[] params = new String[]{};
        if(!TextUtils.isEmpty(str)) {
            sqlCondition = " where Name like ? Or Birthday_Refactor like ?";
            params = new String[]{ "%"+str+"%","%"+str+"%"};
        }
        String sql = "SELECT * FROM Members " + sqlCondition +" Order By Birthday_Refactor ASC";
        Cursor cur = database.rawQuery(sql,params);
        String result = "";

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int idIndex = cur.getColumnIndex("Id");
            int nameIndex = cur.getColumnIndex("Name");
            int birthdayIndex = cur.getColumnIndex("Birthday_Refactor");
            int isMaleIndex = cur.getColumnIndex("IsMale");

            String name = cur.getString(nameIndex);
            String birthdayStr = cur.getString(birthdayIndex);
            String isMale = cur.getString(isMaleIndex);
            DateExt birthdayDE = new DateExt(birthdayStr,"yyyy-MM-dd HH:mm:ss");
            int isMaleI = Integer.parseInt(isMale);
            int id = cur.getInt(idIndex);

            SortModel sortModel = new SortModel();
            sortModel.setName(name);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(name);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            Member member = new Member();
            member.setId(id);
            member.setName(name);
            member.setIsMale(isMaleI == 1 ? true : false);
            member.setBirthday(birthdayDE);
            member.setSortModel(sortModel);
            list.add(member);

        }

        cur.close();
        database.close();

        return list;
    }

    public void initActionBar()
    {
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        //getActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchText = s;
        sourceDateList = getData(s);
        adapter = initMemeberrAdapter();
        listView.setAdapter(adapter);
        return false;
    }

    private MemberAdapter initMemeberrAdapter()
    {
        Collections.sort(sourceDateList, pinyinComparator);

        adapter = new MemberAdapter(MemberHome.this, sourceDateList);

        adapter.setDeleteButtonClick(new ICallBackDialog<Integer>() {
            @Override
            public void onCall(final Integer position) {
                final MemberAdapter temp = adapter;
                AlertDialog.Builder dialog = new AlertDialog.Builder(MemberHome.this);
                dialog.setMessage("删除当前记录？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //temp.notifyDataSetChanged();
                        int id = sourceDateList.get(position.intValue()).getId();
                        dbManager.openDatabase();
                        String[] args = {String.valueOf(id)};
                        dbManager.getDatabase().delete("Members", "Id=?", args);
                        dbManager.closeDatabase();
                        sourceDateList.remove(position.intValue());
                        listView.setAdapter(adapter);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();//取消弹出框
                    }
                }).create().show();
            }
        });



        return adapter;
    }

    @Override
    public boolean onClose() {
        searchText = "";
//        sourceDateList = getData("");
//        adapter = initMemeberrAdapter();
//        listView.setAdapter(adapter);
        return false;
    }
}
