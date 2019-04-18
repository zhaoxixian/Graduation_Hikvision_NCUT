package cn.edu.ncut.hikvision_graduation.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by 赵希贤 on 2019/4/12.
 */

/**
 * 收集每个活动，方便退出后，完全销毁
 */
public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //知晓当前是在哪个活动，子类访问父类的方法的同时super将自己的this带上了

        //getSimpleName():the simple name of the underlying class，当前类名
        LogUtil.logD(TAG, this.getClass().getSimpleName());

        //将每个继承BaseActivity的活动，创建时
        //添加到集合中
        ActivityCollector.addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //finish()后，才会调用销毁方法
        //将每个继承BaseActivity的活动，销毁时
        //从集合中删除
        ActivityCollector.removeActivity(this);
    }
}
