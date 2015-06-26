package com.example.swli.myapplication20150519.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by swli on 6/26/2015.
 */
public class TimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(TimerConstant.Send_Message))
        {
            Intent intentToExecute = new Intent(context,TimerService.class);
            intentToExecute.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intentToExecute);
        }
    }
}
