package tatait.com.noteex.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import tatait.com.noteex.R;

public class AlertDialog {
    private Context mContext;
    private Dialog dialog;
    private LinearLayout lLayout_bg,txt_msg_id_ll;
    private TextView txt_title,txt_msg_id;
    //	private View txt_line;
    private TextView txt_msg;
    private Button btn_neg;
    private Button btn_pos;
    private ImageView img_line;
    private Display display;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showMsgId = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    public AlertDialog(Context mContext) {
        this.mContext = mContext;
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public AlertDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_alertdialog, null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        txt_msg_id_ll = (LinearLayout) view.findViewById(R.id.txt_msg_id_ll);
        txt_title = (TextView) view.findViewById(R.id.txt_title);
//		txt_line = (View) view.findViewById(R.id.txt_line);
        txt_title.setVisibility(View.GONE);
//		txt_line.setVisibility(View.GONE);
        txt_msg = (TextView) view.findViewById(R.id.txt_msg);
        txt_msg.setVisibility(View.GONE);
        txt_msg_id = (TextView) view.findViewById(R.id.txt_msg_id);
        txt_msg_id_ll.setVisibility(View.GONE);
        btn_neg = (Button) view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = (Button) view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);

        img_line = (ImageView) view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));
        return this;
    }

    public AlertDialog setTitle(String title) {
        showTitle = true;
        if ("".equals(title)) {
            txt_title.setText("标题");
        } else {
            txt_title.setText(title);
        }
        return this;
    }

    public AlertDialog setMsg(String msg) {
        showMsg = true;
        if ("".equals(msg)) {
            txt_msg.setText("内容");
        } else {
            txt_msg.setText(msg);
        }
        return this;
    }
    public AlertDialog setMsgId(String id) {
        showMsgId = true;
        if ("".equals(id)) {
            txt_msg_id.setText("");
        } else {
            txt_msg_id.setText(id);
        }
        return this;
    }

    public AlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public AlertDialog setPositiveButton(String text,
                                         final OnClickListener listener) {
        showPosBtn = true;
        if ("".equals(text)) {
            btn_pos.setText("确定");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public AlertDialog setNegativeButton(String text,
                                         final OnClickListener listener) {
        showNegBtn = true;
        if ("".equals(text)) {
            btn_neg.setText("取消");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
//			txt_line.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
//			txt_line.setVisibility(View.VISIBLE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }
        if (showMsgId) {
            txt_msg_id_ll.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btn_pos.setText("确定");
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btn_pos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
            img_line.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
