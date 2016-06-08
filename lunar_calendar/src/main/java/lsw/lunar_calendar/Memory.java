package lsw.lunar_calendar;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import lsw.library.CrossAppKey;
import lsw.library.DateExt;
import lsw.lunar_calendar.common.IntentKeys;
import lsw.lunar_calendar.common.RecordType;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.model.EventRecord;

/**
 * Created by swli on 5/31/2016.
 */
public class Memory extends Activity {

    private TextView tvBeginTime, tvEndTime, tvNote, tvForecast, tvSave, tvReturnValueLiuYao, tvReturnValueBaZi;
    private EditText etForecast, etNote;
    private CheckBox cbBaZi, cbLiuYao;
    private LinearLayout llNote, llForecast;

    private DateExt beginTime, endTime;
    private String lunarTime;

    private RecordType recordType;
    private String recordCycle;

    private static final String DateFormater = "yyyy-MM-dd";

    private DataBase dataBase;

    private int eventRecordId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        dataBase = new DataBase();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        loadParamsFromBundle(bundle);

        initControls();

        loadContent();

        bindAction();


    }

    private void initControls()
    {
        tvBeginTime = (TextView)findViewById(R.id.tvBeginTime);
        tvEndTime = (TextView)findViewById(R.id.tvEndTime);
        tvNote = (TextView)findViewById(R.id.tvNote);
        tvForecast = (TextView)findViewById(R.id.tvForecast);

        tvReturnValueLiuYao = (TextView)findViewById(R.id.tvReturnValueLiuYao);
        tvReturnValueBaZi = (TextView)findViewById(R.id.tvReturnValueBaZi);

        tvSave = (TextView)findViewById(R.id.tvSave);

        etForecast = (EditText)findViewById(R.id.etForecastNote);
        etNote = (EditText)findViewById(R.id.etNote);

        cbBaZi = (CheckBox)findViewById(R.id.cbBaZi);
        cbLiuYao = (CheckBox)findViewById(R.id.cbLiuYao);

        llForecast = (LinearLayout)findViewById(R.id.llForecast);
        llNote = (LinearLayout)findViewById(R.id.llNote);
    }

    private void loadParamsFromBundle(Bundle bundle)
    {
        if(bundle.get(IntentKeys.EventRecordId) != null)
        {
           eventRecordId = bundle.getInt(IntentKeys.EventRecordId);
        }
        else {
            beginTime = new DateExt(bundle.getString(IntentKeys.BeginDate));
            endTime = new DateExt(bundle.getString(IntentKeys.EndDate));
            recordCycle = bundle.getString(IntentKeys.RecordCycle);
            recordType = RecordType.valueOf(bundle.getInt(IntentKeys.RecordType));
            lunarTime = bundle.getString(IntentKeys.LunarTime);
        }
    }

    private void loadContent()
    {
        if(eventRecordId != 0)
        {
            EventRecord eventRecord = dataBase.getEventRecordById(eventRecordId);
            beginTime = eventRecord.getBeginDateExt();
            endTime = eventRecord.getEndDateExt();
            recordCycle = eventRecord.getRecordCycle();
            recordType = RecordType.All;
            lunarTime = eventRecord.getLunarTime();
            etForecast.setText(eventRecord.getAnalyzeResult());
            etNote.setText(eventRecord.getActualResult());
            String referenceContent = eventRecord.getReferenceContent();
            if(referenceContent != null && !referenceContent.isEmpty()) {
                String[] array = referenceContent.split(";");
                for (String s : array) {
                    if (s.contains("hexagramId")) {
                        tvReturnValueLiuYao.setText("所选卦序号" + s.substring(s.indexOf(":")));
                        paramHexagramId = Integer.valueOf(s.substring(s.indexOf(":")+1));
                        cbLiuYao.setChecked(true);
                    }
                    if (s.contains("memberId")) {
                        tvReturnValueBaZi.setText("所选八字序号" + s.substring(s.indexOf(":")));
                        paramMemberId = Integer.valueOf(s.substring(s.indexOf(":")+1));
                        cbBaZi.setChecked(true);
                    }
                }
            }
        }

        if(beginTime.compareTo(endTime) == DateExt.EnumDateCompareResult.Equal)
        {
            tvEndTime.setVisibility(View.GONE);
        }

        if(recordType == RecordType.Forecast)
        {
            llNote.setVisibility(View.GONE);
        }

        if(recordType == RecordType.Note)
        {
            llForecast.setVisibility(View.GONE);
        }

        tvBeginTime.setText(beginTime.getFormatDateTime(DateFormater));
        tvEndTime.setText(endTime.getFormatDateTime(DateFormater));
    }

    private void bindAction()
    {
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventRecord eventRecord = new EventRecord();
                eventRecord.setId(eventRecordId);
                eventRecord.setBeginTime(tvBeginTime.getText().toString());
                eventRecord.setEndTime(tvEndTime.getText().toString());
                eventRecord.setAnalyzeResult(etForecast.getText().toString());
                eventRecord.setActualResult(etNote.getText().toString());
                eventRecord.setLunarTime(lunarTime);
                String referenceContent = "";
                if(paramHexagramId != 0)
                    referenceContent = "hexagramId:"+paramHexagramId+";";
                if(paramMemberId != 0)
                    referenceContent += "memberId:"+paramMemberId+";";
                eventRecord.setReferenceContent(referenceContent);
                eventRecord = dataBase.saveEventRecord(eventRecord);

                eventRecordId = eventRecord.getId();

                Toast.makeText(Memory.this,"保存成功"+eventRecord.getId(),Toast.LENGTH_SHORT).show();
            }
        });

        cbLiuYao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent intent = new Intent();
                    ComponentName componetName = new ComponentName(
                            "lsw.liuyao",
                            "lsw.liuyao.HexagramListActivity");
                    intent.setComponent(componetName);
                    Bundle bundle = new Bundle();
                    bundle.putString(CrossAppKey.RequestInfo, "lsw_liuyao");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 10);
                }
                else
                {
                    paramHexagramId = 0;
                    tvReturnValueLiuYao.setText("------绑定信息从六爻排盘");
                }
            }
        });

        cbBaZi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Intent intent = new Intent();
                    ComponentName componetName = new ComponentName(
                            "com.example.swli.myapplication20150519",
                            "com.example.swli.myapplication20150519.MemberHome");
                    intent.setComponent(componetName);
                    Bundle bundle = new Bundle();
                    bundle.putString(CrossAppKey.RequestInfo, "lsw_bazi");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 10);
                }
                else
                {
                    paramMemberId = 0;
                    tvReturnValueBaZi.setText("------绑定信息从八字排盘");
                }
            }
        });
    }

    int paramHexagramId = 0;
    int paramMemberId = 0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            //第二个页面返回来的数据
            //resultcode 区分结果是否属于正常返回
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                boolean flagLiuYao =  bundle.containsKey(CrossAppKey.HexagramId);
                if(flagLiuYao)
                {
                    paramHexagramId = bundle.getInt(CrossAppKey.HexagramId);
                    tvReturnValueLiuYao.setText("所选卦序号:"+paramHexagramId);
                }

                boolean flagBaZi = bundle.containsKey(CrossAppKey.MemberId);
                if(flagBaZi)
                {
                    paramMemberId = bundle.getInt(CrossAppKey.MemberId);
                    tvReturnValueBaZi.setText("所选八字序号:"+paramMemberId);
                }
            } else if (resultCode == RESULT_CANCELED) {
                //操作失败
            }

        }
    }
}
