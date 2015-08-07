package lsw.liuyao;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.ArrayList;

import lsw.liuyao.common.IntentKeys;
import lsw.liuyao.model.YaoModel;

/**
 * Created by swli on 8/7/2015.
 */
public class AnalyzeGua extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gua_analyze);

        Bundle bundle = getIntent().getExtras();

        ArrayList<YaoModel> models = (ArrayList<YaoModel>)bundle.getSerializable(IntentKeys.YaoModelList);

//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.fl_calendar, f1, "begin_calendar");
//        ft.commit();
    }


}
