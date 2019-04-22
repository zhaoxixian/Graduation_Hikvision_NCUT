package cn.edu.ncut.hikvision_graduation;

import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;

import com.hikvision.netsdk.HCNetSDK;

import java.util.ArrayList;
import java.util.List;

import cn.edu.ncut.hikvision_graduation.pojo.CameraDevice;
import cn.edu.ncut.hikvision_graduation.util.ActivityCollector;
import cn.edu.ncut.hikvision_graduation.util.BaseActivity;
import cn.edu.ncut.hikvision_graduation.util.LogUtils;
import cn.edu.ncut.hikvision_graduation.util.ToastUtil;
import cn.edu.ncut.hikvision_graduation.widget.CustomDialog;


/**
 * 此类测试初始化、异常注册、实时预览（对象方式），
 * <p>
 * 在对象内部解决了Test3Activity没有解决的问题。
 */

public class Test4Activity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

    private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HikVision/sdklog";


    //得到设备的实例
    static HCNetSDK hCNetSDK = HCNetSDK.getInstance();

//    static Player player = Player.getInstance();

    private SurfaceView mrow_1_sv_1, mrow_1_sv_2, mrow_1_sv_3;
    private SurfaceView mrow_2_sv_1, mrow_2_sv_2, mrow_2_sv_3;
    private SurfaceView mrow_3_sv_1, mrow_3_sv_2, mrow_3_sv_3;

    private AlertDialog alertDialog = null;
    private int m_iLogID = -1;


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
            this.finish();
            return;
        }

        if (!initeActivity()) {
            this.finish();
            return;
        }


        CameraDevice cameradevice = new CameraDevice();
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

        device.startSinglePreview();



       /* CameraDevice cameradevice1 = new CameraDevice();
        cameradevice1.setIp("10.13.21.3");
        cameradevice1.setUsername("admin");
        cameradevice1.setPassword("jdzh12345");
        cameradevice1.setPort(8000);
//        cameradevice.setChannel();

        DeviceMain device1 = new DeviceMain(cameradevice1, mrow_1_sv_2.getHolder());


        int loginErr1 = device1.loginDevice();
        LogUtils.logE(TAG, "登陆失败" + loginErr1);

        device1.setExceptionCallBack();

        device1.startSinglePreview();*/

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

                /*//未登陆
                if (m_iLogID < 0) {//-1
                    AlertDialog.Builder builder = new AlertDialog.Builder(Test4Activity.this);
                    View view = LayoutInflater.from(Test4Activity.this).inflate(R.layout.layout_dialog, null);
                    EditText et_IPaddress = (EditText) view.findViewById(R.id.et_ip_address);
                    EditText et_UserName = (EditText) view.findViewById(R.id.et_username);
                    EditText et_PassWord = (EditText) view.findViewById(R.id.et_password);
                    EditText et_Port = (EditText) view.findViewById(R.id.et_port);

                    Button btn_Login = (Button) view.findViewById(R.id.btn_login);

                    builder.setCancelable(false);

                    alertDialog = builder.setTitle("请先登陆").setView(view).show();

                    btn_Login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtil.showMsg2(Test4Activity.this, "登陆了");
                            alertDialog.dismiss();
                        }
                    });


                } else {
                    CustomDialog customDialog = new CustomDialog(Test4Activity.this, R.style.CustomDialog);
                    customDialog.setTitle("提示").setMessage("是否登出？")
                            .setCancel("取消", new CustomDialog.IOnCancelListener() {
                                @Override
                                public void onCancel(CustomDialog customDialog) {
                                    ToastUtil.showMsg2(Test4Activity.this, "Cancel……");
                                    customDialog.dismiss();
                                }
                            }).setConfirm("确认", new CustomDialog.IOnConfirmListener() {
                        @Override
                        public void onConfirm(CustomDialog customDialog) {
                            ToastUtil.showMsg2(Test4Activity.this, "Confirm……");
                            customDialog.dismiss();
                        }
                    }).show();
                    customDialog.setCancelable(false);
//                ToastUtil.showMsg2(this,"row_1_sv_1点我了");
                }*/
                break;
            case R.id.row_1_sv_2:

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
        if (ContextCompat.checkSelfPermission(Test4Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(Test4Activity.this, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        if (ContextCompat.checkSelfPermission(Test4Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(Test4Activity.this, permissions, 1);
            //申请权限
            //开始弹出授权信息，并决定是否同意，之后会调onRequestPermissionsResult()回调
        } else {
            // TODO: 2019/4/17
            ToastUtil.showMsg2(Test4Activity.this, "授权成功了");
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
            ToastUtil.showMsg2(this, "在按一次后退键退出应用");
            lastClickTime = System.currentTimeMillis();
        } else {
            long currentClickTime = System.currentTimeMillis();
            //第二次点击
            //(currentClickTime-lastClickTime)<1秒，关闭
            //(currentClickTime-lastClickTime)>1秒，lastClickTime = System.currentTimeMillis();
            if (currentClickTime - lastClickTime < 1000) {
                // TODO: 2019/4/19 结束资源，
                ActivityCollector.finishAll();
            } else {
                ToastUtil.showMsg2(this, "再按一次后退键退出应用");
                lastClickTime = currentClickTime;
            }
        }
    }

}
