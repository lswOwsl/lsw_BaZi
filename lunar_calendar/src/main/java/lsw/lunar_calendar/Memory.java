package lsw.lunar_calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import lsw.library.DateExt;
import lsw.lunar_calendar.common.IntentKeys;
import lsw.lunar_calendar.common.RecordType;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.model.EventRecord;

/**
 * Created by swli on 5/31/2016.
 */
public class Memory extends Activity {

    private TextView tvBeginTime, tvEndTime, tvNote, tvForecast, tvSave;
    private EditText etForecast, etNote;

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

        tvSave = (TextView)findViewById(R.id.tvSave);

        etForecast = (EditText)findViewById(R.id.etForecastNote);
        etNote = (EditText)findViewById(R.id.etNote);
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
            tvNote.setVisibility(View.GONE);
            etNote.setVisibility(View.GONE);
        }

        if(recordType == RecordType.Note)
        {
            tvForecast.setVisibility(View.GONE);
            etForecast.setVisibility(View.GONE);
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
    }
}
