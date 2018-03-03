package com.example.asus.lxymediaplayer;

import android.app.Activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ASUS on 2018/1/7.
 */

public class ActivityCollector {
    private static List<Activity> activityList = new ArrayList<>();

    public static void addActivity(Activity activity)  {
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity:activityList) {
            if(!activity.isFinishing())
            activity.finish();
        }
    }

}
