package cn.edu.ncut.hikvision_graduation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.edu.ncut.hikvision_graduation.util.ActivityCollector;
import cn.edu.ncut.hikvision_graduation.util.BaseActivity;
import cn.edu.ncut.hikvision_graduation.util.ToastUtil;
import cn.edu.ncut.hikvision_graduation.widget.CustomDialog;

/**
 * 此类原始方式登陆，，很多问题没有解决。。
 */

public class TestActivity extends BaseActivity implements View.OnClickListener {
    private SurfaceView mrow_1_sv_1, mrow_1_sv_2, mrow_1_sv_3;
    private SurfaceView mrow_2_sv_1, mrow_2_sv_2, mrow_2_sv_3;
    private SurfaceView mrow_3_sv_1, mrow_3_sv_2, mrow_3_sv_3;

    private SurfaceHolder holder;
    private SurfaceHolder holder1;
    private SurfaceHolder holder2;
    private MediaPlayer mediaPlayer;

    private AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        进行权限申请
//        riskAuthorization();

        //Android保持屏幕常亮的方法
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mrow_1_sv_1 = (SurfaceView) findViewById(R.id.row_1_sv_1);
        mrow_1_sv_2 = (SurfaceView) findViewById(R.id.row_1_sv_2);
        /*mrow_3_sv_1 = (SurfaceView) findViewById(R.id.row_3_sv_1);
        holder = mrow_1_sv_1.getHolder();
        holder.addCallback(this.new MyCallBack());
        holder1 = mrow_1_sv_2.getHolder();
        holder1.addCallback(this.new MyCallBack());
        holder2 = mrow_3_sv_1.getHolder();
        holder2.addCallback(this.new MyCallBack());*/


        mrow_1_sv_1.setOnClickListener(this);
        mrow_1_sv_2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.row_1_sv_1:
              /*  CustomDialog customDialog = new CustomDialog(TestActivity.this, R.style.CustomDialog);
                customDialog.setTitle("提示").setMessage("是否登出？")
                        .setCancel("取消", new CustomDialog.IOnCancelListener() {
                            @Override
                            public void onCancel(CustomDialog customDialog) {
                                ToastUtil.showMsg2(TestActivity.this, "Cancel……");
                                customDialog.dismiss();
                            }
                        }).setConfirm("确认", new CustomDialog.IOnConfirmListener() {
                    @Override
                    public void onConfirm(CustomDialog customDialog) {
                        ToastUtil.showMsg2(TestActivity.this, "Confirm……");
                        customDialog.dismiss();
                    }
                }).show();
                customDialog.setCancelable(false);
//                ToastUtil.showMsg2(this,"row_1_sv_1点我了");
                break;
            case R.id.row_1_sv_2:
                AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                View view = LayoutInflater.from(TestActivity.this).inflate(R.layout.layout_dialog, null);
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
                        // TODO: 2019/4/8
                        ToastUtil.showMsg2(TestActivity.this, "登陆了");
                        alertDialog.dismiss();
                    }
                });*/
                break;
        }
    }

    class MyCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //本地播放
            mediaPlayer = MediaPlayer.create(TestActivity.this, R.raw.laugh);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mediaPlayer.release();
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
            //申请权限
            //开始弹出授权信息，并决定是否同意，之后会调onRequestPermissionsResult()回调
        } else {
            // TODO: 2019/4/17
            ToastUtil.showMsg2(this, "授权成功了");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    //此步判断同意的结果是否都PackageManager.PERMISSION_GRANTED??
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            ToastUtil.showMsg2(this, "必须同意所有权限才能使用本程序");
//                            this.finish();
                            return;
                        }
                    }
                    // TODO: 2019/4/18
                    ToastUtil.showMsg2(this, "授权成功了");
                } else {
                    ToastUtil.showMsg2(this, "发生未知错误");
                    this.finish();
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
                ActivityCollector.finishAll();
            } else {
                ToastUtil.showMsg2(this, "再按一次后退键退出应用");
                lastClickTime = currentClickTime;
            }
        }
    }

}
