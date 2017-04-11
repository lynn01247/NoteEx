package tatait.com.noteex.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import tatait.com.noteex.R;

public class PopupDialog {
    private Context mContext;
    private Dialog dialog;
    private int resource;
    private View view;

    public PopupDialog(Context mContext, int res) {
        this.mContext = mContext;
        this.resource = res;
    }

    public PopupDialog builder() {
        // 获取Dialog布局
        view = LayoutInflater.from(mContext).inflate(resource, null);

        // 定义Dialog布局和参数
        dialog = new Dialog(mContext, R.style.Theme_Light_Dialog);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(view);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public View getLayoutView() {
        return view;
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
