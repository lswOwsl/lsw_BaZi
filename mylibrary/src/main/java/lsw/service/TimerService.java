package lsw.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


/**
 * Created by swli on 6/26/2015.
 */
public abstract class TimerService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int NOTIFICATION_FLAG = 1;
        //Context context = TimerService.this;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0,
//                new Intent(this, MemberHome.class), 0);
        PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0,
                getIntent(), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API16之后才支持
        Notification notify3 = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setTicker("TickerText:" + "您有新短消息，请注意查收！")
                .setContentTitle("Notification Title")
                .setContentText("This is the notification message")
                .setContentIntent(pendingIntent3).setNumber(1).build(); // 需要注意build()是在API
        // level16及之后增加的，API11可以使用getNotificatin()来替代
        notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        manager.notify(NOTIFICATION_FLAG, notify3);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示

//        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
//        if(gprs == State.CONNECTED || gprs == State.CONNECTING){

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
       super.onDestroy();
    }

    public abstract Intent getIntent();
}
