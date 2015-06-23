package com.example.swli.myapplication20150519;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by swli on 6/4/2015.
 */
public class MemberBase extends Activity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set back button
        getActionBar().setDisplayHomeAsUpEnabled(true);


    }


}
