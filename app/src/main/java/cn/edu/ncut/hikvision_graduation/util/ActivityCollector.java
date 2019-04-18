package cn.edu.ncut.hikvision_graduation.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵希贤 on 2019/4/12.
 */

/*
优雅的结束每个活动。收集每个活动，方便退出后，优雅的完全的销毁活动
 */

public class ActivityCollector {

    //定义成员变量。那么我又想通过类名访问成员方法，只能将成员变量变成静态
    //public static List<Activity> activities = new ArrayList<Activity>();
    private static List<Activity> activities = new ArrayList<Activity>();

    /**
     * 将活动暂存到ArrayList中
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 将活动从ArrayList中删除，销毁的时候调用
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 遍历集合中的每个活动，并结束他们，按下返回键时，调用这个方法
     * <p>
     * 调用finish();此时页面从返回栈中顶部移除，并没有立即调用onDestroy()
     * <p>
     * 那么就可以在onDestroy()中public static void removeActivity(Activity activity) 了
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
