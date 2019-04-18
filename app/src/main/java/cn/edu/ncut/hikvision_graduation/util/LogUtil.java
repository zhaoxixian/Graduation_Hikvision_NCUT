package cn.edu.ncut.hikvision_graduation.util;

import android.util.Log;

/**
 * Created by 赵希贤 on 2019/4/16.
 */

public class LogUtil {

    private static final int Verbose = 1;
    private static final int Debug = 2;
    private static final int Info = 3;
    private static final int Warn = 4;
    private static final int Error = 5;

    public static final int Nothing = 6;//当不用打印日志时，将level置为Nothing

    public static int level = Verbose;//在什么级别，就显示那个级别和它以上的日志，再不用显示日志时，只要level>5即可

    public static void logV(String TAG, String Msg) {
        if (level <= Verbose) {
            Log.v(TAG, Msg);
        }
    }

    public static void logD(String TAG, String Msg) {
        if (level <= Debug) {
            Log.d(TAG, Msg);
        }
    }

    public static void logI(String TAG, String Msg) {
        if (level <= Info) {
            Log.i(TAG, Msg);
        }
    }

    public static void logW(String TAG, String Msg) {
        if (level <= Warn) {
            Log.w(TAG, Msg);
        }
    }

    public static void logE(String TAG, String Msg) {
        if (level <= Error) {
            Log.e(TAG, Msg);
        }
    }
}
