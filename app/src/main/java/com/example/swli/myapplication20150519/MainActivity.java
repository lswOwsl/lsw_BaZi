package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
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

        loadContacts();

        spinnerHelper = new SpinnerHelper(this);
        dbManager = new DBManager(this);
        btnHelper = new ButtonHelper();

        onLoadContent();

        svTop = (ScrollView)findViewById(R.id.svTop);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        svTop.addView(linearLayout);
        //svTop.addView(b2);
    }

    public String getContactInfo() throws JSONException {
        // 获得通讯录信息 ，URI是ContactsContract.Contacts.CONTENT_URI
        list = new ArrayList<Contacts>();
        contactData = new JSONObject();
        String mimetype = "";
        int oldrid = -1;
        int contactId = -1;
        Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI,null, null, null, Data.RAW_CONTACT_ID);
        int numm=0;
        while (cursor.moveToNext()) {
            contactId = cursor.getInt(cursor.getColumnIndex(Data.RAW_CONTACT_ID));
            if (oldrid != contactId) {
                jsonObject = new JSONObject();
                contactData.put("contact" + numm, jsonObject);
                numm++;
                oldrid = contactId;
            }

            // 取得mimetype类型
            mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE));
            // 获得通讯录中每个联系人的ID
            // 获得通讯录中联系人的名字
            if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
