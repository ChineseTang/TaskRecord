package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import android.app.Application;
import android.content.Context;

import model.TUser;


public class AppApplication extends Application {
    private static Context context;
    private static TUser user;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = getApplicationContext();
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