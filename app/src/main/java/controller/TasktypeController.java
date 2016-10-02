package controller;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import db.TaskRecordOpenHelper;

/**
 * Created by tangzhijing on 2016/9/5.
 */
public class TasktypeController {
    /**
     * 添加一条新的类型数据
     */
    public boolean addType(String newType, int uid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "insert into Tasktype(tstyle,uid) values('" + newType + "','" + uid + "')";
        try {
            db.execSQL(sql);
            //往AppApplication中添加新类型
            //AppApplication.getArraytypes().add(newType);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 修改一条类型数据
     */
    public boolean updateType(String newType, int sid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        //String sql = "insert into Tasktype(tstyle,uid) values('"+newType + "','" + uid + "')";
        String sql = "update Tasktype set tstyle='" + newType + "' where sid=" + sid;
        //Log.w("task",sql);
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //结束事务
            db.endTransaction();
            db.close();
        }
       return true;
    }

    public ArrayList<String> selectAllTypes(int uid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<String> types = new ArrayList<String>();
        //Log.w("taskuid",String.valueOf(uid));
        String sql = "select * from Tasktype where uid=0 or uid=" + uid + " order by sid desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    String tstype = cs.getString(cs.getColumnIndex("tstyle"));
                    //Log.w("tasktype",tstype + " " + cs.getInt(cs.getColumnIndex("uid")));
                    //添加到任务表中
                    types.add(tstype);
                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    public ArrayList<String> selectAllTypesWithoutSignal(int uid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<String> types = new ArrayList<String>();
        String sql = "select * from Tasktype where uid='0' or uid='" + uid + "' order by sid desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    String tstype = cs.getString(cs.getColumnIndex("tstyle"));
                    //消除自定义类型
                    if (!("自定义".equals(tstype))) {
                        //添加到任务表中
                        types.add(tstype);
                    }

                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    /**
     * 根据类型的名称 获得主键id
     */
    public int getSidByTstyle(String tstyle) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        int sid = -1;
        String sql = "select sid from Tasktype where tstyle='" + tstyle + "'";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    sid = cs.getInt(cs.getColumnIndex("sid"));
                    //添加到任务表中
                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sid;
    }

    /**
     * 获得该类型的uid
     * @param tstyle
     * @return
     */
    public int getUidByTstyle(String tstyle) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        int sid = -1;
        String sql = "select uid from Tasktype where tstyle='" + tstyle + "'";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    sid = cs.getInt(cs.getColumnIndex("uid"));
                    //添加到任务表中
                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sid;
    }

    /**
     * 根据类型的id 获得名称
     */
    public String getTstyleBySid(int sid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        String tstyle = null;
        String sql = "select tstyle from Tasktype where sid='" + sid + "'";
        //Log.w("task",sql);
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    tstyle = cs.getString(cs.getColumnIndex("tstyle"));
                    //添加到任务表中
                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tstyle;
    }

    public boolean deleteTypeBySid(int sid) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        db.beginTransaction();
        //String sql = "insert into Tasktype(tstyle,uid) values('"+newType + "','" + uid + "')";
        String sql = "delete from Tasktype where sid='" + sid + "'";
        //Log.w("task",sql);
        try {
            db.execSQL(sql);
            //AppApplication.getArraytypes().remove(oldtype);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //结束事务
            db.endTransaction();
            db.close();
        }
        return true;
    }
}
