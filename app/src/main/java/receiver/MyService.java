package receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Date;

import controller.TaskAlertController;
import model.TaskAlert;

/**
 * Created by tangzhijing on 2016/10/3.
 */
public class MyService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获得随机数
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //此处设置开启AlarmReceiver这个Service
        Bundle mExtra = new Bundle();
        //到数据库中查询所有的未提醒的任务，将他们添加到AlarmManger中去
        ArrayList<TaskAlert> taskalerts = new ArrayList<TaskAlert>();
        taskalerts = new TaskAlertController().searchTaskAlerts();
        //如果不为空
        if(!taskalerts.isEmpty()) {
            for (TaskAlert taskalert : taskalerts) {
                Intent i = new Intent(this, MyReceiver.class);
                int number = (int)Math.round(Math.random()*100);
                mExtra.putSerializable("taskalert", taskalert);
                //intent.putExtra("ct",content);
                i.putExtras(mExtra);
                PendingIntent pi = PendingIntent.getBroadcast(MyService.this, number, i, PendingIntent.FLAG_UPDATE_CURRENT);
                //设置闹钟的时间，当时间到达后，会触发MyReceiver广播
               // Log.w("task",taskalert.getAid() + taskalert.getAlertContent());
                long currenttime = new Date().getTime();
                if(taskalert.getAlertTime() <= currenttime) {
                    //设置任务已经提醒过了
                    new TaskAlertController().ChangeToFinish(taskalert.getAid());
                    manager.set(AlarmManager.RTC_WAKEUP, taskalert.getAlertTime(), pi);
                    //Log.w("taskchoose","this" + taskalert.getAlertTime() + " " + currenttime);
                }else{
                    manager.set(AlarmManager.RTC_WAKEUP, taskalert.getAlertTime(), pi);
                    //Log.w("taskchoose","that"+ taskalert.getAlertTime() + " " + currenttime);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
