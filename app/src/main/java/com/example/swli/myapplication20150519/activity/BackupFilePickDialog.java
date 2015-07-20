package com.example.swli.myapplication20150519.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.data.handler.MemberDataHandler;
import com.example.swli.myapplication20150519.model.Member;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swli on 7/20/2015.
 */
public class BackupFilePickDialog {

   Activity activity;

    public BackupFilePickDialog(Activity activity)
    {
        this.activity = activity;
    }

    private List<String> loadFilesFromFolder(String folder)
    {
        List<String> fileNames= new ArrayList<String>();

        File[] files = new File(folder).listFiles();
        for (File file : files) {
            if (getFileExtension(file).toLowerCase().equals("xml")) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private ListView lvFiles;
    private Button btnImport;
    private Dialog ad;
    private String selectedFile;
    String[] source;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/八字lsw/";

    public Dialog pickDialog() {

        ad = new Dialog(activity,R.style.CustomDialog);
        ad.setContentView(R.layout.backup_file_selector);
        DisplayMetrics dm = new DisplayMetrics();
       // ad.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        ad.getWindow().getDecorView().setPadding(0, 0, 0, 0);

        btnImport = (Button) ad.findViewById(R.id.btnImport);
        source = loadFilesFromFolder(path).toArray(new String[]{});
        lvFiles = (ListView) ad.findViewById(R.id.lvFiles);
        lvFiles.setAdapter(getAdapter());

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int childCount = adapterView.getChildCount();
                for (int index = 0; index < childCount; index++) {
                    adapterView.getChildAt(index).setBackgroundColor(Color.TRANSPARENT);
                    TextView tv = (TextView) adapterView.getChildAt(index).findViewById(R.id.tvText);
                    tv.setTextColor(Color.LTGRAY);
                }

                selectedFile = source[i];

                view.setBackgroundColor(Color.LTGRAY);
                TextView tv = (TextView) view.findViewById(R.id.tvText);
                tv.setTextColor(Color.WHITE);
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MemberDataHandler handler = new MemberDataHandler();
                List<Member> members = handler.loadMembersFromXml(path + selectedFile);
                handler.importMembersToDb(members);
                ad.hide();
            }
        });


        ad.show();

        WindowManager windowManager = activity.getWindowManager();

        Display display = windowManager.getDefaultDisplay();
        Window win = ad.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.CustomDialog_Animation);
        DisplayMetrics dmShow = new DisplayMetrics();
        display.getMetrics(dmShow);
        win.setLayout(dmShow.widthPixels, dmShow.heightPixels/2);

        return ad;
    }

    public BaseAdapter getAdapter()
    {
        return new BaseAdapter() {
            private LayoutInflater layoutInflater = LayoutInflater.from(activity);

            @Override
            public int getCount() {
                return source.length;
            }

            @Override
            public Object getItem(int i) {
                return source[i];
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                RowItem controls;
                if (view == null) {
                    controls = new RowItem();
                    view = layoutInflater.inflate(R.layout.backup_file_selector_item, null);
                    controls.tvText = (TextView) view.findViewById(R.id.tvText);
                    view.setTag(controls);
                } else {
                    controls = (RowItem) view.getTag();
                }

                controls.tvText.setText(source[i]);

                return view;
            }

            class RowItem {
                public TextView tvText;
            }
        };
    }
}
