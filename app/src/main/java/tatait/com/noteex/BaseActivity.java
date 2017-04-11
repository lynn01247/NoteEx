package tatait.com.noteex;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.View;

import com.umeng.message.PushAgent;

import cn.pedant.SweetAlert.SweetAlertDialog;
import solid.ren.skinlibrary.base.SkinBaseActivity;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.FontManager;
import tatait.com.noteex.util.ToastUtil;
import tatait.com.noteex.widget.App;
import tatait.com.noteex.widget.Engine;

/**
 * Created by Lynn on 2017/01/22
 */
public abstract class BaseActivity extends SkinBaseActivity implements View.OnClickListener {

    protected String TAG;
    protected App mApp;
    protected Engine mEngine;
    private SweetAlertDialog mLoadingDialog;
    private CountTimer countTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        mApp = App.getInstance();
        mEngine = mApp.getEngine();
        initView(savedInstanceState);
        setListener();
        processLogic(savedInstanceState);
        countTimer = new CountTimer(10000, 1000);

        FontManager.setDefaultFont(getApplicationContext());//由于是读取SD卡 需要重新设置默认字体
        PushAgent.getInstance(mApp).onAppStart();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    /**
     * 初始化布局以及View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    protected void showToast(String text) {
        ToastUtil.show(text);
    }

    public void showLoadingDialog() {
        CommonUtil.showDialogHandler = mHandler;
        if (countTimer != null) {
            countTimer.cancel();
            countTimer.start();
        }
        if (mLoadingDialog == null) {
            mLoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mLoadingDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setTitleText("数据加载中...");
        }
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (countTimer != null) countTimer.cancel();
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.showDialogCode:
                    dismissLoadingDialog();
                    break;
            }
        }
    };

    private class CountTimer extends CountDownTimer {

        /**
         * 构造函数
         *
         * @param millisInFuture    倒计的时间数,以毫秒为单位
         * @param countDownInterval 倒计每秒中间的间隔时间,以毫秒为单位
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (CommonUtil.showDialogHandler != null) {
                Message message = new Message();
                message.what = CommonUtil.showDialogCode;
                CommonUtil.showDialogHandler.sendMessage(message);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }
}