//    String display_name = cursor.getString(cursor.getColumnIndex(StructuredName.DISPLAY_NAME));
                String prefix = cursor.getString(cursor.getColumnIndex(StructuredName.PREFIX));
                jsonObject.put("prefix", prefix);
                String firstName = cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME));
                jsonObject.put("firstName", firstName);
                String middleName = cursor.getString(cursor.getColumnIndex(StructuredName.MIDDLE_NAME));
                jsonObject.put("middleName", middleName);
                String lastname = cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME));
                jsonObject.put("lastname", lastname);
                String suffix = cursor.getString(cursor.getColumnIndex(StructuredName.SUFFIX));
                jsonObject.put("suffix", suffix);
                String phoneticFirstName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_FAMILY_NAME));
                jsonObject.put("phoneticFirstName", phoneticFirstName);
                String phoneticMiddleName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_MIDDLE_NAME));
                jsonObject.put("phoneticMiddleName", phoneticMiddleName);
                String phoneticLastName = cursor.getString(cursor.getColumnIndex(StructuredName.PHONETIC_GIVEN_NAME));
                jsonObject.put("phoneticLastName", phoneticLastName);
            }
            // 获取电话信息
            if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                // 取出电话类型
                int phoneType = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
                // 手机
                if (phoneType == Phone.TYPE_MOBILE) {
                    String mobile = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("mobile", mobile);
                }
                // 住宅电话
                if (phoneType == Phone.TYPE_HOME) {
                    String homeNum = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("homeNum", homeNum);
                }
                // 单位电话
                if (phoneType == Phone.TYPE_WORK) {
                    String jobNum = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobNum", jobNum);
                }
                // 单位传真
                if (phoneType == Phone.TYPE_FAX_WORK) {
                    String workFax = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("workFax", workFax);
                }
                // 住宅传真
                if (phoneType == Phone.TYPE_FAX_HOME) {
                    String homeFax = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("homeFax", homeFax);
                }
                // 寻呼机
                if (phoneType == Phone.TYPE_PAGER) {
                    String pager = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("pager", pager);
                }
                // 回拨号码
                if (phoneType == Phone.TYPE_CALLBACK) {
                    String quickNum = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("quickNum", quickNum);
                }
                // 公司总机
                if (phoneType == Phone.TYPE_COMPANY_MAIN) {
                    String jobTel = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobTel", jobTel);
                }
                // 车载电话
                if (phoneType == Phone.TYPE_CAR) {
                    String carNum = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("carNum", carNum);
                }
                // ISDN
                if (phoneType == Phone.TYPE_ISDN) {
                    String isdn = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("isdn", isdn);
                }
                // 总机
                if (phoneType == Phone.TYPE_MAIN) {
                    String tel = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("tel", tel);
                }
                // 无线装置
                if (phoneType == Phone.TYPE_RADIO) {
                    String wirelessDev = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("wirelessDev", wirelessDev);
                }
                // 电报
                if (phoneType == Phone.TYPE_TELEX) {
                    String telegram = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("telegram", telegram);
                }
                // TTY_TDD
                if (phoneType == Phone.TYPE_TTY_TDD) {
                    String tty_tdd = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("tty_tdd", tty_tdd);
                }
                // 单位手机
                if (phoneType == Phone.TYPE_WORK_MOBILE) {
                    String jobMobile = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobMobile", jobMobile);
                }
                // 单位寻呼机
                if (phoneType == Phone.TYPE_WORK_PAGER) {
                    String jobPager = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("jobPager", jobPager);
                }
                // 助理
                if (phoneType == Phone.TYPE_ASSISTANT) {
                    String assistantNum = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("assistantNum", assistantNum);
                }
                // 彩信
                if (phoneType == Phone.TYPE_MMS) {
                    String mms = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                    jsonObject.put("mms", mms);
                }
            }
            // }
            // 查找email地址
            if (Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                // 取出邮件类型
                int emailType = cursor.getInt(cursor.getColumnIndex(Email.TYPE));

                // 住宅邮件地址
                if (emailType == Email.TYPE_CUSTOM) {
                    String homeEmail = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    jsonObject.put("homeEmail", homeEmail);
                }

                // 住宅邮件地址
                else if (emailType == Email.TYPE_HOME) {
                    String homeEmail = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    jsonObject.put("homeEmail", homeEmail);
                }
                // 单位邮件地址
                if (emailType == Email.TYPE_CUSTOM) {
                    String jobEmail = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    jsonObject.put("jobEmail", jobEmail);
                }

                // 单位邮件地址
                else if (emailType == Email.TYPE_WORK) {
                    String jobEmail = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    jsonObject.put("jobEmail", jobEmail);
                }
                // 手机邮件地址
                if (emailType == Email.TYPE_CUSTOM) {
                    String mobileEmail = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    jsonObject.put("mobileEmail", mobileEmail);
                }

                // 手机邮件地址
                else if (emailType == Email.TYPE_MOBILE) {
                    String mobileEmail = cursor.getString(cursor.getColumnIndex(Email.DATA));
                    jsonObject.put("mobileEmail", mobileEmail);
                }
            }
            // 查找event地址
            if (Event.CONTENT_ITEM_TYPE.equals(mimetype)) {
                // 取出时间类型
                int eventType = cursor.getInt(cursor.getColumnIndex(Event.TYPE));
                // 生日
                if (eventType == Event.TYPE_BIRTHDAY) {
                    String birthday = cursor.getString(cursor.getColumnIndex(Event.START_DATE));
                    jsonObject.put("birthday", birthday);
                }
                // 周年纪念日
                if (eventType == Event.TYPE_ANNIVERSARY) {
                    String anniversary = cursor.getString(cursor.getColumnIndex(Event.START_DATE));
                    jsonObject.put("anniversary", anniversary);
                }
            }
            // 即时消息
            if (Im.CONTENT_ITEM_TYPE.equals(mimetype)) {
                // 取出即时消息类型
                int protocal = cursor.getInt(cursor.getColumnIndex(Im.PROTOCOL));
                if (Im.TYPE_CUSTOM == protocal) {
                    String workMsg = cursor.getString(cursor.getColumnIndex(Im.DATA));
                    jsonObject.put("workMsg", workMsg);
                }

                else if (Im.PROTOCOL_MSN == protocal) {
                    String workMsg = cursor.getString(cursor.getColumnIndex(Im.DATA));
                    jsonObject.put("workMsg", workMsg);
                }
                if (Im.PROTOCOL_QQ == protocal) {
                    String instantsMsg = cursor.getString(cursor.getColumnIndex(Im.DATA));
                    jsonObject.put("instantsMsg", instantsMsg);
                }
            }
            // 获取备注信息
            if (Note.CONTENT_ITEM_TYPE.equals(mimetype)) {
                String remark = cursor.getString(cursor.getColumnIndex(Note.NOTE));
                jsonObject.put("remark", remark);
            }
            // 获取昵称信息
            if (Nickname.CONTENT_ITEM_TYPE.equals(mimetype)) {
                String nickName = cursor.getString(cursor.getColumnIndex(Nickname.NAME));
                jsonObject.put("nickName", nickName);
            }
            // 获取组织信息
            if (Organization.CONTENT_ITEM_TYPE.equals(mimetype)) {
                // 取出组织类型
                int orgType = cursor.getInt(cursor.getColumnIndex(Organization.TYPE));
                // 单位
                if (orgType == Organization.TYPE_CUSTOM) {
//     if (orgType == Organization.TYPE_WORK) {
                    String company = cursor.getString(cursor.getColumnIndex(Organization.COMPANY));
                    jsonObject.put("company", company);
                    String jobTitle = cursor.getString(cursor.getColumnIndex(Organization.TITLE));
                    jsonObject.put("jobTitle", jobTitle);
                    String department = cursor.getString(cursor.getColumnIndex(Organization.DEPARTMENT));
                    jsonObject.put("department", department);
                }
            }
            // 获取网站信息
            if (Website.CONTENT_ITEM_TYPE.equals(mimetype)) {
                // 取出组织类型
                int webType = cursor.getInt(cursor.getColumnIndex(Website.TYPE));
                // 主页
                if (webType == Website.TYPE_CUSTOM) {
                    String home = cursor.getString(cursor.getColumnIndex(Website.URL));
                    jsonObject.put("home", home);
                }
                // 主页
                else if (webType == Website.TYPE_HOME) {
                    String home = cursor.getString(cursor.getColumnIndex(Website.URL));
                    jsonObject.put("home", home);
                }

                // 个人主页
                if (webType == Website.TYPE_HOMEPAGE) {
                    String homePage = cursor.getString(cursor.getColumnIndex(Website.URL));
                    jsonObject.put("homePage", homePage);
                }
                // 工作主页
                if (webType == Website.TYPE_WORK) {
                    String workPage = cursor.getString(cursor.getColumnIndex(Website.URL));
                    jsonObject.put("workPage", workPage);
                }
            }
            // 查找通讯地址
            if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) {
                // 取出邮件类型
                int postalType = cursor.getInt(cursor.getColumnIndex(StructuredPostal.TYPE));
                // 单位通讯地址
                if (postalType == StructuredPostal.TYPE_WORK) {
                    String street = cursor.getString(cursor.getColumnIndex(StructuredPostal.STREET));
                    jsonObject.put("street", street);
                    String ciry = cursor.getString(cursor.getColumnIndex(StructuredPostal.CITY));
                    jsonObject.put("ciry", ciry);
                    String box = cursor.getString(cursor.getColumnIndex(StructuredPostal.POBOX));
                    jsonObject.put("box", box);
                    String area = cursor.getString(cursor.getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                    jsonObject.put("area", area);
                    String state = cursor.getString(cursor.getColumnIndex(StructuredPostal.REGION));
                    jsonObject.put("state", state);
                    String zip = cursor.getString(cursor.getColumnIndex(StructuredPostal.POSTCODE));
                    jsonObject.put("zip", zip);
                    String country = cursor.getString(cursor.getColumnIndex(StructuredPostal.COUNTRY));
                    jsonObject.put("country", country);
                }
                // 住宅通讯地址
                if (postalType == StructuredPostal.TYPE_HOME) {
                    String homeStreet = cursor.getString(cursor.getColumnIndex(StructuredPostal.STREET));
                    jsonObject.put("homeStreet", homeStreet);
                    String homeCity = cursor.getString(cursor.getColumnIndex(StructuredPostal.CITY));
                    jsonObject.put("homeCity", homeCity);
                    String homeBox = cursor.getString(cursor.getColumnIndex(StructuredPostal.POBOX));
                    jsonObject.put("homeBox", homeBox);
                    String homeArea = cursor.getString(cursor.getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                    jsonObject.put("homeArea", homeArea);
                    String homeState = cursor.getString(cursor.getColumnIndex(StructuredPostal.REGION));
                    jsonObject.put("homeState", homeState);
                    String homeZip = cursor.getString(cursor.getColumnIndex(StructuredPostal.POSTCODE));
                    jsonObject.put("homeZip", homeZip);
                    String homeCountry = cursor.getString(cursor.getColumnIndex(StructuredPostal.COUNTRY));
                    jsonObject.put("homeCountry", homeCountry);
                }
                // 其他通讯地址
                if (postalType == StructuredPostal.TYPE_OTHER) {
                    String otherStreet = cursor.getString(cursor.getColumnIndex(StructuredPostal.STREET));
                    jsonObject.put("otherStreet", otherStreet);
                    String otherCity = cursor.getString(cursor.getColumnIndex(StructuredPostal.CITY));
                    jsonObject.put("otherCity", otherCity);
                    String otherBox = cursor.getString(cursor.getColumnIndex(StructuredPostal.POBOX));
                    jsonObject.put("otherBox", otherBox);
                    String otherArea = cursor.getString(cursor.getColumnIndex(StructuredPostal.NEIGHBORHOOD));
                    jsonObject.put("otherArea", otherArea);
                    String otherState = cursor.getString(cursor.getColumnIndex(StructuredPostal.REGION));
                    jsonObject.put("otherState", otherState);
                    String otherZip = cursor.getString(cursor.getColumnIndex(StructuredPostal.POSTCODE));
                    jsonObject.put("otherZip", otherZip);
                    String otherCountry = cursor.getString(cursor.getColumnIndex(StructuredPostal.COUNTRY));
                    jsonObject.put("otherCountry", otherCountry);
                }
            }
        }
        cursor.close();
        Log.i("contactData", contactData.toString());
        return contactData.toString();
    }
}

    private void loadContacts()
    {
       // Uri uri =  ContactsContract.Contacts.CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, null, null, ContactsContract.Data.RAW_CONTACT_ID);
        //Cursor cursor = contentResolver.query(uri, null, null, null, null);

        while(cursor.moveToNext()){
/
            String mimetype = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE));
            if(ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE.equals(mimetype)){
                int eventType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.TYPE));
                if(eventType == ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                {
                    String birthday = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                }
            }



        }

        cursor.close();
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
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
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
