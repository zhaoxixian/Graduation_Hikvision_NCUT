package cn.edu.ncut.hikvision_graduation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.hikvision.netsdk.HCNetSDK;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.ncut.hikvision_graduation.pojo.CameraDevice;
import cn.edu.ncut.hikvision_graduation.util.ActivityCollector;
import cn.edu.ncut.hikvision_graduation.util.BaseActivity;
import cn.edu.ncut.hikvision_graduation.util.LogUtils;
import cn.edu.ncut.hikvision_graduation.util.RegexUtil;
import cn.edu.ncut.hikvision_graduation.util.ToastUtil;
import cn.edu.ncut.hikvision_graduation.widget.CustomDialog;


/**
 * 在test7activity的基础上，
 * <p>
 * 加入正则表达式，校验登陆，提取出一个工具类。传入相应要判断的的字符串，返回boolean
 * <p>
 * 加入设备唯一性校验，根据ip地址判断，用集合，泛型String，存储每个对象的ip值，
 */
public class Test8Activity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

    private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HikVision/sdklog";


    //得到设备的实例
    static HCNetSDK hCNetSDK = HCNetSDK.getInstance();

//    static Player player = Player.getInstance();

    private SurfaceView mrow_1_sv_1, mrow_1_sv_2, mrow_1_sv_3;
    private SurfaceView mrow_2_sv_1, mrow_2_sv_2, mrow_2_sv_3;
    private SurfaceView mrow_3_sv_1, mrow_3_sv_2, mrow_3_sv_3;

    private int m_iLogID = -1;

    //此处定义一个成员集合，收集DeviceMain，在用户按下返回键时，遍历一次执行释放资源
    private List<DeviceMain> deviceMainList = new ArrayList<>();

    //此处定义一个成员集合，收集每个对象的ip，作为设备唯一性的依据
    private List<String> IpList = new ArrayList<>();

    //此处定义一个成员集合，收集每个对象的ip的list，作为设备唯一性的依据
    Set<String> onlyOneIp = new HashSet<String>();


    //测试对象surfaceview--2
    private CameraDevice cameradevice_test = new CameraDevice();
    private DeviceMain device_test = new DeviceMain();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        LogUtils.level = LogUtils.Nothing;

        //设置该活动常亮
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //进行动态权限授权
//        riskAuthorization();


        if (!initeSdk()) {
            ToastUtil.showMsg2(getApplicationContext(), "SDK设备没有初始化成功,重启软件试试呦!");
            return;
        }

        if (!initeActivity()) {
            ToastUtil.showMsg2(getApplicationContext(), "布局没有初始化成功,重启软件试试呦!");
            return;
        }


        /*CameraDevice cameradevice = new CameraDevice();
        cameradevice.setIp("10.13.21.3");
        cameradevice.setUsername("admin");
        cameradevice.setPassword("jdzh12345");
        cameradevice.setPort(8000);
//        cameradevice.setChannel();

        DeviceMain device = new DeviceMain(cameradevice, mrow_1_sv_1.getHolder());


        int loginErr = device.loginDevice();
        if (loginErr < 0) {
            LogUtils.logE(TAG, "登陆失败" + loginErr);
        }
        device.setExceptionCallBack();

        device.startSinglePreview();*/

