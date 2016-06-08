package com.example.swli.myapplication20150519.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.swli.myapplication20150519.MemberHome;
import com.example.swli.myapplication20150519.MemberMaintain;
import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.model.Member;

import java.util.List;

import lsw.library.CrossAppKey;

/**
 * Created by swli on 5/27/2015.
 */
public class MemberAdapter extends BaseAdapter implements SectionIndexer {

    private List<Member> data;
    private Context context;
    private LayoutInflater layoutInflater;

    boolean fromCalendarApp = false;
    Intent in = null;

    private ICallBackDialog<Integer> deleteButtonClick;
    public void setDeleteButtonClick (ICallBackDialog<Integer> deleteButtonClick) {
        this.deleteButtonClick = deleteButtonClick;
    }

    public MemberAdapter(Context context, List<Member> data)
    {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        in = ((Activity)context).getIntent();
        if(in.getExtras() != null && in.getExtras().containsKey(CrossAppKey.RequestInfo))
            fromCalendarApp = true;
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
            //view = layoutInflater.inflate(R.layout.member_row,null);

            view = layoutInflater.inflate(R.layout.layout_swipe_item, null);
            controls.tvName = (TextView) view.findViewById(R.id.tvName);
            controls.tvBirthday = (TextView) view.findViewById(R.id.tvBirthday);
            controls.tvGender = (TextView) view.findViewById(R.id.tvGender);
            controls.tvLunarBirthday = (TextView) view.findViewById(R.id.tvLunarBirthday);

            controls.btnEdit = (Button) view.findViewById(R.id.sliding_row_edit);
            controls.btnDelete = (Button) view.findViewById(R.id.sliding_row_delete);
            //controls.btnShensha = (Button) view.findViewById(R.id.sliding_row_shensha);

            controls.tvLetter = (TextView) view.findViewById(R.id.catalog);

            view.setTag(controls);
        } else {
            controls = (Controls) view.getTag();
        }
        final Member member = data.get(i);
        controls.tvName.setText(member.getName().trim());
        controls.tvGender.setText("性别：" + member.getGender());
        controls.tvLunarBirthday.setText("阴历：" + member.getLunarBirthday());
        controls.tvBirthday.setText("阳历：" + member.getBirthday().getFormatDateTime());

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(i);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(i == getPositionForSection(section)){
            controls.tvLetter.setVisibility(View.VISIBLE);

            String tempLetter = member.getSortModel().getSortLetters();
            int count = 0;
            for (int j=0;j<data.size();j++)
            {
                if(data.get(j).getSortModel().getSortLetters().equals(tempLetter))
                    count++;
            }

            controls.tvLetter.setText(member.getSortModel().getSortLetters() + "-"+count);
        }else{
            controls.tvLetter.setVisibility(View.GONE);
        }

        final Activity activity = (Activity) context;
        final int memberIndex = i;

        controls.btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (deleteButtonClick != null) {
                    deleteButtonClick.onCall(memberIndex);
                }
            }
        });

        if(fromCalendarApp)
        {
            controls.btnEdit.setText("选中");
            controls.btnDelete.setVisibility(View.GONE);
        }


        controls.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (fromCalendarApp) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CrossAppKey.MemberId, member.getId());
                    in.putExtras(bundle);
                    //设置返回结果成功
                    activity.setResult(activity.RESULT_OK, in);
                    //关闭当前activity
                    activity.finish();
                } else {
                    Intent intent = new Intent();

                    Bundle bundle = createBundle(member);
                    if (activity instanceof MemberHome) {
                        bundle.putString("SearchText", ((MemberHome) activity).getSearchText());
                    }

                    intent.putExtras(bundle);
                    intent.setClass(context, MemberMaintain.class);
                    Activity activity = (Activity) context;
                    activity.startActivityForResult(intent, 0);
                }
            }
        });
//        controls.btnShensha.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//
//                intent.putExtras(createBundle(member));
//                intent.setClass(context, ShenSha.class);
//
//                activity.startActivityForResult(intent, 0);
//            }
//        });


        return view;
    }

    private Bundle createBundle(Member member)
    {
        Bundle bundle = new Bundle();
                /*字符、字符串、布尔、字节数组、浮点数等等，都可以传*/
        bundle.putInt("Id", member.getId());
        bundle.putString("Name", member.getName().trim());
        bundle.putBoolean("Ismale", member.getIsMale());
        bundle.putString("Birthday", member.getBirthday().getFormatDateTime());

        return bundle;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = data.get(i).getSortModel().getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int i) {
        return data.get(i).getSortModel().getSortLetters().charAt(0);
    }

    public void updateListView(List<Member> list){
        this.data = list;
        notifyDataSetChanged();
    }


    public final class Controls
    {
        public TextView tvName;
        public TextView tvBirthday;
        public TextView tvLunarBirthday;
        public TextView tvGender;
        public Button btnEdit;
        public Button btnDelete;
        public Button btnShensha;
        public TextView tvLetter;
    }
}
