package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swli.myapplication20150519.activity.ICallBackDialog;
import com.example.swli.myapplication20150519.activity.MemberAdapter;
import com.example.swli.myapplication20150519.activity.MemberPinYinComparator;
import com.example.swli.myapplication20150519.activity.bottombar.BottomBarFragment;
import com.example.swli.myapplication20150519.activity.sidebar.CharacterParser;
import com.example.swli.myapplication20150519.activity.sidebar.SideBar;
import com.example.swli.myapplication20150519.activity.sidebar.SortModel;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.MyApplication;
import com.example.swli.myapplication20150519.common.SwipeListView;
import com.example.swli.myapplication20150519.data.handler.MemberDataHandler;
import com.example.swli.myapplication20150519.model.Member;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lsw.ContactAuthor;
import lsw.library.DateExt;

/**
 * Created by swli on 5/27/2015.
 */
public class MemberHome extends Activity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, BottomBarFragment.OnFragmentInteractionListener {

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

    private Fragment bottomBarFragement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_home);

        initActionBar();

        FragmentManager fm = getFragmentManager();
        bottomBarFragement = fm.findFragmentById(R.id.id_fragment_bottom);

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

        hideShowBottomBar();
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
            String path = Environment.getExternalStorageDirectory() +"/"+
                    MyApplication.getInstance().getResources().getString(R.string.externalSavingFolder)+"/";
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
        if(id == R.id.menuNote)
        {
            Intent intentContact = new Intent();
            intentContact.setClass(MemberHome.this, SlideNote.class);
            startActivityForResult(intentContact, 0);
            return true;
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

    @Override
    public void onFragmentInteraction(Uri uri) {
        sourceDateList = getData("");
        adapter = initMemeberrAdapter();
        listView.setAdapter(adapter);
    }

    int touchSlop = 10;

    private void hideShowBottomBar() {



        View.OnTouchListener onTouchListener = new View.OnTouchListener() {

            float lastY = 0f;
            float currentY = 0f;
            //下面两个表示滑动的方向，大于0表示向下滑动，小于0表示向上滑动，等于0表示未滑动
            int lastDirection = 0;
            int currentDirection = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = event.getY();
                        currentY = event.getY();
                        currentDirection = 0;
                        lastDirection = 0;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (listView.getFirstVisiblePosition() > 0) {
                            //只有在listView.getFirstVisiblePosition()>0的时候才判断是否进行显隐动画。因为listView.getFirstVisiblePosition()==0时，
                            //ToolBar——也就是头部元素必须是可见的，如果这时候隐藏了起来，那么占位置用了headerview就被用户发现了
                            //但是当用户将列表向下拉露出列表的headerview的时候，应该要让头尾元素再次出现才对——这个判断写在了后面onScrollListener里面……
                            float tmpCurrentY = event.getY();
                            if (Math.abs(tmpCurrentY - lastY) > touchSlop) {//滑动距离大于touchslop时才进行判断
                                currentY = tmpCurrentY;
                                currentDirection = (int) (currentY - lastY);
                                if (lastDirection != currentDirection) {
                                    //如果与上次方向不同，则执行显/隐动画
                                    if (currentDirection < 0) {
                                        if(!bottomBarFragement.isHidden()) {
                                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                                            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

                                            ft.hide(bottomBarFragement).commit();
                                        }

                                    } else {
                                        if(bottomBarFragement.isHidden()) {
                                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                                            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

                                            ft.show(bottomBarFragement).commit();
                                        }

                                    }
                                }
                                lastY = currentY;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        //手指抬起的时候要把currentDirection设置为0，这样下次不管向哪拉，都与当前的不同（其实在ACTION_DOWN里写了之后这里就用不着了……）
                        currentDirection = 0;
                        lastDirection = 0;
                        break;
                }
                return false;
            }
        };

        listView.setOnTouchListener(onTouchListener);

//        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            //这个Listener其实是用来对付当用户的手离开列表后列表仍然在滑动的情况，也就是SCROLL_STATE_FLING
//
//            int lastPosition = 0;//上次滚动到的第一个可见元素在listview里的位置——firstVisibleItem
//            int state = SCROLL_STATE_IDLE;
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                //记录当前列表状态
//                state = scrollState;
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    ft.show(bottomBarFragement).commit();
//                }
//                if (firstVisibleItem > 0) {
//                    if (firstVisibleItem > lastPosition && state == SCROLL_STATE_FLING) {
//                        //如果上次的位置小于当前位置，那么隐藏头尾元素
//                        if (!bottomBarFragement.isHidden())
//                            ft.hide(bottomBarFragement).commit();
//                    }
//
//                    //================================
//                    if (firstVisibleItem < lastPosition && state == SCROLL_STATE_FLING) {
//                        //如果上次的位置大于当前位置，那么显示头尾元素，其实本例中，这个if没用
//                        //如果是滑动ListView触发的，那么，animateBack()肯定已经执行过了，所以没有必要
//                        //如果是点击按钮啥的触发滚动，那么根据设计原则，按钮肯定是头尾元素之一，所以也不需要animateBack()
//                        //所以这个if块是不需要的
//                        if(bottomBarFragement.isHidden())
//                            ft.show(bottomBarFragement).commit();
//                    }
//                    //这里没有判断(firstVisibleItem == lastPosition && state == SCROLL_STATE_FLING)的情况，
//                    //但是如果列表中的单个item如果很长的话还是要判断的，只不过代码又要多几行
//                    //但是可以取巧一下，在触发滑动的时候拖动执行一下animateHide()或者animateBack()——本例中的话就写在那个点击事件里就可以了）
//                    //BTW，如果列表的滑动纯是靠手滑动列表，而没有类似于点击一个按钮滚到某个位置的话，只要第一个if就够了…
//
//                }
//                lastPosition = firstVisibleItem;
//            }
//        });


    }
}
