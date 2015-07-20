package com.example.swli.myapplication20150519.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.swli.myapplication20150519.MemberMaintain;
import com.example.swli.myapplication20150519.R;
import com.example.swli.myapplication20150519.common.ButtonHelper;
import com.example.swli.myapplication20150519.common.MyApplication;

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
    private AlertDialog ad;
    private String selectedFile;

    public AlertDialog pickDialog() {
        LinearLayout dateTimeLayout = (LinearLayout)activity
                .getLayoutInflater().inflate(R.layout.backup_file_selector, null);
        btnImport = (Button) dateTimeLayout.findViewById(R.id.btnImport);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/八字lsw/";
        final String[] source = loadFilesFromFolder(path).toArray(new String[]{});
        lvFiles = (ListView) dateTimeLayout.findViewById(R.id.lvFiles);
        lvFiles.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, source));

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFile = source[i];
                ad.setTitle("已选择文件名:" + selectedFile);
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ad.hide();
            }
        });

        ad = new AlertDialog.Builder(activity)
                .setTitle("请选择备份文件")
                .setView(dateTimeLayout)
                .show();

        return ad;
    }
}
