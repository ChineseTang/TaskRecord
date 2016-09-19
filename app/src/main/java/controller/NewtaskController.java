package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import db.TaskRecordOpenHelper;
import model.Newtask;

public class NewtaskController {
    //添加任务
    public boolean addTask(Newtask newtask) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "insert into NewTask(uId,sid,ncontent,nfinish,nTime,notetime,ntasktime) values('"
                + newtask.getuId()
                + "','"
                +newtask.getSid()
                +"','"
                + newtask.getNcontent()
                + "','"
                + newtask.getNfinish()
                + "','"
                + newtask.getaTime()
                + "','"
                + newtask.getNotetime()
                +"','"
                + newtask.getNtasktime() + "')";
       // Log.w("task",sql);
        try {
            db.execSQL(sql);
            db.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    //根据日期查询当天的任务
    public ArrayList<Newtask> searchByTime(int uid,String gettime) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where uid=" + uid + " and nTime='" + gettime + "'";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int ntid = cs.getInt(cs.getColumnIndex("ntId"));
                    int utid = cs.getInt(cs.getColumnIndex("uId"));
                    int sid = cs.getInt(cs.getColumnIndex("sid"));
                    String ncontent = cs.getString(cs.getColumnIndex("ncontent"));
                    int nfinish = cs.getInt(cs.getColumnIndex("nfinish"));
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    String notetime = cs.getString(cs.getColumnIndex("notetime"));
                    long ntasktime = cs.getLong(cs.getColumnIndex("ntasktime"));
                    //创建一个任务
                    Newtask task = new Newtask();

                    task.setNtId(ntid);
                    task.setuId(utid);
                    task.setSid(sid);
                    task.setNcontent(ncontent);
                    task.setNfinish(nfinish);
                    task.setaTime(nTime);
                    task.setNotetime(notetime);
                    task.setNtasktime(ntasktime);
                    //添加到任务表中
                    tasks.add(task);
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
    //根据日期查询当月的任务
    public ArrayList<Newtask> searchDrawByTime(int uid,String gettime) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where uid=" + uid + " and nTime like '" + gettime + "%'";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int ntid = cs.getInt(cs.getColumnIndex("ntId"));
                    int utid = cs.getInt(cs.getColumnIndex("uId"));
                    int sid = cs.getInt(cs.getColumnIndex("sid"));
                    String ncontent = cs.getString(cs.getColumnIndex("ncontent"));
                    int nfinish = cs.getInt(cs.getColumnIndex("nfinish"));
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    String notetime = cs.getString(cs.getColumnIndex("notetime"));
                    long ntasktime = cs.getLong(cs.getColumnIndex("ntasktime"));
                    //创建一个任务
                    Newtask task = new Newtask();

                    task.setNtId(ntid);
                    task.setuId(utid);
                    task.setSid(sid);
                    task.setNcontent(ncontent);
                    task.setNfinish(nfinish);
                    task.setaTime(nTime);
                    task.setNotetime(notetime);
                    task.setNtasktime(ntasktime);
                    //添加到任务表中
                    tasks.add(task);
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

    /**
     *
     * @param uid  查询未提醒的任务
     * @return
     */
    public ArrayList<Newtask> searchNotAlertTasks(int uid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where notetime='不提醒' and uid=" + uid;
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int ntid = cs.getInt(cs.getColumnIndex("ntId"));
                    int utid = cs.getInt(cs.getColumnIndex("uId"));
                    int sid = cs.getInt(cs.getColumnIndex("sid"));
                    String ncontent = cs.getString(cs.getColumnIndex("ncontent"));
                    int nfinish = cs.getInt(cs.getColumnIndex("nfinish"));
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    String notetime = cs.getString(cs.getColumnIndex("notetime"));
                    long ntasktime = cs.getLong(cs.getColumnIndex("ntasktime"));
                    //创建一个任务
                    Newtask task = new Newtask();

                    task.setNtId(ntid);
                    task.setuId(utid);
                    task.setSid(sid);
                    task.setNcontent(ncontent);
                    task.setNfinish(nfinish);
                    task.setaTime(nTime);
                    task.setNotetime(notetime);
                    task.setNtasktime(ntasktime);
                    //添加到任务表中
                    tasks.add(task);
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

    /**
     *
     * @param uid  查询提醒的任务
     * @return
     */
    public ArrayList<Newtask> searchAlertTasks(int uid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where notetime!='不提醒' and uid=" + uid;
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int ntid = cs.getInt(cs.getColumnIndex("ntId"));
                    int utid = cs.getInt(cs.getColumnIndex("uId"));
                    int sid = cs.getInt(cs.getColumnIndex("sid"));
                    String ncontent = cs.getString(cs.getColumnIndex("ncontent"));
                    int nfinish = cs.getInt(cs.getColumnIndex("nfinish"));
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    String notetime = cs.getString(cs.getColumnIndex("notetime"));
                    long ntasktime = cs.getLong(cs.getColumnIndex("ntasktime"));
                    //创建一个任务
                    Newtask task = new Newtask();

                    task.setNtId(ntid);
                    task.setuId(utid);
                    task.setSid(sid);
                    task.setNcontent(ncontent);
                    task.setNfinish(nfinish);
                    task.setaTime(nTime);
                    task.setNotetime(notetime);
                    task.setNtasktime(ntasktime);
                    //添加到任务表中
                    tasks.add(task);
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
    //查询所有的任务
    public ArrayList<Newtask> searchAllTasks(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where uid=" + uid + " order by ntasktime desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int ntid = cs.getInt(cs.getColumnIndex("ntId"));
                    int utid = cs.getInt(cs.getColumnIndex("uId"));
                    int sid = cs.getInt(cs.getColumnIndex("sid"));
                    String ncontent = cs.getString(cs.getColumnIndex("ncontent"));
                    int nfinish = cs.getInt(cs.getColumnIndex("nfinish"));
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    String notetime = cs.getString(cs.getColumnIndex("notetime"));
                    long ntasktime = cs.getLong(cs.getColumnIndex("ntasktime"));
                    //创建一个任务
                    Newtask task = new Newtask();

                    task.setNtId(ntid);
                    task.setuId(utid);
                    task.setSid(sid);
                    task.setNcontent(ncontent);
                    task.setNfinish(nfinish);
                    task.setaTime(nTime);
                    task.setNotetime(notetime);
                    task.setNtasktime(ntasktime);
                    //添加到任务表中
                    tasks.add(task);
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

    /**
     * 查询完成的任务
     * @param uid
     * @return
     */
    public ArrayList<Newtask> searchFinishTasks(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where nfinish=1 and uid=" + uid + " order by ntasktime desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int ntid = cs.getInt(cs.getColumnIndex("ntId"));
                    int utid = cs.getInt(cs.getColumnIndex("uId"));
                    int sid = cs.getInt(cs.getColumnIndex("sid"));
                    String ncontent = cs.getString(cs.getColumnIndex("ncontent"));
                    int nfinish = cs.getInt(cs.getColumnIndex("nfinish"));
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    String notetime = cs.getString(cs.getColumnIndex("notetime"));
                    long ntasktime = cs.getLong(cs.getColumnIndex("ntasktime"));
                    //创建一个任务
                    Newtask task = new Newtask();

                    task.setNtId(ntid);
                    task.setuId(utid);
                    task.setSid(sid);
                    task.setNcontent(ncontent);
                    task.setNfinish(nfinish);
                    task.setaTime(nTime);
                    task.setNotetime(notetime);
                    task.setNtasktime(ntasktime);
                    //添加到任务表中
                    tasks.add(task);
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

    /**
     * 查询未完成任务
     * @param uid
     * @return
     */
    public ArrayList<Newtask> searchNotFinishTasks(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where nfinish=0 and uid=" + uid + " order by ntasktime desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    int ntid = cs.getInt(cs.getColumnIndex("ntId"));
                    int utid = cs.getInt(cs.getColumnIndex("uId"));
                    int sid = cs.getInt(cs.getColumnIndex("sid"));
                    String ncontent = cs.getString(cs.getColumnIndex("ncontent"));
                    int nfinish = cs.getInt(cs.getColumnIndex("nfinish"));
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    String notetime = cs.getString(cs.getColumnIndex("notetime"));
                    long ntasktime = cs.getLong(cs.getColumnIndex("ntasktime"));
                    //创建一个任务
                    Newtask task = new Newtask();

                    task.setNtId(ntid);
                    task.setuId(utid);
                    task.setSid(sid);
                    task.setNcontent(ncontent);
                    task.setNfinish(nfinish);
                    task.setaTime(nTime);
                    task.setNotetime(notetime);
                    task.setNtasktime(ntasktime);
                    //添加到任务表中
                    tasks.add(task);
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
    /**
     * 更新任务的状态，将未完成的任务更换为已经完成的任务。
     */
    public boolean changeToFinish(int ntid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "update Newtask set nfinish=1 where ntid=" + ntid;
        db.execSQL(sql);
        return true;
    }


    /**
     * //更新任务的状态，将未完成的任务更换为已经完成的任务。
     * @param ntid
     * @return
     */
    public boolean changeToNotFinish(int ntid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "update Newtask set nfinish=0 where ntid=" + ntid;
        db.execSQL(sql);
        return true;
    }
    /**
     * 删除任务
     * @param ntid
     * @return
     */
    public boolean deleteTaskById(int ntid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "delete from Newtask where ntid=" + ntid;
        db.execSQL(sql);
        return true;
    }
    /**
     * 修改任务，修改任务内容，修改的任务状态一律改为未完成，修改时间
     */
    public boolean updtateTask(Newtask newtask) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "update Newtask set ncontent='" +newtask.getNcontent()
                + "', sid=" + newtask.getSid()
                + ",nfinish="+ newtask.getNfinish() + ",nTime='"
                + newtask.getaTime() +"',notetime='" + newtask.getNotetime()
                + "',ntasktime=" + newtask.getNtasktime()
                + " where ntId=" + newtask.getNtId();

        //Log.d("taskrecord", sql);
        db.execSQL(sql);
        return true;
    }
    /**
     * 返回完成的数目
     */
    public int searchCompleted(int uid) {
        int finishCount = 0;
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
     /*   DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String reminderdate = format.format(new Date());*/
        //查询已完成语句
        String sql = "select count(*) from Newtask where nfinish=1 and uid=" + uid;
        //查询未完成任务
    /*    String sql2 = "select count(*) from Newtask where nfinish=0 and uid=" + uid;
        //查询过期未完成任务
        String sql3 = "select count(*) from Newtask where nfinish=0 and uid=" + uid;*/
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                finishCount = cs.getInt(0);
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Log.w("task:finishCount",String.valueOf(finishCount));
        return  finishCount;
     }
    /**
     * 返回未完成的数目
     * 第一个是未完成的，第二个是逾期的
     */
    public int[] searchNotCompleted(int uid) {
        int[] finishOverdue = {0,0};
        int finishCount = 0;
        int overdueCount = 0;
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String currentdate = format.format(new Date());
        int result = 0;
        //查询语句
        String sql = "select * from Newtask where nfinish=0 and uid=" + uid + " order by ntasktime desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    String nTime = cs.getString(cs.getColumnIndex("nTime"));
                    result = nTime.compareTo(currentdate);
                    if(result >= 0) {
                        finishCount++;
                    }else{
                        overdueCount++;
                    }

                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finishOverdue[0]= finishCount;
        finishOverdue[1]= overdueCount;
      /*  Log.w("task:finishCount",String.valueOf(finishCount));
        Log.w("task:OverdueCount",String.valueOf(overdueCount));*/
        return  finishOverdue;
    }
}