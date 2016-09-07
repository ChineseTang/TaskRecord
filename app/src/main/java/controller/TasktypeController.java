package controller;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import db.TaskRecordOpenHelper;
import model.Tasktype;

/**
 * Created by tangzhijing on 2016/9/5.
 */
public class TasktypeController {
    /**
     * 添加一条新的类型数据
     */
    public boolean addType(String newType) {
        TaskRecordOpenHelper to = new TaskRecordOpenHelper();
        SQLiteDatabase db = to.getConnection();
        String sql = "insert into Tasktype(tstyle) values('"+newType + "')";
        try {
            db.execSQL(sql);
            db.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<String> selectAllTypes() {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ArrayList<String> types = new ArrayList<String>();
        String sql = "select * from Tasktype order by sid desc";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    String tstype = cs.getString(cs.getColumnIndex("tstyle")) +" ▼";
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
     * 根据类型的id 获得名称
     */
    public String getTstyleBySid(int sid) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        String tstyle = null;
        String sql = "select tstyle from Tasktype where sid='" + sid + "'";
        Log.w("task",sql);
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
}
