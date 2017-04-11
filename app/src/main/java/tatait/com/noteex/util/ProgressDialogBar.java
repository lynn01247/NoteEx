package tatait.com.noteex.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import tatait.com.noteex.R;


/**
 * 自定义进度条
 *
 * @author Administrator
 */
public class ProgressDialogBar extends Dialog {
    public static ProgressDialogBar ProgressDialogBar = null;

    public ProgressDialogBar(Context context) {
        super(context);
    }

    public ProgressDialogBar(Context context, int theme) {
        super(context, theme);
    }

    public static ProgressDialogBar createDialog(Context context) {
        ProgressDialogBar = new ProgressDialogBar(context,
                R.style.CustomProgressDialog);
//        ProgressDialogBar.setContentView(R.layout.progress_layout);
        ProgressDialogBar.setContentView(R.layout.progress_layout_with);
        ((AnimationDrawable) ((ImageView) ProgressDialogBar.findViewById(R.id.person_image_activity_load)).getBackground()).start();
        return ProgressDialogBar;
    }

    public static ProgressDialogBar createDialogwithmsg(Context context, String msg) {
        ProgressDialogBar = new ProgressDialogBar(context,
                R.style.CustomProgressDialog);
        ProgressDialogBar.setContentView(R.layout.progress_layout_with);
        ((TextView) ProgressDialogBar.findViewById(R.id.text_progress)).setText(msg);
        ((AnimationDrawable) ((ImageView) ProgressDialogBar.findViewById(R.id.person_image_activity_load)).getBackground()).start();
        return ProgressDialogBar;
    }

    /*
     * public void onWindowFocusChanged(boolean hasFocus){
     *
     * if (ProgressDialogBar == null){ return; } // ImageView imageView =
     * (ImageView) ProgressDialogBar.findViewById(R.id.img);//////
     * //AnimationDrawable animationDrawable = (AnimationDrawable)
     * imageView.getBackground(); //animationDrawable.start(); }
     *
     * /**
     *
     * [Summary] setTitile 标题
     *
     * @param strTitle
     *
     * @return
     */
    public ProgressDialogBar setTitile(String strTitle) {
        return ProgressDialogBar;
    }

    /**
     * [Summary] setMessage 提示内容
     *
     * @param strMessage
     * @return
     */
    public void setProgressMsg(String strMessage) {
        TextView tvMsg = (TextView) ProgressDialogBar
                .findViewById(R.id.text_progress);
        if (tvMsg != null)
            tvMsg.setText(strMessage);
    }
}
