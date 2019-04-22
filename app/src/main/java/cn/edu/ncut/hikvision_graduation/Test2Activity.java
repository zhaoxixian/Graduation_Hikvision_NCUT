package cn.edu.ncut.hikvision_graduation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

import java.util.ArrayList;
import java.util.List;

import cn.edu.ncut.hikvision_graduation.util.ActivityCollector;
import cn.edu.ncut.hikvision_graduation.util.BaseActivity;
import cn.edu.ncut.hikvision_graduation.util.LogUtils;
import cn.edu.ncut.hikvision_graduation.util.ToastUtil;
import cn.edu.ncut.hikvision_graduation.widget.CustomDialog;


/**
 * 此类原始方式登陆，，很多问题没有解决。。比TestActivity强了一些
 */

public class Test2Activity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "MainActivity";

    private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HikVision/sdklog";


    //得到设备的实例
    static HCNetSDK hCNetSDK = HCNetSDK.getInstance();
    static Player player = Player.getInstance();

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime
    private int m_iPort = -1; // play port


    //byStartChan，模拟通道的起始通道号，从1开始。数字通道的起始通道号见下面参数byStartDChan
//    private int m_iStartChan = 0; // start channel no

    //byChanNum，设备模拟通道个数，数字（IP）通道最大个数为byIPChanNum + byHighDChanNum*256
