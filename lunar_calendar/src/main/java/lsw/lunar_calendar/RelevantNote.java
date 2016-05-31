package lsw.lunar_calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lsw.library.DateExt;
import lsw.lunar_calendar.common.IntentKeys;

/**
 * Created by swli on 5/31/2016.
 */
public class RelevantNote  extends Activity {

    private TextView tvBeginTime, tvEndTime, tvSave;
    private EditText etForecast, etNote;

    private DateExt beginTime, endTime;

    private String formatDate;
    private String categoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        Intent intent= getIntent();
        Bundle bundle = intent.getExtras();

        formatDate = bundle.getString(IntentKeys.FormatDate);
        categoryName = bundle.getString(IntentKeys.NoteCategory);

        tvBeginTime = (TextView)findViewById(R.id.tvBeginTime);
        tvEndTime = (TextView)findViewById(R.id.tvEndTime);
        tvSave = (TextView)findViewById(R.id.tvSave);

        etForecast = (EditText)findViewById(R.id.etForecastNote);
        etNote = (EditText)findViewById(R.id.etNote);

        bindAction();
    }

    private void loadContent()
    {

    }

    private void bindAction()
    {
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
