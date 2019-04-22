package cn.edu.ncut.hikvision_graduation.load_ad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import cn.edu.ncut.hikvision_graduation.MainActivity;
import cn.edu.ncut.hikvision_graduation.util.BaseActivity;
import cn.edu.ncut.hikvision_graduation.util.LogUtils;

/**
 * Created by 赵希贤 on 2019/4/8.
 * <p>
 * 此类用来为下步的活动初始化，做准备
 */

public class AdActivity extends BaseActivity {
    private Handler mHandler;

    private static String TAG = "AdActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        //延时3s之后跳转到MainActivity
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AdActivity.this, MainActivity.class);
                startActivity(intent);

                mHandler.removeCallbacksAndMessages(null);
                //清空handler延时，并防内存泄漏
                LogUtils.logD(TAG, "清空handler延时，并防内存泄漏");

                finish();//此时页面关闭
                LogUtils.logD(TAG, "finish()执行了");
            }
        }, 3000);
    }
}
