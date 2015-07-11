package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.swli.myapplication20150519.common.ButtonHelper;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.SpinnerHelper;
import com.example.swli.myapplication20150519.common.StringHelper;
import com.example.swli.myapplication20150519.phone.base.Contact;


public class MainActivity extends Activity {

    SQLiteDatabase database;
    Spinner spYue;
    Spinner spRiZhu;
    SpinnerHelper spinnerHelper;
    DBManager dbManager;
    Button btnSearch;
    Button btnShenShaSearch;
    ButtonHelper btnHelper;

    ScrollView svTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Contact contact = new Contact(this);
        //contact.getContactUserNames("李");

        spinnerHelper = new SpinnerHelper(this);
        dbManager = new DBManager(this);
        btnHelper = new ButtonHelper();

        onLoadContent();

        svTop = (ScrollView)findViewById(R.id.svTop);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        svTop.addView(linearLayout);
    }


    private void onLoadContent() {
        // 初始化控件
        spYue = (Spinner) findViewById(R.id.spYue);
        spRiZhu = (Spinner) findViewById(R.id.spRiZhu);

        spinnerHelper.BindTerrestrial(spYue,"");
        spinnerHelper.BindCelestialStem(spRiZhu,"");

        btnSearch = (Button)findViewById(R.id.btnChangeText);
        btnShenShaSearch = (Button)findViewById(R.id.btnShenShaRedirect);

        //btnHelper.setRounded(btnSearch,Color.WHITE, 50);
        //btnHelper.setRounded(btnShenShaSearch,Color.WHITE,50);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public void onSearchButtonClick(View view) {

        String riZhu = spRiZhu.getSelectedItem().toString();
        String yuFen = spYue.getSelectedItem().toString();
        dbManager.openDatabase();
        database = dbManager.getDatabase();

        String sql = "SELECT * FROM YuShiTiaoHou where RiZhu=? and YueFen=?";
        Cursor cur = database.rawQuery(sql, new String[]
                {riZhu, yuFen});
        String result = "";

        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            int commentIndex = cur.getColumnIndex("Comment");
            int yongShenIndex1 = cur.getColumnIndex("YongShen1");
            int yongShenIndex2 = cur.getColumnIndex("YongShen2");
            int jiShenIndex = cur.getColumnIndex("JiShen");
            int shiYongZhouQiIndex = cur.getColumnIndex("ShiYongZhouQi");

            String comment = cur.getString(commentIndex);
            String yongShen1 = cur.getString(yongShenIndex1);
            String yongShen2 = cur.getString(yongShenIndex2);
            String jiShen = cur.getString(jiShenIndex);
            String shiYongZhouqi = cur.getString(shiYongZhouQiIndex);

            result += "用神:" + StringHelper.getText(yongShen1) + " " + StringHelper.getText(yongShen2) + "\n";
            result += "忌神:" + StringHelper.getText(jiShen) + "\n";
            result += "适用周期:" + StringHelper.getText(shiYongZhouqi) + "\n";
            result += "说明:" + comment + "\n";
        }

        cur.close();
        database.close();
        TextView tvComment = (TextView) findViewById(R.id.tvYearContent);
        tvComment.setText(result);
        //int count = cursor.getCount();
        //  如果查找单词，显示其中文信息
        //if (count > 0)
        //{

        //  必须使用moveToFirst方法将记录指针移动到第1条记录的位置
        //cursor.moveToFirst();
        //result = cursor.getString(cursor.getColumnIndex("Comment"));
        //Log.i("tran", "success"+result);
        //}
        //  显示查询结果对话框
        //new AlertDialog.Builder(this).setTitle("查询结果").setMessage(result)
        //.setPositiveButton("关闭", null).show();
    }

    public void btnShenShaClick(View view)
    {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,ShenSha.class);
        startActivityForResult(intent, 0);
    }

    public void onMemberButtonClick(View view)
    {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,MemberMaintain.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if(requestCode == 0) {
            //if(resultCode == Activity.RESULT_OK) {
              //  SharedPreferences preferences = getSharedPreferences("Text", 0);
            //    displayContent.setText(preferences.getString("text", null));
          //  }
        //}
        super.onActivityResult(requestCode, resultCode, data);
    }
}
