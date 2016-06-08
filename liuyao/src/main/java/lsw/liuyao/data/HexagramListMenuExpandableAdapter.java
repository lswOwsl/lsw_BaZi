package lsw.liuyao.data;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import lsw.liuyao.model.HexagramMenuData;

/**
 * Created by swli on 4/25/2016.
 */
public class HexagramListMenuExpandableAdapter extends BaseExpandableListAdapter {

    List<HexagramMenuData>  menuDataList;
    Context context;

    public HexagramListMenuExpandableAdapter(Context context, List<HexagramMenuData> menuDataList)
    {
        this.context = context;
        this.menuDataList = menuDataList;
    }

    @Override
    public int getGroupCount() {
        return menuDataList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return menuDataList.get(i).getSecondLevelData().size();
    }

    @Override
    public Object getGroup(int i) {
        return menuDataList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return menuDataList.get(i).getSecondLevelData().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String string = menuDataList.get(i).getName();
        return  getGenericView(string, 18, 70);

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String string = menuDataList.get(i).getSecondLevelData().get(i1).getName();
        return  getGenericView(string,16, 90);

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public  TextView getGenericView(String string, int textSize, int paddingLeft) {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView text = new TextView(context);
        text.setLayoutParams(layoutParams);
        // Center the text vertically
        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        text.setPadding(paddingLeft, 10, 0, 10);
        text.setText(string);
        text.setTextSize(textSize);
        return text;
    }

}
