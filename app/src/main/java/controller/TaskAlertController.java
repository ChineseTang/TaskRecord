package controller;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import db.TaskRecordOpenHelper;
import model.TaskAlert;

/**
 * Created by tangzhijing on 2016/10/3.
 */
public class TaskAlertController {
    /**
     * 添加一个任务提醒
     * @param taskalert
     * @return
     */
    public int addTaskAlert(TaskAlert taskalert) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        int rs = -1;
        String sql = "insert into Taskalert(ntid,alertTime,alertContent,alertFinish,createTime) values('"
                + taskalert.getNtid() +"','"
                + taskalert.getAlertTime() + "','" + taskalert.getAlertContent()   + "','"
                + taskalert.getAlertFinish() + "','" + taskalert.getCreateTime() + "')";
        db.beginTransaction();
        Cursor cursor = null;
        try {
            db.execSQL(sql);
            //Log.w("tasknew",sql);
            //查询该任务的主键
            cursor = db.rawQuery("select last_insert_rowid() from Taskalert",null);
            if(cursor.moveToFirst())
                rs = cursor.getInt(0);
            //设置事务成功
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //结束事务
            db.endTransaction();
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return rs;
    }

    /**
     * 在MyReceiver中判断该任务是否提醒过
     * @param aid
     * @return
     */
    public int selectFinishOrNot(int  aid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        int rs = -1;
        String sql = "select alertFinish from Taskalert where aid=" + aid;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql,null);
            if(cursor.moveToFirst())
                rs = cursor.getInt(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return rs;
    }
    /**
     * 获得aid
     * @param ntid
     * @return
     */
    public int selectAlertAid(int  ntid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        int rs = -1;
        //默认降序 找到最新的一条通知
        String sql = "select aid from Taskalert where ntid=" + ntid + " order by createTime desc";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql,null);
            //找到最新的一条数据
            if(cursor.moveToFirst())
                rs = cursor.getInt(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(cursor != null)
                cursor.close();
            db.close();
        }
        return rs;
    }
    /**
     * 改变到完成状态
     * @param aid
     */
    public void ChangeToFinish(int  aid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
            SQLiteDatabase db = to.getConnection();
            String sql = "update Taskalert set alertFinish=1 where aid=" + aid;
       // Log.w("taaskexecute","execute");
            try {
                db.execSQL(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.close();
            }

    }

    /**
     * 删除任务时用，该条任务的所有通知改变成已完成
     * @param ntid
     */
    public void ChangeToFinishByNtid(int  ntid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "update Taskalert set alertFinish=1 where ntid=" + ntid;
        // Log.w("taaskexecute","execute");
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    /**
     * 删除该条提醒任务
     * @param ntid
     */
    public void deleteAlertEvent(int  ntid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "delete from Taskalert where ntid=" + ntid;
        //Log.w("taaskexecute","execute");
        try {
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

    }

    /**
     * 查询所有未提醒的任务
     * @return
     */
    public ArrayList<TaskAlert> searchTaskAlerts() {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<TaskAlert> tasks = new ArrayList<TaskAlert>();
        //查询语句
        String sql = "select * from Taskalert where alertFinish=0";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int aid = cs.getInt(cs.getColumnIndex("aid"));
                    int ntid = cs.getInt(cs.getColumnIndex("ntid"));
                    long alerTime = cs.getLong(cs.getColumnIndex("alertTime"));
                    String alertContent = cs.getString(cs.getColumnIndex("alertContent"));
                    int alertFinish = cs.getInt(cs.getColumnIndex("alertFinish"));
                    long createTime = cs.getLong(cs.getColumnIndex("createTime"));

                    //创建一个任务
                    TaskAlert taskalert = new TaskAlert();
                    taskalert.setAid(aid);
                    taskalert.setNtid(ntid);
                    taskalert.setAlertTime(alerTime);
                    taskalert.setAlertContent(alertContent);
                    taskalert.setAlertFinish(alertFinish);
                    taskalert.setCreateTime(createTime);
                    //添加到任务表中
                    tasks.add(taskalert);
                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //返回任务
        return tasks;
    }
}

