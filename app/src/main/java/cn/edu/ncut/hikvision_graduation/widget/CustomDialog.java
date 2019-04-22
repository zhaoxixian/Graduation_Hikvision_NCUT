package cn.edu.ncut.hikvision_graduation.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.edu.ncut.hikvision_graduation.R;


/**
 * Created by Administrator on 2019/4/9.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {
    //声明控件
    private TextView mTv_Title, mTv_Message, mTv_Cancel, mTv_Confirm;

    //声明赋值的成员变量
    private String title, message, cancel, confirm;

    private IOnCancelListener iOnCancelListener;

    private IOnConfirmListener iOnConfirmListener;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    /*public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }*/

    //返回的还是本对象，不是另一个对象。可以一直.下去
    public CustomDialog setTitle(String title) {
        this.title = title;
        return this;//返回本对象
    }

    public CustomDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public CustomDialog setCancel(String cancel, IOnCancelListener listener) {
        this.cancel = cancel;
        this.iOnCancelListener = listener;
        return this;
    }

    public CustomDialog setConfirm(String confirm, IOnConfirmListener listener) {
        this.confirm = confirm;
        this.iOnConfirmListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_dialog);

        mTv_Title = (TextView) findViewById(R.id.tv_title);
        mTv_Message = (TextView) findViewById(R.id.tv_message);
        mTv_Cancel = (TextView) findViewById(R.id.tv_cancel);
        mTv_Confirm = (TextView) findViewById(R.id.tv_confirm);


        //设置宽度
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        Point point = new Point();
        display.getSize(point);
        layoutParams.width = (int) ((point.x) * 0.8);//设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(layoutParams);


        /*
            public static boolean isEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
         */
        //将文字设置到定义好的dialog上面
        if (!TextUtils.isEmpty(title)) {
            mTv_Title.setText(title);
        }
        if (!TextUtils.isEmpty(message)) {
            mTv_Message.setText(message);
        }
        if (!TextUtils.isEmpty(cancel)) {
            mTv_Cancel.setText(cancel);
        }
        if (!TextUtils.isEmpty(confirm)) {
            mTv_Confirm.setText(confirm);
        }

        mTv_Confirm.setOnClickListener(this);
        mTv_Cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (iOnCancelListener != null) {
                    iOnCancelListener.onCancel(this);
                }
                break;
            case R.id.tv_confirm:
                if (iOnConfirmListener != null) {
                    iOnConfirmListener.onConfirm(this);
                }
                break;
        }
    }

    public interface IOnCancelListener {
        void onCancel(CustomDialog customDialog);
    }

    public interface IOnConfirmListener {
        void onConfirm(CustomDialog customDialog);
    }
}

