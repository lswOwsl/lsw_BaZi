package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swli.myapplication20150519.activity.DateTimePickDialog;
import com.example.swli.myapplication20150519.common.DBManager;
import com.example.swli.myapplication20150519.common.DateExt;
import com.example.swli.myapplication20150519.common.DateLunar;
import com.example.swli.myapplication20150519.common.LunarCalendarWrapper;

/**
 * Created by swli on 5/26/2015.
 */
public class MemberMaintain extends Activity {

    /** Called when the activity is first created. */
    private EditText etDateTimeOfBirth;
    private EditText etMemberName;
    private TextView tvLunarBirthday;
    private RadioButton rbIsMale;
    private RadioButton rbIsFemale;
    private RadioGroup rgGender;
    private Button btnSave;
    private Button btnAnaylze;
    private Integer memeberId;
    private String preActivitySearchText;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_maintain);

        //getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayShowTitleEnabled(true);

        dbManager = new DBManager(this);

        initControls();
        initContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initControls()
    {
        etDateTimeOfBirth = (EditText) findViewById(R.id.etDateTimeOfBirth);
        etDateTimeOfBirth.setKeyListener(null);

        etMemberName = (EditText)findViewById(R.id.etMemeberName);
        tvLunarBirthday = (TextView)findViewById(R.id.tvMemberLunarBirthday);
        rbIsMale = (RadioButton)findViewById(R.id.rbIsMale);
        rbIsFemale = (RadioButton)findViewById(R.id.rbIsFemale);
        btnAnaylze = (Button)findViewById(R.id.btnAnalyzeMember);
        btnSave = (Button)findViewById(R.id.btnSaveMember);
        rgGender=(RadioGroup)findViewById(R.id.rgGender);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.rbIsMale) {
                    Toast.makeText(MemberMaintain.this, "性别:男", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MemberMaintain.this, "性别:女", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadLunarBirthday(DateExt dateExt)
    {
        LunarCalendarWrapper lunarCalendarWrapper = new LunarCalendarWrapper(dateExt);
        DateLunar dateLunar = lunarCalendarWrapper.getDateLunar(dateExt);
        String strLunarBirthday = lunarCalendarWrapper.toStringWithChineseYear(dateLunar.getLunarYear()) + "年" +
                (dateLunar.getIsLeapMonth() ? "闰":"") +
                lunarCalendarWrapper.toStringWithChineseMonth(dateLunar.getLunarMonth()) + "月" +
                lunarCalendarWrapper.toStringWithChineseDay(dateLunar.getLunarDay()) + " "+
                lunarCalendarWrapper.toStringWithTerrestrialBranch(lunarCalendarWrapper.getChineseEraOfHour()) + "时";
        tvLunarBirthday.setText(strLunarBirthday);
    }

    private void initContent()
    {
        DateExt now = DateExt.getCurrentTime();
        if(this.getIntent().getExtras() != null)
        {
            Bundle bundle = this.getIntent().getExtras();

            /*获取Bundle中的数据，注意类型和key*/
            int id = bundle.getInt("Id");
            String name = bundle.getString("Name");
            boolean ismale = bundle.getBoolean("Ismale");
            final String birthday = bundle.getString("Birthday");
            String isMaleText = ismale?"男":"女";
            getActionBar().setTitle("编辑");
            //getActionBar().setSubtitle(isMaleText);
            now = new DateExt(birthday);

            preActivitySearchText = bundle.getString("SearchText");

            if(ismale)
                rbIsMale.setChecked(true);
            else
                rbIsFemale.setChecked(true);

            memeberId = id;
            etMemberName.setText(name);
        }
        else
        {
            getActionBar().setTitle("新增");
        }

        final DateExt tempDateExt = now;
        etDateTimeOfBirth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                DateTimePickDialog dateTimePicKDialog=new DateTimePickDialog(
                        MemberMaintain.this,tempDateExt);
                dateTimePicKDialog.dateTimePicKDialog(etDateTimeOfBirth);
            }
        });

        etDateTimeOfBirth.setText(now.getFormatDateTime());
        loadLunarBirthday(now);
    }

    public void btnSaveMemberClick(View view)
    {
        if("".equals(etMemberName.getText().toString().trim()))
        {
            Toast.makeText(MemberMaintain.this, "姓名不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isMale = true;
        if(rbIsFemale.isChecked())
            isMale = false;

        dbManager.openDatabase();
        if(memeberId !=null)
        {
            ContentValues cv = new ContentValues();
            cv.put("IsMale", isMale);
            cv.put("Birthday_Refactor",new DateExt(etDateTimeOfBirth.getText().toString()).getDefaultFormatForSqllite());
            cv.put("Name",etMemberName.getText().toString());
            String[] args = {String.valueOf(memeberId.intValue())};
            dbManager.getDatabase().update("Members", cv, "Id=?",args);
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put("IsMale", isMale);
            cv.put("Birthday_Refactor",new DateExt(etDateTimeOfBirth.getText().toString()).getDefaultFormatForSqllite());
            cv.put("Name",etMemberName.getText().toString());
            dbManager.getDatabase().insert("Members", null, cv);
        }
        dbManager.closeDatabase();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("Result",true);
        bundle.putString("SearchText",preActivitySearchText);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
        finish();//此处一定要调用finish()方法
    }

    public void btnAnalyzeMemberClick(View view) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
                /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/
        //bundle.putInt("Id", memeberId.intValue());
        bundle.putString("Name", etMemberName.getText().toString());
        boolean isMale = true;
        if (rbIsFemale.isChecked()) {
            isMale = false;
        }
        bundle.putBoolean("Ismale", isMale);
        bundle.putString("Birthday",  etDateTimeOfBirth.getText().toString());
        intent.putExtras(bundle);
        intent.setClass(MemberMaintain.this, MemberAnalyze.class);
        startActivityForResult(intent, 0);
    }
}
