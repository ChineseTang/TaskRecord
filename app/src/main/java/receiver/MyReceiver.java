package receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import view.com.taskrecord.MainTabActivity;
import view.com.taskrecord.R;

/**
 * Created by tangzhijing on 2016/9/13.
 */
public class MyReceiver extends BroadcastReceiver {
    /**
     * called when the BroadcastReceiver is receiving an Intent broadcast.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
            //获得随机数
            int number = (int)Math.round(Math.random()*100);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, number,new Intent(context, MainTabActivity.class), 0);
            // 通过Notification.Builder来创建通知，注意API Level
            String ct = intent.getStringExtra("ct");
            //Log.w("task","收到了" + content);
            Log.w("task","accept " + ct);
            Notification notify = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_drawer)
                    .setTicker("空空任务提醒:" + "您设置的任务要到期了，请注意完成！")
                    .setContentTitle("任务内容")
                    .setContentText(ct)
                    .setContentIntent(pendingIntent).build();
            // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // 通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
            manager.notify(number, notify);
    }

}
