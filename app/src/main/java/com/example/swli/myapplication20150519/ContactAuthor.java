package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * Created by lsw_wsl on 7/11/15.
 */
public class ContactAuthor extends Activity {


    TextView taoBaoLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_author);

        TextView tvTaoBao = (TextView) findViewById(R.id.tvTaoBao);
        tvTaoBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView mobView = new WebView(ContactAuthor.this);
                mobView.loadUrl("http://shop117249232.taobao.com/");

//                WebSettings wSet = mobView.getSettings();
//                wSet.setJavaScriptEnabled(true);
//                mobView.setWebViewClient(new WebViewClient() {
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
//                        view.loadUrl(url);
//                        return true;
//                    }
//
//                });
            }


        });

        TextView tvWeiDian = (TextView) findViewById(R.id.tvWeiDian);

        tvWeiDian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebView mobView = new WebView(ContactAuthor.this);
                mobView.loadUrl("http://kdt.im/wk6XcyRV1");
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void btnTaoBaoOnClick(View view)
    {


    }
}