//    private int m_iChanNum = 0; // channel number

    //记录开始的通道数，，//NET_DVR_DEVICEINFO_V30类中存着很多信息byChanNum，byIPChanNum
    private int m_iStartChan = 0; // start channel no
    private int m_iChanNum = 0; // channel number

    /**
     * 登入标记 -1未登入，0已登入
     * // return by NET_DVR_Login_v30
     */
    private int m_iLogID = -1;


    private boolean m_bNeedDecode = true;
    private boolean m_bStopPlayback = false;
    private Thread thread;
    private boolean isShow = true;


    public final String ADDRESS = "10.13.21.3";
    public final int PORT = 8000;
    public final String USER = "admin";
    public final String PSD = "jdzh12345";


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

        // login on the device
        m_iLogID = loginDevice();
        if (m_iLogID < 0) {
            LogUtils.logE(TAG, "This device logins failed!");
            return;
        } else {
            //登陆成功
            System.out.println("m_iLogID=" + m_iLogID);//0
        }

        // get instance of exception callback and set
        //得到回调函数实例，方便下面NET_DVR_SetExceptionCallBack，传入
        /*
        1.自己重写的ExceptionCallBack接口中抽象方法，写在实现类中。
        2.自己并没有调用
        3.必要的时候，系统自动调用实现类中的已重写的方法
         */
        // get instance of exception callback and set
        ExceptionCallBack oexceptionCbf = getExceptiongCbf();
        if (oexceptionCbf == null) {
            LogUtils.logE(TAG, "ExceptionCallBack object is failed!");
            return;//关系到注册接收异常的，重连等消息的窗口句柄或回调函数，此参数没有，直接return
        }

        if (!hCNetSDK.NET_DVR_SetExceptionCallBack(
                oexceptionCbf)) {
            LogUtils.logE(TAG, "NET_DVR_SetExceptionCallBack 注册失败!" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }


        /*
        NET_DVR_PREVIEWINFO
            1.public long lChannel;
                通道号，目前设备模拟通道号从1开始，数字通道的起始通道号通过NET_DVR_GetDVRConfig
                （配置命令NET_DVR_GET_IPPARACFG_V40）获取（dwStartDChan）。
            2.public int dwStreamType;码流类型：0-主码流，1-子码流，2-码流3，3-虚拟码流
            3.public int lLinkMode;
                连接方式：0- TCP方式，1- UDP方式，2- 多播方式，3- RTP方式，4-RTP/RTSP，5-RTP/HTTP
            4.public HWND hPlayWnd;播放窗口的句柄，为NULL表示不解码显示。
            5.public boolean bBlocked;
                0- 非阻塞取流，1- 阻塞取流。
                如果阻塞取流，SDK内部connect失败将会有5s的超时才能够返回，不适合于轮询取流操作。
            6.public boolean bPassbackRecord;
                0-不启用录像回传，1-启用录像回传。
                ANR断网补录功能，客户端和设备之间网络异常恢复之后自动将前端数据同步过来，需要设备支持。
            5.public String byPreviewMode;
                预览模式：0- 正常预览，1- 延迟预览
            6.public String byStreamID;
                流ID，为字母、数字和"_"的组合，lChannel为0xffffffff时启用此参数
            7.public String byProtoType;
                应用层取流协议：0- 私有协议，1- RTSP协议。
                主子码流支持的取流协议通过登录返回结构参数NET_DVR_DEVICEINFO_V30的byMainProto、bySubProto值得知。
                设备同时支持私协议和RTSP协议时，该参数才有效，默认使用私有协议，可选RTSP协议
            8.public String byRes1;保留，置为0
            9.public int dwDisplayBufNum;
                播放库播放缓冲区最大缓冲帧数，取值范围：1~50，置0时默认为1。
                设置显示缓冲需要在播放库调用PlayM4_Play之前调用，
                该参数替换原先NET_DVR_SetPlayerBufNumber接口

         */
        //预览模块
        /*final NET_DVR_PREVIEWINFO ClientInfo = new NET_DVR_PREVIEWINFO();
        ClientInfo.lChannel = 0;//通道号，目前设备模拟通道号从1开始，数字通道的起始通道号通过NET_DVR_GetDVRConfig（配置命令NET_DVR_GET_IPPARACFG_V40）获取（dwStartDChan）。
        ClientInfo.dwStreamType = 0; // substream-主码流
        ClientInfo.bBlocked = 1;//1- 阻塞取流*/
        //设置默认点
//        startSinglePreview();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    SystemClock.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isShow)
                                startSinglePreview();
                        }
                    });
                }
            }
        });
        thread.start();

    }


    //m_iPlaybackID，，从NET_DVR_PlayBackByTime返回
    private void startSinglePreview() {
        if (m_iPlaybackID >= 0) {
            LogUtils.logD(TAG, "Please stop palyback first");
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            LogUtils.logE(TAG, "fRealDataCallBack object is failed!");
            return;
        }
        LogUtils.logD(TAG, "m_iStartChan:" + m_iStartChan);//1

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;//0
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;

        //实时预览（支持多码流）。m_iPlayID应该等于lRealHandle前的预览句柄，NET_DVR_RealPlay_V40的返回值
        m_iPlayID = hCNetSDK.NET_DVR_RealPlay_V40(m_iLogID,
                previewInfo, fRealDataCallBack);
        if (m_iPlayID < 0) {
            LogUtils.logE(TAG, "NET_DVR_RealPlay is failed!Err:"
                    + hCNetSDK.NET_DVR_GetLastError());
            return;
        }
//        isShow = false;
    }

    /**
     * @return callback instance
     * @fn getRealPlayerCbf
     * @brief get realplay callback instance
     */
    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            /*
            lRealHandle
            [out] 当前的预览句柄，NET_DVR_RealPlay_V40的返回值
            dwDataType
            [out] 数据类型
                宏定义 宏定义值 含义
                    NET_DVR_SYSHEAD 1 系统头数据
                    NET_DVR_STREAMDATA 2 流数据（包括复合流或音视频分开的视频流数据）
                    NET_DVR_AUDIOSTREAMDATA 3 音频数据
                    NET_DVR_PRIVATE_DATA 112 私有数据,包括智能信息
            pBuffer
            [out] 存放数据的缓冲区指针，，保留pDataBuffer
            dwBufSize
            [out] 缓冲区大小iDataSize
            pUser
            [out] 用户数据，，保留


             * @param lRealHandle 当前的预览句柄，NET_DVR_RealPlay_V40的返回值
             * @param dwDataType 数据类型:1-系统头数据，2-流数据，3音频数据，112-私有数据包括智能信息
             * @param pBuffer 存放数据的缓冲区
             * @param dwBufSize 缓冲区大小
             * @param pUser 用户数据
             * @return -1-表示失败
             */
            public void fRealDataCallBack(int iRealHandle, int iDataType,
                                          byte[] pDataBuffer, int iDataSize) {
                // player channel 1
                Test2Activity.this.processRealData(iDataType, pDataBuffer,
                        iDataSize, Player.STREAM_REALTIME);//过程中的实时数据
            }
        };
        return cbf;
    }


    /**
     * @param iDataType   - data type [in]数据类型
     * @param pDataBuffer - data buffer [in]存放数据的缓冲区指针
     * @param iDataSize   - data size [in]缓冲区大小
     * @param iStreamMode - stream mode [in]
     * @return NULL
     * @fn processRealData
     * @author zhuzhenlei
     * @brief process real data
     */

    /*
    dwDataType
            [out] 数据类型 iDataType
                宏定义 宏定义值 含义
                    NET_DVR_SYSHEAD 1 系统头数据
                    NET_DVR_STREAMDATA 2 流数据（包括复合流或音视频分开的视频流数据）
                    NET_DVR_AUDIOSTREAMDATA 3 音频数据
                    NET_DVR_PRIVATE_DATA 112 私有数据,包括智能信息
     */
    public void processRealData(int iDataType,
                                byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (!m_bNeedDecode) {
            // LogUtils.logD(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" +
            // iDataType + ",iDataSize:" + iDataSize);
        } else {
            if (hCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                if (m_iPort >= 0) {
                    return;
                }
                m_iPort = player.getPort();//获取播放库未使用的通道号
                if (m_iPort == -1) {
                    LogUtils.logE(TAG, "PlayM4_GetLastError-getPort is failed with: "
                            + player.getLastError(m_iPort));
                    return;
                }
                LogUtils.logD(TAG, "getPort succ with: " + m_iPort);
                if (iDataSize > 0) {//缓冲区大小>0
                    if (!player.setStreamOpenMode(m_iPort,
                            iStreamMode)) // set stream mode
                        //PlayM4_SetStreamOpenMode
                        //设置实时流播放模式STREAME_REALTIME
                    /*
                    nMode
                    [in] 流播放模式，如下所示： 宏定义 宏定义值 含义
                    STREAME_REALTIME 0 此模式（默认）下, 会尽量保正实时性, 防止数据阻塞; 而且数据检查严格
                    STREAME_FILE 1 此模式下按时间戳播放
                     */

                    /*
                    必须在播放之前设置。2.2以上版本SDK以后可以做暂停，快放，慢放，单帧播放操作。
                    PlayM4_SetStreamOpenMode在PlayM4_OpenStream之前调用才有效。
                     */ {
                        LogUtils.logE(TAG, "setStreamOpenMode failed with error code:" + player.getLastError(m_iPort));
                        return;
                    }

                    /*PlayM4_SetStreamOpenMode
                    必须在播放之前设置。2.2以上版本SDK以后可以做暂停，快放，慢放，单帧播放操作。
                    PlayM4_SetStreamOpenMode在PlayM4_OpenStream之前调用才有效。
                     */

                    /*
                    PlayM4_OpenStream
                    打开流。

                    BOOL  PlayM4_OpenStream(
                      LONG           nPort,
                      PBYTE          pFileHeadBuf,
                      DWORD          nSize,
                      DWORD          nBufPoolSize
                    );

                    Parameters

                    nPort
                    [in] 播放通道号
                    pFileHeadBuf
                    [in] 文件头数据
                    nSize
                    [in] 文件头长度
                    nBufPoolSize
                    [in] 设置播放器中存放数据流的缓冲区大小。范围是SOURCE_BUF_MIN~ SOURCE_BUF_MAX。该值过小会导致无法解码。 宏定义 宏定义值 含义
                    SOURCE_BUF_MIN 1024*50 缓冲数据流缓冲最小值
                    SOURCE_BUF_MAX 1024*100000 缓冲数据流缓冲最大值
                     */
                    if (!player.openStream(m_iPort, pDataBuffer,
                            iDataSize, 2 * 1024 * 1024)) // open stream
                    {
                        LogUtils.logE(TAG, "openStream failed with error code:" + player.getLastError(m_iPort));
                        return;
                    }
                    //开启播放。
                    /*
                    BOOL PlayM4_Play(
                      LONG   nPort,
                      HWND   hWnd
                    );
                    Parameters
                    nPort
                    [in] 播放通道号
                    HWND
                    [in] 播放视频的窗口句柄
                     */
                    if (!player.play(m_iPort,
                            mrow_1_sv_1.getHolder())) {
                        LogUtils.logE(TAG, "play failed with error code:" + player.getLastError(m_iPort));
                        return;
                    }
                    if (!player.playSound(m_iPort)) {
                        LogUtils.logE(TAG, "playSound failed with error code:"
                                + player.getLastError(m_iPort));
                        return;
                    }
                }
            } else {
                /*
                case HCNetSDK.NET_DVR_STREAMDATA:
                case HCNetSDK.NET_DVR_STD_AUDIODATA:
                case HCNetSDK.NET_DVR_STD_VIDEODATA:
                // Log.i(TAG, "处理流数据");
                 */
                if (!player.inputData(m_iPort, pDataBuffer,
                        iDataSize)) {
                    // LogUtils.logE(TAG, "inputData failed with: " +
                    // player.getLastError(m_iPort));
                    for (int i = 0; i < 4000 && m_iPlaybackID >= 0
                            && !m_bStopPlayback; i++) {
                        /*
                        if (iDataSize > 0 && m_iPort != -1) {
                        for (i = 0; i < 400; i++) {
                            if (Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
                                Log.i(TAG, "输入数据成功！");
                                break;
                            }
                            Thread.sleep(10);
                        }
                        if (i == 400) {
                            Log.e(TAG, "输入数据失败！");
                        }
                    }
                         */
                        if (player.inputData(m_iPort,
                                pDataBuffer, iDataSize)) {
                            LogUtils.logD(TAG, "输入数据成功！");
                            break;

                        }

                        if (i % 100 == 0) {
                            LogUtils.logE(TAG, "inputData failed with: "
                                    + player.getLastError(m_iPort) + ", i:" + i);
                            // Log.e(TAG, "输入数据失败！");
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            LogUtils.logE(TAG, "视频流解码异常！" + e.toString());
                        }
                    }
                }
            }
        }
    }


    /**
     * @return login ID
     * @fn loginDevice
     * @author zhangqing
     * @brief login on device
     */
    private int loginDevice() {
        int iLogID = -1;
        iLogID = loginNormalDevice();
        return iLogID;
    }

    /**
     * @return login ID
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     */
    private int loginNormalDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        //NET_DVR_DEVICEINFO_V30类中存着很多信息byChanNum，byIPChanNum
        if (null == m_oNetDvrDeviceInfoV30) {
            LogUtils.logE(TAG, "DeviceInfo设备信息(同步登录即pLoginInfo中bUseAsynLogin为0时有效) \n new is failed!");
            return -1;//NET_DVR_Login_V30需要这个，如果为null，直接return
        }
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = hCNetSDK.NET_DVR_Login_V30(ADDRESS, PORT,
                USER, PSD, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            LogUtils.logE(TAG, "NET_DVR_Login is failed!Err:"
                    + hCNetSDK.NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        LogUtils.logD(TAG, "NET_DVR_Login is Successful!");
        return iLogID;
    }


    /**
     * @return exception instance
     * @fn getExceptiongCbf
     */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
                LogUtils.logD(TAG, "" + iType);
            }
        };
        return oExceptionCbf;
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
//        hCNetSDK.NET_DVR_SetLogToFile(3, SD_CARD_PATH, true);
        return true;
    }


    /**
     * GUI init
     *
     * @return true--成功，false--失败
     */
    private boolean initeActivity() {
        findViews();
        mrow_1_sv_1.getHolder().addCallback(this.new MyCallBack());
        /*
        此时findViews()一执行，SurfaceView就会显示，那么Surface就会被创建，那么就会立即调用holder.addCallback()

        //Surface通过SurfaceHolder接口中的getSurface()获取
        //SurfaceView一旦显示，那么Surface被创建，SurfaceView隐藏，Surface被销毁。都会立即调用holder.addCallback()
         */
        return true;
    }

    /**
     * get view instance
     */
    private void findViews() {

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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.row_1_sv_1:
                /*//未登陆
                if (m_iLogID < 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Test2Activity.this);
                    View view = LayoutInflater.from(Test2Activity.this).inflate(R.layout.layout_dialog, null);
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
                            ToastUtil.showMsg2(Test2Activity.this, "登陆了");
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    CustomDialog customDialog = new CustomDialog(Test2Activity.this, R.style.CustomDialog);
                    customDialog.setTitle("提示").setMessage("是否登出？")
                            .setCancel("取消", new CustomDialog.IOnCancelListener() {
                                @Override
                                public void onCancel(CustomDialog customDialog) {
                                    ToastUtil.showMsg2(Test2Activity.this, "Cancel……");
                                    customDialog.dismiss();
                                }
                            }).setConfirm("确认", new CustomDialog.IOnConfirmListener() {
                        @Override
                        public void onConfirm(CustomDialog customDialog) {
                            ToastUtil.showMsg2(Test2Activity.this, "Confirm……");
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
     * 内部类，实现Callback接口
     */
    class MyCallBack implements SurfaceHolder.Callback {

        /*
         surfaceCreated(SurfaceHolder holder)：当Surface第一次创建后会立即调用该函数。
            程序可以在该函数中做些和绘制界面相关的初始化工作，一般情况下都是在另外的线程来绘制界面，
            所以不要在这个函数中绘制Surface。
         surfaceChanged(SurfaceHolder holder, int format, int width,int height)：
            当Surface的状态（大小和格式）发生变化的时候会调用该函数，
            在surfaceCreated调用后该函数至少会被调用一次。
         surfaceDestroyed(SurfaceHolder holder)：当Surface被摧毁前会调用该函数，
            该函数被调用后就不能继续使用Surface了，一般在该函数中来清理使用的资源。
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setFormat(PixelFormat.TRANSLUCENT);

            LogUtils.logD(TAG, "surface is created" + m_iPort);
            if (-1 == m_iPort) {
                return;
            }
            //Surface通过SurfaceHolder接口中的getSurface()获取
            //SurfaceView一旦显示，那么Surface被创建，SurfaceView隐藏，Surface被销毁。都会立即调用holder.addCallback()
            Surface surface = holder.getSurface();
            if (surface.isValid()) {
                if (!player.setVideoWindow(m_iPort, 0, holder)) {//设置视频显示窗口
                    LogUtils.logE(TAG, "Player setVideoWindow failed!");
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
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
        if (ContextCompat.checkSelfPermission(Test2Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(Test2Activity.this, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        if (ContextCompat.checkSelfPermission(Test2Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(Test2Activity.this, permissions, 1);
            //申请权限
            //开始弹出授权信息，并决定是否同意，之后会调onRequestPermissionsResult()回调
        } else {
            // TODO: 2019/4/17
            ToastUtil.showMsg2(Test2Activity.this, "授权成功了");
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
