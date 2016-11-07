package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import db.TaskRecordOpenHelper;
import model.TUser;


public class TUserController {
    /**
     * 判断用户名是否存在
     * @param username
     * @return 存在返回true
     */
    public boolean isExistUsername(String username) {
        boolean rs = false;
        int returnback = 0;
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        String sql = "select count(*) from TUser where uName='" + username + "'";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                returnback = cs.getInt(0);
                //Log.w("task",String.valueOf(returnback));
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(returnback != 0) {
            rs = true;
        }
        return rs;
    }
    /**
     * 注册用户
     * @param user
     * @return
     */
    public boolean registerUser(TUser user) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        ContentValues values = new ContentValues();
        values.put("uName", user.getuName());
        values.put("uEmail", user.getuEmail());
        //Log.w("task",user.getuEmail() + " ");
        values.put("uPwd", user.getuPwd());
        values.put("uGender", user.getuGender());
        values.put("uImage",user.getuImage());
        values.put("uTime", user.getuTime());
        //插入数据
        db.insert("TUser", null, values);
        tdb.close(db);
        return true;
    }

    /**
     * 更新用户名
     * @param id
     * @param newName
     * @return
     */
    public boolean updateUserName(int  id,String newName) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        String sql = "update TUser set uName='" + newName + "' where uId=" + id;
        AppApplication.getUser().setuName(newName);
        db.execSQL(sql);
        tdb.close(db);
        return true;
    }

    /**
     * 更新密码
     * @param id
     * @param newPwd
     * @return
     */
    public boolean updateUserPwd(int  id,String newPwd) {

        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        String sql = "update TUser set uPwd='" + newPwd + "' where uId=" + id;
        //AppApplication.getUser().setuName(newName);
        db.execSQL(sql);
        tdb.close(db);
        return true;
    }
    /**
     * 登录 成功返回TUser ，否则返回 null
     * @param user
     * @return
     */
    public TUser loginUser(TUser user) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        TUser tuser = new TUser();
        String sql = "select uPwd from TUser where uName='" + user.getuName() + "'";
        Cursor cursor = db.rawQuery(sql, null);
        //Cursor cursor = db.rawQuery(sql, new String[] { user.getuName(), user.getuPwd() });
        try {
            if (cursor.moveToFirst()) {
                String getpwd = cursor.getString(cursor.getColumnIndex("uPwd"));
                //判断密码相等
                if(getpwd.equals(user.getuPwd())) {
                    String newsql = "select * from TUser where uName='" + user.getuName() +"'";
                    cursor = db.rawQuery(newsql,null);
                    if(cursor.moveToFirst()) {
                        int uId = cursor.getInt(cursor.getColumnIndex("uId"));
                        String uName = cursor.getString(cursor.getColumnIndex("uName"));
                        String uEmail = cursor.getString(cursor.getColumnIndex("uEmail"));
                        String uGender = cursor.getString(cursor.getColumnIndex("uGender"));
                        String uImage = cursor.getString(cursor.getColumnIndex("uImage"));
                        String uTime = cursor.getString(cursor.getColumnIndex("uTime"));
                        tuser.setuId(uId);
                        tuser.setuName(uName);
                        tuser.setuEmail(uEmail);
                        tuser.setuGender(uGender);
                        tuser.setuImage(uImage);
                        tuser.setuTime(uTime);
                    }
                }
            }
            cursor.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //searchAllUser();
        return tuser;
    }
    /**
     * 判断密码 成功返回true ，否则返回 false
     * @param
     * @return
     */
    public boolean judgePwd(String username,String pwd) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        boolean rs = false;
        String sql = "select uPwd from TUser where uName='" + username + "'";
        Cursor cursor = db.rawQuery(sql, null);
        //Cursor cursor = db.rawQuery(sql, new String[] { user.getuName(), user.getuPwd() });
            if (cursor.moveToFirst()) {
                String getpwd = cursor.getString(cursor.getColumnIndex("uPwd"));
                //判断密码相等
                if(getpwd.equals(pwd)) {
                     rs = true;
                    }
                }
            cursor.close();
            db.close();
        return rs;
    }
    /*public void searchAllUser() {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        String sql = "select uName from TUser";
        Cursor cs = db.rawQuery(sql, null);
        try {
            if (cs.moveToFirst()) {
                do {
                    String uName = cs.getString(cs.getColumnIndex("uName"));
                    //添加到任务表中
                    Log.w("taskname",uName);
                } while (cs.moveToNext());
            }
            cs.close();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
}