/*
        cameradevice_test = new CameraDevice();
        cameradevice_test.setIp("10.13.21.3");
        cameradevice_test.setUsername("admin");
        cameradevice_test.setPassword("jdzh12345");
        cameradevice_test.setPort(8000);
//        cameradevice.setChannel();

        device_test = new DeviceMain(cameradevice_test, mrow_1_sv_2.getHolder());


        int loginErr1 = device_test.loginDevice();
        LogUtils.logE(TAG, "登陆失败" + loginErr1);

        device_test.setExceptionCallBack();

        device_test.startSinglePreview();

        deviceMainList.add(device_test);*/
    }

    /**
     * 初始化SDK
     *
     * @return true - success;false - fail
     */
    private boolean initeSdk() {

        // init net sdk
        if (!hCNetSDK.NET_DVR_Init()) {
            LogUtils.logE(TAG, "HCNetSDK init is failed!:" + hCNetSDK.NET_DVR_GetLastError());
            return false;
        }
        return true;
    }


    /**
     * GUI init
     *
     * @return true--成功，false--失败
     */
    private boolean initeActivity() {
        mrow_1_sv_1 = (SurfaceView) findViewById(R.id.row_1_sv_1);
        mrow_1_sv_2 = (SurfaceView) findViewById(R.id.row_1_sv_2);
        mrow_1_sv_3 = (SurfaceView) findViewById(R.id.row_1_sv_3);
        mrow_2_sv_1 = (SurfaceView) findViewById(R.id.row_2_sv_1);
        mrow_2_sv_2 = (SurfaceView) findViewById(R.id.row_2_sv_2);
        mrow_2_sv_3 = (SurfaceView) findViewById(R.id.row_2_sv_3);
        mrow_3_sv_1 = (SurfaceView) findViewById(R.id.row_3_sv_1);
        mrow_3_sv_2 = (SurfaceView) findViewById(R.id.row_3_sv_2);
        mrow_3_sv_3 = (SurfaceView) findViewById(R.id.row_3_sv_3);


        mrow_1_sv_1.setOnClickListener(this);
        mrow_1_sv_2.setOnClickListener(this);
        mrow_1_sv_3.setOnClickListener(this);
        mrow_2_sv_1.setOnClickListener(this);
        mrow_2_sv_2.setOnClickListener(this);
        mrow_2_sv_3.setOnClickListener(this);
        mrow_3_sv_1.setOnClickListener(this);
        mrow_3_sv_2.setOnClickListener(this);
        mrow_3_sv_3.setOnClickListener(this);

        return true;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.row_1_sv_1:

                break;
            case R.id.row_1_sv_2:
                int isLogin = device_test.getLoginID();
                if (isLogin < 0) {//isLogin=-1，未登陆，或者登陆失败
//未登陆
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Test8Activity.this);
                    builder.setTitle("请先登陆");

                    //通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View view = LayoutInflater.from(Test8Activity.this).inflate(R.layout.layout_dialog, null);

                    //设置我们自己定义的布局文件作为弹出框的Content
                    builder.setView(view);

                    //局部内部类访问局部变量，需要加final
                    final EditText et_IPaddress = (EditText) view.findViewById(R.id.et_ip_address);
                    final EditText et_UserName = (EditText) view.findViewById(R.id.et_username);
                    final EditText et_PassWord = (EditText) view.findViewById(R.id.et_password);
                    final EditText et_Port = (EditText) view.findViewById(R.id.et_port);

                    builder.setCancelable(false);


                    builder.setPositiveButton("确定登陆", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //1.获取登陆所需要的信息
                            String ip = et_IPaddress.getText().toString().trim();
                            String username = et_UserName.getText().toString().trim();
                            String password = et_PassWord.getText().toString().trim();
//                            int port = Integer.parseInt(et_Port.getText().toString().trim());
                            String portString = et_Port.getText().toString().trim();


                            //2.开始校验
                            if (!RegexUtil.isMatchIp(ip)) {
                                ToastUtil.showMsg2(getApplicationContext(), "IP地址不合法");
                                et_IPaddress.setText("");
                                et_IPaddress.requestFocus();
                                return;
                            }

                            if (!RegexUtil.isMatchUsername(username)) {
                                ToastUtil.showMsg2(getApplicationContext(), "用户名不合法(大小写都行，必须是5到7个)");
                                et_UserName.setText("");
                                et_UserName.requestFocus();
                                return;
                            }

                            if (!RegexUtil.isMatchPassword(password)) {
                                ToastUtil.showMsg2(getApplicationContext(), "密码不合法(下划线的任何单词字符，必须是6到12个)");
                                et_PassWord.setText("");
                                et_PassWord.requestFocus();
                                return;
                            }


                            if (!RegexUtil.isMatchPort(portString)) {
                                ToastUtil.showMsg2(getApplicationContext(), "端口号不合法");
                                et_Port.setText("");
                                et_Port.requestFocus();
                                return;
                            }

                            //校验通过后，写入Arraylist(IP)，和sharedpreferences

                            //3.写入Arraylist(IP)
                            IpList.add(ip);
                            if (!IpList.isEmpty()) {
                                for (String strIP : IpList) {
                                    boolean only = onlyOneIp.add(strIP);
                                    if (!only) {
                                        ToastUtil.showMsg2(getApplicationContext(), "亲,此设备已经添加过了!");
                                        return;
                                    }
                                }
                            }

                            //4.写入sharedpreferences
                            //5.开始登陆
                            int port = Integer.parseInt(portString);
                            cameradevice_test.setIp(ip);
                            cameradevice_test.setUsername(username);
                            cameradevice_test.setPassword(password);
                            cameradevice_test.setPort(port);

                            if (!initeSdk()) {
                                ToastUtil.showMsg2(getApplicationContext(), "SDK设备没有初始化成功");
                                return;
                            }

                            //将登陆信息初始化
                            device_test.setPara(cameradevice_test);

                            //设置播放容器
                            device_test.setHolder(mrow_1_sv_2.getHolder());


                            int m_iLogID = device_test.loginDevice();
                            if (m_iLogID < 0) { //还小于0那么登陆失败
                                LogUtils.logE(TAG, "登陆失败" + m_iLogID);
                            }

                            //设置错误回调函数
                            device_test.setExceptionCallBack();

                            //开始预览
                            device_test.startSinglePreview();

                            //将device_test添加到集合中，方便管理
                            deviceMainList.add(device_test);

                            ToastUtil.showMsg2(Test8Activity.this, "登陆成功……");
                            dialog.dismiss();
                        }
                    });

