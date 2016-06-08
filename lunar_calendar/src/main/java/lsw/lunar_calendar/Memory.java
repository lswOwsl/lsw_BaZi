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

    private TextView tvBeginTime, tvEndTime, tvNote, tvForecast, tvSave, tvReturnValue;
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

        tvReturnValue = (TextView)findViewById(R.id.tvReturnValue);

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
            }
        });

        cbBaZi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            //第二个页面返回来的数据
            //resultcode 区分结果是否属于正常返回
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                boolean flag =  bundle.containsKey(CrossAppKey.HexagramId);
                if(flag)
                {
                    int hexagramId = bundle.getInt(CrossAppKey.HexagramId);
                    tvReturnValue.setText(hexagramId);
                }
            } else if (resultCode == RESULT_CANCELED) {
                //操作失败
            }

        }
    }
}
