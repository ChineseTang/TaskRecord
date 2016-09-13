package receiver;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import controller.AppApplication;
import view.com.taskrecord.MainTabActivity;
import view.com.taskrecord.R;

/**
 * Created by tangzhijing on 2016/9/13.
 */
public class MyReceiver extends BroadcastReceiver {
    private static  int NOTIFICATION_FLAG = 1;
    /**
     * called when the BroadcastReceiver is receiving an Intent broadcast.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        /* start another activity - MyAlarm to display the alarm */
        if (intent.getAction().equals("VIDEO_TIMER")) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1,
                    new Intent(context, MainTabActivity.class), 0);
            // 通过Notification.Builder来创建通知，注意API Level
            // API16之后才支持
            String content = intent.getStringExtra("content");
            //Log.w("task","收到了" + content);

            Notification notify = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_drawer)
                    .setTicker("空空任务提醒:" + "您设置的任务要到期了，请注意完成！")
                    .setContentTitle("任务内容")
                    .setContentText(content)
                    .setContentIntent(pendingIntent).setNumber(1).build(); // 需要注意build()是在API
            // level16及之后增加的，API11可以使用getNotificatin()来替代
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
            // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
            NotificationManager manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_FLAG, notify);
            // 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示

        }
    }

}
