package cn.edu.ncut.hikvision_graduation.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 赵希贤 on 2019/4/8.
 */

public class ToastUtil {
    private static Toast mToast;

    private ToastUtil() {
    }

    /**
     * 不能去排队效果，因为每次都执行一遍方法，返回一个对象。
     *
     * @param context
     * @param text
     */
    public static void showMsg(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 能去排队效果，因为第一次都执行if，之后执行else，都只有一个Toast对象
     *
     * @param context
     * @param msg
     */
    public static void showMsg2(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
