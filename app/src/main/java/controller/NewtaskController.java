package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        db.beginTransaction();
        try {
            db.execSQL(sql);
            //再在AppApplication中去将该条事务修改
            AppApplication.addTask(newtask);
            //设置事务成功
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //结束事务
            db.endTransaction();
            db.close();
        }
        return true;
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
        String sql = "select * from Newtask where uid=" + uid + " and nTime like '" + gettime + "%' order by nTime desc";
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
        String sql = "select * from Newtask where notetime='不提醒' and uid=" + uid + " order by nTime desc";
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
        String sql = "select * from Newtask where notetime!='不提醒' and uid=" + uid + " order by nTime desc";
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
     * @param uid  查询提醒的任务的数目
     * @return
     */
    public int searchAlertTasksNumber(int uid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        int alertCount = 0;
        //查询语句
        String sql = "select count(*) from Newtask where notetime!='不提醒' and uid=" + uid;
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                alertCount = cs.getInt(0);
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //返回任务
        return alertCount;
    }
    /**
     *
     * @param uid  查询不提醒的任务的数目
     * @return
     */
    public int searchNotAlertTasksNumber(int uid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        int alertCount = 0;
        //查询语句
        String sql = "select count(*) from Newtask where notetime='不提醒' and uid=" + uid;
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                alertCount = cs.getInt(0);
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //返回任务
        return alertCount;
    }


    //查询所有的任务
    public ArrayList<Newtask> searchAllTasks(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where uid=" + uid + " order by nTime desc";
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
    //查询所有的任务数目
    public int searchAllTasksNumber(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        int alertCount = 0;
        //查询语句
        String sql = "select count(*) from Newtask where uid=" + uid;
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                alertCount = cs.getInt(0);
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //返回任务
        return alertCount;

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
        String sql = "select * from Newtask where nfinish=1 and uid=" + uid + " order by nTime desc";
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
     * 查询未完成任务 包括过期的
     * @param uid
     * @return
     */
    public ArrayList<Newtask> searchNotFinishTasks(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where nfinish=0 and uid=" + uid + " order by nTime desc";
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
        db.beginTransaction();
        String sql = "update Newtask set nfinish=1 where ntid=" + ntid;
        try {
            db.execSQL(sql);
            //再在AppApplication中去将该条事务的状态修改
            AppApplication.changeToFinish(ntid);
            //设置事务成功
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //结束事务
            db.endTransaction();
            db.close();
        }
        return true;
    }


    /**
     * //更新任务的状态，将完成的任务更换为已经未完成的任务。
     * @param ntid
     * @return
     */
    public boolean changeToNotFinish(int ntid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        db.beginTransaction();
        String sql = "update Newtask set nfinish=0 where ntid=" + ntid;
        try {
            db.execSQL(sql);
            //再在AppApplication中去将该条事务的状态修改
            AppApplication.changeToNotFinish(ntid);
            //设置事务成功
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //结束事务
            db.endTransaction();
            db.close();
        }
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
        //开启一个事务
        db.beginTransaction();
        String sql = "delete from Newtask where ntid=" + ntid;
        try {
            db.execSQL(sql);
            //再在AppApplication中去将该条事务删除掉
            AppApplication.deleteTaskByNtid(ntid);
            //设置事务成功
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //结束事务
            db.endTransaction();
            db.close();
        }
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
        db.beginTransaction();
        try {
            db.execSQL(sql);
            //再在AppApplication中去将该条事务修改
            AppApplication.updateTask(newtask);
            //设置事务成功
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //结束事务
            db.endTransaction();
            db.close();
        }
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
        String sql = "select * from Newtask where nfinish=0 and uid=" + uid + " order by nTime desc";
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
    /**
     * 查询未完成任务 不包括过期的
     * @param uid
     * @return
     */
    public ArrayList<Newtask> searchNotCompleteTasks(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String currentdate = format.format(new Date());
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        int result = 0;
        //查询语句
        String sql = "select * from Newtask where nfinish=0 and uid=" + uid + " order by nTime desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    //获得的时间与现在作对比
                    String anTime = cs.getString(cs.getColumnIndex("nTime"));
                    result = anTime.compareTo(currentdate);
                    if(result >= 0) {
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
                    }

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
     * 查询未完成任务 不包括过期的
     * @param uid
     * @return
     */
    public ArrayList<Newtask> searchOverdueTasks(int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String currentdate = format.format(new Date());
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        int result = 0;
        //查询语句
        String sql = "select * from Newtask where nfinish=0 and uid=" + uid + " order by nTime desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    //获得的时间与现在作对比
                    String anTime = cs.getString(cs.getColumnIndex("nTime"));
                    result = anTime.compareTo(currentdate);
                    if(result < 0) {
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
                    }

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
    public int getTasktypeNumber(int sid , int uid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        int alertCount = 0;
        //查询语句
        String sql = "select count(*) from Newtask where sid=" + sid +" and uid=" + uid;
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                alertCount = cs.getInt(0);
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //返回任务
        return alertCount;
    }

    /**
     * 根据用户id 和 类型id 返回该用户该类型的任务
     * @param tsid
     * @param uid
     * @return
     */
    public ArrayList<Newtask> getTasksBytypeNumber(int tsid , int uid) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask where sid=" + tsid +" and uid=" + uid + " order by nTime desc";
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
     * 查询所有任务存储到AppApplication中
     * @return
     */
    public ArrayList<Newtask> searchAllTasksIntoApp() {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<Newtask> tasks = new ArrayList<Newtask>();
        //查询语句
        String sql = "select * from Newtask order by nTime desc";
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
}