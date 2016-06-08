package com.example.swli.myapplication20150519.common;

import android.content.Intent;

import com.example.swli.myapplication20150519.MemberHome;

import lsw.service.TimerService;

/**
 * Created by swli on 5/11/2016.
 */
public class TimerServiceCustomize extends TimerService {
    @Override
    public Intent getIntent() {
        return new Intent(this, MemberHome.class);
    }
}
