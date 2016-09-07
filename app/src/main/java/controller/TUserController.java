package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import db.TaskRecordOpenHelper;
import model.TUser;


public class TUserController {
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
     * 登录 成功返回TUser ，否则返回 null
     * @param user
     * @return
     */
    public TUser loginUser(TUser user) {
        TaskRecordOpenHelper tdb = new TaskRecordOpenHelper();
        SQLiteDatabase db = tdb.getConnection();
        String sql = "select * from TUser where uName=? and uPwd=?";
        Cursor cursor = db.rawQuery(sql, new String[] { user.getuName(), user.getuPwd() });
        if (cursor.moveToFirst() == true) {

            int uId = cursor.getInt(cursor.getColumnIndex("uId"));
            String uName = cursor.getString(cursor.getColumnIndex("uName"));
            String uEmail = cursor.getString(cursor.getColumnIndex("uEmail"));
            String uGender = cursor.getString(cursor.getColumnIndex("uGender"));
            String uImage = cursor.getString(cursor.getColumnIndex("uImage"));
            String uTime = cursor.getString(cursor.getColumnIndex("uTime"));

            TUser tuser = new TUser();
            user.setuId(uId);
            user.setuName(uName);
            user.setuEmail(uEmail);
            user.setuGender(uGender);
            user.setuImage(uImage);
            user.setuTime(uTime);
            cursor.close();
            db.close();
            return tuser;
        }
        return null;
    }
}