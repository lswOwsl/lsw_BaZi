package lsw.lunar_calendar.data_source;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import lsw.library.ColorHelper;
import lsw.lunar_calendar.R;
import lsw.lunar_calendar.model.MemberDataRow;

/**
 * Created by swli on 5/9/2016.
 */
public class MemberListAdapter extends BaseAdapter {
    private List<MemberDataRow> data;
    private Context context;
    private LayoutInflater layoutInflater;


    public MemberListAdapter(Context context, List<MemberDataRow> data)
    {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Controls controls;
        if (view == null) {
            controls = new Controls();

            view = layoutInflater.inflate(R.layout.birthday_list_item, null);
            controls.tvName = (TextView) view.findViewById(R.id.tvName);
            controls.tvBirthday = (TextView) view.findViewById(R.id.tvBirthday);
            controls.tvGender = (TextView) view.findViewById(R.id.tvGender);
            controls.tvLunarBirthday = (TextView) view.findViewById(R.id.tvLunarBirthday);

            view.setTag(controls);
        } else {
            controls = (Controls) view.getTag();
        }
        final MemberDataRow member = data.get(i);
        controls.tvName.setText(member.getName().trim());
        controls.tvGender.setText("性别：" + (member.isMale() ? "男":"女"));
        controls.tvLunarBirthday.setText("阴历：" + member.getLunarBirthday());
        controls.tvBirthday.setText("阳历：" + member.getBirthday().getFormatDateTime());

        if(member.isLunarBirthday())
            controls.tvLunarBirthday.setBackgroundColor(Color.LTGRAY);

        return view;
    }

    public final class Controls
    {
        public TextView tvName;
        public TextView tvBirthday;
        public TextView tvLunarBirthday;
        public TextView tvGender;
    }
}
