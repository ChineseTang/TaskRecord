package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tangzhijing on 2016/8/31.
 */
public class TaskRecordOpenHelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;// 数据库版本号
    private final static String DATABASE_NAME = "taskrecord.db";// 数据库名
    private static Context context;// 内容上下文

    private final String tuser = "create table TUser("
            + "uId INTEGER PRIMARY KEY autoincrement,"
            + "uName   varchar(64) not null," + "uEmail	 varchar(64) not null,"
            + "uPwd	 varchar(64) not null," + "uGender  varchar(4) not null,"
            + "uImage   varchar(128) not null," + "uTime    long  not null)";
    private final String task = "create table Task("
            + "tId INTEGER PRIMARY KEY autoincrement,"
            + "uId integer not null," + "topic varchar(256) not null,"
            + "descripe	text,endtime long," + "notetime long," + "finish int,"
            + "importance int," + "ttime long," + "note int," + "noteway int,"
            + "rate int)";
    private final String newtask = "create table Newtask("
            + "ntId INTEGER PRIMARY KEY autoincrement,"
            + "sid integer not null,"
            + "uId integer not null," + "ncontent	text," + "nfinish int,"
            + "nTime varchar(64)," + "notetime varchar(64),"+ "ntasktime long)";
    private final String tasktype = "create table Tasktype(" +
            "sid INTEGER PRIMARY KEY autoincrement," +
            "tstyle varchar(64) not null,uid int not null)";
    private List<String> tasktypedatas = new LinkedList<String>();
    private final String tasktypedata0 = "insert into Tasktype(tstyle,uid) values('自定义','0')";
    private final String tasktypedata1 = "insert into Tasktype(tstyle,uid) values('学习','0')";
    private final String tasktypedata2 = "insert into Tasktype(tstyle,uid) values('工作','0')";
    private final String tasktypedata3 = "insert into Tasktype(tstyle,uid) values('家庭','0')";
    private final String tasktypedata4 = "insert into Tasktype(tstyle,uid) values('杂事','0')";
    private final String tasktypedata5 = "insert into Tasktype(tstyle,uid) values('记账','0')";
    //private final String tasktypedata5 = "insert into tasktype(tstyle) values('自定义')";
    public static void setContext(Context context) {
        TaskRecordOpenHelper.context = context;
    }

    public TaskRecordOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public TaskRecordOpenHelper() {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);// 创建数据库
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 第一次执行时，创建表格
        db.execSQL(tuser);
        db.execSQL(newtask);
        db.execSQL(tasktype);
        //往类型表里面插入几条数据
        tasktypedatas.add(tasktypedata0);
        tasktypedatas.add(tasktypedata1);
        tasktypedatas.add(tasktypedata2);
        tasktypedatas.add(tasktypedata3);
        tasktypedatas.add(tasktypedata4);
        tasktypedatas.add(tasktypedata5);
        inertOrUpdateDateBatch(tasktypedatas,db);
    }

    public SQLiteDatabase getConnection() {
        SQLiteDatabase db = getWritableDatabase();
        return db;
    }
    //批量插入数据
    public void inertOrUpdateDateBatch(List<String> sqls,SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (String sql : sqls) {
                db.execSQL(sql);
            }
            // 设置事务标志为成功，当结束事务时就会提交事务
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 结束事务
            db.endTransaction();
        }
    }
    public void close(SQLiteDatabase db) {
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

}