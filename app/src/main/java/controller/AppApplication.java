package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import android.app.Application;
import android.content.Context;

import model.Newtask;
import model.TUser;


public class AppApplication extends Application {
    private static Context context;
    private static TUser user;
    private static String touchtime;
    private static Newtask updatetask;//修改的任务
    private static boolean loginout;

    public static boolean isLoginout() {
        return loginout;
    }

    public static void setLoginout(boolean loginout) {
        AppApplication.loginout = loginout;
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
        loginout = false;
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


}