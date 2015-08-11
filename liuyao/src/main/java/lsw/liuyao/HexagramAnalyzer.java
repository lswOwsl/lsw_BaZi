package lsw.liuyao;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import lsw.hexagram.Builder;
import lsw.liuyao.common.IntentKeys;
import lsw.model.Hexagram;
import lsw.model.Line;

/**
 * Created by swli on 8/7/2015.
 */
public class HexagramAnalyzer extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hexagram_analyze_activity);

        Bundle bundle = getIntent().getExtras();
        String formatDate = getIntent().getStringExtra(IntentKeys.FormatDate);

        ArrayList<Line> models = (ArrayList<Line>)bundle.getSerializable(IntentKeys.LineModelList);

        Builder builder = Builder.getInstance(this);
        try {
            Hexagram hexagram = builder.getHexagramByLines(models, true);
            Hexagram changed = builder.getChangedHexagramByOriginal(hexagram, false);

            HexagramAnalyzerFragment analyzerFragment = HexagramAnalyzerFragment.newInstance(hexagram,changed,formatDate);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fl_Hexagram_Analyzer, analyzerFragment, null);
            ft.commit();
        }
        catch (Exception ex)
        {
            Log.e("Hexagram Builder", ex.getMessage());
        }


    }


}
