package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import model.Newtask;
import model.TUser;

//公共模块，app共享
public class AppApplication extends Application {
    private static Context context;//应用程序上下文
    private static TUser user;//存储用户信息
    private static String touchtime;//存储添加任务的时间
    private static Newtask updatetask;//修改的任务
    private static ArrayList<Newtask> tasks;//存储所有的任务，方便查询，不用每次都去数据库中查询，提高效率
    private static ArrayList<String> arraytypes;

    public static ArrayList<String> getArraytypes() {
        return arraytypes;
    }

    public static void setArraytypes(ArrayList<String> arraytypes) {
        AppApplication.arraytypes = arraytypes;
    }

    public static ArrayList<Newtask> getTasks() {
        return tasks;
    }

    public static void setTasks(ArrayList<Newtask> tasks) {
        AppApplication.tasks = tasks;
    }

    public static Newtask getUpdatetask() {
        return updatetask;
    }

    public static void setUpdatetask(Newtask updatetask) {
        AppApplication.updatetask = updatetask;
    }

    public static String getTouchtime() {
        return touchtime;
    }

    public static void setTouchtime(String touchtime) {
        AppApplication.touchtime = touchtime;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = getApplicationContext();
        touchtime = null;
        updatetask = null;
        tasks = new ArrayList<Newtask>();
    }
    public static Context getContext() {
        return context;
    }

    public static TUser getUser() {
        return user;
    }

    public static void setUser(TUser user) {
        AppApplication.user = user;
    }

    /**
     * 根据用户的id和时间查询当天的任务
     * @param uid
     * @param gettime
     * @return
     */
    public  static ArrayList<Newtask> searchByTime(int uid,String gettime) {
        //先新建返回的任务
        ArrayList<Newtask>  searchByTimeTasks = new ArrayList<Newtask>();
        //然后遍历tasks
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid && nt.getaTime().equals(gettime)) {
                searchByTimeTasks.add(nt);
            }
        }

        return searchByTimeTasks;
    }

    /**
     * 根据用户的id和时间选取一个时间段的
     * @param uid
     * @param gettime
     * @return
     */
    public  static ArrayList<Newtask> searchDrawByTime(int uid,String gettime) {
        //先新建返回的任务
        ArrayList<Newtask>  searchByTimeTasks = new ArrayList<Newtask>();
        String nttime = null;
        //然后遍历tasks
        for (Newtask nt: tasks) {
            nttime = nt.getaTime().substring(0, gettime.length());
            //Log.w("tasktime",nttime);
            if(nt.getuId() == uid && nttime.equals(gettime)) {
                searchByTimeTasks.add(nt);
            }
        }
        return searchByTimeTasks;
    }

    /**
     * 根据任务ntid主键删除任务
     * @param ntid
     */
    public static void deleteTaskByNtid(int ntid) {
        for (Newtask nt: tasks) {
            if(nt.getNtId() == ntid ) {
                tasks.remove(nt);
                break;
            }
        }
    }

    /**
     * 更新任务的状态，将完成的任务更换为未完成的任务。
     * @param ntid
     */
    public static void  changeToNotFinish(int ntid){
        for (Newtask nt: tasks) {
            if(nt.getNtId() == ntid ) {
                nt.setNfinish(0);
            }
        }
    }
    /**
     * 更新任务的状态，将未完成的任务更换为已完成的任务。
     * @param ntid
     */
    public static void  changeToFinish(int ntid){
        for (Newtask nt: tasks) {
            if(nt.getNtId() == ntid ) {
                nt.setNfinish(1);
            }
        }
    }

    /**
     * 更新任务
     * @param newtask
     */
    public static void updateTask(Newtask newtask) {
        int newtaskId = newtask.getNtId();
        for (Newtask nt: tasks) {
            if(nt.getNtId() == newtaskId ) {
                //更新任务
                nt.setNcontent(newtask.getNcontent());
                nt.setSid(newtask.getSid());
                nt.setNfinish(newtask.getNfinish());
                nt.setaTime(newtask.getaTime());
                nt.setNotetime(newtask.getNotetime());
                nt.setNtasktime(newtask.getNtasktime());
                break;
            }
        }
    }

    /**
     * 添加新的任务
     * @param newtask
     */
    public static void addTask(Newtask newtask) {
        tasks.add(newtask);
    }
}