//                    Button btn_Login = (Button) view.findViewById(R.id.btn_login);



                    /*btn_Login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtil.showMsg2(Test7Activity.this, "登陆了");
                            alertDialog.dismiss();
                        }
                    });*/
                    builder.setNegativeButton("取消登陆", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtil.showMsg2(Test8Activity.this, "取消登陆……");
                        }
                    });

                    builder.show();

                } else {
                    CustomDialog customDialog = new CustomDialog(Test8Activity.this, R.style.CustomDialog);
                    customDialog.setTitle("提示").setMessage("是否登出 ?")
                            .setCancel("取消", new CustomDialog.IOnCancelListener() {
                                @Override
                                public void onCancel(CustomDialog customDialog) {
                                    ToastUtil.showMsg2(Test8Activity.this, "Cancel……");
                                    customDialog.dismiss();
                                }
                            }).setConfirm("确认", new CustomDialog.IOnConfirmListener() {
                        @Override
                        public void onConfirm(CustomDialog customDialog) {
                            device_test.stopPlay();

                            device_test.logoutDevice();

                            device_test.freeSDK();

                            if (!IpList.isEmpty()) {
                                for (String ipList : IpList) {
                                    //将ip对象从list集合中清理
                                    IpList.remove(ipList);
                                }
                            }

                            if (!onlyOneIp.isEmpty()) {
                                for (String ipSet : onlyOneIp) {
                                    //将ip对象从set集合中清理
                                    onlyOneIp.remove(ipSet);
                                }
                            }

                            ToastUtil.showMsg2(Test8Activity.this, "登出成功啦!");

                            customDialog.dismiss();

                        }
                    }).show();
                    customDialog.setCancelable(false);
                }


                /*
                04-20 20:22:06.649 12359-12359/cn.edu.ncut.hikvision_graduation D/DeviceMain: 停止实时播放成功！
                04-20 20:22:06.655 12359-12359/cn.edu.ncut.hikvision_graduation D/DeviceMain: 停止本地播放成功！
                04-20 20:22:06.663 12359-12359/cn.edu.ncut.hikvision_graduation D/DeviceMain: 关闭视频流成功！
                04-20 20:22:06.664 12359-12359/cn.edu.ncut.hikvision_graduation D/DeviceMain: 释放播放端口成功！
                04-20 20:22:06.664 12359-12359/cn.edu.ncut.hikvision_graduation D/DeviceMain: 停止播放成功！
                04-20 20:22:06.686 12359-12359/cn.edu.ncut.hikvision_graduation D/DeviceMain: 登出设备成功！
                04-20 20:22:06.712 12359-12359/cn.edu.ncut.hikvision_graduation D/DeviceMain: 释放SDK资源成功！
                 */
                break;
        }
    }

    /**
     * 进行运行时权限处理
     */
    private void riskAuthorization() {
        /*
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.CAPTURE_AUDIO_OUTPUT" />
        <uses-permission android:name="android.permission.RECORD_AUDIO" />
        */
        List<String> permissionList = new ArrayList<>();
        //检查是否获得了权限
        if (ContextCompat.checkSelfPermission(Test8Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(Test8Activity.this, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        if (ContextCompat.checkSelfPermission(Test8Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(Test8Activity.this, permissions, 1);
            //申请权限
            //开始弹出授权信息，并决定是否同意，之后会调onRequestPermissionsResult()回调
        } else {
            // TODO: 2019/4/17
            ToastUtil.showMsg2(Test8Activity.this, "授权成功了");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    //此步判断同意的结果是否都PackageManager.PERMISSION_GRANTED??
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //必须同意所有权限才能使用本程序
                            ToastUtil.showMsg2(this, "必须同意所有权限才能使用本程序");
                            this.finish();
                            return;
                        }
                    }
                    // TODO: 2019/4/18
                } else {
                    ToastUtil.showMsg2(this, "发生未知错误");
                    this.finish();
                    return;
                }
                break;
        }
    }

    /**
     * 按下返回键后关闭的逻辑判断
     */
    private long lastClickTime = 0;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        //第一次点击
        if (lastClickTime <= 0) {
            ToastUtil.showMsg2(this, "再按一次后退键退出应用");
            lastClickTime = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            //第二次点击
            //(currentClickTime-lastClickTime)<1秒，关闭
            //(currentClickTime-lastClickTime)>1秒，lastClickTime = System.currentTimeMillis();
            if (currentClickTime - lastClickTime < 1000) {
                // TODO: 2019/4/19 结束资源，

                if (!IpList.isEmpty()) {
                    for (String ipList : IpList) {
                        //将ip对象从list集合中清理
                        IpList.remove(ipList);
                    }
                }

                if (!onlyOneIp.isEmpty()) {
                    for (String ipSet : onlyOneIp) {
                        //将ip对象从set集合中清理
                        onlyOneIp.remove(ipSet);
                    }
                }
                if (!deviceMainList.isEmpty()) {
                    for (DeviceMain deviceMain : deviceMainList) {
                        if (null != deviceMain) {
                            deviceMain.stopPlay();
                            deviceMain.logoutDevice();
                            deviceMain.freeSDK();
                        }
                        //将对象从集合中清理
                        deviceMainList.remove(deviceMain);
                    }
                }

                ToastUtil.showMsg2(Test8Activity.this, "资源已回收,再会了!");

                ActivityCollector.finishAll();
            } else {
                ToastUtil.showMsg2(this, "再按一次后退键退出应用");
                lastClickTime = currentClickTime;
            }
        }
    }

}
