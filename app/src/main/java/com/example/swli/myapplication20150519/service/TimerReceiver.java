package com.example.swli.myapplication20150519.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by swli on 6/26/2015.
 */
public class TimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        boolean isServiceRunning = false;


        if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {

            //检查Service状态

            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("com.example.swli.myapplication20150519.service.TimerService".equals(service.service.getClassName()))

                {
                    isServiceRunning = true;
                }

            }
            if (!isServiceRunning) {
                Intent i = new Intent(context, TimerService.class);
                context.startService(i);
            }


//            if(intent.getAction().equals(TimerConstant.Send_Message))
//        {
//            Intent intentToExecute = new Intent(context,TimerService.class);
//            intentToExecute.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startService(intentToExecute);
//        }

        }
    }
}
