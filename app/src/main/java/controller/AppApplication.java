package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import android.app.Application;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    /**1
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

    /**2
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

    /**3
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

    /**4
     * 更新任务的状态，将完成的任务更换为未完成的任务。
     * @param ntid
     */
    public static void  changeToNotFinish(int ntid){
        for (Newtask nt: tasks) {
            if(nt.getNtId() == ntid ) {
                nt.setNfinish(0);
                break;
            }
        }
    }
    /**5
     * 更新任务的状态，将未完成的任务更换为已完成的任务。
     * @param ntid
     */
    public static void  changeToFinish(int ntid){
        for (Newtask nt: tasks) {
            if(nt.getNtId() == ntid ) {
                nt.setNfinish(1);
                break;
            }
        }
    }

    /**6
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

    /**7
     * 添加新的任务
     * @param newtask
     */
    public static void addTask(Newtask newtask) {
        tasks.add(newtask);
    }

    /**8
     * 根据用户id查询已经完成的任务 ，nfinish = 1；
     * @param uid
     * @return
     */
    public static int searchCompleted(int uid) {
        int sum = 0;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && nt.getNfinish() == 1) {
                sum = sum + 1;
            }
        }
        return sum;
    }

    /**9
     * 返回未完成的数目
     * 第一个是未完成的，第二个是逾期的
     * @param uid
     * @return
     */
    public static  int[] searchNotCompleted(int uid){
        int[] finishOverdue = {0,0};
        int finishCount = 0;
        int overdueCount = 0;
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String currentdate = format.format(new Date());//目前日期
        int result = 0;
        String nTime = null;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && nt.getNfinish() == 0) {
                nTime = nt.getaTime();
                result = nTime.compareTo(currentdate);
                if(result >= 0) {
                    finishCount++;
                }else{
                    overdueCount++;
                }
            }
        }
        finishOverdue[0]= finishCount;
        finishOverdue[1]= overdueCount;
        return  finishOverdue;
    }

    /**10
     * 根据用户id查询已经完成的任务
     * @param uid
     * @return
     */
    public static ArrayList<Newtask> searchFinishTasks(int uid) {
        ArrayList<Newtask> ts = new ArrayList<Newtask>();
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && nt.getNfinish() == 1) {
                ts.add(nt);
            }
        }
        return ts;
    }

    /**11
     * 查询未完成任务 不包括过期的
     * @param uid
     * @return
     */
    public static ArrayList<Newtask> searchNotCompleteTasks(int uid){
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String currentdate = format.format(new Date());
        ArrayList<Newtask> ts = new ArrayList<Newtask>();
        int result = 0;
        String anTime = null;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && nt.getNfinish() == 0) {
                anTime = nt.getaTime();
                result = anTime.compareTo(currentdate);
                if(result >= 0) {
                    ts.add(nt);
                }
            }
        }
        return ts;
    }

    /**12
     * 查询未完成任务 不包括过期的
     * @param uid
     * @return
     */
    public static ArrayList<Newtask> searchOverdueTasks(int uid) {
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String currentdate = format.format(new Date());
        ArrayList<Newtask> ts = new ArrayList<Newtask>();
        int result = 0;
        String anTime = null;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && nt.getNfinish() == 0) {
                anTime = nt.getaTime();
                result = anTime.compareTo(currentdate);
                if(result < 0) {
                    ts.add(nt);
                }
            }
        }
        return ts;
    }

    /**13
     * 根据用户id查询提醒的任务
     * @param uid
     * @return
     */
    public static ArrayList<Newtask> searchAlertTasks(int uid) {
        ArrayList<Newtask> ts = new ArrayList<Newtask>();
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && !(nt.getNotetime().equals("不提醒"))) {
                ts.add(nt);
            }
        }
        return ts;
    }

    /**14
     * 根据用户id查询未提醒的任务
     * @param uid
     * @return
     */
    public static ArrayList<Newtask> searchNotAlertTasks(int uid){
        ArrayList<Newtask> ts = new ArrayList<Newtask>();
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && nt.getNotetime().equals("不提醒")) {
                ts.add(nt);
            }
        }
        return ts;
    }

    /**15
     * 根据用户id查询提醒的数目
     * @param uid
     * @return
     */
    public static int searchAlertTasksNumber(int uid){
        int alertCount = 0;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && !(nt.getNotetime().equals("不提醒"))) {
                alertCount++;
            }
        }
        return alertCount;
    }
    /**16
     * 根据用户id查询不提醒的数目
     * @param uid
     * @return
     */
    public static int searchNotAlertTasksNumber(int uid){
        int alertCount = 0;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid  && nt.getNotetime().equals("不提醒")) {
                alertCount++;
            }
        }
        return alertCount;
    }

    /**17
     * 根据用户uid查询该用户的所有任务
     * @param uid
     * @return
     */
    public static int searchAllTasksNumber(int uid) {
        int allCount = 0;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid ) {
               allCount = allCount + 1;
            }
        }
        return allCount;
    }

    /**18
     * 查询除自定义之外的所有类型
     * @param uid
     * @return
     */
    public static ArrayList<String> selectAllTypesWithoutSignal(int uid){
        ArrayList<String> types = new ArrayList<String>();
        for(String tstype: arraytypes){
            if(!("自定义".equals(tstype))) {
                //添加到任务表中
                types.add(tstype);
            }
        }
        return types;
    }

    /**19
     * 查询该类型的任务数目
     * @param sid
     * @param uid
     * @return
     */
    public static int getTasktypeNumber(int sid , int uid) {
        int typeCount = 0;
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid && nt.getSid() == sid) {
               typeCount++;
            }
        }
        return typeCount;
    }

    /**20
     * 查询该类型的任务
     * @param tsid
     * @param uid
     * @return
     */
    public static ArrayList<Newtask> getTasksBytypeNumber(int tsid , int uid){
        ArrayList<Newtask> ts = new ArrayList<Newtask>();
        for (Newtask nt: tasks) {
            if(nt.getuId() == uid && nt.getSid() == tsid) {
                ts.add(nt);
            }
        }
        return ts;
    }

    /**21
     * 根据用户的id和时间选取某天的事务
     * @param uid
     * @param gettime
     * @return
     */
    public static ArrayList<Newtask> searchByDate(int uid,String gettime) {
        //先新建返回的任务
        ArrayList<Newtask>  searchByTimeTasks = new ArrayList<Newtask>();
        String nttime = null;
        //然后遍历tasks
        for (Newtask nt: tasks) {
            nttime = nt.getaTime();
            //Log.w("tasktime",nttime);
            if(nt.getuId() == uid && nttime.equals(gettime)) {
                searchByTimeTasks.add(nt);
            }
        }
        return searchByTimeTasks;
    }
}