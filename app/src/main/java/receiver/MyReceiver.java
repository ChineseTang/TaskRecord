package receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import controller.AppApplication;
import controller.TaskAlertController;
import model.TaskAlert;
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
            int number = (int)Math.round(Math.random()*1000);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, number,new Intent(context, MainTabActivity.class), 0);
            // 通过Notification.Builder来创建通知，注意API Level
           //获得TaskAlert对象
           final TaskAlert  taskalert = (TaskAlert) intent.getSerializableExtra("taskalert");
          //判断该taskalert 是否已经通知了
          int getalert = new TaskAlertController().selectFinishOrNot(taskalert.getAid());
          //如果已经为0 ，表示未通知
          if(getalert == 0) {
              if (taskalert != null) {
                  // 添加到AppApplication中的ArrayList<Integer> alert
                  new TaskAlertController().ChangeToFinish(taskalert.getAid());
                  int rs = taskalert.getAlertFinish();
                  Log.w("taskrem", AppApplication.getRs() + " " + taskalert.getAid() + " ");
                  if (rs == 0 && AppApplication.getRs() != taskalert.getAid()) {
                      AppApplication.setRs(taskalert.getAid());
                      //然后去数据库中判断该任务是否已经提醒过没有
                      Log.w("tasktest", taskalert.getAid() + taskalert.getAlertContent());
                      String ct = taskalert.getAlertContent();
                      Notification notify = new Notification.Builder(context)
                              .setSmallIcon(R.drawable.logoalert)
                              .setTicker("空空清单提醒:" + ct)
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
          }

    }

}
