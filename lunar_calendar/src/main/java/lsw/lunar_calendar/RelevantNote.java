package lsw.lunar_calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lsw.library.DateExt;
import lsw.lunar_calendar.common.IntentKeys;
import lsw.lunar_calendar.common.RecordType;
import lsw.lunar_calendar.data.DataBase;
import lsw.lunar_calendar.model.EventRecord;

/**
 * Created by swli on 5/31/2016.
 */
public class RelevantNote  extends Activity {

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

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        loadParamsFromBundle(bundle);

        initControls();

        loadContent();

        bindAction();

        dataBase = new DataBase();
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
        beginTime = new DateExt(bundle.getString(IntentKeys.BeginDate));
        endTime = new DateExt(bundle.getString(IntentKeys.EndDate));
        recordCycle = bundle.getString(IntentKeys.RecordCycle);
        recordType = RecordType.valueOf(bundle.getInt(IntentKeys.RecordType));
        lunarTime = bundle.getString(IntentKeys.LunarTime);
    }

    private void loadContent()
    {
        if(beginTime.equals(endTime))
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
                dataBase.saveEventRecord(eventRecord);
            }
        });
    }
}
