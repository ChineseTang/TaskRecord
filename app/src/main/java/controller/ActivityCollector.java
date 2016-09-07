package controller;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();

    //add a activity into the ArrayList
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }
    //remove a activity into the ArrayList
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }
    // log out all the activities
    public static void finishAll() {
        for(Activity ac : activities) {
            if(!ac.isFinishing()) {
                ac.finish();
            }
        }
    }
}